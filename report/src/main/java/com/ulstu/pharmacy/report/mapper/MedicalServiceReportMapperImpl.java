package com.ulstu.pharmacy.report.mapper;

import com.ulstu.pharmacy.pmmsl.medicament.view.MedicamentCountViewModel;
import com.ulstu.pharmacy.pmmsl.medservice.view.MedicalServiceViewModel;
import com.ulstu.pharmacy.report.model.MedicalServiceReportViewModel;
import com.ulstu.pharmacy.report.model.MedicamentCountReportViewModel;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class MedicalServiceReportMapperImpl implements MedicalServiceReportMapper {

    @Override
    public MedicalServiceReportViewModel toMedicalServiceReportViewModel(MedicalServiceViewModel medicalServiceModel) {
        return Objects.isNull(medicalServiceModel) ? null :
                MedicalServiceReportViewModel.builder()
                        .date(medicalServiceModel.getProvisionDate())
                        .medicamentsCount(
                                this.getMedicamentCountReportViewModels(
                                        medicalServiceModel.getMedicamentsWithCount()
                                )
                        )
                        .build();
    }

    private List<MedicamentCountReportViewModel> getMedicamentCountReportViewModels(Set<MedicamentCountViewModel> medicamentCountViewModels) {
        return Objects.isNull(medicamentCountViewModels) ? null
                : medicamentCountViewModels.stream()
                .map(medicamentMedicalService ->
                        MedicamentCountReportViewModel.builder()
                                .medicamentName(medicamentMedicalService.getName())
                                .count(medicamentMedicalService.getCount())
                                .build()
                ).collect(Collectors.toList());
    }
}