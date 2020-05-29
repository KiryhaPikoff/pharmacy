package com.ulstu.pharmacy.report.template.xls;

import com.ulstu.pharmacy.report.model.MedicalServiceReportViewModel;
import com.ulstu.pharmacy.report.template.FileModel;

import java.util.List;

/**
 * Шаблоны отчётов в .xls формате.
 */
public interface XlsTemplate {

    FileModel getMedicalServiceWithMedicaments(List<MedicalServiceReportViewModel> medicalServiceModels);
}