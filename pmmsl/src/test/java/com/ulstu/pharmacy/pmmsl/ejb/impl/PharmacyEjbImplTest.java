package com.ulstu.pharmacy.pmmsl.ejb.impl;

import com.ulstu.pharmacy.pmmsl.common.exception.CrudOperationException;
import com.ulstu.pharmacy.pmmsl.common.exception.MedicamentDiscountException;
import com.ulstu.pharmacy.pmmsl.medicament.dao.MedicamentDaoImpl;
import com.ulstu.pharmacy.pmmsl.medicament.entity.Medicament;
import com.ulstu.pharmacy.pmmsl.medicament.mapper.MedicamentMapperImpl;
import com.ulstu.pharmacy.pmmsl.medicament.view.MedicamentViewModel;
import com.ulstu.pharmacy.pmmsl.pharmacy.dao.PharmacyDaoImpl;
import com.ulstu.pharmacy.pmmsl.pharmacy.dao.PharmacyMedicamentDao;
import com.ulstu.pharmacy.pmmsl.pharmacy.dao.PharmacyMedicamentDaoImpl;
import com.ulstu.pharmacy.pmmsl.pharmacy.ejb.PharmacyEjbImpl;
import com.ulstu.pharmacy.pmmsl.pharmacy.entity.Pharmacy;
import com.ulstu.pharmacy.pmmsl.pharmacy.entity.PharmacyMedicament;
import com.ulstu.pharmacy.pmmsl.pharmacy.mapper.PharmacyMapperImpl;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RunWith(MockitoJUnitRunner.class)
public class PharmacyEjbImplTest {

    @Mock
    private PharmacyDaoImpl pharmacyDao;

    @Mock
    private PharmacyMedicamentDaoImpl pharmacyMedicamentDao;

    @Mock
    private MedicamentDaoImpl medicamentDao;

    @Mock
    private PharmacyMapperImpl pharmacyMapper;

    @Mock
    private MedicamentMapperImpl medicamentMapper;

    @InjectMocks
    private PharmacyEjbImpl pharmacyEjbRemote;

    @Test
    public void simple() {
        Assert.assertEquals(4, 2 + 2);
    }

    @Before
    public void init() {
        List<Pharmacy> expectedPharmacys = this.initPharmacys();
        List<PharmacyMedicament> pharmacyMedicaments = this.initPharmacyMedicaments();

        Mockito.when(pharmacyDao.getAll())
                .thenReturn(expectedPharmacys);

        Mockito.when(pharmacyMedicamentDao.getAll())
                .thenReturn(pharmacyMedicaments);

        Mockito.when(pharmacyMapper.toEntity(Mockito.anyObject()))
                .thenCallRealMethod();

        Mockito.when(pharmacyMapper.toViewModel(Mockito.anyObject()))
                .thenCallRealMethod();

        Mockito.when(medicamentMapper.toEntity(Mockito.anyObject()))
                .thenCallRealMethod();

        Mockito.when(medicamentMapper.toViewModel(Mockito.anyObject()))
                .thenCallRealMethod();

    }

    @Test
    /**
     * Проверка на то, что метод getAll()
     * вызовет 1 раз getAll() у pharmcayDao
     * вызовет N раз toViewModel() у pharmacyMapper
     * где N - количество апткек в хранилище.
     * Проверяется соответствие количества аптек в возвращаемом
     * списке и количеством аптек в хранилище.
     */
    public void getAll() {
        List<Pharmacy> expectedPharmacys = this.initPharmacys();

        List<Pharmacy> actualPharmacys = pharmacyEjbRemote.getAll()
                .stream()
                .map(pharmacyMapper::toEntity)
                .collect(Collectors.toList());

        Mockito.verify(pharmacyDao, Mockito.times(1))
                .getAll();

        Mockito.verify(pharmacyMapper, Mockito.times(expectedPharmacys.size()))
                .toViewModel(Mockito.anyObject());

        Assert.assertEquals(
                expectedPharmacys,
                actualPharmacys
        );
    }

    @Test
    /**
     * Проверка на то, что метод create(String name) где name != null
     * вызовет 1 раз метод save у pharmacyDao и у аптеки будет
     * name - переданное агрументом название.
     * Здесь CrudOperationException по логике создания не выкидывается,
     * он нужен для обёртки ошибок базы данных.
     */
    public void createWithSpecificString() throws CrudOperationException {
        String pharmacyName = "pharm";

        pharmacyEjbRemote.create(pharmacyName);

        Mockito.verify(pharmacyDao, Mockito.times(1))
                .save(Mockito.anyObject());

        ArgumentCaptor<Pharmacy> pharmacyArgumentCaptor = ArgumentCaptor.forClass(Pharmacy.class);
        Mockito.verify(pharmacyDao).save(pharmacyArgumentCaptor.capture());
        Pharmacy savedPharmacy = pharmacyArgumentCaptor.getValue();

        Assert.assertEquals(pharmacyName, savedPharmacy.getName());
    }

