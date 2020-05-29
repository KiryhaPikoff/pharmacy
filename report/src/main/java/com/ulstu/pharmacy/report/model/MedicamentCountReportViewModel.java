package com.ulstu.pharmacy.report.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MedicamentCountReportViewModel {

    private final String medicamentName;

    private final Integer count;
}