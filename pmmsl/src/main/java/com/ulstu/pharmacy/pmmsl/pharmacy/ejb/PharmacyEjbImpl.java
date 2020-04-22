package com.ulstu.pharmacy.pmmsl.pharmacy.ejb;

import com.google.common.collect.Streams;
import com.ulstu.pharmacy.pmmsl.common.exception.CrudOperationException;
import com.ulstu.pharmacy.pmmsl.common.exception.MedicamentDiscountException;
import com.ulstu.pharmacy.pmmsl.medicament.binding.MedicamentCountBindingModel;
import com.ulstu.pharmacy.pmmsl.medicament.dao.MedicamentDao;
import com.ulstu.pharmacy.pmmsl.medicament.mapper.MedicamentMapper;
import com.ulstu.pharmacy.pmmsl.medicament.view.MedicamentViewModel;
import com.ulstu.pharmacy.pmmsl.pharmacy.binding.PharmacyBindingModel;
import com.ulstu.pharmacy.pmmsl.pharmacy.dao.PharmacyDao;
import com.ulstu.pharmacy.pmmsl.pharmacy.dao.PharmacyMedicamentDao;
import com.ulstu.pharmacy.pmmsl.pharmacy.entity.Pharmacy;
import com.ulstu.pharmacy.pmmsl.pharmacy.entity.PharmacyMedicament;
import com.ulstu.pharmacy.pmmsl.pharmacy.mapper.PharmacyMapper;
import com.ulstu.pharmacy.pmmsl.pharmacy.view.PharmacyViewModel;

import javax.ejb.Stateless;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Default
@Stateless
public class PharmacyEjbImpl implements PharmacyEjbLocal {

    private final PharmacyDao pharmacyDao;

    private final PharmacyMedicamentDao pharmacyMedicamentDao;

    private final MedicamentDao medicamentDao;

    private final PharmacyMapper pharmacyMapper;

    private final MedicamentMapper medicamentMapper;

