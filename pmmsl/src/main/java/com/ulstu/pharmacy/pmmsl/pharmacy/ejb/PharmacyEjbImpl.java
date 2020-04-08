package com.ulstu.pharmacy.pmmsl.pharmacy.ejb;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import com.ulstu.pharmacy.pmmsl.common.exception.MedicamentDiscountException;
import com.ulstu.pharmacy.pmmsl.medicament.dao.MedicamentDao;
import com.ulstu.pharmacy.pmmsl.medicament.mapper.MedicamentMapper;
import com.ulstu.pharmacy.pmmsl.medicament.view.MedicamentViewModel;
import com.ulstu.pharmacy.pmmsl.pharmacy.dao.PharmacyDao;
import com.ulstu.pharmacy.pmmsl.pharmacy.dao.PharmacyMedicamentDao;
import com.ulstu.pharmacy.pmmsl.pharmacy.entity.Pharmacy;
import com.ulstu.pharmacy.pmmsl.pharmacy.entity.PharmacyMedicament;
import com.ulstu.pharmacy.pmmsl.pharmacy.mapper.PharmacyMapper;
import com.ulstu.pharmacy.pmmsl.pharmacy.view.PharmacyViewModel;
import lombok.AllArgsConstructor;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor(onConstructor = @__({@Inject}))
public class PharmacyEjbImpl implements PharmacyEjbRemote {

    private final PharmacyDao pharmacyDao;

    private final PharmacyMedicamentDao pharmacyMedicamentDao;

    private final MedicamentDao medicamentDao;

    private final PharmacyMapper pharmacyMapper;

    private final MedicamentMapper medicamentMapper;

    /**
     * Получение списка всех аптек.
     */
    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<PharmacyViewModel> getAll() {
        return pharmacyDao.getAll().stream()
                .map(pharmacyMapper::toViewModel)
                .collect(Collectors.toList());
    }

    /**
     * Метод создания аптеки. Если pharmacyName == null, будет дано название по умолчанию.
     *
     * @param pharmacyName название аптеки.
     */
    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public void create(String pharmacyName) {
        pharmacyDao.save(
                Pharmacy.builder()
                        .name(Objects.nonNull(pharmacyName) ? pharmacyName : "default")
                        .build()
        );
    }

    /**
     * Метод получение списка медикаментов со всех аптек.
     * Включаются только те медикаменты, количество которых в аптеке > 0.
     */
    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public Map<MedicamentViewModel, Integer> getPharmacyMedicamentsInStock() {
        Map<MedicamentViewModel, Integer> result = new LinkedHashMap<>();
        // получаем все медикаменты в аптеках с количетсвом
        List<PharmacyMedicament> pharmacyMedicaments = pharmacyMedicamentDao.getAll();
        // вытаскиваем стрим медикаментов
        Stream<MedicamentViewModel> medicamentsStream = pharmacyMedicaments.stream()
                .map(PharmacyMedicament::getMedicament)
                .map(medicamentMapper::toViewModel);
        // вытаскиваем стрим количества этих медикаментов
        Stream<Integer> medciamentCountStream = pharmacyMedicaments.stream()
                .map(PharmacyMedicament::getCount);
        // собираем из 2ух стримов в мапу, если там есть такой медикамент, то суммируем количество,
        // если нет - то просто добавляем.
        Streams.forEachPair(
                medicamentsStream,
                medciamentCountStream,
                (medicament, count) -> {
                    Integer newCount = result.containsKey(medicament) ? result.get(medicament) + count : count;
                    result.put(medicament, newCount);
                }
        );
        return result;
    }

    /**
     * Проверка наличия медикамента в аптеках в необходимом количестве.
     *
     * @param medicamentId проверяемый медикамент.
     * @param count        необходимое количество медикамента.
     * @return true, если медикамет есть в нужном количестве в аптеках (в сумме)
     * false, в противном случае (или если параметр medicament == null).
     */
    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public boolean isMedicamentInStocks(Long medicamentId, Integer count) {
        Stream<PharmacyMedicament> pharmacyMedicaments = this.pharmacyMedicamentDao.getAll().stream();
        return this.isMedicamentInStocks(pharmacyMedicaments, medicamentId, count);
    }

    /**
     * Метод списания медикамента с аптек.
     *
     * @param medicamentId Id списываемого медикамент.
     * @param count        количество медикамента для списывания.
     * @throws MedicamentDiscountException возникает, если
     *                                     1) В аптеках не хватает в общей сумме колчиства списываемого медикамента.
     *                                     2) Медикамента не существует.
     *                                     3) Параметры null.
     */
    @Override
    public void discountMedicament(Long medicamentId, Integer count) throws MedicamentDiscountException {
        Map<Long, Integer> discountingMedicament = new HashMap<>();
        discountingMedicament.put(medicamentId, count);
        this.discountMedicaments(discountingMedicament);
    }

