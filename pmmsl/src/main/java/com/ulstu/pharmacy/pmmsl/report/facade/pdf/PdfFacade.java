package com.ulstu.pharmacy.pmmsl.report.facade.pdf;

import com.ulstu.pharmacy.pmmsl.report.facade.util.Style;

import java.io.File;

/**
 *  Фасад для работы с .pdf файлами.
 */
public interface PdfFacade {

    /** Инициализирует новый документ. */
    void createNew();

    /** Добавляет текст в файл. */
    void append(String text);

    /** Добавляет текст в файл. Пользовательский стиль. */
    void append(String text, Style style);

    /** Перевод каретки на новую строку. */
    void newLine();

    /** Перевод из модели в файл. */
    File createFile();
}