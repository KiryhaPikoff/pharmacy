package com.ulstu.pharmacy.pmmsl.classifire.facade;

import java.util.List;

public interface ClassifierFacade {

    /**
     * Подсчитывает веса для классов.
     * @param data данные, которые необходимо классифицировать.
     * @param classCount количество классов, на которые надо поделить данные.
     * @return почитанные веса для каждого класса.
     */
    List<double[]> classify(List<double[]> data, Integer classCount);
}