package com.ulstu.pharmacy.pmmsl.medservice.mapper;


import com.ulstu.pharmacy.pmmsl.medservice.entity.MedicalService;
import com.ulstu.pharmacy.pmmsl.medservice.view.MedicalServiceViewModel;

import javax.inject.Inject;
import java.util.Objects;
import java.util.stream.Collectors;

public class MedicalServiceMapperImpl implements MedicalServiceMapper {

    private final MedicamentMedicalServiceMapper medicamentMedicalServiceMapper;

    @Inject
    public MedicalServiceMapperImpl(MedicamentMedicalServiceMapper medicamentMedicalServiceMapper) {
        this.medicamentMedicalServiceMapper = medicamentMedicalServiceMapper;
    }

    @Override
    public MedicalService toEntity(MedicalServiceViewModel medicalServiceViewModel) {
        MedicalService medicalService = null;
        if (Objects.nonNull(medicalServiceViewModel)) {
            medicalService = MedicalService.builder()
                    .medicamentMedicalServices(
                            Objects.nonNull(medicalServiceViewModel.getMedicamentMedicalServices()) ?
                                    medicalServiceViewModel.getMedicamentMedicalServices().stream()
                                            .map(medicamentMedicalServiceMapper::toEntity)
                                            .collect(Collectors.toList())
                                    : null
                    )
                    .sumPrice(medicalServiceViewModel.getSumPrice())
                    .build();
            medicalService.setId(medicalServiceViewModel.getId());
        }
        return medicalService;
    }

    @Override
    public MedicalServiceViewModel toViewModel(MedicalService medicalService) {
        return Objects.isNull(medicalService) ? null :
                MedicalServiceViewModel.builder()
                        .id(medicalService.getId())
                        .medicamentMedicalServices(
                                Objects.nonNull(medicalService.getMedicamentMedicalServices()) ?
                                        medicalService.getMedicamentMedicalServices().stream()
                                                .map(medicamentMedicalServiceMapper::toViewModel)
                                                .collect(Collectors.toList())
                                        : null
                        )
                        .sumPrice(medicalService.getSumPrice())
                        .provisionDate(medicalService.getProvisionDate())
                        .build();
    }
}