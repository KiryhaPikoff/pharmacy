package com.ulstu.pharmacy.pmmsl.ejb.impl;

import com.google.common.collect.Streams;
import com.ulstu.pharmacy.pmmsl.common.exception.CrudOperationException;
import com.ulstu.pharmacy.pmmsl.common.exception.MedicamentDiscountException;
import com.ulstu.pharmacy.pmmsl.medicament.entity.Medicament;
import com.ulstu.pharmacy.pmmsl.medicament.mapper.MedicamentMapperImpl;
import com.ulstu.pharmacy.pmmsl.medicament.view.MedicamentViewModel;
import com.ulstu.pharmacy.pmmsl.medservice.dao.MedicalServiceDaoImpl;
import com.ulstu.pharmacy.pmmsl.medservice.ejb.MedicalServiceEjbRemote;
import com.ulstu.pharmacy.pmmsl.medservice.entity.MedicalService;
import com.ulstu.pharmacy.pmmsl.medservice.entity.MedicamentMedicalService;
import com.ulstu.pharmacy.pmmsl.medservice.mapper.MedicalServiceMapperImpl;
import com.ulstu.pharmacy.pmmsl.medservice.view.MedicalServiceViewModel;
import com.ulstu.pharmacy.pmmsl.pharmacy.ejb.PharmacyEjbImpl;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RunWith(MockitoJUnitRunner.class)
public class MedicalServiceEjbImplTest {

    @Mock
    private PharmacyEjbImpl pharmacyEjbRemote;

    @Mock
    private MedicalServiceDaoImpl medicalServiceDao;

    @Mock
    private MedicalServiceMapperImpl medicalServiceMapper;

    @Mock
    private MedicamentMapperImpl medicamentMapper;

    @InjectMocks
    private MedicalServiceEjbRemote medicalServiceEjbRemote;

    @Before
    public void init() {
        List<MedicalService> expectedMedicalServices = this.initMedicalServices();

        Mockito.when(medicalServiceDao.getAll())
                .thenReturn(expectedMedicalServices);

        Mockito.when(medicalServiceMapper.toEntity(Mockito.anyObject()))
                .thenCallRealMethod();

        Mockito.when(medicalServiceMapper.toViewModel(Mockito.anyObject()))
                .thenCallRealMethod();
    }

    @Test
    public void simple() {
        Assert.assertEquals(4, 2 + 2);
    }

    @Test
    /**
     * Проверка получения списка всех услуг.
     */
    public void getAll() {
        List<MedicalService> expectedMedicalServices = this.initMedicalServices();

        List<MedicalService> actualMedivalServices = medicalServiceEjbRemote.getAll()
                .stream()
                .map(medicalServiceMapper::toEntity)
                .collect(Collectors.toList());

        Mockito.verify(medicalServiceDao, Mockito.times(1))
                .getAll();

        Mockito.verify(medicalServiceMapper, Mockito.times(expectedMedicalServices.size()))
                .toViewModel(Mockito.anyObject());

        Assert.assertEquals(
                expectedMedicalServices,
                actualMedivalServices
        );
    }

    @Test
    /**
     * Корректная отработка метода с корректными входными данными.
     */
    public void getFromDateToDateCorrectedValues() {
        List<MedicalService> expectedMedicalServices = this.initMedicalServices();

        Timestamp expectedFromDate = Timestamp.valueOf("2009-06-04 18:13:56");
        Timestamp expectedToDay = Timestamp.valueOf("2020-10-11 14:12:51");

        List<MedicalServiceViewModel> actualMedicalServices = medicalServiceEjbRemote.getFromDateToDate(
                expectedFromDate,
                expectedToDay
        );

        Mockito.verify(medicalServiceDao, Mockito.times(1))
                .getFromDateTo(Mockito.anyObject(), Mockito.anyObject());

        Mockito.verify(medicalServiceMapper, Mockito.times(expectedMedicalServices.size()))
                .toViewModel(Mockito.anyObject());

        ArgumentCaptor<Timestamp> timestampArgumentCaptor = ArgumentCaptor.forClass(Timestamp.class);
        Mockito.verify(medicalServiceDao).getFromDateTo(timestampArgumentCaptor.capture(), timestampArgumentCaptor.capture());
        List<Timestamp> timestamps = timestampArgumentCaptor.getAllValues();

        Assert.assertEquals(expectedFromDate, timestamps.get(0));
        Assert.assertEquals(expectedToDay, timestamps.get(1));

        Assert.assertEquals(
                expectedMedicalServices.stream()
                        .map(medicalServiceMapper::toViewModel)
                        .collect(Collectors.toList()),
                actualMedicalServices
        );
    }

