package com.ulstu.pharmacy.pmmsl.medservice.mapper;

import com.ulstu.pharmacy.pmmsl.medicament.mapper.MedicamentMapper;
import com.ulstu.pharmacy.pmmsl.medservice.entity.MedicamentMedicalService;
import com.ulstu.pharmacy.pmmsl.medservice.view.MedicamentMedicalServiceViewModel;
import lombok.AllArgsConstructor;

import javax.inject.Inject;
import java.util.Objects;

@AllArgsConstructor(onConstructor = @__({@Inject}))
public class MedicamentMedicalServiceMapperImpl implements MedicamentMedicalServiceMapper {

    @Inject
    private final MedicamentMapper medicamentMapper;

    @Override
    public MedicamentMedicalService toEntity(MedicamentMedicalServiceViewModel medicamentMedicalServiceViewModel) {
        MedicamentMedicalService medicamentMedicalService = null;
        if (Objects.nonNull(medicamentMedicalServiceViewModel)) {
            medicamentMedicalService = MedicamentMedicalService.builder()
                    .medicament(
                            medicamentMapper.toEntity(
                                    medicamentMedicalServiceViewModel.getMedicament()
                            )
                    )
                    .price(medicamentMedicalServiceViewModel.getPrice())
                    .count(medicamentMedicalServiceViewModel.getCount())
                    .build();
            medicamentMedicalService.setId(medicamentMedicalServiceViewModel.getId());
        }
        return medicamentMedicalService;
    }

    @Override
    public MedicamentMedicalServiceViewModel toViewModel(MedicamentMedicalService medicamentMedicalService) {
        return Objects.nonNull(medicamentMedicalService) ? null :
                MedicamentMedicalServiceViewModel.builder()
                        .id(medicamentMedicalService.getId())
                        .medicament(
                                medicamentMapper.toViewModel(
                                        medicamentMedicalService.getMedicament()
                                )
                        )
                        .price(medicamentMedicalService.getPrice())
                        .count(medicamentMedicalService.getCount())
                        .build();
    }
}