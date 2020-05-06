package com.ulstu.pharmacy.pmmsl.report.mapper;

import com.ulstu.pharmacy.pmmsl.medservice.view.MedicalServiceViewModel;
import com.ulstu.pharmacy.pmmsl.report.model.MedicalServiceReportViewModel;

public interface MedicalServiceReportMapper {

    MedicalServiceReportViewModel toMedicalServiceReportViewModel(MedicalServiceViewModel medicalServiceModel);
}