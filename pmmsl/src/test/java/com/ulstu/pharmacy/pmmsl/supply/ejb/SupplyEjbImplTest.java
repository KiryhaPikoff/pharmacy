package com.ulstu.pharmacy.pmmsl.supply.ejb;

import com.ulstu.pharmacy.pmmsl.common.exception.CrudOperationException;
import com.ulstu.pharmacy.pmmsl.medicament.binding.MedicamentCountBindingModel;
import com.ulstu.pharmacy.pmmsl.medicament.entity.Medicament;
import com.ulstu.pharmacy.pmmsl.pharmacy.ejb.PharmacyEjbLocal;
import com.ulstu.pharmacy.pmmsl.pharmacy.entity.Pharmacy;
import com.ulstu.pharmacy.pmmsl.supply.binding.SupplyBindingModel;
import com.ulstu.pharmacy.pmmsl.supply.dao.SupplyDaoImpl;
import com.ulstu.pharmacy.pmmsl.supply.entity.Supply;
import com.ulstu.pharmacy.pmmsl.supply.entity.SupplyMedicament;
import com.ulstu.pharmacy.pmmsl.supply.mapper.SupplyMapper;
import com.ulstu.pharmacy.pmmsl.supply.view.SupplyViewModel;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RunWith(MockitoJUnitRunner.class)
public class SupplyEjbImplTest {


    @Mock
    private PharmacyEjbLocal pharmacyEjb;

    @Mock
    private SupplyDaoImpl supplyDao;

    @Mock
    private SupplyMapper supplyMapper;

    @InjectMocks
    private SupplyEjbImpl supplyEjb;

    @Before
    public void init() {
        List<Supply> expectedSupplys = this.initSupplys();

        Mockito.when(supplyDao.getFromDateToDate(Mockito.anyObject(), Mockito.anyObject()))
                .thenReturn(expectedSupplys);

        Mockito.when(supplyMapper.toViewModel(Mockito.anyObject()))
                .thenCallRealMethod();

    }

    @Test
    public void simple() {
        Assert.assertEquals(4, 2 + 2);
    }

    @Test
    public void createCorrect() {
        Set<MedicamentCountBindingModel> medicamentCounts = Set.of(
                MedicamentCountBindingModel.builder()
                        .medicamentId(0L)
                        .count(4)
                        .build(),
                MedicamentCountBindingModel.builder()
                        .medicamentId(1L)
                        .count(16)
                        .build()
        );

        SupplyBindingModel supplyBindingModel = SupplyBindingModel.builder()
                .pharmacyId(0L)
                .medicamentCountSet(medicamentCounts)
                .build();

        this.supplyEjb.create(supplyBindingModel);

        Mockito.verify(pharmacyEjb).replenishMedicaments(0L, medicamentCounts);
        Mockito.verify(supplyDao).save(Mockito.anyObject());
    }

    @Test
    public void getFromDateToDate() {
        List<Supply> expectedSupplys = this.initSupplys();

        Timestamp expectedFromDate = Timestamp.valueOf("2009-06-04 18:13:56");
        Timestamp expectedToDay = Timestamp.valueOf("2020-10-11 14:12:51");

        List<SupplyViewModel> actualSupplyViewModels = supplyEjb.getFromDateToDate(
                expectedFromDate,
                expectedToDay
        );

        Mockito.verify(supplyDao, Mockito.times(1))
                .getFromDateToDate(Mockito.anyObject(), Mockito.anyObject());

        ArgumentCaptor<Timestamp> timestampArgumentCaptor = ArgumentCaptor.forClass(Timestamp.class);
        Mockito.verify(supplyDao).getFromDateToDate(timestampArgumentCaptor.capture(), timestampArgumentCaptor.capture());
        List<Timestamp> timestamps = timestampArgumentCaptor.getAllValues();

        Assert.assertEquals(expectedFromDate, timestamps.get(0));
        Assert.assertEquals(expectedToDay, timestamps.get(1));

        Assert.assertEquals(
                expectedSupplys.stream()
                        .map(supplyMapper::toViewModel)
                        .collect(Collectors.toList()),
                actualSupplyViewModels
        );
    }


    @Test(expected = CrudOperationException.class)
    public void getFromDateToDateIncorrectedValues() {

        List<SupplyViewModel> actualSupplyViewModels = supplyEjb.getFromDateToDate(
                null,
                null
        );

        Mockito.verify(supplyDao, Mockito.never())
                .getFromDateToDate(Mockito.anyObject(), Mockito.anyObject());

        Assert.assertNotNull(actualSupplyViewModels);
        Assert.assertTrue(actualSupplyViewModels.isEmpty());
    }

    @Ignore
    private List<Supply> initSupplys() {
        List<Supply> supplys = new LinkedList<>();
        List<Medicament> medicaments = this.initMedicaments();
        List<Pharmacy> pharmacys = this.initPharmacys();

        supplys.add(
                Supply.builder()
                        .id(0L)
                        .date(new Timestamp(System.currentTimeMillis()))
                        .pharmacy(pharmacys.get(0))
                        .supplyMedicaments(
                                Set.of(
                                        SupplyMedicament.builder()
                                                .id(0L)
                                                .medicament(medicaments.get(0))
                                                .count(4)
                                                .build(),
                                        SupplyMedicament.builder()
                                                .id(1L)
                                                .medicament(medicaments.get(3))
                                                .count(10)
                                                .build()
                                )
                        )
                        .build()
        );

        supplys.add(
                Supply.builder()
                        .id(1L)
                        .date(new Timestamp(System.currentTimeMillis()))
                        .pharmacy(pharmacys.get(0))
                        .supplyMedicaments(
                                Set.of(
                                        SupplyMedicament.builder()
                                                .id(0L)
                                                .medicament(medicaments.get(2))
                                                .count(15)
                                                .build(),
                                        SupplyMedicament.builder()
                                                .id(1L)
                                                .medicament(medicaments.get(4))
                                                .count(3)
                                                .build()
                                )
                        )
                        .build()
        );

        return supplys;
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
        //TODO del
    }
}