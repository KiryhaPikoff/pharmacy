package com.ulstu.pharmacy.pmmsl.supply.view;

import com.ulstu.pharmacy.pmmsl.medicament.view.MedicamentCountViewModel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Set;

@Getter
@Setter
@Builder
@EqualsAndHashCode
public class SupplyViewModel {

    @EqualsAndHashCode.Exclude
    private Long id;

    private Timestamp date;

    private String pharmacyName;

    private Set<MedicamentCountViewModel> medicamentsWithCount;
}