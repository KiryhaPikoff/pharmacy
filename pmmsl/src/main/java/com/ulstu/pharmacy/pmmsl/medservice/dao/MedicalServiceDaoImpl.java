package com.ulstu.pharmacy.pmmsl.medservice.dao;

import com.ulstu.pharmacy.pmmsl.medservice.entity.MedicalService;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

public class MedicalServiceDaoImpl extends MedicalServiceDao {

    @Override
    public List<MedicalService> getAll() {
        return this.entityManager
                .createQuery("SELECT ms FROM MedicalService ms")
                .getResultList();
    }

    @Override
    public List<MedicalService> getFromDateToDate(Timestamp from, Timestamp to) {
        return this.entityManager
                .createQuery(
                        "SELECT ms FROM MedicalService ms" +
                                " WHERE ms.provisionDate BETWEEN :fromDate AND :toDate"
                )
                .setParameter("fromDate", from)
                .setParameter("toDate", to)
                .getResultList();
    }

    @Override
    public boolean isAlreadyDiscounted(Long medicalServiceId) {
        return Objects.nonNull(
                this.entityManager.createQuery(
                        "SELECT ms.provisionDate FROM MedicalService ms" +
                                " WHERE ms.id = :medicalServiceId"
                )
                .setParameter("medicalServiceId", medicalServiceId)
                .getSingleResult()
        );
    }
}