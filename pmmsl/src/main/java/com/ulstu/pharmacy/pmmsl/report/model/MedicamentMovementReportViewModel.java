package com.ulstu.pharmacy.pmmsl.report.model;

import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
@Builder
public class MedicamentMovementReportViewModel {

    private final MovementType movementType;

    private final String medicamentName;

    private final Integer count;

    private final Timestamp date;
}