    /**
     * Метод списания медикаментов с аптек.
     *
     * @param medicamentsWithCounts Ключ - Id списываемого медикамента.
     *                              Значение - количество медикамента для списывания.
     * @throws MedicamentDiscountException возникает, если
     *                                     1) В аптеках не хватает в общей сумме колчиства списываемого медикамента.
     *                                     2) Медикамента не существует.
     *                                     3) Параметры null.
     */
    @Override
    @Transactional(value = Transactional.TxType.MANDATORY, rollbackOn = MedicamentDiscountException.class)
    public void discountMedicaments(Map<Long, Integer> medicamentsWithCounts) throws MedicamentDiscountException {
        ImmutableList<PharmacyMedicament> pharmacyMedicaments = ImmutableList.
                <PharmacyMedicament>builder()
                .addAll(this.pharmacyMedicamentDao.getAll())
                .build();
        ConcurrentLinkedQueue<PharmacyMedicament> updatedPharmacyMedicaments = new ConcurrentLinkedQueue<>();

        List<String> errors = this.validateArgsForDiscount(medicamentsWithCounts);

        if (errors.isEmpty()) {
            medicamentsWithCounts.entrySet()
                    .parallelStream()
                    .forEach(discMedicament -> {
                        Long medicamentId = discMedicament.getKey();
                        Integer count     = discMedicament.getValue();
                        updatedPharmacyMedicaments.addAll(this.getDiscountedPharmacyMedicaments(pharmacyMedicaments, medicamentId, count));
                    });
            // сохраняем обновлённое состояние медикаментов в аптеке
            updatedPharmacyMedicaments.forEach(pharmacyMedicamentDao::update);
        } else {
            throw new MedicamentDiscountException(String.join(" | ", errors));
        }
    }

    /**
     * Валидация аргументов, необходимых для списания медикамента с аптек.
     * @param medicamentsWithCounts
     * @return
     */
    private List<String> validateArgsForDiscount(Map<Long, Integer> medicamentsWithCounts) {
        List<String> errors = new LinkedList<>();
        for (Map.Entry<Long, Integer> discMedicament : medicamentsWithCounts.entrySet()) {
            Long medicamentId = discMedicament.getKey();
            Integer count     = discMedicament.getValue();
            String error = this.validateArgsForDiscount(medicamentId, count);
            if (Objects.nonNull(error)) {
                errors.add(error);
            }
        }
        return errors;
    }

    /**
     * Валидация аргументов, необходимых для списания медикамента с аптек.
     * @param medicamentId
     * @param count
     * @return
     */
    private String validateArgsForDiscount(Long medicamentId, Integer count) {
        StringBuilder errors = new StringBuilder();

        if (Objects.isNull(medicamentId)) {
            errors.append("Argument medicamentId is null; ");
        } else  {
            if (!this.medicamentDao.existsById(medicamentId)) {
                errors.append("Such medicament not exist; ");
            }
        }
        if (Objects.isNull(count)) {
            errors.append("Argument count is null; ");
        } else {
            if (count < 0) {
                errors.append("Argument count is negative; ");
            }
        }
        return errors.toString().isEmpty() ? null : errors.toString();
    }

    /**
     * Провеяет хватает ли в переданном стриме медикаментов из аптек требуемого медикамента в таком количестве.
     * @param currentPharmacyState текущее состояние медикаментов в аптеке
     * @param medicamentId id проверяемого медикамента
     * @param count необходимое количество списываемого медикамента
     * @return
     */
    private boolean isMedicamentInStocks(Stream<PharmacyMedicament> currentPharmacyState, Long medicamentId, Integer count) {
        if (Objects.nonNull(this.validateArgsForDiscount(medicamentId, count))) {
            return false;
        }
        Optional<Integer> medicamentCountInStock = this.getPharmacyMedicamentsByMedicamentId(currentPharmacyState, medicamentId)
                // выбираем их количество
                .map(PharmacyMedicament::getCount)
                // суммируем количества
                .reduce(Integer::sum);
        // если получено количество медикамента на складе и оно больше требуемого
        return medicamentCountInStock.isPresent() && medicamentCountInStock.get() >= count;
    }

    private Stream<PharmacyMedicament> getPharmacyMedicamentsByMedicamentId(Stream<PharmacyMedicament> currentPharmacyState, Long medicamentId) {
        return currentPharmacyState
                .filter(pharmacyMedicament -> pharmacyMedicament
                        .getMedicament()
                        .getId()
                        .equals(medicamentId));
    }

    /**
     * Метод предназначен для формирования списка медикаментов в аптеке которые подверглись списыванию.
     * (с уменьшением количества медикаментов)
     * @param currentPharmacyState текущее состояние медикаментов в аптеке
     * @param medicamentId id списываемого медикамента
     * @param count количество списываемого медикамента
     * @return список изменённых медикаментов в аптеке
     * @throws MedicamentDiscountException
     */
    private List<PharmacyMedicament> getDiscountedPharmacyMedicaments(ImmutableList<PharmacyMedicament> currentPharmacyState,
                                                                      Long medicamentId,
                                                                      Integer count) {
        /* Остаток списания, число будет уменьшаться по мере списания медикаментов,
        для отслеживания количества которое ещё необходимо списать */
        AtomicInteger restCount = new AtomicInteger(count);
        return this.getPharmacyMedicamentsByMedicamentId(
                currentPharmacyState.stream(),
                medicamentId)
                // меняем количество медикамента в зависимости от restCount
                // пока мы не списали столько, сколько нам требуется
                .takeWhile(pharmacyMedicament -> restCount.get() > 0)
                .map(pharmacyMedicament -> {
                    // Текущее количество медикамента на складе
                    int stockCount = pharmacyMedicament.getCount();
                    // Сколько можно списать?
                    int toDiscount = Math.min(stockCount, restCount.get());
                    // Списываем
                    pharmacyMedicament.setCount(stockCount - toDiscount);
                    // Обновляем количество, которое осталось списать
                    restCount.set(restCount.get() - toDiscount);
                    return pharmacyMedicament;
                })
                .collect(Collectors.toList());
    }
}