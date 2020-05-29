package com.ulstu.pharmacy.classifire.medicament.category;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class PriceCategory {

    private final Integer threshold;
}