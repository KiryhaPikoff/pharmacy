package com.ulstu.pharmacy.pmmsl.medservice.ejb;

import com.ulstu.pharmacy.pmmsl.common.exception.MedicamentDiscountException;
import com.ulstu.pharmacy.pmmsl.medicament.mapper.MedicamentMapper;
import com.ulstu.pharmacy.pmmsl.medservice.dao.MedicalServiceDao;
import com.ulstu.pharmacy.pmmsl.medservice.mapper.MedicalServiceMapper;
import com.ulstu.pharmacy.pmmsl.medservice.view.MedicalServiceViewModel;
import com.ulstu.pharmacy.pmmsl.pharmacy.ejb.PharmacyEjbRemote;
import lombok.AllArgsConstructor;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@AllArgsConstructor(onConstructor = @__({@Inject}))
public class MedicalServiceEjbImpl implements MedicalServiceEjbRemote {

    private final PharmacyEjbRemote pharmacyEjbRemote;

    private final MedicalServiceDao medicalServiceDao;

    private final MedicalServiceMapper medicalServiceMapper;

    private final MedicamentMapper medicamentMapper;

    /**
     * Метод получения всех услуг.
     *
     * @return список всех услуг.
     */
    @Override
    public List<MedicalServiceViewModel> getAll() {
        return null;
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
        return null;
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
    public void discount(Long id) throws MedicamentDiscountException {

    }

    /**
     * Метод создания услуги. Услуга формируется на основе списка медикамнтов, где каждому из них
     * сопоставлено необходимое количество.
     *
     * @param medicamentsWithCounts фактически набор соответствий:
     *                              медикамент - количество.
     *                              Ключ - медикамент,
     *                              Значение - необходимое количество.
     */
    @Override
    public void create(Map<Long, Integer> medicamentsWithCounts) {

    }
}