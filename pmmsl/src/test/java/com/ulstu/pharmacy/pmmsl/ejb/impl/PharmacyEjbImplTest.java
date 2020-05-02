package com.ulstu.pharmacy.pmmsl.ejb.impl;

import com.ulstu.pharmacy.pmmsl.common.exception.CrudOperationException;
import com.ulstu.pharmacy.pmmsl.common.exception.MedicamentWriteOffException;
import com.ulstu.pharmacy.pmmsl.medicament.binding.MedicamentCountBindingModel;
import com.ulstu.pharmacy.pmmsl.medicament.dao.MedicamentDaoImpl;
import com.ulstu.pharmacy.pmmsl.medicament.entity.Medicament;
import com.ulstu.pharmacy.pmmsl.medicament.mapper.MedicamentMapperImpl;
import com.ulstu.pharmacy.pmmsl.medicament.view.MedicamentViewModel;
import com.ulstu.pharmacy.pmmsl.pharmacy.binding.PharmacyBindingModel;
import com.ulstu.pharmacy.pmmsl.pharmacy.dao.PharmacyDaoImpl;
import com.ulstu.pharmacy.pmmsl.pharmacy.dao.PharmacyMedicamentDao;
import com.ulstu.pharmacy.pmmsl.pharmacy.dao.PharmacyMedicamentDaoImpl;
import com.ulstu.pharmacy.pmmsl.pharmacy.ejb.PharmacyEjbImpl;
import com.ulstu.pharmacy.pmmsl.pharmacy.entity.Pharmacy;
import com.ulstu.pharmacy.pmmsl.pharmacy.entity.PharmacyMedicament;
import com.ulstu.pharmacy.pmmsl.pharmacy.mapper.PharmacyMapperImpl;
import com.ulstu.pharmacy.pmmsl.pharmacy.view.PharmacyViewModel;
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

