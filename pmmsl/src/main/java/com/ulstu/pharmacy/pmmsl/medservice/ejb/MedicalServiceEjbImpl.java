package com.ulstu.pharmacy.pmmsl.medservice.ejb;

import com.ulstu.pharmacy.pmmsl.common.exception.CrudOperationException;
import com.ulstu.pharmacy.pmmsl.common.exception.MedicamentWriteOffException;
import com.ulstu.pharmacy.pmmsl.medicament.binding.MedicamentCountBindingModel;
import com.ulstu.pharmacy.pmmsl.medicament.dao.MedicamentDao;
import com.ulstu.pharmacy.pmmsl.medicament.entity.Medicament;
import com.ulstu.pharmacy.pmmsl.medservice.dao.MedicalServiceDao;
import com.ulstu.pharmacy.pmmsl.medservice.entity.MedicalService;
import com.ulstu.pharmacy.pmmsl.medservice.entity.MedicamentMedicalService;
import com.ulstu.pharmacy.pmmsl.medservice.mapper.MedicalServiceMapper;
import com.ulstu.pharmacy.pmmsl.medservice.view.MedicalServiceViewModel;
import com.ulstu.pharmacy.pmmsl.pharmacy.ejb.PharmacyEjbLocal;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
public class MedicalServiceEjbImpl implements MedicalServiceEjbLocal {

    private final PharmacyEjbLocal pharmacyEjbLocal;

    private final MedicalServiceDao medicalServiceDao;

    private final MedicamentDao medicamentDao;

    private final MedicalServiceMapper medicalServiceMapper;

    @Inject
    public MedicalServiceEjbImpl(PharmacyEjbLocal pharmacyEjbLocal,
                                 MedicalServiceDao medicalServiceDao,
                                 MedicamentDao medicamentDao,
                                 MedicalServiceMapper medicalServiceMapper) {
        this.pharmacyEjbLocal = pharmacyEjbLocal;
        this.medicalServiceDao = medicalServiceDao;
        this.medicamentDao = medicamentDao;
        this.medicalServiceMapper = medicalServiceMapper;
    }

    /**
     * Метод получения всех услуг.
     *
     * @return список всех услуг.
     */
    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<MedicalServiceViewModel> getAll() {
        return medicalServiceDao.getAll().stream()
                .map(medicalServiceMapper::toViewModel)
                .collect(Collectors.toList());
    }

    /**
     * Получение услуг по их id.
     *
     * @param ids
     * @return
     */
    @Override
    public List<MedicalServiceViewModel> getByIds(Set<Long> ids) {
        return medicalServiceDao.getByIds(ids).stream()
                .map(medicalServiceMapper::toViewModel)
                .collect(Collectors.toList());
    }

    /**
     * Метод получения списка услуг, дата списания которых входит в указанный период.
     *
     * @param fromDate "С" какого времени.
     * @param toDate   "ПО" какое время.
     * @return список услуг за указанный период.
     */
    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<MedicalServiceViewModel> getFromDateToDate(Timestamp fromDate, Timestamp toDate) {

        StringBuilder errors = new StringBuilder();
        errors.append(Objects.isNull(fromDate) ? "DateFrom is null; "           : "");
        errors.append(Objects.isNull(toDate)   ? "DateTo is null; "             : "");
        if(Objects.nonNull(fromDate) && Objects.nonNull(toDate)) {
            errors.append(fromDate.after(toDate) ? "DateFrom is after DateTo; " : "");
        }

        if(!errors.toString().isBlank()) {
            throw new CrudOperationException(errors.toString());
        }

        return medicalServiceDao.getFromDateToDate(fromDate, toDate).stream()
                .map(medicalServiceMapper::toViewModel)
                .collect(Collectors.toList());
    }

