package com.ulstu.pharmacy.pmmsl.supply.entity;

import com.ulstu.pharmacy.pmmsl.common.entity.AbstractEntity;
import com.ulstu.pharmacy.pmmsl.medicament.entity.Medicament;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class SupplyMedicament extends AbstractEntity<Long> {

    @ToString.Exclude
    @ManyToOne(optional = false)
    private Supply supply;

    @ManyToOne(optional = false)
    private Medicament medicament;

    @Column(nullable = false, updatable = false)
    @EqualsAndHashCode.Exclude
    private Integer count;

    @Override
    @SequenceGenerator(name = "supply_medicament_seq", sequenceName = "supply_medicament_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "supply_medicament_seq")
    public Long getId() {
        return super.getId();
    }

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