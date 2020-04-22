package com.ulstu.pharmacy.pmmsl.pharmacy.binding;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Builder
public class PharmacyReplanishmentBindingModel {

    private final Long pharmacyId;

    private final Set<MedicamentCountBindingModel> medicamentCountSet;
}