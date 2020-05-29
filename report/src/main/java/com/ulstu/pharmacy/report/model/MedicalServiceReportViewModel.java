package com.ulstu.pharmacy.report.model;

import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Builder
public class MedicalServiceReportViewModel {

    private final Timestamp date;

    private final List<MedicamentCountReportViewModel> medicamentsCount;
}