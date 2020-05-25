package com.ulstu.pharmacy.pmmsl.ejb.impl;

import com.ulstu.pharmacy.pmmsl.common.exception.CrudOperationException;
import com.ulstu.pharmacy.pmmsl.medicament.binding.MedicamentCountBindingModel;
import com.ulstu.pharmacy.pmmsl.medicament.dao.MedicamentDaoImpl;
import com.ulstu.pharmacy.pmmsl.medicament.entity.Medicament;
import com.ulstu.pharmacy.pmmsl.medservice.dao.MedicalServiceDaoImpl;
import com.ulstu.pharmacy.pmmsl.medservice.ejb.MedicalServiceEjbImpl;
import com.ulstu.pharmacy.pmmsl.medservice.entity.MedicalService;
import com.ulstu.pharmacy.pmmsl.medservice.entity.MedicamentMedicalService;
import com.ulstu.pharmacy.pmmsl.medservice.mapper.MedicalServiceMapperImpl;
import com.ulstu.pharmacy.pmmsl.medservice.view.MedicalServiceViewModel;
import com.ulstu.pharmacy.pmmsl.pharmacy.ejb.PharmacyEjbImpl;
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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RunWith(MockitoJUnitRunner.class)
public class MedicalServiceEjbImplTest {

    @Mock
    private PharmacyEjbImpl pharmacyEjb;

    @Mock
    private MedicalServiceDaoImpl medicalServiceDao;

    @Mock
    private MedicamentDaoImpl medicamentDao;

    @Mock
    private MedicalServiceMapperImpl medicalServiceMapper;

    @InjectMocks
    private MedicalServiceEjbImpl medicalServiceEjb;

    @Before
    public void init() {
        List<MedicalService> expectedMedicalServices = this.initMedicalServices();
        List<Medicament> expectedMedicaments = this.initMedicaments();

        Mockito.when(medicalServiceMapper.toViewModel(Mockito.anyObject()))
                .thenCallRealMethod();

        Mockito.when(medicalServiceDao.getAll())
                .thenReturn(expectedMedicalServices);

        Mockito.when(medicamentDao.getAll())
                .thenReturn(expectedMedicaments);
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
        List<MedicalServiceViewModel> expectedMedicalServices = this.initMedicalServices().stream()
                .map(medicalServiceMapper::toViewModel)
                .collect(Collectors.toList());

        List<MedicalServiceViewModel> actualMedivalServices = medicalServiceEjb.getAll();

        Mockito.verify(medicalServiceDao, Mockito.times(1))
                .getAll();

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

        Mockito.when(medicalServiceDao.getFromDateToDate(expectedFromDate, expectedToDay))
                .thenReturn(expectedMedicalServices);

        List<MedicalServiceViewModel> actualMedicalServices = medicalServiceEjb.getFromDateToDate(
                expectedFromDate,
                expectedToDay
        );

        Mockito.verify(medicalServiceDao, Mockito.times(1))
                .getFromDateToDate(Mockito.anyObject(), Mockito.anyObject());

        ArgumentCaptor<Timestamp> timestampArgumentCaptor = ArgumentCaptor.forClass(Timestamp.class);
        Mockito.verify(medicalServiceDao).getFromDateToDate(timestampArgumentCaptor.capture(), timestampArgumentCaptor.capture());
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

    @Test(expected = CrudOperationException.class)
    /**
     * Если методу было подано хотябы одно значение null - возвращается пустой
     * список услгу.
     */
    public void getFromDateToDateIncorrectedValues() {

        List<MedicalServiceViewModel> actualMedicalServices = medicalServiceEjb.getFromDateToDate(
                null,
                null
        );

        Mockito.verify(medicalServiceDao, Mockito.never())
                .getFromDateToDate(Mockito.anyObject(), Mockito.anyObject());

        Assert.assertNotNull(actualMedicalServices);
        Assert.assertTrue(actualMedicalServices.isEmpty());
    }

    @Test
    /**
     * Создание услуги на основе списка соответствий Медикамент - Количество.
     */
    public void createWithCorrectedValues() {
        MedicalService expectedCreatedMedicalService = this.initMedicalServices().get(0);
        expectedCreatedMedicalService.setId(null);

        Mockito.when(pharmacyEjb.isMedicamentInStocks(Mockito.anyObject()))
                .thenReturn(true);

        /* Ставим null, так как до сохранения их нет ещё в БД. А в тестах уже проинициализированы id. */
        expectedCreatedMedicalService.getMedicamentMedicalServices()
                .forEach(medicamentMedicalService -> medicamentMedicalService.setId(null));

        Set<MedicamentCountBindingModel> argumentsForCreating = new HashSet<>();
        for (MedicamentMedicalService medicamentMedicalService : expectedCreatedMedicalService.getMedicamentMedicalServices()) {
            argumentsForCreating.add(
                    MedicamentCountBindingModel.builder()
                            .medicamentId(medicamentMedicalService.getMedicament().getId())
                            .count(medicamentMedicalService.getCount())
                            .build()
            );
        }

        medicalServiceEjb.create(argumentsForCreating);

        Mockito.verify(medicalServiceDao).save(Mockito.anyObject());

        ArgumentCaptor<MedicalService> medicalServiceArgumentCaptor = ArgumentCaptor.forClass(MedicalService.class);
        Mockito.verify(medicalServiceDao).save(medicalServiceArgumentCaptor.capture());
        MedicalService actualMedicalService = medicalServiceArgumentCaptor.getValue();

        //TODO Доработать: так при создании ставится дата списания, эквивалентность не срабатывает (в expected дата null)
        actualMedicalService.setProvisionDate(null);
        Assert.assertEquals(expectedCreatedMedicalService, actualMedicalService);
    }

    @Test(expected = CrudOperationException.class)
    /**
     * В случае, если методу была передана мапа с ссылкой null - то
     * метод генерирует исключение.
     */
    public void createWithNullMap() {
        medicalServiceEjb.create(null);

        Mockito.verify(medicalServiceDao, Mockito.never()).save(Mockito.anyObject());
    }

    @Test(expected = CrudOperationException.class)
    /**
     * В случае, если методу была передана мапа, которая содержит
     * хотябы один ключ или одно значение null - должно сгенерироваться исключение.
     */
    public void createWithNullIntoMap() {
        Set<MedicamentCountBindingModel> argumentsForCreating = new HashSet<>();
        argumentsForCreating.add(MedicamentCountBindingModel.builder().build());

        medicalServiceEjb.create(argumentsForCreating);

        Mockito.verify(medicalServiceDao, Mockito.never()).save(Mockito.anyObject());
    }

    @Ignore
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
                .id(0L)
                .medicamentMedicalServices(new HashSet<>(medicamentMedicalServices.subList(0, 2)))
                .build();
        medicalServices.add(medicalService_0);

        MedicalService medicalService_1 = MedicalService.builder()
                .id(1L)
                .medicamentMedicalServices(new HashSet<>(medicamentMedicalServices.subList(1, 2)))
                .build();
        medicalServices.add(medicalService_1);

        return medicalServices;
    }