    @Test
    /**
     * Если методу было подано хотябы одно значение null - возвращается пустой
     * список услгу.
     */
    public void getFromDateToDateIncorrectedValues() {
        List<MedicalService> expectedMedicalServices = this.initMedicalServices();

        List<MedicalServiceViewModel> actualMedicalServices = medicalServiceEjbRemote.getFromDateToDate(
                null,
                null
        );

        Mockito.verify(medicalServiceDao, Mockito.never())
                .getFromDateTo(Mockito.anyObject(), Mockito.anyObject());

        Assert.assertNotNull(actualMedicalServices);
        Assert.assertTrue(actualMedicalServices.isEmpty());
    }

    @Test
    /**
     * Списание услуги, медикаментов которой хватает на складах, и
     * переданные значения корректны.
     */
    public void discountMedServiceWithMedicamentsInStock() throws MedicamentDiscountException {
        MedicalService discountedMedicalService = this.initMedicalServices().get(0);

        Mockito.when(pharmacyEjbRemote.isMedicamentInStocks(Mockito.anyObject(), Mockito.anyInt())).thenReturn(true);

        medicalServiceEjbRemote.discount(discountedMedicalService.getId());

        Mockito.verify(pharmacyEjbRemote, Mockito.times(discountedMedicalService.getMedicamentMedicalServices().size()))
                .isMedicamentInStocks(Mockito.anyObject(), Mockito.anyInt());
        Mockito.verify(pharmacyEjbRemote, Mockito.times(discountedMedicalService.getMedicamentMedicalServices().size()))
                .discountMedicament(Mockito.anyObject(), Mockito.anyInt());

        Mockito.verify(medicalServiceDao).update(Mockito.anyObject());

        ArgumentCaptor<MedicamentViewModel> medicamentViewModelArgumentCaptor = ArgumentCaptor.forClass(MedicamentViewModel.class);
        ArgumentCaptor<Integer> countArgumentCaptor = ArgumentCaptor.forClass(Integer.class);

    //    Mockito.verify(pharmacyEjbRemote).isMedicamentInStocks(medicamentViewModelArgumentCaptor.capture(), countArgumentCaptor.capture());

        List<MedicamentViewModel> actualMedicamentViewModels = medicamentViewModelArgumentCaptor.getAllValues();
        List<Integer> actualMedicamentsCount = countArgumentCaptor.getAllValues();

        Map<MedicamentViewModel, Integer> actualMedicamentsWithCount = new HashMap<>();
        Streams.forEachPair(actualMedicamentViewModels.stream(), actualMedicamentsCount.stream(), actualMedicamentsWithCount::put);

        HashMap<MedicamentViewModel, Integer> expectedMedicamentsWithCount = new HashMap<>();
        for (MedicamentMedicalService medicamentMedicalService : discountedMedicalService.getMedicamentMedicalServices()) {
            expectedMedicamentsWithCount.put(
                    new MedicamentMapperImpl()
                            .toViewModel(medicamentMedicalService.getMedicament()),
                    medicamentMedicalService.getCount()
            );
        }

        Assert.assertEquals(
                expectedMedicamentsWithCount,
                actualMedicamentsWithCount
        );

        ArgumentCaptor<MedicalService> medicalServiceArgumentCaptor = ArgumentCaptor.forClass(MedicalService.class);
        MedicalService actualMedicalService = medicalServiceArgumentCaptor.getValue();

        Assert.assertNotNull(actualMedicalService.getProvisionDate());
    }

    @Test(expected = MedicamentDiscountException.class)
    /**
     * Списание услуги, медикаментов которой нехватает на складах, и
     * переданные значения корректны.
     */
    public void discountMedServiceWithMedicamentsNotInStock() throws MedicamentDiscountException {
        MedicalService discountedMedicalService = this.initMedicalServices().get(0);

        Mockito.when(pharmacyEjbRemote.isMedicamentInStocks(Mockito.anyObject(), Mockito.anyInt())).thenReturn(false);

        medicalServiceEjbRemote.discount(discountedMedicalService.getId());

        Mockito.verify(pharmacyEjbRemote, Mockito.times(discountedMedicalService.getMedicamentMedicalServices().size()))
                .isMedicamentInStocks(Mockito.anyObject(), Mockito.anyInt());
        Mockito.verify(pharmacyEjbRemote, Mockito.never())
                .discountMedicament(Mockito.anyObject(), Mockito.anyInt());
    }

