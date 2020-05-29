package com.ulstu.pharmacy.report.mapper;

import com.ulstu.pharmacy.pmmsl.medservice.view.MedicalServiceViewModel;
import com.ulstu.pharmacy.report.model.MedicalServiceReportViewModel;

public interface MedicalServiceReportMapper {

    MedicalServiceReportViewModel toMedicalServiceReportViewModel(MedicalServiceViewModel medicalServiceModel);
}