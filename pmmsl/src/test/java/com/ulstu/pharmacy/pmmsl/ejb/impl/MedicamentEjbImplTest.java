package com.ulstu.pharmacy.pmmsl.ejb.impl;

import com.ulstu.pharmacy.pmmsl.common.exception.CrudOperationException;
import com.ulstu.pharmacy.pmmsl.medicament.dao.MedicamentDaoImpl;
import com.ulstu.pharmacy.pmmsl.medicament.ejb.MedicamentEjbRemote;
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
    private MedicamentEjbRemote medicamentEjbRemote;

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
    public void getById() {
        Long idExist = 15L;
        Long idNonExist = 0L;

        MedicamentViewModel expectedCorrectedMedicament = medicamentMapper.toViewModel(this.initMedicaments().get(2));

        Mockito.when(medicamentDao.findById(idExist))
                .thenReturn(Optional.ofNullable(medicamentMapper.toEntity(expectedCorrectedMedicament)));

        Mockito.when(medicamentDao.findById(idNonExist))
                .thenReturn(Optional.empty());

        Assert.assertEquals(expectedCorrectedMedicament, medicamentEjbRemote.getById(idExist));
        Assert.assertNull(medicamentEjbRemote.getById(idNonExist));
        Assert.assertNull(medicamentEjbRemote.getById(null));

        Mockito.verify(medicamentEjbRemote, Mockito.times(2))
                .getById(Mockito.anyLong());
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

        Mockito.when(medicamentDao.existByName(Mockito.anyString())).thenReturn(false);

        Medicament expectedMedicament = Medicament.builder()
                .name("Медикамент T")
                .price(new BigDecimal(10))
                .contraindications("Противопоказания T")
                .description("Описание T")
                .instruction("Инструкция T")
                .build();

        medicamentEjbRemote.create(
                expectedMedicament.getName(),
                expectedMedicament.getDescription(),
                expectedMedicament.getContraindications(),
                expectedMedicament.getInstruction(),
                expectedMedicament.getPrice()
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
    public void createCorrectedValuesThatInStock() throws CrudOperationException {

        Mockito.when(medicamentDao.existByName(Mockito.anyString())).thenReturn(true);

        Medicament expectedMedicament = Medicament.builder()
                .name("Медикамент T")
                .price(new BigDecimal(10))
                .contraindications("Противопоказания T")
                .description("Описание T")
                .instruction("Инструкция T")
                .build();

        medicamentEjbRemote.create(
                expectedMedicament.getName(),
                expectedMedicament.getDescription(),
                expectedMedicament.getContraindications(),
                expectedMedicament.getInstruction(),
                expectedMedicament.getPrice()
        );

        Mockito.verify(medicamentDao, Mockito.times(1))
                .existByName("Медикамент T");

        Mockito.verify(medicamentDao, Mockito.never())
                .save(Mockito.anyObject());
    }

    @Test(expected = CrudOperationException.class)
    /**
     * Добавление некорректной записи о медикаменте.
     * Предполагается, что метод save вызовется,
     * но выкенет исключение, которой обернётся в CrudOperationException
     * и передастся дальше.
     */
    public void createIncorrectedValuesOthersArgsNull() throws CrudOperationException {

        Mockito.when(medicamentDao.existByName(Mockito.anyString()))
                .thenReturn(false);

        Mockito.doThrow(new Exception())
                .when(medicamentDao)
                .save(Mockito.anyObject());

        Medicament expectedMedicament = Medicament.builder()
                .name("Name")
                .price(null)
                .contraindications(null)
                .description(null)
                .instruction(null)
                .build();

        medicamentEjbRemote.create(
                expectedMedicament.getName(),
                expectedMedicament.getDescription(),
                expectedMedicament.getContraindications(),
                expectedMedicament.getInstruction(),
                expectedMedicament.getPrice()
        );

        Mockito.verify(medicamentDao, Mockito.times(1))
                .existByName("Name");

        Mockito.verify(medicamentDao, Mockito.times(1))
                .save(Mockito.anyObject());

        ArgumentCaptor<Medicament> medicamentArgumentCaptor = ArgumentCaptor.forClass(Medicament.class);
        Mockito.verify(medicamentDao).save(medicamentArgumentCaptor.capture());
        Medicament actualMedicament = medicamentArgumentCaptor.getValue();

        Assert.assertEquals(expectedMedicament, actualMedicament);
    }

    @Test(expected = CrudOperationException.class)
    /**
     * Добавление некорректной записи о медикаменте.
     * Предполагается, что вызовется метод проверки на существование
     * в базе медикамента с именем null и возникнет исключение.
     */
    public void createIncorrectedValuesNameNull() throws CrudOperationException {

        Mockito.doThrow(new Exception())
                .when(medicamentDao)
                .save(Mockito.anyObject());

        Medicament expectedMedicament = Medicament.builder()
                .name(null)
                .price(null)
                .contraindications(null)
                .description(null)
                .instruction(null)
                .build();

        medicamentEjbRemote.create(
                expectedMedicament.getName(),
                expectedMedicament.getDescription(),
                expectedMedicament.getContraindications(),
                expectedMedicament.getInstruction(),
                expectedMedicament.getPrice()
        );

        Mockito.verify(medicamentDao, Mockito.times(1))
                .existByName(null);

        Mockito.verify(medicamentDao, Mockito.never())
                .save(Mockito.anyObject());
    }

    @Test
    public void updateCorrectedValuesThatNotInStock() throws CrudOperationException {

        Mockito.when(medicamentDao.existByName("Медикамент T")).thenReturn(false);

        MedicamentViewModel expectedMedicament = MedicamentViewModel.builder()
                .id(100L)
                .name("Медикамент T")
                .price(new BigDecimal(10))
                .contraindications("Противопоказания T")
                .description("Описание T")
                .instruction("Инструкция T")
                .build();

        medicamentEjbRemote.update(expectedMedicament);

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

    @Test
    public void updateCorrectedValuesThatInStock() throws CrudOperationException {

        Mockito.when(medicamentDao.existByName("Медикамент T")).thenReturn(true);

        MedicamentViewModel expectedMedicament = MedicamentViewModel.builder()
                .id(100L)
                .name("Медикамент T")
                .price(new BigDecimal(10))
                .contraindications("Противопоказания T")
                .description("Описание T")
                .instruction("Инструкция T")
                .build();

        medicamentEjbRemote.update(expectedMedicament);

        Mockito.verify(medicamentDao, Mockito.times(1))
                .existByName("Медикамент T");

        Mockito.verify(medicamentDao, Mockito.never())
                .update(Mockito.anyObject());
    }

    @Test(expected = CrudOperationException.class)
    public void updateIncorrectedValuesWithNotNullNameOthresNull() throws CrudOperationException {

        Mockito.doThrow(new Exception())
                .when(medicamentDao)
                .update(Mockito.anyObject());

        MedicamentViewModel expectedMedicament = MedicamentViewModel.builder()
                .id(null)
                .name("Name")
                .price(null)
                .contraindications(null)
                .description(null)
                .instruction(null)
                .build();

        medicamentEjbRemote.update(expectedMedicament);

        Mockito.verify(medicamentDao, Mockito.times(1))
                .existByName(null);


        Mockito.verify(medicamentDao, Mockito.times(1))
                .update(Mockito.anyObject());

        ArgumentCaptor<Medicament> medicamentArgumentCaptor = ArgumentCaptor.forClass(Medicament.class);
        Mockito.verify(medicamentDao).save(medicamentArgumentCaptor.capture());
        Medicament actualMedicament = medicamentArgumentCaptor.getValue();

        Assert.assertEquals(expectedMedicament, actualMedicament);
    }

    @Test(expected = CrudOperationException.class)
    public void updateIncorrectedValuesWithValues() throws CrudOperationException {
        MedicamentViewModel expectedMedicament = MedicamentViewModel.builder()
                .id(null)
                .name(null)
                .price(null)
                .contraindications(null)
                .description(null)
                .instruction(null)
                .build();

        medicamentEjbRemote.update(expectedMedicament);

        Mockito.verify(medicamentDao, Mockito.times(1))
                .existByName(null);

        Mockito.verify(medicamentDao, Mockito.never())
                .update(Mockito.anyObject());
    }

    @Test(expected = CrudOperationException.class)
    public void updateNullValue() throws CrudOperationException {
        medicamentEjbRemote.update(null);

        Mockito.verify(medicamentDao, Mockito.never())
                .update(Mockito.anyObject());
    }

    @Test
    /**
     * Проверка на то, что ID передастся в метод deleteById
     * и не возникнет никаких исключений.
     */
    public void deleteExitingMedicament() throws CrudOperationException {
        Long idExistMedicament = 15L;

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
     * Проверка на то, что ID передастся в метод deleteById
     * и возникнет исключение CrudOperationException.
     */
    public void deleteNotExitingMedicament() throws CrudOperationException {
        Long idNotExistMedicament = 100L;

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