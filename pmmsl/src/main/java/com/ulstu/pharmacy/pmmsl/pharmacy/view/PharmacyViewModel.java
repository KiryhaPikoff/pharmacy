package com.ulstu.pharmacy.pmmsl.pharmacy.view;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@EqualsAndHashCode
public class PharmacyViewModel {

    @EqualsAndHashCode.Exclude
    private Long id;

    private String name;

    private List<PharmacyMedicamentViewModel> pharmacyMedicaments;
}