    @Test
    /**
     * Проверка на то, что метод create(String name) где name == null
     * вызовет 1 раз метод save у pharmacyDao и у аптеки будет
     * name - значение по умолчанию.
     * Здесь CrudOperationException по логике создания не выкидывается,
     * он нужен для обёртки ошибок базы данных.
     */
    public void createWithNullString() throws CrudOperationException {
        String defaultPharmName = "default";

        pharmacyEjbRemote.create(null);

        Mockito.verify(pharmacyDao, Mockito.times(1))
                .save(Mockito.anyObject());

        ArgumentCaptor<Pharmacy> pharmacyArgumentCaptor = ArgumentCaptor.forClass(Pharmacy.class);
        Mockito.verify(pharmacyDao).save(pharmacyArgumentCaptor.capture());
        Pharmacy savedPharmacy = pharmacyArgumentCaptor.getValue();

        Assert.assertEquals(defaultPharmName, savedPharmacy.getName());
    }

    @Test
    /**
     * Проверка на корректную группировку медикаментов с разных аптек.
     * Выход - множество сопоставлений медикамент - количество, где медикаменты будет уникальными,
     * а количество - сумма количества медикамента со всех аптек.
     */
    public void getPharmacyMedicamentsInStock() {
        List<Medicament> medicaments = this.initMedicaments();

        Map<MedicamentViewModel, Integer> expectedMap = new HashMap<>();
        expectedMap.put(medicamentMapper.toViewModel(medicaments.get(0)), 5);
        expectedMap.put(medicamentMapper.toViewModel(medicaments.get(1)), 12);
        expectedMap.put(medicamentMapper.toViewModel(medicaments.get(3)), 9);

        Assert.assertEquals(expectedMap, pharmacyEjbRemote.getPharmacyMedicamentsInStock());

        Mockito.verify(pharmacyMedicamentDao, Mockito.times(1))
                .getAll();
    }

