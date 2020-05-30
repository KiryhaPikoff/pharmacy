package com.ulstu.pharmacy.report.facade.doc;

import com.ulstu.pharmacy.report.facade.util.Style;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.File;
import java.io.FileOutputStream;

public class DocFacadeImpl implements DocFacade {

    private XWPFDocument document;

    private XWPFParagraph currentParagraph;

    private Style DEFAULT_STYLE;

    /**
     * Инициализирует новый документ.
     */
    @Override
    public void createNew() {
        this.document = new XWPFDocument();
        this.currentParagraph = this.document.createParagraph();
        this.DEFAULT_STYLE = Style.builder()
                .fontSize(14)
                .isBold(false)
                .isInCenter(false)
                .build();
    }

    /**
     * Добавляет текст в файл.
     *
     * @param text
     */
    @Override
    public void append(String text) {
        this.append(text, this.DEFAULT_STYLE);
    }

    /**
     * Добавляет текст в файл. Пользовательский стиль.
     *
     * @param text
     * @param style
     */
    @Override
    public void append(String text, Style style) {
        XWPFRun run = this.currentParagraph.createRun();
        this.applyStyle(run, style);
        run.setText(text);
    }

    /**
     * Перевод каретки на новую строку.
     */
    @Override
    public void newLine() {
        this.currentParagraph = this.document.createParagraph();
    }

    /**
     * Перевод из модели в файл.
     */
    @Override
    public File createFile() {
        File file = new File(this.generateFileTitle());
        try {
            document.write(
                    new FileOutputStream(file)
            );
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
        return file;
    }

    private String generateFileTitle() {
        return System.currentTimeMillis() + ".doc";
    }

    private void applyStyle(XWPFRun run, Style style) {
        if (style.isInCenter()) {
            this.currentParagraph.setAlignment(ParagraphAlignment.CENTER);
        }
        run.setFontSize(
                style.getFontSize()
        );
        run.setBold(
                style.isBold()
        );
    }
}