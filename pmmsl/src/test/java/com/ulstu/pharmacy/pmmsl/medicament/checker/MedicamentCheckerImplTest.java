package com.ulstu.pharmacy.pmmsl.medicament.checker;

import com.ulstu.pharmacy.pmmsl.medicament.binding.MedicamentBindingModel;
import com.ulstu.pharmacy.pmmsl.medicament.dao.MedicamentDaoImpl;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

@RunWith(MockitoJUnitRunner.class)
public class MedicamentCheckerImplTest {

    @Mock
    private MedicamentDaoImpl medicamentDao;

    @InjectMocks
    private MedicamentCheckerImpl medicamentValidator;

    @Before
    public void init() {

    }

    @Test
    public void simple() {
        Assert.assertEquals(4, 2 + 2);
    }

    @Test
    public void createValidationCorrect() {
        Mockito.when(medicamentDao.existByName(Mockito.anyString()))
                .thenReturn(false);

        String errors = medicamentValidator.createCheck(
                MedicamentBindingModel.builder()
                        .id(null)
                        .contraindications("")
                        .description("")
                        .instruction("")
                        .name("")
                        .price(new BigDecimal(1L))
                        .build()
        );

        Mockito.verify(medicamentDao).existByName(Mockito.anyString());

        Assert.assertTrue(errors.isBlank());
    }

    @Test
    public void createValidationNull() {
        String errors = medicamentValidator.createCheck(
                MedicamentBindingModel.builder()
                        .id(null)
                        .contraindications(null)
                        .description(null)
                        .instruction(null)
                        .name(null)
                        .price(new BigDecimal(1L))
                        .build()
        );

        Assert.assertFalse(errors.isBlank());
    }

    @Test
    public void createValidationNullArg() {
        String errors = medicamentValidator.createCheck(
                null
        );

        Assert.assertFalse(errors.isBlank());
    }

    @Test
    public void createValidationNameExist() {
        Mockito.when(medicamentDao.existByName(Mockito.anyString()))
                .thenReturn(true);

        String errors = medicamentValidator.createCheck(
                MedicamentBindingModel.builder()
                        .id(null)
                        .contraindications("")
                        .description("")
                        .instruction("")
                        .name("")
                        .price(new BigDecimal(1L))
                        .build()
        );

        Mockito.verify(medicamentDao).existByName(Mockito.anyString());

        Assert.assertFalse(errors.isBlank());
    }

    @Test
    public void updateValidationCorrect() {

        Mockito.when(medicamentDao.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito.when(medicamentDao.existByName(Mockito.anyString()))
                .thenReturn(false);

        String errors = medicamentValidator.updateCheck(
                MedicamentBindingModel.builder()
                        .id(1L)
                        .contraindications("")
                        .description("")
                        .instruction("")
                        .name("")
                        .price(new BigDecimal(1L))
                        .build()
        );

        Mockito.verify(medicamentDao).existByName(Mockito.anyString());

        Assert.assertTrue(errors.isBlank());
    }

    @Test
    public void updateValidationNullArg() {
        String errors = medicamentValidator.createCheck(
                null
        );

        Assert.assertFalse(errors.isBlank());
    }

    @Test
    public void updateValidationNotCorrect() {
        String errors = medicamentValidator.updateCheck(
                MedicamentBindingModel.builder()
                        .id(1L)
                        .contraindications(null)
                        .description(null)
                        .instruction(null)
                        .name(null)
                        .price(null)
                        .build()
        );

        Assert.assertFalse(errors.isBlank());
    }

    @Test
    public void updateValidationNull() {
        String errors = medicamentValidator.updateCheck(
                MedicamentBindingModel.builder()
                        .id(null)
                        .contraindications("")
                        .description("")
                        .instruction("")
                        .name("")
                        .price(new BigDecimal(1L))
                        .build()
        );

        Assert.assertFalse(errors.isBlank());
    }

    @Test
    public void deleteCorrect() {
        Mockito.when(medicamentDao.existsById(Mockito.anyLong()))
                .thenReturn(true);

        String errors = medicamentValidator.deleteCheck(
                1L
        );

        Mockito.verify(medicamentDao).existsById(Mockito.anyLong());

        Assert.assertTrue(errors.isBlank());
    }

    @Test
    public void deleteValidationIdNotExist() {
        Mockito.when(medicamentDao.existsById(Mockito.anyLong()))
                .thenReturn(false);

        String errors = medicamentValidator.deleteCheck(
                1L
        );

        Mockito.verify(medicamentDao).existsById(Mockito.anyLong());

        Assert.assertFalse(errors.isBlank());
    }

    @Test
    public void deleteValidationNull() {
        String errors = medicamentValidator.deleteCheck(
                null
        );

        Assert.assertFalse(errors.isBlank());
    }
}