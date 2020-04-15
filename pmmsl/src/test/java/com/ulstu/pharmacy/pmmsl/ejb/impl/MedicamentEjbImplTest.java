package com.ulstu.pharmacy.pmmsl.ejb.impl;

import com.ulstu.pharmacy.pmmsl.common.exception.CrudOperationException;
import com.ulstu.pharmacy.pmmsl.medicament.binding.MedicamentBindingModel;
import com.ulstu.pharmacy.pmmsl.medicament.dao.MedicamentDaoImpl;
import com.ulstu.pharmacy.pmmsl.medicament.ejb.MedicamentEjbImpl;
import com.ulstu.pharmacy.pmmsl.medicament.entity.Medicament;
import com.ulstu.pharmacy.pmmsl.medicament.mapper.MedicamentMapperImpl;
import com.ulstu.pharmacy.pmmsl.medicament.view.MedicamentViewModel;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RunWith(MockitoJUnitRunner.class)
public class MedicamentEjbImplTest {

    @Mock
    private MedicamentDaoImpl medicamentDao;

    @Mock
    private MedicamentMapperImpl medicamentMapper;

    @InjectMocks
    private MedicamentEjbImpl medicamentEjbRemote;

    @Before
    public void init() {
        List<Medicament> expectedMedicaments = this.initMedicaments();

        Mockito.when(medicamentDao.getAll())
                .thenReturn(expectedMedicaments);

        Mockito.when(medicamentMapper.toEntity(Mockito.anyObject()))
                .thenCallRealMethod();

        Mockito.when(medicamentMapper.toViewModel(Mockito.anyObject()))
                .thenCallRealMethod();

        Mockito.when(medicamentDao.existByName(null)).thenReturn(false);
    }

    @Test
    public void simple() {
        Assert.assertEquals(4, 2 + 2);
    }

    @Test
    /**
     * Получение медикамета по id для которого есть запись в БД.
     */
    public void getByIdMedicamntExist() {
        Long idExist = 15L;

        MedicamentViewModel expectedMedicament = medicamentMapper.toViewModel(this.initMedicaments().get(2));

        Optional<Medicament> returnValue = Optional.of(medicamentMapper.toEntity(expectedMedicament));
        Mockito.when(medicamentDao.findById(idExist))
                .thenReturn(returnValue);

        Assert.assertEquals(expectedMedicament, medicamentEjbRemote.getById(idExist));

        Mockito.verify(medicamentDao, Mockito.times(1))
                .findById(15L);
    }

    @Test
    /**
     * Попытка получить медикамент по id, а медикамента с таким id нет в БД
     * Будет возвращен null-object.
     */
    public void getByIdMedicamntNotExist() {
        Long idNonExist = 0L;

        Mockito.when(medicamentDao.findById(idNonExist))
                .thenReturn(Optional.empty());

        Assert.assertNotNull(medicamentEjbRemote.getById(idNonExist));

        Mockito.verify(medicamentDao, Mockito.times(1))
                .findById(Mockito.anyLong());
    }

    @Test
    /**
     * Попытка получить медикамент по id = null
     * Будет возвращен null-object.
     */
    public void getByINulld() {
        Mockito.when(medicamentDao.findById(null))
                .thenReturn(Optional.empty());

        Assert.assertNotNull(medicamentEjbRemote.getById(null));

        Mockito.verify(medicamentDao, Mockito.times(1))
                .findById(null);
    }

    @Test
    public void getAll() {
        List<Medicament> expectedMedicaments = this.initMedicaments();

        List<Medicament> actualMedicaments = medicamentEjbRemote.getAll()
                .stream()
                .map(medicamentMapper::toEntity)
                .collect(Collectors.toList());

        Mockito.verify(medicamentDao, Mockito.times(1))
                .getAll();

        Mockito.verify(medicamentMapper, Mockito.times(expectedMedicaments.size()))
                .toViewModel(Mockito.anyObject());

        Assert.assertEquals(
                expectedMedicaments,
                actualMedicaments
        );
    }

