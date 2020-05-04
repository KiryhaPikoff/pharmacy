package com.ulstu.pharmacy.pmmsl.report.helper;

import lombok.Builder;
import lombok.Getter;

import java.io.File;

@Getter
@Builder
public class FileModel {

    private final String title;

    private final FileExtension extension;

    private final File file;
}