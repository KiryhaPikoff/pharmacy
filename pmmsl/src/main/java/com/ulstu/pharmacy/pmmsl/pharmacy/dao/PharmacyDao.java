package com.ulstu.pharmacy.pmmsl.pharmacy.dao;

import com.ulstu.pharmacy.pmmsl.common.dao.AbstractDao;
import com.ulstu.pharmacy.pmmsl.pharmacy.entity.Pharmacy;

import java.util.List;

public abstract class PharmacyDao extends AbstractDao<Pharmacy, Long> {

    protected PharmacyDao() {
        super(Pharmacy.class);
    }

    public abstract List<Pharmacy> getAll();
}