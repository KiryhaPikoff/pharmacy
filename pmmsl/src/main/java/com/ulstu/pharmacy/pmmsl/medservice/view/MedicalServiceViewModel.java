package com.ulstu.pharmacy.pmmsl.medservice.view;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@Builder
@EqualsAndHashCode
public class MedicalServiceViewModel {

    @EqualsAndHashCode.Exclude
    private Long id;

    private Timestamp provisionDate;

    private BigDecimal sumPrice;

    private List<MedicamentMedicalServiceViewModel> medicamentMedicalServices;
}