package com.ulstu.pharmacy.pmmsl.medservice.dao;

import com.ulstu.pharmacy.pmmsl.medservice.entity.MedicalService;

import java.sql.Timestamp;
import java.util.List;

public class MedicalServiceDaoImpl extends MedicalServiceDao {

    @Override
    public List<MedicalService> getAll() {
        return this.entityManager
                .createQuery("SELECT ms FROM MedicalService ms")
                .getResultList();
    }

    @Override
    public List<MedicalService> getFromDateTo(Timestamp from, Timestamp to) {
        return this.entityManager
                .createQuery(
                        "SELECT ms FROM MedicalService ms" +
                                " WHERE ms.provisionDate BETWEEN :fromDate AND :toDate"
                )
                .setParameter("fromDate", from)
                .setParameter("toDate", to)
                .getResultList();
    }
}