    @Test
    /**
     * Добавление корректной записи о медикаменте. В случае, если
     * медикамента с таким названием нет в базе.
     */
    public void createCorrectedValuesThatNotInStock() throws CrudOperationException {

        Mockito.when(medicamentDao.existByName(Mockito.anyString()))
                .thenReturn(false);

        Medicament expectedMedicament = Medicament.builder()
                .name("Медикамент T")
                .price(new BigDecimal(10))
                .contraindications("Противопоказания T")
                .description("Описание T")
                .instruction("Инструкция T")
                .build();

        medicamentEjbRemote.addOrUpdate(
                MedicamentBindingModel.builder()
                .name(expectedMedicament.getName())
                .description(expectedMedicament.getDescription())
                .contraindications(expectedMedicament.getContraindications())
                .instruction(expectedMedicament.getInstruction())
                .price(expectedMedicament.getPrice())
                .build()
        );

        Mockito.verify(medicamentDao, Mockito.times(1))
                .existByName(Mockito.anyString());

        Mockito.verify(medicamentDao, Mockito.times(1))
                .save(Mockito.anyObject());

        ArgumentCaptor<Medicament> medicamentArgumentCaptor = ArgumentCaptor.forClass(Medicament.class);
        Mockito.verify(medicamentDao).save(medicamentArgumentCaptor.capture());
        Medicament actualMedicament = medicamentArgumentCaptor.getValue();

        Assert.assertEquals(expectedMedicament, actualMedicament);
    }

    @Test(expected = CrudOperationException.class)
    /**
     * Добавление корректной записи о медикаменте. В случае, если
     * медикамент с таким названием есть в базе.
     */
    public void createMedicametnThatNameExist() throws CrudOperationException {

        Mockito.when(medicamentDao.existByName(Mockito.anyString()))
                .thenReturn(true);

        Medicament expectedMedicament = Medicament.builder()
                .name("Медикамент T")
                .price(new BigDecimal(10))
                .contraindications("Противопоказания T")
                .description("Описание T")
                .instruction("Инструкция T")
                .build();

        medicamentEjbRemote.addOrUpdate(
                MedicamentBindingModel.builder()
                        .name(expectedMedicament.getName())
                        .description(expectedMedicament.getDescription())
                        .contraindications(expectedMedicament.getContraindications())
                        .instruction(expectedMedicament.getInstruction())
                        .price(expectedMedicament.getPrice())
                        .build()
        );

        Mockito.verify(medicamentDao, Mockito.times(1))
                .existByName("Медикамент T");

        Mockito.verify(medicamentDao, Mockito.never())
                .save(Mockito.anyObject());
    }

    @Test(expected = CrudOperationException.class)
    /**
     * Добавление некорректной записи о медикаменте.
     * Предполагается, что метод save не вызовется, а
     * выкенет исключение CrudOperationException.
     */
    public void createIncorrectedValuesOthersArgsNull() throws CrudOperationException {

        Mockito.when(medicamentDao.existByName(Mockito.anyString()))
                .thenReturn(false);

        Medicament expectedMedicament = Medicament.builder()
                .name("Name")
                .price(null)
                .contraindications(null)
                .description(null)
                .instruction(null)
                .build();

        medicamentEjbRemote.addOrUpdate(
                MedicamentBindingModel.builder()
                        .name(expectedMedicament.getName())
                        .description(expectedMedicament.getDescription())
                        .contraindications(expectedMedicament.getContraindications())
                        .instruction(expectedMedicament.getInstruction())
                        .price(expectedMedicament.getPrice())
                        .build()
        );

        Mockito.verify(medicamentDao, Mockito.times(1))
                .existByName("Name");

        Mockito.verify(medicamentDao, Mockito.never())
                .save(Mockito.anyObject());
    }

