package com.ulstu.pharmacy.pmmsl.report.helper.doc;

import com.ulstu.pharmacy.pmmsl.report.helper.util.Style;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.File;
import java.io.FileOutputStream;
import java.rmi.server.ExportException;

public class DocFacadeImpl implements DocFacade {

    private XWPFDocument document;

    private XWPFParagraph currentParagraph;

    private final Style DEFAULT_STYLE;

    public DocFacadeImpl() {
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
            //TODO логгер
            throw new RuntimeException("Can't create file..");
        }
        return file;
    }

    private String generateFileTitle() {
        return String.valueOf(System.currentTimeMillis());
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