    @Inject
    public PharmacyEjbImpl(PharmacyDao pharmacyDao,
                           PharmacyMedicamentDao pharmacyMedicamentDao,
                           MedicamentDao medicamentDao,
                           PharmacyMapper pharmacyMapper,
                           MedicamentMapper medicamentMapper) {
        this.pharmacyDao = pharmacyDao;
        this.pharmacyMedicamentDao = pharmacyMedicamentDao;
        this.medicamentDao = medicamentDao;
        this.pharmacyMapper = pharmacyMapper;
        this.medicamentMapper = medicamentMapper;
    }

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
     * Метод создания аптеки.
     * Если pharmacyName == null, будет дано название по умолчанию.
     *
     * @param pharmacyBindingModel
     */
    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public void create(PharmacyBindingModel pharmacyBindingModel) {
        if(Objects.isNull(pharmacyBindingModel)) {
            throw new CrudOperationException("PharmacyBindingModel is null");
        } else {
            String name = pharmacyBindingModel.getName();
            pharmacyDao.save(
                    Pharmacy.builder()
                            .name(Objects.nonNull(name) ? name : "default" ) //TODO вынести дефолтное имя
                            .build()
            );
        }
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
        Stream<Integer> medicamentsCountStream = pharmacyMedicaments.stream()
                .map(PharmacyMedicament::getCount);
        // собираем из 2ух стримов в мапу, если там есть такой медикамент, то суммируем количество,
        // если нет - то просто добавляем.
        Streams.forEachPair(
                medicamentsStream,
                medicamentsCountStream,
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
     * @param medicamentCountBindingModel
     * @return true, если медикамет есть в нужном количестве в аптеках (в сумме)
     * false, в противном случае (или если параметр medicament == null).
     */
    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public boolean isMedicamentInStocks(MedicamentCountBindingModel medicamentCountBindingModel) {
        if (Objects.nonNull(this.validateDiscountArgs(medicamentCountBindingModel))) {
            return false;
        }
        List<PharmacyMedicament> medicamentsInStock = this.getByMedicamentId(
                this.pharmacyMedicamentDao.getAll(),
                medicamentCountBindingModel.getMedicamentId()
        );
        Optional<Integer> medicamentCountInStock = medicamentsInStock.stream()
                // выбираем их количество
                .map(PharmacyMedicament::getCount)
                // суммируем количества
                .reduce(Integer::sum);
        // если получено количество медикамента на складе и оно больше требуемого
        return medicamentCountInStock.isPresent() &&
               medicamentCountInStock.get() >= medicamentCountBindingModel.getCount();
    }

    /**
     * Метод списания медикаментов с аптек.
     *
     * @param medicamentsCountSet
     */
    @Override
    @Transactional(value = Transactional.TxType.MANDATORY)
    public void discountMedicaments(Set<MedicamentCountBindingModel> medicamentsCountSet) {

        List<String> errors = this.validateDiscountArgs(medicamentsCountSet);

        if (!errors.isEmpty()) {
            throw new MedicamentDiscountException(String.join(" | ", errors));
        } else {
            this.applyDiscount(medicamentsCountSet);
        }
    }

    /**
     * Здесь сожержится логика списания и она применяется к переданному множеству медикаментов.
     * @param medicamentsCountSet множество медикаментов с количеством для списания.
     */
    private void applyDiscount(Set<MedicamentCountBindingModel> medicamentsCountSet) {
        List<PharmacyMedicament> pharmacyMedicaments = this.pharmacyMedicamentDao.getAll();
        for (MedicamentCountBindingModel medicamentCount : medicamentsCountSet) {
            List<PharmacyMedicament> medicamentsWithThatId = pharmacyMedicaments.stream()
                    .filter(pharmacyMedicament ->
                            pharmacyMedicament.getMedicament().getId()
                                    .equals(medicamentCount.getMedicamentId())
                    ).collect(Collectors.toList());
            /* Сколько осталось списать. */
            int restToDiscount = medicamentCount.getCount();
            for (PharmacyMedicament pharmacyMedicament : medicamentsWithThatId) {
                // Текущее количество медикамента на складе
                int stockCount = pharmacyMedicament.getCount();
                // Сколько можно списать?
                int toDiscount = Math.min(stockCount, restToDiscount);
                if(toDiscount > 0) {
                    // Списываем
                    pharmacyMedicament.setCount(stockCount - toDiscount);
                    // Обновляем количество, которое осталось списать
                    restToDiscount -= toDiscount;
                    this.pharmacyMedicamentDao.update(pharmacyMedicament);
                    // создание движения...
                }
                /* Если списали уже достаточно, то выходим из цикла. */
                if(restToDiscount == 0) {
                    break;
                }
            }
            if(restToDiscount != 0) {
                throw new MedicamentDiscountException(
                        "There is not enough medicament with an id " + medicamentCount.getMedicamentId() +
                                ". Required " + medicamentCount.getCount() +
                                ", but there is only " + (medicamentCount.getCount() - restToDiscount)
                );
            }
        }
    }

    /**
     * Валидация аргументов, необходимых для списания медикамента с аптек.
     * @param medicamentsWithCounts
     * @return список ошибок.
     */
    private List<String> validateDiscountArgs(Set<MedicamentCountBindingModel> medicamentsWithCounts) {
        List<String> errors = new LinkedList<>();
        for (MedicamentCountBindingModel medicamentWithCount : medicamentsWithCounts) {
            String error = this.validateDiscountArgs(medicamentWithCount);
            if (Objects.nonNull(error)) {
                errors.add(error);
            }
        }
        return errors;
    }

    /**
     * Валидация аргументов, необходимых для списания медикамента с аптек.
     * @param model
     * @return строка с описанием ошибок в переданных аргументах
     * Если строка null - ошибок нет.
     */
    private String validateDiscountArgs(MedicamentCountBindingModel model) {
        StringBuilder errors = new StringBuilder();

        if(Objects.isNull(model)) {
            errors.append("Model is null; ");
        } else {
            Long medicamentId = model.getMedicamentId();
            errors.append(
                    Objects.isNull(medicamentId) ? "MedicamentId is null; "
                            : !this.medicamentDao.existsById(medicamentId) ? "Such medicament not exist; " : ""
            );
            Integer count = model.getCount();
            errors.append(
                    Objects.isNull(count) ? "Count is null; "
                            : count < 0   ? "Count is negative; " : ""
            );
        }
        return errors.toString().isBlank() ? null : errors.toString();
    }

    /**
     * Поиск медикамнтов в аптеке по Id.
     * @param currentPharmacyState текущее состояние медикаментов в аптеках.
     * @param medicamentId id искомого медикамента
     * @return
     */
    private List<PharmacyMedicament> getByMedicamentId(List<PharmacyMedicament> currentPharmacyState,
                                                       Long medicamentId) {
        return currentPharmacyState.stream()
                .filter(pharmacyMedicament -> pharmacyMedicament
                        .getMedicament()
                        .getId()
                        .equals(medicamentId))
                .collect(Collectors.toList());
    }
}