package com.ulstu.pharmacy.pmmsl.medicament.checker;

import com.ulstu.pharmacy.pmmsl.medicament.binding.MedicamentBindingModel;

public interface MedicamentChecker {

    /**
     * Проверка параметров модели, необходимых для создания медикамента.
     * @param medicamentBindingModel
     * @return
     */
    String createCheck(MedicamentBindingModel medicamentBindingModel);

    /**
     * Проверка параметров модели, необходимых для обновления медикамента.
     * @param medicamentBindingModel
     * @return
     */
    String updateCheck(MedicamentBindingModel medicamentBindingModel);

    /**
     * Проверка условий, необходимых для удаления медикамета.
     * @param medicamentId id удаляемого медикамента.
     * @return
     */
    String deleteCheck(Long medicamentId);
}