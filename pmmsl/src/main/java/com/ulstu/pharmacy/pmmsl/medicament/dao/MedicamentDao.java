package com.ulstu.pharmacy.pmmsl.medicament.dao;

import com.ulstu.pharmacy.pmmsl.common.dao.AbstractDao;
import com.ulstu.pharmacy.pmmsl.medicament.entity.Medicament;

import java.util.List;

public abstract class MedicamentDao extends AbstractDao<Medicament, Long> {

    protected MedicamentDao() {
        super(Medicament.class);
    }

    public abstract List<Medicament> getAll();

    public abstract boolean existByNameAndNotId(String medicamentName, Long medicamentId);
}