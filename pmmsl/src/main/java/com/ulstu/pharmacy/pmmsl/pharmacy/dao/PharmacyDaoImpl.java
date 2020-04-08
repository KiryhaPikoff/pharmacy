package com.ulstu.pharmacy.pmmsl.pharmacy.dao;

import com.ulstu.pharmacy.pmmsl.pharmacy.entity.Pharmacy;

import java.util.List;

public class PharmacyDaoImpl extends PharmacyDao {

    @Override
    public List<Pharmacy> getAll() {
        return this.entityManager
                .createQuery("SELECT p FROM Pharmacy p")
                .getResultList();
    }
}