package com.ulstu.pharmacy.pmmsl.supply.dao;

import com.ulstu.pharmacy.pmmsl.common.dao.AbstractDao;
import com.ulstu.pharmacy.pmmsl.supply.entity.Supply;

import java.sql.Timestamp;
import java.util.List;

public abstract class SupplyDao extends AbstractDao<Supply, Long> {

    public SupplyDao() {
        super(Supply.class);
    }

    public abstract List<Supply> getFromDateToDate(Timestamp from, Timestamp to);
}