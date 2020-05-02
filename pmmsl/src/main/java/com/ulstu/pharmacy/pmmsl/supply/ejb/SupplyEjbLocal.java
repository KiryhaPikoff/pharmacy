package com.ulstu.pharmacy.pmmsl.supply.ejb;

import com.ulstu.pharmacy.pmmsl.supply.binding.SupplyBindingModel;
import com.ulstu.pharmacy.pmmsl.supply.view.SupplyViewModel;

import java.sql.Timestamp;
import java.util.List;

public interface SupplyEjbLocal {

    /**
     * Создание и проведение прихода.
     * @param supplyBindingModel
     */
    void create(SupplyBindingModel supplyBindingModel);

    /**
     * Получение списка приходов за промежуток времени.
     * @param fromDate с какого промежутка времени.
     * @param toDate по какой промежуток времени.
     * @return
     */
    List<SupplyViewModel> getFromDateToDate(Timestamp fromDate, Timestamp toDate);
}