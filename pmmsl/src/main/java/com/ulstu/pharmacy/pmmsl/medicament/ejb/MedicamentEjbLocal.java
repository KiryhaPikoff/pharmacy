package com.ulstu.pharmacy.pmmsl.medicament.ejb;


import com.ulstu.pharmacy.pmmsl.medicament.binding.MedicamentBindingModel;
import com.ulstu.pharmacy.pmmsl.medicament.view.MedicamentViewModel;

import javax.ejb.Local;
import java.util.List;

@Local
public interface MedicamentEjbLocal {

    /**
     * Метод получения медикамента по его id.
     */
    MedicamentViewModel getById(Long id);

    /**
     * Метод получения списка всех медикаментов.
     */
    List<MedicamentViewModel> getAll();

    /**
     * Метод предназначени для сохранения (если id = null) или
     * обновления (id отличен от null) медикамента.
     * @param medicamentBindingModel
     */
    void createOrUpdate(MedicamentBindingModel medicamentBindingModel);

    /**
     * Метод удаления медикамента по его id.
     */
    void delete(Long id);
}