package com.ulstu.pharmacy.pmmsl.pharmacy.mapper;

import com.ulstu.pharmacy.pmmsl.medicament.view.MedicamentCountViewModel;
import com.ulstu.pharmacy.pmmsl.pharmacy.entity.Pharmacy;
import com.ulstu.pharmacy.pmmsl.pharmacy.entity.PharmacyMedicament;
import com.ulstu.pharmacy.pmmsl.pharmacy.view.PharmacyViewModel;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class PharmacyMapperImpl implements PharmacyMapper {

    @Override
    public PharmacyViewModel toViewModel(Pharmacy pharmacy) {
        return Objects.isNull(pharmacy) ? null :
                PharmacyViewModel.builder()
                        .id(pharmacy.getId())
                        .name(pharmacy.getName())
                        .medicamentsWithCount(
                                this.toMedicamentCountViewModel(
                                        pharmacy.getPharmacyMedicaments()
                                )
                        )
                        .build();
    }

    private Set<MedicamentCountViewModel> toMedicamentCountViewModel(Set<PharmacyMedicament> pharmacyMedicaments) {
        return Objects.isNull(pharmacyMedicaments) ? null
                : pharmacyMedicaments.stream()
                .map(pharmacyMedicament ->
                     MedicamentCountViewModel.builder()
                            .medicamentId(pharmacyMedicament.getMedicament().getId())
                            .name(pharmacyMedicament.getMedicament().getName())
                            .count(pharmacyMedicament.getCount())
                            .build()
                ).collect(Collectors.toSet());
    }
}