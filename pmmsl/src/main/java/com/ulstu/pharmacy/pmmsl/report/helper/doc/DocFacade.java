package com.ulstu.pharmacy.pmmsl.report.helper.doc;

import com.ulstu.pharmacy.pmmsl.report.helper.util.Style;

import java.io.File;

/**
 *  Фасад для работы с .doc файлами.
 */
public interface DocFacade {

    /** Добавляет текст в файл. */
    void append(String text);

    /** Добавляет текст в файл. Пользовательский стиль. */
    void append(String text, Style style);

    /** Перевод каретки на новую строку. */
    void newLine();

    /** Перевод из модели в файл. */
    File createFile();
}