package com.ulstu.pharmacy.pmmsl.report.model;

public enum MovementType {

    REPLENISHMENT("Пополнение"),
    WRITE_OFF("Списание");

    private String movementType;

    MovementType(String movementType) {
        this.movementType = movementType;
    }
}