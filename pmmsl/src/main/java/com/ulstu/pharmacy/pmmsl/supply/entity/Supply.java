package com.ulstu.pharmacy.pmmsl.supply.entity;

import com.ulstu.pharmacy.pmmsl.common.entity.AbstractEntity;
import com.ulstu.pharmacy.pmmsl.pharmacy.entity.Pharmacy;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class Supply extends AbstractEntity<Long> {

    @Column(nullable = false, updatable = false)
    private Timestamp date;

    @ManyToOne(optional = false)
    private Pharmacy pharmacy;

    @OneToMany(mappedBy = "supply",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER)
    private Set<SupplyMedicament> supplyMedicaments;

    @Override
    @SequenceGenerator(name = "supply_seq", sequenceName = "supply_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "supply_seq")
    public Long getId() {
        return super.getId();
    }

    public static Supply.Builder builder() {
        return new Supply.Builder();
    }

    public static class Builder {
        private Supply newSupply;

        public Builder() {
            newSupply = new Supply();
        }

        public Builder id(Long id) {
            newSupply.setId(id);
            return this;
        }

        public Builder date(Timestamp date) {
            newSupply.setDate(date);
            return this;
        }

        public Builder pharmacy(Pharmacy pharmacy) {
            newSupply.setPharmacy(pharmacy);
            return this;
        }

        public Builder supplyMedicaments(Set<SupplyMedicament> supplyMedicaments) {
            newSupply.setSupplyMedicaments(supplyMedicaments);
            return this;
        }

        public Supply build() {
            return newSupply;
        }
    }
}