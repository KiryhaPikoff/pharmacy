package com.ulstu.pharmacy.pmmsl.medicament.entity;

import com.ulstu.pharmacy.pmmsl.common.entity.AbstractEntity;
import com.ulstu.pharmacy.pmmsl.medservice.entity.MedicamentMedicalService;
import com.ulstu.pharmacy.pmmsl.pharmacy.entity.PharmacyMedicament;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name = "default_gen", sequenceName = "medicament_seq", allocationSize = 1)
/** Медикамент. */
public class Medicament extends AbstractEntity<Long> {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String contraindications;

    @Column(nullable = false)
    private String instruction;

    @Column(nullable = false, scale = 2)
    private BigDecimal price;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "pharmacy")
    private List<PharmacyMedicament> pharmacyMedicaments;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "medicalService")
    private List<MedicamentMedicalService> medicamentMedicalServices;

    public static Medicament.Builder builder() {
        return new Medicament.Builder();
    }

    public static class Builder {
        private Medicament newMedicament;

        public Builder() {
            newMedicament = new Medicament();
        }

        public Builder id(Long id) {
            newMedicament.setId(id);
            return this;
        }

        public Builder name(String name) {
            newMedicament.setName(name);
            return this;
        }

        public Builder description(String description) {
            newMedicament.setDescription(description);
            return this;
        }

        public Builder contraindications(String contraindications) {
            newMedicament.setContraindications(contraindications);
            return this;
        }

        public Builder instruction(String instruction) {
            newMedicament.setInstruction(instruction);
            return this;
        }

        public Builder price(BigDecimal price) {
            newMedicament.setPrice(price);
            return this;
        }

        public Medicament build() {
            return newMedicament;
        }
    }
}