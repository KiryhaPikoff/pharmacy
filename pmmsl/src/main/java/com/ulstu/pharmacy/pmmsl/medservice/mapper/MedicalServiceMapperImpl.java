package com.ulstu.pharmacy.pmmsl.medservice.mapper;


import com.ulstu.pharmacy.pmmsl.medicament.view.MedicamentCountViewModel;
import com.ulstu.pharmacy.pmmsl.medservice.entity.MedicalService;
import com.ulstu.pharmacy.pmmsl.medservice.entity.MedicamentMedicalService;
import com.ulstu.pharmacy.pmmsl.medservice.view.MedicalServiceViewModel;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class MedicalServiceMapperImpl implements MedicalServiceMapper {

    @Override
    public MedicalServiceViewModel toViewModel(MedicalService medicalService) {
        return Objects.isNull(medicalService) ? null :
                MedicalServiceViewModel.builder()
                        .id(medicalService.getId())
                        .medicamentsWithCount(
                                this.getMedicamentCountViewModels(
                                        medicalService.getMedicamentMedicalServices()
                                )
                        )
                        .sumPrice(medicalService.getSumPrice())
                        .provisionDate(medicalService.getProvisionDate())
                        .build();
    }

    private Set<MedicamentCountViewModel> getMedicamentCountViewModels(Set<MedicamentMedicalService> medicamentMedicalServices) {
        return Objects.isNull(medicamentMedicalServices) ? null
                : medicamentMedicalServices.stream()
                .map(medicamentMedicalService ->
                        MedicamentCountViewModel.builder()
                                .medicamentId(medicamentMedicalService.getMedicament().getId())
                                .name(medicamentMedicalService.getMedicament().getName())
                                .count(medicamentMedicalService.getCount())
                                .build()
                ).collect(Collectors.toSet());
    }
}