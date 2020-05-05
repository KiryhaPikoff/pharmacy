package com.ulstu.pharmacy.pmmsl.report.facade.xls;

import com.ulstu.pharmacy.pmmsl.report.facade.xls.helper.CellProperty;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Objects;

public class XlsFacadeImpl implements XlsFacade {


    private HSSFWorkbook workbook;

    private HSSFSheet sheet;

    /**
     * Инициализирует новый документ.
     */
    @Override
    public void createNew() {
        this.workbook = new HSSFWorkbook();
        this.sheet = this.workbook.createSheet();
    }

    /**
     * Добавляет текст в ячейку. Ячейка в формате БуквыЧисло
     *
     * @param text
     * @param cellProperty
     */
    @Override
    public void write(String text, CellProperty cellProperty) {
        this.createCell(cellProperty)
                .setCellValue(text);
    }

    private HSSFCell createCell(CellProperty cellProperty) {
        HSSFRow row = this.sheet.getRow(cellProperty.getRow());
        row = Objects.isNull(row) ? this.sheet.createRow(cellProperty.getRow()) : row;

        HSSFCell cell = row.getCell(cellProperty.getColumn());
        cell = Objects.isNull(cell) ? row.createCell(cellProperty.getColumn()) : cell;

        return cell;
    }

    /**
     * Перевод из модели в файл.
     */
    @Override
    public File createFile() {
        File file = new File(this.generateFileTitle());
        try {
            workbook.write(
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
}