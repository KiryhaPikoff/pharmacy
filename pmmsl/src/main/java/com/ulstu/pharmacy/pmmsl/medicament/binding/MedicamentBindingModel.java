package com.ulstu.pharmacy.pmmsl.medicament.binding;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@Builder
public class MedicamentBindingModel {

    private Long id;

    private String name;

    private String description;

    private String contraindications;

    private String instruction;

    private BigDecimal price;
}