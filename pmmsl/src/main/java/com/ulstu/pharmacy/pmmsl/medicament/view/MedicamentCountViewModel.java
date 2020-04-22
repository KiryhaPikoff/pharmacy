package com.ulstu.pharmacy.pmmsl.medicament.view;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
public class MedicamentCountViewModel {

    private Long medicamentId;

    @EqualsAndHashCode.Exclude
    private String name;

    private Integer count;
}