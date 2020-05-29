package com.ulstu.pharmacy.report;

import com.ulstu.pharmacy.report.model.MedicalServiceReportViewModel;
import com.ulstu.pharmacy.report.model.MedicamentMovementReportViewModel;
import com.ulstu.pharmacy.report.template.FileModel;

import javax.ejb.Local;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Local
public interface ReportEjbLocal {

    /**
     * Получение моделей услуг для отчёта по их id.
     * @param medicalServiceIds набор id услуг по которым надо сформировать модели.
     * @return
     */
    List<MedicalServiceReportViewModel> getMedicalServicesByIds(Set<Long> medicalServiceIds);

    /**
     * Получение моделей движения медикаментов за указанный период.
     * @param fromDate
     * @param toDate
     * @return
     */
    List<MedicamentMovementReportViewModel> getMedicamentMovementFromToDate(Timestamp fromDate, Timestamp toDate);

    /**
     * Создание отчёта в формате .doc по выбранным услугам.
     * @param medicalServiceIds id выбранных услуг.
     * @return файл отчёта в формате .doc .
     */
    FileModel createDocMedicalServiceReport(Set<Long> medicalServiceIds);

    /**
     * Создание отчёта в формате .xls по выбранным услугам.
     * @param medicalServiceIds
     * @return файл отчёта в формате .xls .
     */
    FileModel createXlsMedicalServiceReport(Set<Long> medicalServiceIds);

    /**
     * Создание отчёта в формате .pdf по движению медикаментов за период.
     * @param fromDate
     * @param toDate
     * @return файл отчёта в формате .pdf .
     */
    FileModel createPdfMedicamentMovementReport(Timestamp fromDate, Timestamp toDate);
}