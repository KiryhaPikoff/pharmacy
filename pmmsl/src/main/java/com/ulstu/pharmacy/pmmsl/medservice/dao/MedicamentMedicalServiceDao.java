package com.ulstu.pharmacy.pmmsl.medservice.dao;

import com.ulstu.pharmacy.pmmsl.common.dao.AbstractDao;
import com.ulstu.pharmacy.pmmsl.medservice.entity.MedicamentMedicalService;

public abstract class MedicamentMedicalServiceDao extends AbstractDao<MedicamentMedicalService, Long> {

    protected MedicamentMedicalServiceDao() {
        super(MedicamentMedicalService.class);
    }
}