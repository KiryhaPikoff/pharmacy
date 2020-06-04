package com.ulstu.pharmacy.pmmsl.supply.entity;

import com.ulstu.pharmacy.pmmsl.common.entity.AbstractEntity;
import com.ulstu.pharmacy.pmmsl.medicament.entity.Medicament;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "supply_medicament")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@SequenceGenerator(name = "default_gen", sequenceName = "supply_medicament_seq", allocationSize = 1)
public class SupplyMedicament extends AbstractEntity<Long> {

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(optional = false)
    private Supply supply;

    @EqualsAndHashCode.Include
    @ManyToOne(optional = false)
    private Medicament medicament;

    @Column(nullable = false, updatable = false)
    private Integer count;

    public static SupplyMedicament.Builder builder() {
        return new SupplyMedicament.Builder();
    }

    public static class Builder {
        private SupplyMedicament newSupplyMedicament;

        public Builder() {
            newSupplyMedicament = new SupplyMedicament();
        }

        public Builder id(Long id) {
            newSupplyMedicament.setId(id);
            return this;
        }

        public Builder medicament(Medicament medicament) {
            newSupplyMedicament.setMedicament(medicament);
            return this;
        }

        public Builder supply(Supply supply) {
            newSupplyMedicament.setSupply(supply);
            return this;
        }

        public Builder count(Integer count) {
            newSupplyMedicament.setCount(count);
            return this;
        }

        public SupplyMedicament build() {
            return newSupplyMedicament;
        }
    }
}