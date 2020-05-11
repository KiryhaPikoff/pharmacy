package com.ulstu.pharmacy.pmmsl.report.facade.pdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;

public class PdfFacadeImpl implements PdfFacade {

    private File file;

    private Document document;

    private Paragraph currentParagraph;

    /**
     * Инициализирует новый документ.
     */
    @Override
    public void createNew() {
        this.file = new File(this.generateFileTitle());
        try {
            document = new Document();
            PdfWriter.getInstance(
                    document,
                    new FileOutputStream(this.file)
            );
            document.open();
            this.currentParagraph = new Paragraph();
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    /**
     * Добавляет текст в файл.
     *
     * @param text
     */
    @Override
    public void append(String text) {
        this.currentParagraph.add(text);
    }

    /**
     * Перевод каретки на новую строку.
     */
    @Override
    public void newLine() {
        try {
            document.add(currentParagraph);
            this.currentParagraph = new Paragraph();
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    /**
     * Перевод из модели в файл.
     */
    @Override
    public File createFile() {
        this.document.close();
        return this.file;
    }

    private String generateFileTitle() {
        return String.valueOf(System.currentTimeMillis());
    }
}