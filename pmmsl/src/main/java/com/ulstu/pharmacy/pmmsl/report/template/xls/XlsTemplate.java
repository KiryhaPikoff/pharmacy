package com.ulstu.pharmacy.pmmsl.report.template.xls;

import com.ulstu.pharmacy.pmmsl.report.model.MedicalServiceReportViewModel;
import com.ulstu.pharmacy.pmmsl.report.template.FileModel;

import java.util.List;

/**
 * Шаблоны отчётов в .xls формате.
 */
public interface XlsTemplate {

    FileModel getMedicalServiceWithMedicaments(List<MedicalServiceReportViewModel> medicalServiceModels);
}