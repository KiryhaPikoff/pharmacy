package com.ulstu.pharmacy.web.helper;

import com.ulstu.pharmacy.report.template.FileModel;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Date;

public class DownloadHelper {

    public static StreamedContent download(FileModel fileModel) {
        return DefaultStreamedContent.builder()
                    .name(new Date() + fileModel.getFileExtension().getExtension())
                    .contentType("file/" + fileModel.getFileExtension().getExtension())
                    .stream(() -> {
                        try {
                            return new FileInputStream(fileModel.getFile());
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        return null;
                }).build();
    }
}