    @Test(expected = CrudOperationException.class)
    /**
     * Добавление некорректной записи о медикаменте.
     * Предполагается, что вызовется метод проверки на существование
     * в базе медикамента с именем null и возникнет исключение.
     */
    public void createIncorrectedValuesNameNull() throws CrudOperationException {

        Mockito.when(medicamentDao.existByName(Mockito.anyString()))
                .thenReturn(false);

        Medicament expectedMedicament = Medicament.builder()
                .name(null)
                .price(null)
                .contraindications(null)
                .description(null)
                .instruction(null)
                .build();

        medicamentEjbRemote.addOrUpdate(
                MedicamentBindingModel.builder()
                        .name(expectedMedicament.getName())
                        .description(expectedMedicament.getDescription())
                        .contraindications(expectedMedicament.getContraindications())
                        .instruction(expectedMedicament.getInstruction())
                        .price(expectedMedicament.getPrice())
                        .build()
        );

        Mockito.verify(medicamentDao, Mockito.never())
                .save(Mockito.anyObject());
    }

    @Test
    /**
     * Обновление медикамента, данные корректные. Медикамента с новым именем нет в БД.
     */
    public void updateCorrectedValuesThatNotInStock() throws CrudOperationException {

        Mockito.when(medicamentDao.existByName("Медикамент T"))
                .thenReturn(false);

        MedicamentViewModel expectedMedicament = MedicamentViewModel.builder()
                .id(100L)
                .name("Медикамент T")
                .price(new BigDecimal(10))
                .contraindications("Противопоказания T")
                .description("Описание T")
                .instruction("Инструкция T")
                .build();

        medicamentEjbRemote.addOrUpdate(
                MedicamentBindingModel.builder()
                        .id(expectedMedicament.getId())
                        .name(expectedMedicament.getName())
                        .description(expectedMedicament.getDescription())
                        .contraindications(expectedMedicament.getContraindications())
                        .instruction(expectedMedicament.getInstruction())
                        .price(expectedMedicament.getPrice())
                        .build()
        );

        Mockito.verify(medicamentDao, Mockito.times(1))
                .existByName("Медикамент T");

        Mockito.verify(medicamentDao, Mockito.times(1))
                .update(Mockito.anyObject());

        ArgumentCaptor<Medicament> medicamentArgumentCaptor = ArgumentCaptor.forClass(Medicament.class);
        Mockito.verify(medicamentDao).update(medicamentArgumentCaptor.capture());
        Medicament actualMedicament = medicamentArgumentCaptor.getValue();

        Assert.assertEquals(
                medicamentMapper.toEntity(expectedMedicament),
                actualMedicament
        );
    }

    @Test(expected = CrudOperationException.class)
    /**
     * Обновление медикамента, данные корректные. Медикамент с новым именем есть в БД.
     */
    public void updateCorrectedValuesThatInStock() throws CrudOperationException {

        Mockito.when(medicamentDao.existByName("Медикамент T"))
                .thenReturn(true);

        MedicamentViewModel expectedMedicament = MedicamentViewModel.builder()
                .id(100L)
                .name("Медикамент T")
                .price(new BigDecimal(10))
                .contraindications("Противопоказания T")
                .description("Описание T")
                .instruction("Инструкция T")
                .build();

        medicamentEjbRemote.addOrUpdate(
                MedicamentBindingModel.builder()
                        .id(expectedMedicament.getId())
                        .name(expectedMedicament.getName())
                        .description(expectedMedicament.getDescription())
                        .contraindications(expectedMedicament.getContraindications())
                        .instruction(expectedMedicament.getInstruction())
                        .price(expectedMedicament.getPrice())
                        .build()
        );

        Mockito.verify(medicamentDao, Mockito.times(1))
                .existByName("Медикамент T");

        Mockito.verify(medicamentDao, Mockito.never())
                .update(Mockito.anyObject());
    }

