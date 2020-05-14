package com.ulstu.pharmacy.pmmsl.medservice.entity;

import com.ulstu.pharmacy.pmmsl.common.entity.AbstractEntity;
import com.ulstu.pharmacy.pmmsl.medicament.entity.Medicament;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@SequenceGenerator(name = "default_gen", sequenceName = "medicament_medical_service_seq", allocationSize = 1)
/** Связь медикамента и медицинской услуги. */
public class MedicamentMedicalService extends AbstractEntity<Long> {

    @ToString.Exclude
    @ManyToOne(optional = false)
    private Medicament medicament;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(optional = false)
    private MedicalService medicalService;

    @Column(nullable = false, updatable = false)
    private Integer count;

    @Column(nullable = false, scale = 2, updatable = false)
    private BigDecimal price;

    public static MedicamentMedicalService.Builder builder() {
        return new MedicamentMedicalService.Builder();
    }

    public static class Builder {
        private MedicamentMedicalService newMedicamentMedicalService;

        public Builder() {
            newMedicamentMedicalService = new MedicamentMedicalService();
        }

        public Builder id(Long id) {
            newMedicamentMedicalService.setId(id);
            return this;
        }

        public Builder medicament(Medicament medicament) {
            newMedicamentMedicalService.setMedicament(medicament);
            return this;
        }

        public Builder medicalService(MedicalService medicalService) {
            newMedicamentMedicalService.setMedicalService(medicalService);
            return this;
        }

        public Builder count(Integer count) {
            newMedicamentMedicalService.setCount(count);
            return this;
        }

        public Builder price(BigDecimal price) {
            newMedicamentMedicalService.setPrice(price);
            return this;
        }

        public MedicamentMedicalService build() {
            return newMedicamentMedicalService;
        }
    }
}