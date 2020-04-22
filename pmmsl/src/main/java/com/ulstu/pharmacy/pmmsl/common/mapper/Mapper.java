package com.ulstu.pharmacy.pmmsl.common.mapper;

public interface Mapper<Entity, ViewModel> {

    ViewModel toViewModel(Entity entity);
}