    @Test(expected = CrudOperationException.class)
    /**
     * Обновление медикамента с null полями, кроме name.
     */
    public void updateIncorrectedValuesWithNotNullNameOthresNull() throws CrudOperationException {

        medicamentEjbRemote.addOrUpdate(
                MedicamentBindingModel.builder()
                        .id(100L)
                        .name("Name")
                        .description(null)
                        .contraindications(null)
                        .instruction(null)
                        .price(null)
                        .build()
        );

        Mockito.verify(medicamentDao, Mockito.times(1))
                .existByName("Name");

        Mockito.verify(medicamentDao, Mockito.never())
                .update(Mockito.anyObject());
    }

    @Test(expected = CrudOperationException.class)
    public void updateIncorrectedValuesWithValues() throws CrudOperationException {

        medicamentEjbRemote.addOrUpdate(
                MedicamentBindingModel.builder()
                        .id(100L)
                        .name(null)
                        .description(null)
                        .contraindications(null)
                        .instruction(null)
                        .price(null)
                        .build()
        );

        Mockito.verify(medicamentDao, Mockito.never())
                .update(Mockito.anyObject());
    }

    @Test(expected = CrudOperationException.class)
    public void updateOrCreateNullValue() throws CrudOperationException {

        medicamentEjbRemote.addOrUpdate(null);

        Mockito.verify(medicamentDao, Mockito.never())
                .update(Mockito.anyObject());
    }

    @Test
    /**
     * Проверка на корректное удаление медикамента с id, запись для которого
     * существует в БД.
     */
    public void deleteExitingMedicament() throws CrudOperationException {
        Long idExistMedicament = 15L;

        Mockito.when(medicamentDao.existsById(idExistMedicament))
                .thenReturn(true);

        medicamentEjbRemote.delete(idExistMedicament);

        Mockito.verify(medicamentDao, Mockito.times(1))
                .deleteById(Mockito.anyObject());

        ArgumentCaptor<Long> longArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(medicamentDao).deleteById(longArgumentCaptor.capture());
        Long actualId = longArgumentCaptor.getValue();

        Assert.assertEquals(
                idExistMedicament,
                actualId
        );
    }

    @Test(expected = CrudOperationException.class)
    /**
     * Попытка удалить несуществующий медикамент.
     */
    public void deleteNotExitingMedicament() throws CrudOperationException {
        Long idNotExistMedicament = 100L;

        Mockito.when(medicamentDao.existsById(idNotExistMedicament))
                .thenReturn(false);

        medicamentEjbRemote.delete(idNotExistMedicament);

        Mockito.verify(medicamentDao, Mockito.times(1))
                .deleteById(Mockito.anyObject());

        ArgumentCaptor<Long> longArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(medicamentDao).deleteById(longArgumentCaptor.capture());
        Long actualId = longArgumentCaptor.getValue();

        Assert.assertEquals(
                idNotExistMedicament,
                actualId
        );
    }

    @Ignore
    public List<Medicament> initMedicaments() {
        List<Medicament> medicaments = new LinkedList<>();
        medicaments.add(
                Medicament.builder()
                        .name("Медикамент 0")
                        .price(new BigDecimal(100))
                        .contraindications("Противопоказания 0")
                        .description("Описание 0")
                        .instruction("Инструкция 0")
                        .build()
        );
        medicaments.add(
                Medicament.builder()
                        .name("Медикамент 1")
                        .price(new BigDecimal(50))
                        .contraindications("Противопоказания 1")
                        .description("Описание 1")
                        .instruction("Инструкция 1")
                        .build()
        );
        medicaments.add(
                Medicament.builder()
                        .name("Медикамент 2")
                        .price(new BigDecimal(30))
                        .contraindications("Противопоказания 2")
                        .description("Описание 2")
                        .instruction("Инструкция 2")
                        .build()
        );
        medicaments.add(
                Medicament.builder()
                        .name("Медикамент 3")
                        .price(new BigDecimal(70))
                        .contraindications("Противопоказания 3")
                        .description("Описание 3")
                        .instruction("Инструкция 3")
                        .build()
        );
        return medicaments;
    }
}