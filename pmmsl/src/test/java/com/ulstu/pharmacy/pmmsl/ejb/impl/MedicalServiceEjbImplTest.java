package com.ulstu.pharmacy.pmmsl.ejb.impl;

import com.ulstu.pharmacy.pmmsl.common.exception.CrudOperationException;
import com.ulstu.pharmacy.pmmsl.medicament.dao.MedicamentDao;
import com.ulstu.pharmacy.pmmsl.medicament.entity.Medicament;
import com.ulstu.pharmacy.pmmsl.medicament.mapper.MedicamentMapperImpl;
import com.ulstu.pharmacy.pmmsl.medservice.dao.MedicalServiceDao;
import com.ulstu.pharmacy.pmmsl.medservice.dao.MedicalServiceDaoImpl;
import com.ulstu.pharmacy.pmmsl.medservice.ejb.MedicalServiceEjbImpl;
import com.ulstu.pharmacy.pmmsl.medservice.entity.MedicalService;
import com.ulstu.pharmacy.pmmsl.medservice.entity.MedicamentMedicalService;
import com.ulstu.pharmacy.pmmsl.medservice.mapper.MedicalServiceMapperImpl;
import com.ulstu.pharmacy.pmmsl.medservice.mapper.MedicamentMedicalServiceMapper;
import com.ulstu.pharmacy.pmmsl.medservice.mapper.MedicamentMedicalServiceMapperImpl;
import com.ulstu.pharmacy.pmmsl.medservice.view.MedicalServiceViewModel;
import com.ulstu.pharmacy.pmmsl.pharmacy.binding.MedicamentCountBindingModel;
import com.ulstu.pharmacy.pmmsl.pharmacy.ejb.PharmacyEjbImpl;
import com.ulstu.pharmacy.pmmsl.pharmacy.ejb.PharmacyEjbLocal;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@RunWith(MockitoJUnitRunner.class)
public class MedicalServiceEjbImplTest {

    private final PharmacyEjbLocal pharmacyEjbLocal = Mockito.mock(PharmacyEjbImpl.class);

    private final MedicalServiceDao medicalServiceDao = Mockito.mock(MedicalServiceDaoImpl.class);

    private final MedicamentDao medicamentDao = Mockito.mock(MedicamentDao.class);

    private final MedicamentMapperImpl medicamentMapper = new MedicamentMapperImpl();

    private final MedicamentMedicalServiceMapper medicamentMedicalServiceMapper =
            new MedicamentMedicalServiceMapperImpl(medicamentMapper);

    private final MedicalServiceMapperImpl medicalServiceMapper = new MedicalServiceMapperImpl(medicamentMedicalServiceMapper);

    private final MedicalServiceEjbImpl medicalServiceEjbRemote = new MedicalServiceEjbImpl(
            pharmacyEjbLocal,
            medicalServiceDao,
            medicamentDao,
            medicalServiceMapper
    );

