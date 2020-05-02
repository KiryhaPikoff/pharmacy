package com.ulstu.pharmacy.pmmsl.medicament.mapper;


import com.ulstu.pharmacy.pmmsl.medicament.binding.MedicamentBindingModel;
import com.ulstu.pharmacy.pmmsl.medicament.entity.Medicament;
import com.ulstu.pharmacy.pmmsl.medicament.view.MedicamentViewModel;

import java.util.Objects;

public class MedicamentMapperImpl implements MedicamentMapper {

    public Medicament toEntity(MedicamentBindingModel medicamentBindingModel) {
        return Objects.isNull(medicamentBindingModel) ? null :
                Medicament.builder()
                        .id(medicamentBindingModel.getId())
                        .name(medicamentBindingModel.getName())
                        .description(medicamentBindingModel.getDescription())
                        .instruction(medicamentBindingModel.getInstruction())
                        .contraindications(medicamentBindingModel.getContraindications())
                        .price(medicamentBindingModel.getPrice())
                        .build();
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