package com.ulstu.pharmacy.pmmsl.pharmacy.entity;

import com.ulstu.pharmacy.pmmsl.common.entity.AbstractEntity;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
/** Аптека. */
public class Pharmacy extends AbstractEntity<Long> {

    private String name;

    @OneToMany
    private List<PharmacyMedicament> pharmacyMedicaments;

    @Override
    @SequenceGenerator(name = "pharmacy_seq", sequenceName = "pharmacy_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pharmacy_seq")
    public Long getId() {
        return super.getId();
    }
}