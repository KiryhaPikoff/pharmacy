package com.ulstu.pharmacy.pmmsl.medservice.ejb;

import com.ulstu.pharmacy.pmmsl.common.exception.CrudOperationException;
import com.ulstu.pharmacy.pmmsl.common.exception.MedicamentDiscountException;
import com.ulstu.pharmacy.pmmsl.medicament.dao.MedicamentDao;
import com.ulstu.pharmacy.pmmsl.medicament.entity.Medicament;
import com.ulstu.pharmacy.pmmsl.medservice.dao.MedicalServiceDao;
import com.ulstu.pharmacy.pmmsl.medservice.entity.MedicalService;
import com.ulstu.pharmacy.pmmsl.medservice.entity.MedicamentMedicalService;
import com.ulstu.pharmacy.pmmsl.medservice.mapper.MedicalServiceMapper;
import com.ulstu.pharmacy.pmmsl.medservice.view.MedicalServiceViewModel;
import com.ulstu.pharmacy.pmmsl.pharmacy.binding.MedicamentCountBindingModel;
import com.ulstu.pharmacy.pmmsl.pharmacy.ejb.PharmacyEjbRemote;
import lombok.AllArgsConstructor;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor(onConstructor = @__({@Inject}))
public class MedicalServiceEjbImpl implements MedicalServiceEjbRemote {

    private final PharmacyEjbRemote pharmacyEjbRemote;

    private final MedicalServiceDao medicalServiceDao;

    private final MedicamentDao medicamentDao;

    private final MedicalServiceMapper medicalServiceMapper;

    /**
     * Метод получения всех услуг.
     *
     * @return список всех услуг.
     */
    @Override
    public List<MedicalServiceViewModel> getAll() {
        return medicalServiceDao.getAll().stream()
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
     * @throws MedicamentDiscountException возникает, если
     *                                     в аптеках не хватает медикаментов, указанных в услуге, в нужном количестве.
     */
    @Override
    public void discount(Long id) {

        StringBuilder errors = new StringBuilder();
        errors.append(Objects.isNull(id) ? "An id is null; "
                : !medicalServiceDao.existsById(id) ? "MedicalService with an id = " + id + " not exist; " : "");

        if(!errors.toString().isBlank()) {
            throw new CrudOperationException(errors.toString());
        }

        MedicalService discountedMedicalService = this.medicalServiceDao.findById(id).get();

        discountedMedicalService.getMedicamentMedicalServices()
                .forEach(medicamentMedicalService -> {
                        MedicamentCountBindingModel model = MedicamentCountBindingModel.builder()
                                .medicamentId(medicamentMedicalService.getMedicament().getId())
                                .count(medicamentMedicalService.getCount())
                                .build();
                        errors.append(
                                !this.pharmacyEjbRemote.isMedicamentInStocks(model) ?
                                        model + " is not in stock; " : ""
                        );
        });

        pharmacyEjbRemote.discountMedicaments(
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
    public void create(Set<MedicamentCountBindingModel> medicamentCountBindingModels) {

        StringBuilder errors = new StringBuilder();

        if (Objects.isNull(medicamentCountBindingModels)) {
            errors.append("Set of MedicamentCountBindingModel is null; ");
        } else {
            medicamentCountBindingModels.forEach(medicamentCountBindingModel -> {
                errors.append(
                    !this.pharmacyEjbRemote.isMedicamentInStocks(medicamentCountBindingModel) ?
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
     * Формирует список медикаментов для привязки их к услуге на основе множества моделей MedicamentCountBindingModel.
     * @param modelsSet
     * @return список сформированных медикаментов для услуги.
     */
    private List<MedicamentMedicalService> formForNewMedicalService(Set<MedicamentCountBindingModel> modelsSet) {
        //TODO задавать размер мапы из количества медикаметов. Написать запрос.
        Map<Long, Medicament> medicamentMap = new HashMap<>();
        this.medicamentDao.getAll()
                .forEach(medicament -> medicamentMap.put(
                        medicament.getId(),
                        medicament
                ));

        /* Формирование списка медикаментов привязанных к услуге. */
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
                .collect(Collectors.toList());
    }
}