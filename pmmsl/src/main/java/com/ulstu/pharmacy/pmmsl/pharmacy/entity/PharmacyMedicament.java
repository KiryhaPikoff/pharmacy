package com.ulstu.pharmacy.pmmsl.pharmacy.entity;

import com.ulstu.pharmacy.pmmsl.common.entity.AbstractEntity;
import com.ulstu.pharmacy.pmmsl.medicament.entity.Medicament;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "pharmacy_medicament")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@SequenceGenerator(name = "default_gen", sequenceName = "pharmacy_medicament_seq", allocationSize = 1)
/** Свзяь аптеки и медикамента. */
public class PharmacyMedicament extends AbstractEntity<Long> {

    @ToString.Exclude
    @ManyToOne(optional = false)
    private Medicament medicament;

    @ToString.Exclude
    @ManyToOne(optional = false)
    private Pharmacy pharmacy;

    @Column(nullable = false)
    @EqualsAndHashCode.Exclude
    private Integer count;

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