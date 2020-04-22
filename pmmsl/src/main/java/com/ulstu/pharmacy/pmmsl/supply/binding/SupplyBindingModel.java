package com.ulstu.pharmacy.pmmsl.supply.binding;

import com.ulstu.pharmacy.pmmsl.medicament.binding.MedicamentCountBindingModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Builder
public class SupplyBindingModel {

    private final Long pharmacyId;

    private final Set<MedicamentCountBindingModel> medicamentCountSet;
}