    @Test
    /**
     * Создание услуги на основе списка соответствий Медикамент - Количество.
     */
    public void createWithCorrectedValues() throws CrudOperationException {
        MedicalService expectedCreatedMedicalService = this.initMedicalServices().get(0);
        expectedCreatedMedicalService.setId(null);

        Map<MedicamentViewModel, Integer> argumentsForCreating = new HashMap<>();
        for (MedicamentMedicalService medicamentMedicalService : expectedCreatedMedicalService.getMedicamentMedicalServices()) {
            argumentsForCreating.put(
                    new MedicamentMapperImpl()
                            .toViewModel(medicamentMedicalService.getMedicament()),
                    medicamentMedicalService.getCount()
            );
        }

    //    medicalServiceEjbRemote.create(argumentsForCreating);

        Mockito.verify(medicalServiceDao).save(Mockito.anyObject());

        ArgumentCaptor<MedicalService> medicalServiceArgumentCaptor = ArgumentCaptor.forClass(MedicalService.class);
        MedicalService actualMedicalService = medicalServiceArgumentCaptor.getValue();

        Assert.assertEquals(expectedCreatedMedicalService, actualMedicalService);
    }

    @Test(expected = CrudOperationException.class)
    /**
     * В случае, если методу была передана мапа с ссылкой null - то
     * метод генерирует исключение.
     */
    public void createWithNullMap() throws CrudOperationException {
        medicalServiceEjbRemote.create(null);

        Mockito.verify(medicalServiceDao, Mockito.never()).save(Mockito.anyObject());
    }

    @Test(expected = CrudOperationException.class)
    /**
     * В случае, если методу была передана мапа, которая содержит
     * хотябы один ключ или одно значение null - должно сгенерироваться исключение.
     */
    public void createWithNullIntoMap() throws CrudOperationException {
        MedicalService expectedCreatedMedicalService = this.initMedicalServices().get(0);
        expectedCreatedMedicalService.setId(null);

        Map<Long, Integer> argumentsForCreating = new HashMap<>();
        argumentsForCreating.put(null, null);

        medicalServiceEjbRemote.create(argumentsForCreating);

        Mockito.verify(medicalServiceDao, Mockito.never()).save(Mockito.anyObject());
    }

    @Before
    /**
     * МС_0:
     *      ММС_0
     *      ММС_1
     *
     * МС_1:
     *      ММС_2
     */
    public List<MedicalService> initMedicalServices() {
        List<MedicalService> medicalServices = new LinkedList<>();
        List<MedicamentMedicalService> medicamentMedicalServices = this.initMedicamentMedicalServices();

        MedicalService medicalService_0 = MedicalService.builder()
                .medicamentMedicalServices(medicamentMedicalServices.subList(0, 2))
                .build();
        medicalService_0.setId(0L);
        medicalServices.add(medicalService_0);

        MedicalService medicalService_1 = MedicalService.builder()
                .medicamentMedicalServices(medicamentMedicalServices.subList(2, 3))
                .build();
        medicalService_0.setId(1L);
        medicalServices.add(medicalService_1);

        return medicalServices;
    }

    @Before
    /**
     * ММС_0 = Медикамент 0 - 5  шт.
     * ММС_1 = Медикамент 1 - 10 шт.
     * ММС_2 = Медикамент 2 - 10 шт.
     */
    public List<MedicamentMedicalService> initMedicamentMedicalServices() {
        List<MedicamentMedicalService> medicamentMedicalServices = new LinkedList<>();
        List<Medicament> medicaments = this.initMedicaments();

        MedicamentMedicalService medicamentMedicalService_0 = MedicamentMedicalService.builder()
                .medicament(medicaments.get(0))
                .count(5)
                .build();
        medicamentMedicalService_0.setId(0L);
        medicamentMedicalServices.add(medicamentMedicalService_0);

        MedicamentMedicalService medicamentMedicalService_1 = MedicamentMedicalService.builder()
                .medicament(medicaments.get(1))
                .count(10)
                .build();
        medicamentMedicalService_1.setId(1L);
        medicamentMedicalServices.add(medicamentMedicalService_1);

        MedicamentMedicalService medicamentMedicalService_2 = MedicamentMedicalService.builder()
                .medicament(medicaments.get(2))
                .count(10)
                .build();
        medicamentMedicalService_2.setId(2L);
        medicamentMedicalServices.add(medicamentMedicalService_2);

        return medicamentMedicalServices;
    }

    @Before
    public List<Medicament> initMedicaments() {
        List<Medicament> medicaments = new LinkedList<>();

        Medicament medicament_0 = Medicament.builder().name("Нулевой медикамент").build();
        medicament_0.setId(0L);
        medicaments.add(medicament_0);

        Medicament medicament_1 = Medicament.builder().name("Первый медикамент").build();
        medicament_1.setId(0L);
        medicaments.add(medicament_1);

        Medicament medicament_2 = Medicament.builder().name("Второй медикамент").build();
        medicament_2.setId(0L);
        medicaments.add(medicament_2);

        Medicament medicament_3 = Medicament.builder().name("Третий медикамент").build();
        medicament_3.setId(0L);
        medicaments.add(medicament_3);

        return medicaments;
    }
}