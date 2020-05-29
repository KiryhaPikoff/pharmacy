package com.ulstu.pharmacy.report.mapper;

import com.ulstu.pharmacy.pmmsl.medservice.view.MedicalServiceViewModel;
import com.ulstu.pharmacy.pmmsl.supply.view.SupplyViewModel;
import com.ulstu.pharmacy.report.model.MedicamentMovementReportViewModel;
import com.ulstu.pharmacy.report.model.MovementType;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MedicamentMovementReportMapperImpl implements MedicamentMovementReportMapper {

    @Override
    public List<MedicamentMovementReportViewModel> toMedicamentMovementViewModels(MedicalServiceViewModel medicalServiceModel) {
        return Objects.isNull(medicalServiceModel) ? Collections.emptyList() :
                medicalServiceModel.getMedicamentsWithCount().stream()
                    .map(medicamentCountModel -> MedicamentMovementReportViewModel.builder()
                                .movementType(MovementType.WRITE_OFF)
                                .date(medicalServiceModel.getProvisionDate())
                                .count(medicamentCountModel.getCount())
                                .medicamentName(medicamentCountModel.getName())
                                .build())
                    .collect(Collectors.toList());
    }

    @Override
    public List<MedicamentMovementReportViewModel> toMedicamentMovementViewModels(SupplyViewModel supplyModel) {
        return Objects.isNull(supplyModel) ? Collections.emptyList() :
                supplyModel.getMedicamentsWithCount().stream()
                        .map(medicamentCountModel -> MedicamentMovementReportViewModel.builder()
                                .movementType(MovementType.REPLENISHMENT)
                                .date(supplyModel.getDate())
                                .count(medicamentCountModel.getCount())
                                .medicamentName(medicamentCountModel.getName())
                                .build())
                        .collect(Collectors.toList());
    }
}