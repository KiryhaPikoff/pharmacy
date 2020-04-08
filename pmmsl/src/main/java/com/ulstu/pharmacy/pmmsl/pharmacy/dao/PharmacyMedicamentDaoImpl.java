package com.ulstu.pharmacy.pmmsl.pharmacy.dao;

import com.ulstu.pharmacy.pmmsl.pharmacy.entity.PharmacyMedicament;

import java.util.List;

public class PharmacyMedicamentDaoImpl extends PharmacyMedicamentDao {

    @Override
    public List<PharmacyMedicament> getAll() {
        return this.entityManager
                .createQuery("SELECT pm FROM PharmacyMedicament pm")
                .getResultList();
    }
}