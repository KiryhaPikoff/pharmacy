package com.ulstu.pharmacy.pmmsl.pharmacy.entity;

import com.ulstu.pharmacy.pmmsl.common.entity.AbstractEntity;
import com.ulstu.pharmacy.pmmsl.medicament.entity.Medicament;
import com.ulstu.pharmacy.pmmsl.medservice.entity.MedicamentMedicalService;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
/** Свзяь аптеки и медикамента. */
public class PharmacyMedicament extends AbstractEntity<Long> {

    @ManyToOne(optional = false)
    private Medicament medicament;

    @ManyToOne(optional = false)
    private Pharmacy pharmacy;

    @Column(nullable = false)
    @EqualsAndHashCode.Exclude
    private Integer count;

    @Override
    @SequenceGenerator(name = "pharmacy_medicament_seq", sequenceName = "pharmacy_medicament_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pharmacy_medicament_seq")
    public Long getId() {
        return super.getId();
    }

    public static PharmacyMedicament.Builder builder() {
        return new PharmacyMedicament.Builder();
    }

    public static class Builder {
        private PharmacyMedicament newPharmacyMedicament;

        public Builder() {
            newPharmacyMedicament = new PharmacyMedicament();
        }

        public Builder id(Long id) {
            newPharmacyMedicament.setId(id);
            return this;
        }

        public Builder medicament(Medicament medicament) {
            newPharmacyMedicament.setMedicament(medicament);
            return this;
        }

        public Builder pharmacy(Pharmacy pharmacy) {
            newPharmacyMedicament.setPharmacy(pharmacy);
            return this;
        }

        public Builder count(Integer count) {
            newPharmacyMedicament.setCount(count);
            return this;
        }

        public PharmacyMedicament build() {
            return newPharmacyMedicament;
        }
    }
}