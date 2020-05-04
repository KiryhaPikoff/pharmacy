package com.ulstu.pharmacy.pmmsl.report.helper;

public enum FileExtension {

    DOC(".doc"),
    XLS(".xls"),
    PDF(".pdf");

    private String extension;

    FileExtension(String extension) {
        this.extension = extension;
    }
}