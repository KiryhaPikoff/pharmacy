package com.ulstu.pharmacy.pmmsl.pharmacy.view;

import com.ulstu.pharmacy.pmmsl.medicament.binding.MedicamentCountBindingModel;
import com.ulstu.pharmacy.pmmsl.medicament.view.MedicamentCountViewModel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@EqualsAndHashCode
public class PharmacyViewModel {

    @EqualsAndHashCode.Exclude
    private Long id;

    private String name;

    private Set<MedicamentCountViewModel> medicamentsWithCount;
}