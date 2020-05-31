package com.ulstu.pharmacy.report.facade.pdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.SneakyThrows;

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
            this.createNewParagraph();
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
            this.createNewParagraph();
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    @SneakyThrows
    private void createNewParagraph() {
        BaseFont helvetica = BaseFont.createFont("fonts/FreeSans.ttf", "Cp1251", BaseFont.EMBEDDED);
        Font font = new Font(helvetica, 14, Font.NORMAL);
        this.currentParagraph = new Paragraph();
        this.currentParagraph.setFont(font);
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
        return System.currentTimeMillis() + ".pdf";
    }
}