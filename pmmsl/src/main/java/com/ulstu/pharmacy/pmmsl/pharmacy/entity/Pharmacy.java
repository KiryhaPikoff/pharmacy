package com.ulstu.pharmacy.pmmsl.pharmacy.entity;

import com.ulstu.pharmacy.pmmsl.common.entity.AbstractEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@SequenceGenerator(name = "default_gen", sequenceName = "pharmacy_seq", allocationSize = 1)
/** Аптека. */
public class Pharmacy extends AbstractEntity<Long> {

    @Column(nullable = false)
    private String name;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "pharmacy")
    private Set<PharmacyMedicament> pharmacyMedicaments;

    public static Pharmacy.Builder builder() {
        return new Pharmacy.Builder();
    }

    public static class Builder {
        private Pharmacy newPharmacy;

        public Builder() {
            newPharmacy = new Pharmacy();
        }

        public Builder id(Long id) {
            newPharmacy.setId(id);
            return this;
        }

        public Builder name(String name) {
            newPharmacy.setName(name);
            return this;
        }

        public Builder pharmacyMedicaments(Set<PharmacyMedicament> pharmacyMedicaments) {
            newPharmacy.setPharmacyMedicaments(pharmacyMedicaments);
            return this;
        }

        public Pharmacy build() {
            return newPharmacy;
        }
    }
}