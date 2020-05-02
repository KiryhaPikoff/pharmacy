package com.ulstu.pharmacy.pmmsl.supply.mapper;

import com.ulstu.pharmacy.pmmsl.medicament.view.MedicamentCountViewModel;
import com.ulstu.pharmacy.pmmsl.supply.entity.Supply;
import com.ulstu.pharmacy.pmmsl.supply.entity.SupplyMedicament;
import com.ulstu.pharmacy.pmmsl.supply.view.SupplyViewModel;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class SupplyMapperImpl implements SupplyMapper {

    @Override
    public SupplyViewModel toViewModel(Supply supply) {
        return Objects.isNull(supply) ? null :
                SupplyViewModel.builder()
                        .id(supply.getId())
                        .date(supply.getDate())
                        .pharmacyName(supply.getPharmacy().getName())
                        .medicamentsWithCount(
                                this.toMedicamentCountViewModel(
                                        supply.getSupplyMedicaments()
                                )
                        )
                        .build();
    }

    private Set<MedicamentCountViewModel> toMedicamentCountViewModel(Set<SupplyMedicament> supplyMedicaments) {
        return Objects.isNull(supplyMedicaments) ? null
                : supplyMedicaments.stream()
                .map(supplyMedicament ->
                        MedicamentCountViewModel.builder()
                                .medicamentId(supplyMedicament.getMedicament().getId())
                                .name(supplyMedicament.getMedicament().getName())
                                .count(supplyMedicament.getCount())
                                .build()
                ).collect(Collectors.toSet());
    }
}