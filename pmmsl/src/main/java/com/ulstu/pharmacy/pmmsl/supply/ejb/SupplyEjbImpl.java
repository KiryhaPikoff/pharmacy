package com.ulstu.pharmacy.pmmsl.supply.ejb;

import com.ulstu.pharmacy.pmmsl.pharmacy.ejb.PharmacyEjbLocal;
import com.ulstu.pharmacy.pmmsl.supply.binding.SupplyBindingModel;
import com.ulstu.pharmacy.pmmsl.supply.dao.SupplyDao;
import com.ulstu.pharmacy.pmmsl.supply.dao.SupplyMedicamentDao;
import com.ulstu.pharmacy.pmmsl.supply.mapper.SupplyMapper;
import com.ulstu.pharmacy.pmmsl.supply.view.SupplyViewModel;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.sql.Timestamp;
import java.util.List;

@Stateless
public class SupplyEjbImpl implements SupplyEjbLocal {

    private final PharmacyEjbLocal pharmacyEjbLocal;

    private final SupplyDao supplyDao;

    private final SupplyMedicamentDao supplyMedicamentDao;

    private final SupplyMapper supplyMapper;

    @Inject
    public SupplyEjbImpl(PharmacyEjbLocal pharmacyEjbLocal,
                         SupplyDao supplyDao,
                         SupplyMedicamentDao supplyMedicamentDao,
                         SupplyMapper supplyMapper) {
        this.pharmacyEjbLocal = pharmacyEjbLocal;
        this.supplyDao = supplyDao;
        this.supplyMedicamentDao = supplyMedicamentDao;
        this.supplyMapper = supplyMapper;
    }

    /**
     * Создание и проведение прихода.
     *
     * @param supplyBindingModel
     */
    @Override
    public void create(SupplyBindingModel supplyBindingModel) {

    }

    /**
     * Получение списка приходов за промежуток времени.
     *
     * @param fromDate с какого промежутка времени.
     * @param toDate   по какой промежуток времени.
     * @return
     */
    @Override
    public List<SupplyViewModel> getFromDateToDate(Timestamp fromDate, Timestamp toDate) {
        return null;
    }
}
