package com.ulstu.pharmacy.report.facade.pdf;

import java.io.File;

/**
 *  Фасад для работы с .pdf файлами.
 */
public interface PdfFacade {

    /** Инициализирует новый документ. */
    void createNew();

    /** Добавляет текст в файл. */
    void append(String text);

    /** Перевод каретки на новую строку. */
    void newLine();

    /** Перевод из модели в файл. */
    File createFile();
}