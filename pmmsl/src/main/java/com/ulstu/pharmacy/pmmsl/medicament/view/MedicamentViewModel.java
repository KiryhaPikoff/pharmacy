package com.ulstu.pharmacy.pmmsl.medicament.view;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@EqualsAndHashCode
public class MedicamentViewModel {

    @EqualsAndHashCode.Exclude
    private Long id;

    private String name;

    private String description;

    private String contraindications;

    private String instruction;

    private BigDecimal price;
}