package com.ulstu.pharmacy.pmmsl.medicament.mapper;


import com.ulstu.pharmacy.pmmsl.medicament.entity.Medicament;
import com.ulstu.pharmacy.pmmsl.medicament.view.MedicamentViewModel;

import java.util.Objects;

public class MedicamentMapperImpl implements MedicamentMapper {

    @Override
    public Medicament toEntity(MedicamentViewModel medicamentViewModel) {
        Medicament medicament = null;
        if (Objects.nonNull(medicamentViewModel)) {
            medicament = new Medicament.Builder()
                    .name(medicamentViewModel.getName())
                    .contraindications(medicamentViewModel.getContraindications())
                    .description(medicamentViewModel.getDescription())
                    .instruction(medicamentViewModel.getInstruction())
                    .price(medicamentViewModel.getPrice())
                    .build();
            medicament.setId(medicamentViewModel.getId());
        }
        return medicament;
    }

    @Override
    public MedicamentViewModel toViewModel(Medicament medicament) {
        return Objects.isNull(medicament) ? null :
                MedicamentViewModel.builder()
                        .id(medicament.getId())
                        .name(medicament.getName())
                        .contraindications(medicament.getContraindications())
                        .description(medicament.getDescription())
                        .instruction(medicament.getInstruction())
                        .price(medicament.getPrice())
                        .build();
    }
}