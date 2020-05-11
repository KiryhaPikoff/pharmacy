package com.ulstu.pharmacy.pmmsl.report;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.ulstu.pharmacy.pmmsl.medservice.ejb.MedicalServiceEjbLocal;
import com.ulstu.pharmacy.pmmsl.report.mapper.MedicalServiceReportMapper;
import com.ulstu.pharmacy.pmmsl.report.mapper.MedicamentMovementReportMapper;
import com.ulstu.pharmacy.pmmsl.report.model.MedicalServiceReportViewModel;
import com.ulstu.pharmacy.pmmsl.report.model.MedicamentMovementReportViewModel;
import com.ulstu.pharmacy.pmmsl.report.template.FileModel;
import com.ulstu.pharmacy.pmmsl.report.template.doc.DocTemplate;
import com.ulstu.pharmacy.pmmsl.report.template.pdf.PdfTemplate;
import com.ulstu.pharmacy.pmmsl.report.template.xls.XlsTemplate;
import com.ulstu.pharmacy.pmmsl.supply.ejb.SupplyEjbLocal;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Stateless
public class ReportEjbImpl implements ReportEjbLocal {

    private final MedicalServiceEjbLocal medicalServiceEjb;

    private final SupplyEjbLocal supplyEjb;

    private final MedicalServiceReportMapper medicalServiceReportMapper;

    private final MedicamentMovementReportMapper medicamentMovementReportMapper;

    private final DocTemplate docTemplate;

    private final XlsTemplate xlsTemplate;

    private final PdfTemplate pdfTemplate;

    @Inject
    public ReportEjbImpl(MedicalServiceEjbLocal medicalServiceEjb,
                         SupplyEjbLocal supplyEjb,
                         MedicalServiceReportMapper medicalServiceReportMapper,
                         MedicamentMovementReportMapper medicamentMovementReportMapper,
                         DocTemplate docTemplate,
                         XlsTemplate xlsTemplate,
                         PdfTemplate pdfTemplate) {
        this.medicalServiceEjb = medicalServiceEjb;
        this.supplyEjb = supplyEjb;
        this.medicalServiceReportMapper = medicalServiceReportMapper;
        this.medicamentMovementReportMapper = medicamentMovementReportMapper;
        this.docTemplate = docTemplate;
        this.xlsTemplate = xlsTemplate;
        this.pdfTemplate = pdfTemplate;
    }

    /**
     * Получение моделей услуг для отчёта по их id.
     *
     * @param medicalServiceIds набор id услуг по которым надо сформировать модели.
     * @return
     */
    @Override
    public List<MedicalServiceReportViewModel> getMedicalServicesByIds(Set<Long> medicalServiceIds) {
        return this.medicalServiceEjb.getByIds(medicalServiceIds).stream()
                .map(medicalServiceReportMapper::toMedicalServiceReportViewModel)
                .collect(Collectors.toList());
    }

    /**
     * Получение моделей движения медикаментов за указанный период.
     *
     * @param fromDate
     * @param toDate
     * @return
     */
    @Override
    public List<MedicamentMovementReportViewModel> getMedicamentMovementFromToDate(Timestamp fromDate, Timestamp toDate) {
        var medicalServices = this.medicalServiceEjb.getFromDateToDate(fromDate, toDate).stream()
                .map(medicamentMovementReportMapper::toMedicamentMovementViewModels)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        var supplys = this.supplyEjb.getFromDateToDate(fromDate, toDate).stream()
                .map(medicamentMovementReportMapper::toMedicamentMovementViewModels)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        var unionList = Lists.newLinkedList(Iterables.concat(medicalServices, supplys));
        unionList.sort(Comparator.comparing(MedicamentMovementReportViewModel::getDate));
        return unionList;
    }

    /**
     * Создание отчёта в формате .doc по выбранным услугам.
     *
     * @param medicalServiceIds id выбранных услуг.
     * @return файл отчёта в формате .doc .
     */
    @Override
    public FileModel createDocMedicalServiceReport(Set<Long> medicalServiceIds) {
        var medicalServices = this.getMedicalServicesByIds(medicalServiceIds);
        return docTemplate.getMedicalServiceWithMedicaments(medicalServices);
    }

    /**
     * Создание отчёта в формате .xls по выбранным услугам.
     *
     * @param medicalServiceIds
     * @return файл отчёта в формате .xls .
     */
    @Override
    public FileModel createXlsMedicalServiceReport(Set<Long> medicalServiceIds) {
        var medicalServices = this.getMedicalServicesByIds(medicalServiceIds);
        return xlsTemplate.getMedicalServiceWithMedicaments(medicalServices);
    }

    /**
     * Создание отчёта в формате .pdf по движению медикаментов за период.
     *
     * @param fromDate
     * @param toDate
     * @return файл отчёта в формате .pdf .
     */
    @Override
    public FileModel createPdfMedicamentMovementReport(Timestamp fromDate, Timestamp toDate) {
        var medicamentMovements = this.getMedicamentMovementFromToDate(fromDate, toDate);
        return pdfTemplate.getMedicamentMovement(medicamentMovements);
    }
}