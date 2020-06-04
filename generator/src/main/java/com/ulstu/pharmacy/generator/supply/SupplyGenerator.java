package com.ulstu.pharmacy.generator.supply;

import com.ulstu.pharmacy.generator.random.RandomHelper;
import com.ulstu.pharmacy.pmmsl.medicament.binding.MedicamentCountBindingModel;
import com.ulstu.pharmacy.pmmsl.medicament.ejb.MedicamentEjbLocal;
import com.ulstu.pharmacy.pmmsl.pharmacy.ejb.PharmacyEjbLocal;
import com.ulstu.pharmacy.pmmsl.supply.binding.SupplyBindingModel;
import com.ulstu.pharmacy.pmmsl.supply.ejb.SupplyEjbLocal;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.configuration.PropertiesConfiguration;

import javax.annotation.PostConstruct;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Stateless
@NoArgsConstructor
public class SupplyGenerator {

    private SupplyEjbLocal supplyEjb;

    private PharmacyEjbLocal pharmacyEjb;

    private MedicamentEjbLocal medicamentEjb;

    private Integer minMedicalServicesCount;

    private Integer maxMedicalServicesCount;

    private Integer minMedicamentsCount;

    private Integer maxMedicamentsCount;

    private Integer minMedicamentCount;

    private Integer maxMedicamentCount;

    @Inject
    public void setPharmacyEjb(PharmacyEjbLocal pharmacyEjb) {
        this.pharmacyEjb = pharmacyEjb;
    }

    @Inject
    public void setSupplyEjb(SupplyEjbLocal supplyEjb) {
        this.supplyEjb = supplyEjb;
    }

    @Inject
    public void setMedicamentEjb(MedicamentEjbLocal medicamentEjb) {
        this.medicamentEjb = medicamentEjb;
    }

    @PostConstruct
    @SneakyThrows
    public void init() {
        var configuration = new PropertiesConfiguration("generators.properties");

        this.minMedicalServicesCount = configuration.getInt("supply.min.count");
        this.maxMedicalServicesCount = configuration.getInt("supply.max.count");

        this.minMedicamentsCount     = configuration.getInt("supply.medcount.count.min");
        this.maxMedicamentsCount     = configuration.getInt("supply.medcount.count.max");

        this.minMedicamentCount      = configuration.getInt("supply.medicaments.min.count");
        this.maxMedicamentCount      = configuration.getInt("supply.medicaments.max.count");
    }

    @Schedule(second = "*/30", minute = "*", hour = "*")
    public void generate() {
        int count = RandomHelper.randomInRange(this.minMedicalServicesCount, this.maxMedicalServicesCount);
        var pharmacys = pharmacyEjb.getAll();
        for (int i = 0; i < count; i++) {
            supplyEjb.create(
                    SupplyBindingModel.builder()
                            .pharmacyId(pharmacys.get(
                                    RandomHelper.randomInRange(0, pharmacys.size() - 1)
                            ).getId())
                            .medicamentCountSet(
                                    this.generateMedicamentSet(
                                            RandomHelper.randomInRange(this.minMedicamentsCount, this.maxMedicamentsCount)
                                    )
                            )
                            .build()
            );
        }
    }

    private Set<MedicamentCountBindingModel> generateMedicamentSet(Integer count) {
        if (Objects.isNull(count) || count < 1) {
            throw new RuntimeException("Вы хотите сгенерировать " + count + " записей?");
        }
        var medicaments = medicamentEjb.getAll();
        Set<MedicamentCountBindingModel> result = new HashSet<>();
        for (int i = 0; i < count; i++) {
            result.add(
                    MedicamentCountBindingModel.builder()
                            .medicamentId(
                                    medicaments.get(
                                            RandomHelper.randomInRange(0, medicaments.size() - 1)
                                    ).getId())
                            .count(RandomHelper.randomInRange(this.minMedicamentCount, this.maxMedicamentCount))
                            .build()
            );
        }
        return result;
    }
}