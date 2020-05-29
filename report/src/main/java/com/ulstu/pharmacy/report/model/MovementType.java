package com.ulstu.pharmacy.report.model;

public enum MovementType {

    REPLENISHMENT("ПОПОЛНЕНИЕ"),
    WRITE_OFF("СПИСАНИЕ");

    private String movementType;

    MovementType(String movementType) {
        this.movementType = movementType;
    }

    public String getType() {
        return this.movementType;
    }
}