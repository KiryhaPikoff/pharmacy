package com.ulstu.pharmacy.pmmsl.pharmacy.binding;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
public class MedicamentCountBindingModel {

    private Long medicamentId;

    private Integer count;
}