package com.ulstu.pharmacy.pmmsl.medicament.ejb;


import com.ulstu.pharmacy.pmmsl.common.exception.CrudOperationException;
import com.ulstu.pharmacy.pmmsl.medicament.view.MedicamentViewModel;

import javax.ejb.Remote;
import java.math.BigDecimal;
import java.util.List;

@Remote
public interface MedicamentEjbRemote {

    /**
     * Метод получения медикамента по его id.
     */
    MedicamentViewModel getById(Long id);

    /**
     * Метод получения списка всех медикаментов.
     */
    List<MedicamentViewModel> getAll();

    /**
     * Метод создания медикамента.
     *
     * @param name              название медикамента.
     * @param description       описание медикамента.
     * @param contraindications противопоказания.
     * @param instruction       инструкция по применению.
     * @param price             цена медикамнта.
     */
    void create(String name,
                String description,
                String contraindications,
                String instruction,
                BigDecimal price) throws CrudOperationException;

    /**
     * Метод изменения медикамента.
     */
    void update(MedicamentViewModel medicamentViewModel) throws CrudOperationException;

    /**
     * Метод удаления медикамента по его id.
     */
    void delete(Long id) throws CrudOperationException;
}