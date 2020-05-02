package com.ulstu.pharmacy.pmmsl.medicament.mapper;

import com.ulstu.pharmacy.pmmsl.common.mapper.Mapper;
import com.ulstu.pharmacy.pmmsl.medicament.binding.MedicamentBindingModel;
import com.ulstu.pharmacy.pmmsl.medicament.entity.Medicament;
import com.ulstu.pharmacy.pmmsl.medicament.view.MedicamentViewModel;

public interface MedicamentMapper extends Mapper<Medicament, MedicamentViewModel> {

    Medicament toEntity(MedicamentBindingModel medicamentBindingModel);
}