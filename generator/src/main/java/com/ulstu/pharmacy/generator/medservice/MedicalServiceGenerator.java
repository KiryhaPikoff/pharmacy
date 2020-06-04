package com.ulstu.pharmacy.generator.medservice;

import com.ulstu.pharmacy.generator.random.RandomHelper;
import com.ulstu.pharmacy.pmmsl.medicament.binding.MedicamentCountBindingModel;
import com.ulstu.pharmacy.pmmsl.medservice.ejb.MedicalServiceEjbLocal;
import com.ulstu.pharmacy.pmmsl.pharmacy.ejb.PharmacyEjbLocal;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.configuration.PropertiesConfiguration;

import javax.annotation.PostConstruct;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;

@Stateless
@NoArgsConstructor
public class MedicalServiceGenerator {

    private MedicalServiceEjbLocal medicalServiceEjb;

    private PharmacyEjbLocal pharmacyEjb;

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
    public void setMedicalServiceEjb(MedicalServiceEjbLocal medicalServiceEjb) {
        this.medicalServiceEjb = medicalServiceEjb;
    }

    @PostConstruct
    @SneakyThrows
    public void init() {
        var configuration = new PropertiesConfiguration("generators.properties");

        this.minMedicalServicesCount = configuration.getInt("medservice.min.count");
        this.maxMedicalServicesCount = configuration.getInt("medservice.max.count");

        this.minMedicamentsCount     = configuration.getInt("medservice.medcount.count.min");
        this.maxMedicamentsCount     = configuration.getInt("medservice.medcount.count.max");

        this.minMedicamentCount      = configuration.getInt("medservice.medicaments.min.count");
        this.maxMedicamentCount      = configuration.getInt("medservice.medicaments.max.count");
    }

    @Schedule(minute = "*", hour = "*")
    public void generateMedicamentSet() {
        int count = RandomHelper.randomInRange(this.minMedicalServicesCount, this.maxMedicalServicesCount);
        for (int i = 0; i < count; i++) {
            medicalServiceEjb.create(
                    this.generateMedicamentSet(
                            RandomHelper.randomInRange(this.minMedicamentsCount, this.maxMedicamentsCount)
                    )
            );
        }
    }

    private Set<MedicamentCountBindingModel> generateMedicamentSet(Integer count) {
        if (Objects.isNull(count) || count < 1) {
            throw new RuntimeException("Вы хотите сгенерировать " + count + " записей?");
        }
        var medicamentsWithCount = pharmacyEjb.getPharmacyMedicamentsInStock();
        var medicaments = new LinkedList<>(medicamentsWithCount.keySet());
        Set<MedicamentCountBindingModel> result = new HashSet<>();
        for (int i = 0; i < count; i++) {
            if (medicaments.isEmpty()) {
                continue;
            }
            var medicament = medicaments.get(RandomHelper.randomInRange(0, medicaments.size() - 1));
            medicaments.remove(medicament);
            int countInStore = medicamentsWithCount.get(medicament);
            int randomizedCount = RandomHelper.randomInRange(this.minMedicamentCount, this.maxMedicamentCount);
            int countCreating = Math.min(randomizedCount, countInStore);
            result.add(
                    MedicamentCountBindingModel.builder()
                            .medicamentId(medicament.getId())
                            .count(countCreating)
                            .build()
            );
        }
        return result;
    }
}