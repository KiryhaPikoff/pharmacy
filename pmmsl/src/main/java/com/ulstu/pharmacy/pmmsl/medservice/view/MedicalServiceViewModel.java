package com.ulstu.pharmacy.pmmsl.medservice.view;

import com.ulstu.pharmacy.pmmsl.medicament.view.MedicamentCountViewModel;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
public class MedicalServiceViewModel {

    @EqualsAndHashCode.Exclude
    private Long id;

    private Timestamp provisionDate;

    private BigDecimal sumPrice;

    private Set<MedicamentCountViewModel> medicamentsWithCount;
}