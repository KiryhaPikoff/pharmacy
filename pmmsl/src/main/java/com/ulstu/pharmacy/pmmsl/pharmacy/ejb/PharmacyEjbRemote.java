package com.ulstu.pharmacy.pmmsl.pharmacy.ejb;


import com.ulstu.pharmacy.pmmsl.common.exception.CrudOperationException;
import com.ulstu.pharmacy.pmmsl.common.exception.MedicamentDiscountException;
import com.ulstu.pharmacy.pmmsl.medicament.view.MedicamentViewModel;
import com.ulstu.pharmacy.pmmsl.pharmacy.view.PharmacyViewModel;

import javax.ejb.Remote;
import java.util.List;
import java.util.Map;

@Remote
public interface PharmacyEjbRemote {

    /**
     * Получение списка всех аптек.
     */
    List<PharmacyViewModel> getAll();

    /**
     * Метод создания аптеки. Если pharmacyName == null, будет дано название по умолчанию.
     *
     * @param pharmacyName название аптеки.
     */
    void create(String pharmacyName) throws CrudOperationException;

    /**
     * Метод получение списка медикаментов со всех аптек.
     * Включаются только те медикаменты, количество которых в аптеке > 0.
     */
    Map<MedicamentViewModel, Integer> getPharmacyMedicamentsInStock();

    /**
     * Проверка наличия медикамента в аптеках в необходимом количестве.
     *
     * @param medicamentId проверяемый медикамент.
     * @param count        необходимое количество медикамента.
     * @return true, если медикамет есть в нужном количестве в аптеках (в сумме)
     * false, в противном случае (или если параметр medicament == null).
     */
    boolean isMedicamentInStocks(Long medicamentId, Integer count);

    /**
     * Метод списания медикамента с аптек.
     *
     * @param medicamentId Id списываемого медикамент.
     * @param count        количество медикамента для списывания.
     * @throws MedicamentDiscountException возникает, если
     *                                     1) В аптеках не хватает в общей сумме колчиства списываемого медикамента.
     *                                     2) Медикамента не существует.
     *                                     3) Параметры null.
     */
    void discountMedicament(Long medicamentId, Integer count) throws MedicamentDiscountException;

    /**
     * Метод списания медикамента с аптек.
     * @param medicamentsWithCounts
     *  Ключ - Id списываемого медикамент.
     *  Значение - количество медикамента для списывания.
     * @throws MedicamentDiscountException возникает, если
     *                                     1) В аптеках не хватает в общей сумме колчиства списываемого медикамента.
     *                                     2) Медикамента не существует.
     *                                     3) Параметры null.
     */
    void discountMedicaments(Map<Long, Integer> medicamentsWithCounts) throws MedicamentDiscountException;
}