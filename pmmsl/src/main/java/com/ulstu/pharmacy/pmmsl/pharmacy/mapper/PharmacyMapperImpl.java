package com.ulstu.pharmacy.pmmsl.pharmacy.mapper;

import com.ulstu.pharmacy.pmmsl.pharmacy.entity.Pharmacy;
import com.ulstu.pharmacy.pmmsl.pharmacy.view.PharmacyViewModel;

import java.util.Objects;

public class PharmacyMapperImpl implements PharmacyMapper {

    @Override
    public Pharmacy toEntity(PharmacyViewModel pharmacyViewModel) {
        Pharmacy pharmacy = null;
        if (Objects.nonNull(pharmacyViewModel)) {
            pharmacy = Pharmacy.builder()
                    .name(pharmacyViewModel.getName())
                    .build();
            pharmacy.setId(pharmacyViewModel.getId());
        }
        return pharmacy;
    }

    @Override
    public PharmacyViewModel toViewModel(Pharmacy pharmacy) {
        return Objects.isNull(pharmacy) ? null :
                PharmacyViewModel.builder()
                        .id(pharmacy.getId())
                        .name(pharmacy.getName())
                        .build();
    }
}