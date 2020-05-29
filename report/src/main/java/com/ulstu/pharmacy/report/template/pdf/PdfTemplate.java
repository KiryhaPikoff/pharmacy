package com.ulstu.pharmacy.report.template.pdf;

import com.ulstu.pharmacy.report.model.MedicamentMovementReportViewModel;
import com.ulstu.pharmacy.report.template.FileModel;

import java.util.List;

/**
 * Шаблоны отчётов в .pdf формате.
 */
public interface PdfTemplate {

    FileModel getMedicamentMovement(List<MedicamentMovementReportViewModel> medicamentMovementModels);
}