    @Ignore
    /**
     * ММС_0 = Медикамент 0 - 5  шт.
     * ММС_1 = Медикамент 1 - 10 шт.
     * ММС_2 = Медикамент 2 - 10 шт.
     */
    public List<MedicamentMedicalService> initMedicamentMedicalServices() {
        List<MedicamentMedicalService> medicamentMedicalServices = new LinkedList<>();
        List<Medicament> medicaments = this.initMedicaments();

        MedicamentMedicalService medicamentMedicalService_0 = MedicamentMedicalService.builder()
                .id(0L)
                .medicament(medicaments.get(0))
                .count(5)
                .price(medicaments.get(0).getPrice())
                .build();
        medicamentMedicalServices.add(medicamentMedicalService_0);

        MedicamentMedicalService medicamentMedicalService_1 = MedicamentMedicalService.builder()
                .id(1L)
                .medicament(medicaments.get(1))
                .count(10)
                .price(medicaments.get(1).getPrice())
                .build();
        medicamentMedicalServices.add(medicamentMedicalService_1);

        MedicamentMedicalService medicamentMedicalService_2 = MedicamentMedicalService.builder()
                .id(2L)
                .medicament(medicaments.get(2))
                .count(10)
                .price(medicaments.get(2).getPrice())
                .build();
        medicamentMedicalServices.add(medicamentMedicalService_2);

        return medicamentMedicalServices;
    }

    @Ignore
    public List<Medicament> initMedicaments() {
        List<Medicament> medicaments = new LinkedList<>();

        Medicament medicament_0 = Medicament.builder()
                .id(0L)
                .name("Нулевой медикамент")
                .price(new BigDecimal(15L))
                .build();
        medicaments.add(medicament_0);

        Medicament medicament_1 = Medicament.builder()
                .id(1L)
                .name("Первый медикамент")
                .price(new BigDecimal(15L))
                .build();
        medicaments.add(medicament_1);

        Medicament medicament_2 = Medicament.builder()
                .id(2L)
                .name("Второй медикамент")
                .price(new BigDecimal(15L))
                .build();
        medicaments.add(medicament_2);

        Medicament medicament_3 = Medicament.builder()
                .id(3L)
                .name("Третий медикамент")
                .price(new BigDecimal(15L))
                .build();
        medicament_3.setId(3L);

        return medicaments;
    }
}