    @Test
    public void isMedicamentInStocks() {
        Mockito.when(medicamentDao.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Assert.assertTrue(pharmacyEjbRemote.isMedicamentInStocks(0L, 5));
        Assert.assertTrue(pharmacyEjbRemote.isMedicamentInStocks(1L, 8));
        Assert.assertFalse(pharmacyEjbRemote.isMedicamentInStocks(0L, 10));
        Assert.assertFalse(pharmacyEjbRemote.isMedicamentInStocks(2L, 1));

        Assert.assertTrue(pharmacyEjbRemote.isMedicamentInStocks(0L, 0));
        Assert.assertFalse(pharmacyEjbRemote.isMedicamentInStocks(0L, -15));
        Assert.assertFalse(pharmacyEjbRemote.isMedicamentInStocks(null, 0));
    }

    @Test
    /**
     * Проверка на корректное списание медикаментов с аптеки.
     * (Представленны корректные случаи, которые точно должны отработать)
     */
    public void discountMedicamentThatIsInStock() throws MedicamentDiscountException {
        List<Pharmacy> pharmacys = this.initPharmacys();
        List<Medicament> medicaments = this.initMedicaments();
        List<PharmacyMedicament> pharmacyMedicaments = this.initPharmacyMedicaments();

        Mockito.when(medicamentDao.existsById(Mockito.anyLong()))
                .thenReturn(true);

        pharmacyEjbRemote.discountMedicament(0L, 5);
        pharmacyMedicaments.get(0).setCount(0);
        pharmacyEjbRemote.discountMedicament(1L, 1);
        pharmacyMedicaments.get(1).setCount(1);
        pharmacyEjbRemote.discountMedicament(3L, 8);
        pharmacyMedicaments.get(2).setCount(0);
        pharmacyMedicaments.get(4).setCount(1);
        // При списывании нуля медикаментов, ничего не произойдёт. Но это корректный ввод и исключение не возникнет.
        pharmacyEjbRemote.discountMedicament(3L, 0);

        ArgumentCaptor<PharmacyMedicament> pharmacyMedicamentArgumentCaptor = ArgumentCaptor.forClass(PharmacyMedicament.class);
        Mockito.verify(pharmacyMedicamentDao, Mockito.times(4)).update(pharmacyMedicamentArgumentCaptor.capture());

        List<PharmacyMedicament> savedPharmacyMedicaments = pharmacyMedicamentArgumentCaptor.getAllValues();

        List<PharmacyMedicament> expectedForSavingPharmacyMedicaments = new LinkedList<>();
        expectedForSavingPharmacyMedicaments.add(PharmacyMedicament.builder().pharmacy(pharmacys.get(0)).medicament(medicaments.get(0)).count(0).build());
        expectedForSavingPharmacyMedicaments.add(PharmacyMedicament.builder().pharmacy(pharmacys.get(0)).medicament(medicaments.get(1)).count(1).build());
        expectedForSavingPharmacyMedicaments.add(PharmacyMedicament.builder().pharmacy(pharmacys.get(0)).medicament(medicaments.get(3)).count(0).build());
        expectedForSavingPharmacyMedicaments.add(PharmacyMedicament.builder().pharmacy(pharmacys.get(2)).medicament(medicaments.get(3)).count(1).build());

        Assert.assertEquals(expectedForSavingPharmacyMedicaments, savedPharmacyMedicaments);
    }

    @Test(expected = MedicamentDiscountException.class)
    /**
     * Проверка на корректное списание медикаментов с аптеки.
     * Исключительная ситуация, в которой агрументы корректны,
     * но не хватает количества медикаментов в аптеках для списания.
     */
    public void discountMedicamentThatNotInStock() throws MedicamentDiscountException {
        List<Medicament> medicaments = this.initMedicaments();
        List<PharmacyMedicament> pharmacyMedicaments = this.initPharmacyMedicaments();

        Mockito.when(medicamentDao.existsById(1L))
                .thenReturn(false);

        pharmacyEjbRemote.discountMedicament(1L, 15);

        Mockito.verify(pharmacyMedicamentDao, Mockito.never()).update(Mockito.anyObject());
    }

    @Test(expected = MedicamentDiscountException.class)
    /**
     * Проверка на корректное списание медикаментов с аптеки.
     * Исключительная ситуация, когда количество отрицательное.
     */
    public void discountMedicamentThatCountNotCorrected() throws MedicamentDiscountException {
        List<PharmacyMedicament> pharmacyMedicaments = this.initPharmacyMedicaments();

        PharmacyMedicamentDao pharmacyMedicamentDao = Mockito.mock(PharmacyMedicamentDaoImpl.class);
        Mockito.when(pharmacyMedicamentDao.getAll())
                .thenReturn(pharmacyMedicaments);

        pharmacyEjbRemote.discountMedicament(1L, -15);

        Mockito.verify(pharmacyMedicamentDao, Mockito.never()).update(Mockito.anyObject());
    }

    @Test(expected = MedicamentDiscountException.class)
    /**
     * Проверка на корректное списание медикаментов с аптеки.
     * Исключительная ситуация, когда переданный медикамент null.
     */
    public void discountMedicamentThatNull() throws MedicamentDiscountException {
        pharmacyEjbRemote.discountMedicament(null, 14);

        Mockito.verify(pharmacyMedicamentDao, Mockito.never()).update(Mockito.anyObject());
    }

    /**
     * Аптека 0:
     * Медикамент 0 - 5 шт.
     * Медикамент 1 - 2 шт.
     * Медикамент 3 - 7 шт.
     * <p>
     * Аптека 1:
     * Пустая.
     * <p>
     * Аптека 2:
     * Медикамент 1 - 10 шт.
     * Медикамент 3 - 2 шт.
     * <p>
     * Итого:
     * Медикамент 0 - 5  шт.
     * Медикамент 1 - 12 шт.
     * Медикамент 2 - 0  шт.
     * Медикамент 3 - 9  шт.
     */
    @Ignore
    public List<PharmacyMedicament> initPharmacyMedicaments() {
        List<Pharmacy> pharmacys = this.initPharmacys();
        List<Medicament> medicaments = this.initMedicaments();
        List<PharmacyMedicament> pharmacyMedicaments = new LinkedList<>();

        pharmacyMedicaments.add(PharmacyMedicament.builder().pharmacy(pharmacys.get(0)).medicament(medicaments.get(0)).count(5).build());
        pharmacyMedicaments.add(PharmacyMedicament.builder().pharmacy(pharmacys.get(0)).medicament(medicaments.get(1)).count(2).build());
        pharmacyMedicaments.add(PharmacyMedicament.builder().pharmacy(pharmacys.get(0)).medicament(medicaments.get(3)).count(7).build());
        pharmacyMedicaments.add(PharmacyMedicament.builder().pharmacy(pharmacys.get(2)).medicament(medicaments.get(1)).count(10).build());
        pharmacyMedicaments.add(PharmacyMedicament.builder().pharmacy(pharmacys.get(2)).medicament(medicaments.get(3)).count(2).build());

        return pharmacyMedicaments;
    }

    @Ignore
    public List<Pharmacy> initPharmacys() {
        List<Pharmacy> pharmacys = new LinkedList<>();
        pharmacys.add(Pharmacy.builder().name("Нулевая апткека").build());
        pharmacys.add(Pharmacy.builder().name("Первая апткека").build());
        pharmacys.add(Pharmacy.builder().name("Вторая апткека").build());
        return pharmacys;
    }

    @Ignore
    public List<Medicament> initMedicaments() {
        List<Medicament> medicaments = new LinkedList<>();

        Medicament medicament_0 = Medicament.builder().name("Нулевой медикамент").build();
        medicament_0.setId(0L);
        medicaments.add(medicament_0);

        Medicament medicament_1 = Medicament.builder().name("Первый медикамент").build();
        medicament_1.setId(1L);
        medicaments.add(medicament_1);

        Medicament medicament_2 = Medicament.builder().name("Второй медикамент").build();
        medicament_2.setId(2L);
        medicaments.add(medicament_2);

        Medicament medicament_3 = Medicament.builder().name("Третий медикамент").build();
        medicament_3.setId(3L);
        medicaments.add(medicament_3);

        return medicaments;
    }
}