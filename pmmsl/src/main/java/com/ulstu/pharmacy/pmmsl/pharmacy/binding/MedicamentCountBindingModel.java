package com.ulstu.pharmacy.pmmsl.pharmacy.binding;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class MedicamentCountBindingModel {

    private Long medicamentId;

    private Integer count;
}