    /**
     * Метод списания услуги. Списываются все медикаменты в нужном количестве,
     * объявленные в этой услуге.
     *
     * @param id id списываемой услуги.
     */
    @Override
    @Transactional(Transactional.TxType.MANDATORY)
    public void writeOff(Long id) {

        StringBuilder errors = new StringBuilder();
        errors.append(Objects.isNull(id) ? "An id is null; "
                : !medicalServiceDao.existsById(id) ? "MedicalService with an id = " + id + " not exist; "
                : medicalServiceDao.isAlreadyDiscounted(id) ? "MedicalService with an id = " + id + " already writeOff." : "");

        if(!errors.toString().isBlank()) {
            throw new MedicamentWriteOffException(errors.toString());
        }

        MedicalService discountedMedicalService = this.medicalServiceDao.findById(id).get();

        pharmacyEjbLocal.writeOffMedicaments(
                discountedMedicalService.getMedicamentMedicalServices()
                        .stream()
                        .map(medicamentMedicalService -> MedicamentCountBindingModel.builder()
                               .medicamentId(medicamentMedicalService.getMedicament().getId())
                               .count(medicamentMedicalService.getCount())
                               .build())
                        .collect(Collectors.toSet())
        );

        discountedMedicalService.setProvisionDate(
                new Timestamp(System.currentTimeMillis())
        );

        this.medicalServiceDao.update(discountedMedicalService);
    }

    /**
     * Метод создания услуги. Услуга формируется на основе списка медикамнтов, где каждому из них
     * сопоставлено необходимое количество.
     *
     * @param medicamentCountBindingModels множество медикаментов с количеством для формирования услуги.
     */
    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public void create(Set<MedicamentCountBindingModel> medicamentCountBindingModels) {

        StringBuilder errors = new StringBuilder();

        if (Objects.isNull(medicamentCountBindingModels)) {
            errors.append("Set of MedicamentCountBindingModel is null; ");
        } else {
            medicamentCountBindingModels.forEach(medicamentCountBindingModel -> {
                errors.append(
                    !this.pharmacyEjbLocal.isMedicamentInStocks(medicamentCountBindingModel) ?
                            medicamentCountBindingModel + " is not in stock; " : ""
                );
                errors.append(
                    Objects.isNull(medicamentCountBindingModel.getMedicamentId()) ? "MedicamentId is null; " : ""
                );
                errors.append(
                    Objects.isNull(medicamentCountBindingModel.getCount()) ? "Count is null; " : ""
                );
            });
        }

        if (!errors.toString().isBlank()) {
            throw new CrudOperationException(errors.toString());
        }

        /* Инициализация новой услуги. */
        MedicalService newMedicalService = MedicalService.builder()
                .medicamentMedicalServices(
                        this.formForNewMedicalService(medicamentCountBindingModels)
                )
                .build();

        this.medicalServiceDao.save(newMedicalService);
    }

    /**
     * Формирует медикаменты для привязки их к услуге на основе множества моделей MedicamentCountBindingModel.
     * @param modelsSet
     * @return множество сформированных медикаментов для услуги.
     */
    private Set<MedicamentMedicalService> formForNewMedicalService(Set<MedicamentCountBindingModel> modelsSet) {
        //TODO задавать размер мапы из количества медикаметов. Написать запрос.
        Map<Long, Medicament> medicamentMap = new HashMap<>();
        this.medicamentDao.getAll()
                .forEach(medicament -> medicamentMap.put(
                        medicament.getId(),
                        medicament
                ));

        /* Формирование множества медикаментов привязанных к услуге. */
        return modelsSet.stream()
                .map(medicamentCountBindingModel -> MedicamentMedicalService.builder()
                        .medicament(medicamentMap.get(medicamentCountBindingModel.getMedicamentId()))
                        .count(medicamentCountBindingModel.getCount())
                        .price(
                                medicamentMap.get(
                                        medicamentCountBindingModel.getMedicamentId()
                                ).getPrice()
                        )
                        .build())
                .collect(Collectors.toSet());
    }
}