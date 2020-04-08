package com.ulstu.pharmacy.pmmsl.pharmacy.dao;

import com.ulstu.pharmacy.pmmsl.common.dao.AbstractDao;
import com.ulstu.pharmacy.pmmsl.pharmacy.entity.PharmacyMedicament;

import java.util.List;

public abstract class PharmacyMedicamentDao extends AbstractDao<PharmacyMedicament, Long> {

    protected PharmacyMedicamentDao() {
        super(PharmacyMedicament.class);
    }

    public abstract List<PharmacyMedicament> getAll();
}