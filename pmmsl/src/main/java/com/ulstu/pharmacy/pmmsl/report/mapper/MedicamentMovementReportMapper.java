package com.ulstu.pharmacy.pmmsl.report.mapper;

import com.ulstu.pharmacy.pmmsl.medservice.view.MedicalServiceViewModel;
import com.ulstu.pharmacy.pmmsl.report.model.MedicamentMovementReportViewModel;
import com.ulstu.pharmacy.pmmsl.supply.view.SupplyViewModel;

import java.util.List;

public interface MedicamentMovementReportMapper {

    List<MedicamentMovementReportViewModel> toMedicamentMovementViewModels(MedicalServiceViewModel medicalServiceModel);

    List<MedicamentMovementReportViewModel> toMedicamentMovementViewModels(SupplyViewModel supplyModel);
}