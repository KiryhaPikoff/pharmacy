package com.ulstu.pharmacy.pmmsl.supply.view;

import com.ulstu.pharmacy.pmmsl.medicament.view.MedicamentCountViewModel;
import lombok.*;

import java.sql.Timestamp;
import java.util.Set;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@ToString
public class SupplyViewModel {

    @EqualsAndHashCode.Exclude
    private Long id;

    private Timestamp date;

    private String pharmacyName;

    private Set<MedicamentCountViewModel> medicamentsWithCount;
}