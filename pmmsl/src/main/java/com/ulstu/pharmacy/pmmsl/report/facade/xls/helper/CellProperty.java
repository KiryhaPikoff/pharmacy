package com.ulstu.pharmacy.pmmsl.report.facade.xls.helper;

import lombok.Getter;

@Getter
public class CellProperty {

    private final Integer row;

    private final Integer column;

    public CellProperty(Integer row, Integer column) {
        this.row = row;
        this.column = column;
    }
}