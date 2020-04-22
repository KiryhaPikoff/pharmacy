package com.ulstu.pharmacy.pmmsl.supply.dao;

import com.ulstu.pharmacy.pmmsl.supply.entity.Supply;

import java.sql.Timestamp;
import java.util.List;

public class SupplyDaoImpl extends SupplyDao {

    @Override
    public List<Supply> getFromDateToDate(Timestamp from, Timestamp to) {
        return this.entityManager
                .createQuery(
                        "SELECT s FROM Supply s" +
                                " WHERE s.date BETWEEN :fromDate AND :toDate"
                )
                .setParameter("fromDate", from)
                .setParameter("toDate", to)
                .getResultList();
    }
}