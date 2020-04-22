package com.ulstu.pharmacy.pmmsl.medicament.view;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@ToString
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