package com.ulstu.pharmacy.pmmsl.supply.dao;

import com.ulstu.pharmacy.pmmsl.common.dao.AbstractDao;
import com.ulstu.pharmacy.pmmsl.supply.entity.SupplyMedicament;

public abstract class SupplyMedicamentDao extends AbstractDao<SupplyMedicament, Long> {

    public SupplyMedicamentDao() {
        super(SupplyMedicament.class);
    }
}