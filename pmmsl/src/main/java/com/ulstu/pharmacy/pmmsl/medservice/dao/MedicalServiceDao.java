package com.ulstu.pharmacy.pmmsl.medservice.dao;

import com.ulstu.pharmacy.pmmsl.common.dao.AbstractDao;
import com.ulstu.pharmacy.pmmsl.medservice.entity.MedicalService;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

public abstract class MedicalServiceDao extends AbstractDao<MedicalService, Long> {

    protected MedicalServiceDao() {
        super(MedicalService.class);
    }

    public abstract List<MedicalService> getAll();

    public abstract List<MedicalService> getFromDateToDate(Timestamp from, Timestamp to);

    public abstract boolean isAlreadyDiscounted(Long medicalServiceId);

    public abstract List<MedicalService> getByIds(Set<Long> ids);
}