package com.ulstu.pharmacy.report.template.doc;

import com.ulstu.pharmacy.report.model.MedicalServiceReportViewModel;
import com.ulstu.pharmacy.report.template.FileModel;

import java.util.List;

/**
 * Шаблоны отчётов в .doc формате.
 */
public interface DocTemplate {

    FileModel getMedicalServiceWithMedicaments(List<MedicalServiceReportViewModel> medicalServiceModels);
}