package com.ulstu.pharmacy.pmmsl.report.template;

public enum FileExtension {

    DOC(".doc"),
    XLS(".xls"),
    PDF(".pdf");

    private String extension;

    FileExtension(String extension) {
        this.extension = extension;
    }
}