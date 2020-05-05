package com.ulstu.pharmacy.pmmsl.report.template.pdf;

import com.ulstu.pharmacy.pmmsl.report.model.MedicamentMovementReportViewModel;
import com.ulstu.pharmacy.pmmsl.report.template.FileModel;

import java.util.List;

/**
 * Шаблоны отчётов в .pdf формате.
 */
public interface PdfTemplate {

    FileModel getMedicamentMovement(List<MedicamentMovementReportViewModel> medicamentMovementModels);
}