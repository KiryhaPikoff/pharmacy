package com.ulstu.pharmacy.pmmsl.pharmacy.binding;

import com.ulstu.pharmacy.pmmsl.medicament.binding.MedicamentCountBindingModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Builder
public class PharmacyReplenishmentBindingModel {

    private final Long pharmacyId;

    private final Set<MedicamentCountBindingModel> medicamentCountSet;
}