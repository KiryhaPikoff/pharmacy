package com.ulstu.pharmacy.pmmsl.common.mapper;

public interface Mapper<Entity, ViewModel> {

    Entity toEntity(ViewModel viewModel);

    ViewModel toViewModel(Entity entity);
}