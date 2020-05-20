package com.ulstu.pharmacy.web.medservice.model;

import com.ulstu.pharmacy.pmmsl.medicament.view.MedicamentViewModel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@EqualsAndHashCode
public class MedicamentCountViewModel {

    private MedicamentViewModel medicament;

    @EqualsAndHashCode.Exclude
    private Integer count;

    @Override
    public String toString() {
        return medicament.getName();
    }
}