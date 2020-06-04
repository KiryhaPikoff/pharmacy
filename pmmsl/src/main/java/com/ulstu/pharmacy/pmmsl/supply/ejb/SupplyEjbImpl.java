package com.ulstu.pharmacy.pmmsl.supply.ejb;

import com.ulstu.pharmacy.pmmsl.common.exception.CrudOperationException;
import com.ulstu.pharmacy.pmmsl.medicament.entity.Medicament;
import com.ulstu.pharmacy.pmmsl.pharmacy.ejb.PharmacyEjbLocal;
import com.ulstu.pharmacy.pmmsl.pharmacy.entity.Pharmacy;
import com.ulstu.pharmacy.pmmsl.supply.binding.SupplyBindingModel;
import com.ulstu.pharmacy.pmmsl.supply.dao.SupplyDao;
import com.ulstu.pharmacy.pmmsl.supply.entity.Supply;
import com.ulstu.pharmacy.pmmsl.supply.entity.SupplyMedicament;
import com.ulstu.pharmacy.pmmsl.supply.mapper.SupplyMapper;
import com.ulstu.pharmacy.pmmsl.supply.view.SupplyViewModel;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Stateless
public class SupplyEjbImpl implements SupplyEjbLocal {

    private final PharmacyEjbLocal pharmacyEjbLocal;

    private final SupplyDao supplyDao;

    private final SupplyMapper supplyMapper;

    @Inject
    public SupplyEjbImpl(PharmacyEjbLocal pharmacyEjbLocal,
                         SupplyDao supplyDao,
                         SupplyMapper supplyMapper) {
        this.pharmacyEjbLocal = pharmacyEjbLocal;
        this.supplyDao = supplyDao;
        this.supplyMapper = supplyMapper;
    }

    /**
     * Создание и проведение прихода.
     *
     * @param supplyBindingModel
     */
    @Override
    @Transactional(Transactional.TxType.MANDATORY)
    public void create(SupplyBindingModel supplyBindingModel) {
        pharmacyEjbLocal.replenishMedicaments(
                supplyBindingModel.getPharmacyId(),
                supplyBindingModel.getMedicamentCountSet()
        );
        Supply newSupply = Supply.builder()
                .pharmacy(Pharmacy.builder().id(supplyBindingModel.getPharmacyId()).build())
                .date(new Timestamp(System.currentTimeMillis()))
                .supplyMedicaments(supplyBindingModel.getMedicamentCountSet().stream()
                        .map(medicamentCountBindingModel -> SupplyMedicament.builder()
                                .medicament(Medicament.builder().id(medicamentCountBindingModel.getMedicamentId()).build())
                                .count(medicamentCountBindingModel.getCount())
                                .build()
                        )
                        .collect(Collectors.toSet())
                )
                .build();
        supplyDao.save(newSupply);
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
        StringBuilder errors = new StringBuilder();
        errors.append(Objects.isNull(fromDate) ? "DateFrom is null; "           : "");
        errors.append(Objects.isNull(toDate)   ? "DateTo is null; "             : "");
        if(Objects.nonNull(fromDate) && Objects.nonNull(toDate)) {
            errors.append(fromDate.after(toDate) ? "DateFrom is after DateTo; " : "");
        }

        if(!errors.toString().isBlank()) {
            throw new CrudOperationException(errors.toString());
        }

        return supplyDao.getFromDateToDate(fromDate, toDate).stream()
                .map(supplyMapper::toViewModel)
                .collect(Collectors.toList());
    }
}