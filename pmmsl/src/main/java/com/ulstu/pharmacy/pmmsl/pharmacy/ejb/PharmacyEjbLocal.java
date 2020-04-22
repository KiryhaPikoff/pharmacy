package com.ulstu.pharmacy.pmmsl.pharmacy.ejb;


import com.ulstu.pharmacy.pmmsl.medicament.view.MedicamentViewModel;
import com.ulstu.pharmacy.pmmsl.medicament.binding.MedicamentCountBindingModel;
import com.ulstu.pharmacy.pmmsl.pharmacy.binding.PharmacyBindingModel;
import com.ulstu.pharmacy.pmmsl.pharmacy.view.PharmacyViewModel;

import javax.ejb.Local;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Local
public interface PharmacyEjbLocal {

    /**
     * Получение списка всех аптек.
     */
    List<PharmacyViewModel> getAll();

    /**
     * Метод создания аптеки.
     * Если pharmacyName == null, будет дано название по умолчанию.
     * @param pharmacyBindingModel
     */
    void create(PharmacyBindingModel pharmacyBindingModel);

    /**
     * Метод получение списка медикаментов со всех аптек.
     * Включаются только те медикаменты, количество которых в аптеке > 0.
     */
    Map<MedicamentViewModel, Integer> getPharmacyMedicamentsInStock();

    /**
     * Проверка наличия медикамента в аптеках в необходимом количестве.
     *
     * @param medicamentCountBindingModel
     * @return true, если медикамет есть в нужном количестве в аптеках (в сумме)
     * false, в противном случае (или если параметр medicament == null).
     */
    boolean isMedicamentInStocks(MedicamentCountBindingModel medicamentCountBindingModel);


    /**
     * Метод списания медикаментов с аптек.
     * @param medicamentCountBindingModels множество медикаментов с количеством для списывания.
     */
    void discountMedicaments(Set<MedicamentCountBindingModel> medicamentCountBindingModels);
}