package com.ulstu.pharmacy.report.mapper;

import com.ulstu.pharmacy.pmmsl.medservice.view.MedicalServiceViewModel;
import com.ulstu.pharmacy.pmmsl.supply.view.SupplyViewModel;
import com.ulstu.pharmacy.report.model.MedicamentMovementReportViewModel;

import java.util.List;

public interface MedicamentMovementReportMapper {

    List<MedicamentMovementReportViewModel> toMedicamentMovementViewModels(MedicalServiceViewModel medicalServiceModel);

    List<MedicamentMovementReportViewModel> toMedicamentMovementViewModels(SupplyViewModel supplyModel);
}