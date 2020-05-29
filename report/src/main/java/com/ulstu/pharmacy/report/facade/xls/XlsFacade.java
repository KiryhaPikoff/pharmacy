package com.ulstu.pharmacy.report.facade.xls;

import com.ulstu.pharmacy.report.facade.xls.helper.CellProperty;

import java.io.File;

/**
 *  Фасад для работы с .xls файлами.
 */
public interface XlsFacade {

    /** Инициализирует новый документ. */
    void createNew();

    /** Добавляет текст в ячейку. Ячейка в формате БуквыЧисло */
    void write(String text, CellProperty cellProperty);

    /** Перевод из модели в файл. */
    File createFile();
}