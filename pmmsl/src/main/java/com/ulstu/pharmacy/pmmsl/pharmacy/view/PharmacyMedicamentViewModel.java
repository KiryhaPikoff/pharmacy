package com.ulstu.pharmacy.pmmsl.pharmacy.view;

import com.ulstu.pharmacy.pmmsl.medicament.view.MedicamentViewModel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@EqualsAndHashCode
public class PharmacyMedicamentViewModel {

    @EqualsAndHashCode.Exclude
    private Long id;

    private MedicamentViewModel medicament;

    private PharmacyViewModel pharmacy;

    @EqualsAndHashCode.Exclude
    private Integer count;

    private Timestamp receiptDate;
}