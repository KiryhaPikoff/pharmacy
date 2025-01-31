package com.ulstu.pharmacy.pmmsl.medservice.ejb;


import com.ulstu.pharmacy.pmmsl.medicament.binding.MedicamentCountBindingModel;
import com.ulstu.pharmacy.pmmsl.medservice.view.MedicalServiceViewModel;

import javax.ejb.Local;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Local
public interface MedicalServiceEjbLocal {

    /**
     * Метод получения всех услуг.
     *
     * @return список всех услуг.
     */
    List<MedicalServiceViewModel> getAll();


    /**
     * Получение услуг по их id.
     * @param ids
     * @return
     */
    List<MedicalServiceViewModel> getByIds(Set<Long> ids);

    /**
     * Метод получения списка услуг, дата списания которых входит в указанный период.
     *
     * @param fromDate "С" какого времени.
     * @param toDate   "ПО" какое время.
     * @return список услуг за указанный период.
     */
    List<MedicalServiceViewModel> getFromDateToDate(Timestamp fromDate, Timestamp toDate);

    /**
     * Метод создания услуги. Услуга формируется на основе списка медикамнтов, где каждому из них
     * сопоставлено необходимое количество.
     *
     * @param medicamentCountBindingModels множество медикаментов с количеством для формирования услуги.
     */
    void create(Set<MedicamentCountBindingModel> medicamentCountBindingModels);
}