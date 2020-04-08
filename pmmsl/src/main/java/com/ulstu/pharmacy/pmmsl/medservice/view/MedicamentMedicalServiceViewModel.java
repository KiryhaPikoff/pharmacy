package com.ulstu.pharmacy.pmmsl.medservice.view;

import com.ulstu.pharmacy.pmmsl.medicament.view.MedicamentViewModel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@EqualsAndHashCode
public class MedicamentMedicalServiceViewModel {

    @EqualsAndHashCode.Exclude
    private Long id;

    private MedicamentViewModel medicament;

    private Integer count;

    private BigDecimal price;
}