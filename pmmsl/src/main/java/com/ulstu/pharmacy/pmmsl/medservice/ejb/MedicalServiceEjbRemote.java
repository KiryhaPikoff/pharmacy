package com.ulstu.pharmacy.pmmsl.medservice.ejb;


import com.ulstu.pharmacy.pmmsl.common.exception.CrudOperationException;
import com.ulstu.pharmacy.pmmsl.common.exception.MedicamentDiscountException;
import com.ulstu.pharmacy.pmmsl.medservice.view.MedicalServiceViewModel;

import javax.ejb.Remote;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Remote
public interface MedicalServiceEjbRemote {

    /**
     * Метод получения всех услуг.
     *
     * @return список всех услуг.
     */
    List<MedicalServiceViewModel> getAll();

    /**
     * Метод получения списка услуг, дата списания которых входит в указанный период.
     *
     * @param fromDate "С" какого времени.
     * @param toDate   "ПО" какое время.
     * @return список услуг за указанный период.
     */
    List<MedicalServiceViewModel> getFromDateToDate(Timestamp fromDate, Timestamp toDate);

    /**
     * Метод списания услуги. Списываются все медикаменты в нужном количестве,
     * объявленные в этой услуге.
     *
     * @param id id списываемой услуги.
     * @throws MedicamentDiscountException возникает, если
     *                                     в аптеках не хватает медикаментов, указанных в услуге, в нужном количестве.
     */
    void discount(Long id) throws MedicamentDiscountException;

    /**
     * Метод создания услуги. Услуга формируется на основе списка медикамнтов, где каждому из них
     * сопоставлено необходимое количество.
     *
     * @param medicamentsWithCounts фактически набор соответствий:
     *                              медикамент - количество.
     *                              Ключ - медикамент,
     *                              Значение - необходимое количество.
     */
    void create(Map<Long, Integer> medicamentsWithCounts) throws CrudOperationException;
}