    @Before
    public void init() {
        List<MedicalService> expectedMedicalServices = this.initMedicalServices();
        List<Medicament> expectedMedicaments = this.initMedicaments();

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
        List<MedicalService> expectedMedicalServices = this.initMedicalServices();

        List<MedicalService> actualMedivalServices = medicalServiceEjbRemote.getAll()
                .stream()
                .map(medicalServiceMapper::toEntity)
                .collect(Collectors.toList());

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

        List<MedicalServiceViewModel> actualMedicalServices = medicalServiceEjbRemote.getFromDateToDate(
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

        List<MedicalServiceViewModel> actualMedicalServices = medicalServiceEjbRemote.getFromDateToDate(
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
     * Списание услуги, медикаментов которой хватает на складах, и
     * переданные значения корректны.
     */
    public void discountMedServiceWithMedicamentsInStock() {
        MedicalService discountedMedicalService = this.initMedicalServices().get(0);

        Mockito.when(pharmacyEjbLocal.isMedicamentInStocks(Mockito.anyObject()))
                .thenReturn(true);

        Mockito.when(medicalServiceDao.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito.when(medicalServiceDao.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(discountedMedicalService));

        medicalServiceEjbRemote.discount(discountedMedicalService.getId());

        Mockito.verify(pharmacyEjbLocal, Mockito.times(1))
                .discountMedicaments(Mockito.anyObject());

        Mockito.verify(medicalServiceDao).update(Mockito.anyObject());

        var medicamentCountArgumentCaptor = ArgumentCaptor.forClass(Set.class);
        Mockito.verify(pharmacyEjbLocal).discountMedicaments(medicamentCountArgumentCaptor.capture());
        var actualMedicamentsWithCount = medicamentCountArgumentCaptor.getValue();

        var expectedMedicamentsWithCount = discountedMedicalService.getMedicamentMedicalServices()
                .stream()
                .map(medicamentMedicalService -> MedicamentCountBindingModel.builder()
                        .medicamentId(medicamentMedicalService.getMedicament().getId())
                        .count(medicamentMedicalService.getCount())
                        .build()
                )
                .collect(Collectors.toSet());

        Assert.assertEquals(
                expectedMedicamentsWithCount,
                actualMedicamentsWithCount
        );

        ArgumentCaptor<MedicalService> medicalServiceArgumentCaptor = ArgumentCaptor.forClass(MedicalService.class);
        Mockito.verify(medicalServiceDao).update(medicalServiceArgumentCaptor.capture());
        MedicalService actualMedicalService = medicalServiceArgumentCaptor.getValue();

        Assert.assertNotNull(actualMedicalService.getProvisionDate());
    }

    @Test(expected = CrudOperationException.class)
    /**
     * Списание услуги, медикаментов которой нехватает на складах, и
     * переданные значения корректны.
     */
    public void discountMedServiceWithMedicamentsNotInStock() {
        MedicalService discountedMedicalService = this.initMedicalServices().get(0);

        Mockito.when(pharmacyEjbLocal.isMedicamentInStocks(Mockito.anyObject()))
                .thenReturn(false);

        medicalServiceEjbRemote.discount(discountedMedicalService.getId());

        Mockito.verify(pharmacyEjbLocal, Mockito.times(discountedMedicalService.getMedicamentMedicalServices().size()))
                .isMedicamentInStocks(Mockito.anyObject());
        Mockito.verify(pharmacyEjbLocal, Mockito.never())
                .discountMedicaments(Mockito.anyObject());
    }

    @Test
    /**
     * Создание услуги на основе списка соответствий Медикамент - Количество.
     */
    public void createWithCorrectedValues() {
        MedicalService expectedCreatedMedicalService = this.initMedicalServices().get(0);
        expectedCreatedMedicalService.setId(null);

        Mockito.when(pharmacyEjbLocal.isMedicamentInStocks(Mockito.anyObject()))
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

        medicalServiceEjbRemote.create(argumentsForCreating);

        Mockito.verify(medicalServiceDao).save(Mockito.anyObject());

        ArgumentCaptor<MedicalService> medicalServiceArgumentCaptor = ArgumentCaptor.forClass(MedicalService.class);
        Mockito.verify(medicalServiceDao).save(medicalServiceArgumentCaptor.capture());
        MedicalService actualMedicalService = medicalServiceArgumentCaptor.getValue();

        Assert.assertEquals(expectedCreatedMedicalService, actualMedicalService);
    }

    @Test(expected = CrudOperationException.class)
    /**
     * В случае, если методу была передана мапа с ссылкой null - то
     * метод генерирует исключение.
     */
    public void createWithNullMap() {
        medicalServiceEjbRemote.create(null);

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

        medicalServiceEjbRemote.create(argumentsForCreating);

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

        MedicalService medicalService_0 = new MedicalService.Builder()
                .medicamentMedicalServices(medicamentMedicalServices.subList(0, 2))
                .build();
        medicalService_0.setId(0L);
        medicalServices.add(medicalService_0);

        MedicalService medicalService_1 = new MedicalService.Builder()
                .medicamentMedicalServices(medicamentMedicalServices.subList(1, 2))
                .build();
        medicalService_1.setId(1L);
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

        MedicamentMedicalService medicamentMedicalService_0 = new MedicamentMedicalService.Builder()
                .medicament(medicaments.get(0))
                .count(5)
                .price(medicaments.get(0).getPrice())
                .build();
        medicamentMedicalService_0.setId(0L);
        medicamentMedicalServices.add(medicamentMedicalService_0);

        MedicamentMedicalService medicamentMedicalService_1 = new MedicamentMedicalService.Builder()
                .medicament(medicaments.get(1))
                .count(10)
                .price(medicaments.get(1).getPrice())
                .build();
        medicamentMedicalService_1.setId(1L);
        medicamentMedicalServices.add(medicamentMedicalService_1);

        MedicamentMedicalService medicamentMedicalService_2 = new MedicamentMedicalService.Builder()
                .medicament(medicaments.get(2))
                .count(10)
                .price(medicaments.get(2).getPrice())
                .build();
        medicamentMedicalService_2.setId(2L);
        medicamentMedicalServices.add(medicamentMedicalService_2);

        return medicamentMedicalServices;
    }

    @Ignore
    public List<Medicament> initMedicaments() {
        List<Medicament> medicaments = new LinkedList<>();

        Medicament medicament_0 = new Medicament.Builder()
                .name("Нулевой медикамент")
                .price(new BigDecimal(15L))
                .build();
        medicament_0.setId(0L);
        medicaments.add(medicament_0);

        Medicament medicament_1 = new Medicament.Builder()
                .name("Первый медикамент")
                .price(new BigDecimal(15L))
                .build();
        medicament_1.setId(1L);
        medicaments.add(medicament_1);

        Medicament medicament_2 = new Medicament.Builder()
                .name("Второй медикамент")
                .price(new BigDecimal(15L))
                .build();
        medicament_2.setId(2L);
        medicaments.add(medicament_2);

        Medicament medicament_3 = new Medicament.Builder()
                .name("Третий медикамент")
                .price(new BigDecimal(15L))
                .build();
        medicament_3.setId(3L);
        medicaments.add(medicament_3);

        return medicaments;
    }
}