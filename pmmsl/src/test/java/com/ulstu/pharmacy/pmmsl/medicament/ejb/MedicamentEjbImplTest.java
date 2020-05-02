package com.ulstu.pharmacy.pmmsl.medicament.ejb;

import com.ulstu.pharmacy.pmmsl.common.exception.CrudOperationException;
import com.ulstu.pharmacy.pmmsl.medicament.binding.MedicamentBindingModel;
import com.ulstu.pharmacy.pmmsl.medicament.checker.MedicamentCheckerImpl;
import com.ulstu.pharmacy.pmmsl.medicament.dao.MedicamentDaoImpl;
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

    @Mock
    private MedicamentCheckerImpl medicamentValidator;

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

        Optional<Medicament> returnValue = Optional.of(
                Medicament.builder()
                        .id(expectedMedicament.getId())
                        .name(expectedMedicament.getName())
                        .contraindications(expectedMedicament.getContraindications())
                        .description(expectedMedicament.getDescription())
                        .instruction(expectedMedicament.getInstruction())
                        .price(expectedMedicament.getPrice())
                        .build()
        );
        Mockito.when(medicamentDao.findById(idExist))
                .thenReturn(returnValue);

        MedicamentViewModel actual = medicamentEjbRemote.getById(idExist);
        Assert.assertEquals(expectedMedicament, actual);

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
        List<MedicamentViewModel> expectedMedicaments = this.initMedicaments().stream()
                .map(medicamentMapper::toViewModel)
                .collect(Collectors.toList());

        List<MedicamentViewModel> actualMedicaments = medicamentEjbRemote.getAll();

        Mockito.verify(medicamentDao, Mockito.times(1))
                .getAll();

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
    public void createWithoutValidationErrors() {

        this.validationOk();

        Medicament expectedMedicament = Medicament.builder()
                .name("Медикамент T")
                .price(new BigDecimal(10))
                .contraindications("Противопоказания T")
                .description("Описание T")
                .instruction("Инструкция T")
                .build();

        medicamentEjbRemote.createOrUpdate(
                MedicamentBindingModel.builder()
                    .name(expectedMedicament.getName())
                    .description(expectedMedicament.getDescription())
                    .contraindications(expectedMedicament.getContraindications())
                    .instruction(expectedMedicament.getInstruction())
                    .price(expectedMedicament.getPrice())
                    .build()
        );

        Mockito.verify(medicamentDao, Mockito.times(1))
                .save(Mockito.anyObject());

        ArgumentCaptor<Medicament> medicamentArgumentCaptor = ArgumentCaptor.forClass(Medicament.class);
        Mockito.verify(medicamentDao).save(medicamentArgumentCaptor.capture());
        Medicament actualMedicament = medicamentArgumentCaptor.getValue();

        Assert.assertEquals(expectedMedicament, actualMedicament);
    }

    @Test(expected = CrudOperationException.class)
    /**
     * Добавление медикамета с ошибками валидации.
     */
    public void createWithValidationErrors() {

        this.validationError();

        medicamentEjbRemote.createOrUpdate(
                MedicamentBindingModel.builder().build()
        );

        Mockito.verify(medicamentDao, Mockito.never())
                .save(Mockito.anyObject());
    }

    @Test
    /**
     * Обновление медикамента, без ошибки валидации.
     */
    public void updateWithoutValidationErrors() {

        this.validationOk();

        MedicamentViewModel expectedMedicament = MedicamentViewModel.builder()
                .id(100L)
                .name("Медикамент T")
                .price(new BigDecimal(10))
                .contraindications("Противопоказания T")
                .description("Описание T")
                .instruction("Инструкция T")
                .build();

        medicamentEjbRemote.createOrUpdate(
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
                .update(Mockito.anyObject());

        ArgumentCaptor<Medicament> medicamentArgumentCaptor = ArgumentCaptor.forClass(Medicament.class);
        Mockito.verify(medicamentDao).update(medicamentArgumentCaptor.capture());
        Medicament actualMedicament = medicamentArgumentCaptor.getValue();

        Medicament expected = Medicament.builder()
                .id(expectedMedicament.getId())
                .name(expectedMedicament.getName())
                .description(expectedMedicament.getDescription())
                .contraindications(expectedMedicament.getContraindications())
                .instruction(expectedMedicament.getInstruction())
                .price(expectedMedicament.getPrice())
                .build();

        Assert.assertEquals(
                expected,
                actualMedicament
        );
    }

    @Test(expected = CrudOperationException.class)
    /**
     * Обновление медикамента, но с ошибкой валидации аргументов
     */
    public void updateWithValidationErrors() {

        this.validationError();

        medicamentEjbRemote.createOrUpdate(
                MedicamentBindingModel.builder()
                        .id(100L)
                        .build()
        );

        Mockito.verify(medicamentDao, Mockito.never())
                .update(Mockito.anyObject());
    }

    @Test(expected = CrudOperationException.class)
    public void createOrUpdateNullValue() {

        this.validationError();

        medicamentEjbRemote.createOrUpdate(null);

        Mockito.verify(medicamentDao, Mockito.never())
                .save(Mockito.anyObject());

        Mockito.verify(medicamentDao, Mockito.never())
                .update(Mockito.anyObject());
    }

    @Test
    /**
     * Проверка на корректное удаление медикамента, без ошибок валидации.
     */
    public void deleteWithoutValidationError() {
        Long idExistMedicament = 15L;

        this.validationOk();

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
     * Попытка удалить меидкамент, с ошибкой валидации.
     */
    public void deleteWithValidationErrors() {
        Long idNotExistMedicament = 100L;

        this.validationError();

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
    public void validationOk() {
        Mockito.when(medicamentValidator.createCheck(Mockito.anyObject()))
                .thenReturn("");
        Mockito.when(medicamentValidator.updateCheck(Mockito.anyObject()))
                .thenReturn("");
        Mockito.when(medicamentValidator.deleteCheck(Mockito.anyObject()))
                .thenReturn("");
    }

    @Ignore
    public void validationError() {
        Mockito.when(medicamentValidator.createCheck(Mockito.anyObject()))
                .thenReturn("Error");
        Mockito.when(medicamentValidator.updateCheck(Mockito.anyObject()))
                .thenReturn("Error");
        Mockito.when(medicamentValidator.deleteCheck(Mockito.anyObject()))
                .thenReturn("Error");
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