import java.util.*;
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
    private PharmacyEjbImpl pharmacyEjb;

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

        Mockito.when(pharmacyMapper.toViewModel(Mockito.anyObject()))
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
        List<PharmacyViewModel> expectedPharmacys = this.initPharmacys().stream()
                .map(pharmacyMapper::toViewModel)
                .collect(Collectors.toList());

        List<PharmacyViewModel> actualPharmacys = pharmacyEjb.getAll();

        Mockito.verify(pharmacyDao, Mockito.times(1))
                .getAll();

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
    public void createWithSpecificString() {
        String pharmacyName = "pharm";

        pharmacyEjb.create(
                PharmacyBindingModel.builder().name(pharmacyName).build()
        );

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
    public void createWithNullString() {
        String defaultPharmName = "default";

        pharmacyEjb.create(PharmacyBindingModel.builder().name(null).build());

        Mockito.verify(pharmacyDao, Mockito.times(1))
                .save(Mockito.anyObject());

        ArgumentCaptor<Pharmacy> pharmacyArgumentCaptor = ArgumentCaptor.forClass(Pharmacy.class);
        Mockito.verify(pharmacyDao).save(pharmacyArgumentCaptor.capture());
        Pharmacy savedPharmacy = pharmacyArgumentCaptor.getValue();

        Assert.assertEquals(defaultPharmName, savedPharmacy.getName());
    }

    @Test(expected = CrudOperationException.class)
    /**
     * Проверка на то, что метод create(String name) где name == null
     * вызовет 1 раз метод save у pharmacyDao и у аптеки будет
     * name - значение по умолчанию.
     * Здесь CrudOperationException по логике создания не выкидывается,
     * он нужен для обёртки ошибок базы данных.
     */
    public void createWithNullArgument() {
        pharmacyEjb.create(null);

        Mockito.verify(pharmacyDao, Mockito.never())
                .save(Mockito.anyObject());
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

        Assert.assertEquals(expectedMap, pharmacyEjb.getPharmacyMedicamentsInStock());

        Mockito.verify(pharmacyMedicamentDao, Mockito.times(1))
                .getAll();
    }

    @Test
    public void isMedicamentInStocks() {
        Mockito.when(medicamentDao.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Assert.assertTrue(pharmacyEjb.isMedicamentInStocks(
                MedicamentCountBindingModel.builder().medicamentId(0L).count(5).build()));
        Assert.assertTrue(pharmacyEjb.isMedicamentInStocks(
                MedicamentCountBindingModel.builder().medicamentId(1L).count(8).build()));
        Assert.assertFalse(pharmacyEjb.isMedicamentInStocks(
                MedicamentCountBindingModel.builder().medicamentId(0L).count(10).build()));
        Assert.assertFalse(pharmacyEjb.isMedicamentInStocks(
                MedicamentCountBindingModel.builder().medicamentId(2L).count(1).build()));

        Assert.assertTrue(pharmacyEjb.isMedicamentInStocks(
                MedicamentCountBindingModel.builder().medicamentId(0L).count(0).build()));
        Assert.assertFalse(pharmacyEjb.isMedicamentInStocks(
                MedicamentCountBindingModel.builder().medicamentId(0L).count(-15).build()));
        Assert.assertFalse(pharmacyEjb.isMedicamentInStocks(
                MedicamentCountBindingModel.builder().medicamentId(null).count(0).build()));
    }

    @Test
    /**
     * Проверка на корректное списание медикаментов с аптеки.
     * (Представленны корректные случаи, которые точно должны отработать)
     */
    public void writeOffMedicamentThatIsInStock() {
        List<Pharmacy> pharmacys = this.initPharmacys();
        List<Medicament> medicaments = this.initMedicaments();

        Mockito.when(medicamentDao.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Set<MedicamentCountBindingModel> writeOffSet = new HashSet<>();
        // При списывании нуля медикаментов, ничего не произойдёт. Но это корректный ввод и исключение не возникнет.
        writeOffSet.add(MedicamentCountBindingModel.builder().medicamentId(0L).count(0).build());
        writeOffSet.add(MedicamentCountBindingModel.builder().medicamentId(1L).count(1).build());
        writeOffSet.add(MedicamentCountBindingModel.builder().medicamentId(3L).count(8).build());

        this.pharmacyEjb.writeOffMedicaments(writeOffSet);

        ArgumentCaptor<PharmacyMedicament> pharmacyMedicamentArgumentCaptor = ArgumentCaptor.forClass(PharmacyMedicament.class);
        Mockito.verify(pharmacyMedicamentDao, Mockito.times(3)).update(pharmacyMedicamentArgumentCaptor.capture());

        List<PharmacyMedicament> savedPharmacyMedicaments = pharmacyMedicamentArgumentCaptor.getAllValues();

        List<PharmacyMedicament> expectedForSavingPharmacyMedicaments = new LinkedList<>();
        expectedForSavingPharmacyMedicaments.add(
                new PharmacyMedicament.Builder().pharmacy(pharmacys.get(0)).medicament(medicaments.get(1)).count(1).build()
        );
        expectedForSavingPharmacyMedicaments.add(
                new PharmacyMedicament.Builder().pharmacy(pharmacys.get(0)).medicament(medicaments.get(3)).count(0).build()
        );
        expectedForSavingPharmacyMedicaments.add(
                new PharmacyMedicament.Builder().pharmacy(pharmacys.get(2)).medicament(medicaments.get(3)).count(1).build()
        );

        List<MedicamentCountBindingModel> actual = savedPharmacyMedicaments.stream()
                .map(pharmacyMedicament -> MedicamentCountBindingModel.builder()
                        .medicamentId(pharmacyMedicament.getMedicament().getId())
                        .count(pharmacyMedicament.getCount())
                        .build())
                .collect(Collectors.toList());

        List<MedicamentCountBindingModel> expected = expectedForSavingPharmacyMedicaments.stream()
                .map(pharmacyMedicament -> MedicamentCountBindingModel.builder()
                        .medicamentId(pharmacyMedicament.getMedicament().getId())
                        .count(pharmacyMedicament.getCount())
                        .build())
                .collect(Collectors.toList());

        for (MedicamentCountBindingModel actualModel : actual) {
            expected.remove(actualModel);
        }

        Assert.assertEquals(0, expected.size());
    }

    @Test(expected = MedicamentWriteOffException.class)
    /**
     * Проверка на корректное списание медикаментов с аптеки.
     * Исключительная ситуация, в которой агрументы корректны,
     * но не хватает количества медикаментов в аптеках для списания.
     */
    public void writeOffMedicamentThatNotInStock() {

        Mockito.when(medicamentDao.existsById(1L))
                .thenReturn(false);

        pharmacyEjb.writeOffMedicaments(
                Set.of(
                        MedicamentCountBindingModel.builder().medicamentId(1L).count(15).build()
                )
        );

        Mockito.verify(pharmacyMedicamentDao, Mockito.never()).update(Mockito.anyObject());
    }

    @Test(expected = MedicamentWriteOffException.class)
    /**
     * Проверка на корректное списание медикаментов с аптеки.
     * Исключительная ситуация, когда количество отрицательное.
     */
    public void writeOffMedicamentThatCountNotCorrected() {
        List<PharmacyMedicament> pharmacyMedicaments = this.initPharmacyMedicaments();

        PharmacyMedicamentDao pharmacyMedicamentDao = Mockito.mock(PharmacyMedicamentDaoImpl.class);
        Mockito.when(pharmacyMedicamentDao.getAll())
                .thenReturn(pharmacyMedicaments);

        pharmacyEjb.writeOffMedicaments(
                Set.of(
                        MedicamentCountBindingModel.builder().medicamentId(1L).count(-15).build()
                )
        );

        Mockito.verify(pharmacyMedicamentDao, Mockito.never()).update(Mockito.anyObject());
    }

    @Test(expected = MedicamentWriteOffException.class)
    /*
     * Проверка на корректное списание медикаментов с аптеки.
     * Исключительная ситуация, когда переданный медикамент null.
     */
    public void writeOffMedicamentThatNull() {
        pharmacyEjb.writeOffMedicaments(
                Set.of(
                        MedicamentCountBindingModel.builder().medicamentId(null).count(15).build()
                )
        );

        Mockito.verify(pharmacyMedicamentDao, Mockito.never()).update(Mockito.anyObject());
    }

    @Test
    /**
     * Проверка на корректное пополнение медикаментов аптеки.
     */
    public void addMedicamentsCorrect() {
        List<Pharmacy> pharmacys = this.initPharmacys();
        List<Medicament> medicaments = this.initMedicaments();
        List<PharmacyMedicament> pharmacyMedicaments = this.initPharmacyMedicaments();

        Mockito.when(medicamentDao.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Set<MedicamentCountBindingModel> addSet = new HashSet<>();

        addSet.add(MedicamentCountBindingModel.builder().medicamentId(1L).count(3).build());
        addSet.add(MedicamentCountBindingModel.builder().medicamentId(2L).count(5).build());

        Mockito.when(pharmacyMedicamentDao.getByPharmacyAndMedicamentId(2L, 1L))
                .thenReturn(pharmacyMedicaments.get(3));

        this.pharmacyEjb.replenishMedicaments(2L, addSet);

        ArgumentCaptor<PharmacyMedicament> pharmacyMedicamentArgumentCaptorSave = ArgumentCaptor.forClass(PharmacyMedicament.class);
        Mockito.verify(pharmacyMedicamentDao).save(pharmacyMedicamentArgumentCaptorSave.capture());

        ArgumentCaptor<PharmacyMedicament> pharmacyMedicamentArgumentCaptorUpdate = ArgumentCaptor.forClass(PharmacyMedicament.class);
        Mockito.verify(pharmacyMedicamentDao).update(pharmacyMedicamentArgumentCaptorUpdate.capture());

        PharmacyMedicament savedPharmacyMedicament   = pharmacyMedicamentArgumentCaptorSave.getValue();
        PharmacyMedicament updatedPharmacyMedicament = pharmacyMedicamentArgumentCaptorUpdate.getValue();

        PharmacyMedicament expectedForSavePharmacyMedicament   = PharmacyMedicament.builder()
                .pharmacy(Pharmacy.builder().id(2L).build())
                .medicament(Medicament.builder().id(2L).build())
                .count(5)
                .build();
        PharmacyMedicament expectedForUpdatePharmacyMedicament = PharmacyMedicament.builder()
                .pharmacy(pharmacys.get(2))
                .medicament(medicaments.get(1))
                .count(13)
                .build();

        Assert.assertEquals(expectedForSavePharmacyMedicament, savedPharmacyMedicament);
        Assert.assertEquals(expectedForUpdatePharmacyMedicament, updatedPharmacyMedicament);
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

        pharmacyMedicaments.add(new PharmacyMedicament.Builder().pharmacy(pharmacys.get(0)).medicament(medicaments.get(0)).count(5).build());
        pharmacyMedicaments.add(new PharmacyMedicament.Builder().pharmacy(pharmacys.get(0)).medicament(medicaments.get(1)).count(2).build());
        pharmacyMedicaments.add(new PharmacyMedicament.Builder().pharmacy(pharmacys.get(0)).medicament(medicaments.get(3)).count(7).build());
        pharmacyMedicaments.add(new PharmacyMedicament.Builder().pharmacy(pharmacys.get(2)).medicament(medicaments.get(1)).count(10).build());
        pharmacyMedicaments.add(new PharmacyMedicament.Builder().pharmacy(pharmacys.get(2)).medicament(medicaments.get(3)).count(2).build());

        return pharmacyMedicaments;
    }

    @Ignore
    public List<Pharmacy> initPharmacys() {
        List<Pharmacy> pharmacys = new LinkedList<>();
        pharmacys.add(new Pharmacy.Builder().id(0L).name("Нулевая апткека").build());
        pharmacys.add(new Pharmacy.Builder().id(1L).name("Первая апткека").build());
        pharmacys.add(new Pharmacy.Builder().id(2L).name("Вторая апткека").build());
        return pharmacys;
    }

    @Ignore
    public List<Medicament> initMedicaments() {
        List<Medicament> medicaments = new LinkedList<>();

        Medicament medicament_0 = new Medicament.Builder().name("Нулевой медикамент").build();
        medicament_0.setId(0L);
        medicaments.add(medicament_0);

        Medicament medicament_1 = new Medicament.Builder().name("Первый медикамент").build();
        medicament_1.setId(1L);
        medicaments.add(medicament_1);

        Medicament medicament_2 = new Medicament.Builder().name("Второй медикамент").build();
        medicament_2.setId(2L);
        medicaments.add(medicament_2);

        Medicament medicament_3 = new Medicament.Builder().name("Третий медикамент").build();
        medicament_3.setId(3L);
        medicaments.add(medicament_3);

        return medicaments;
    }
}