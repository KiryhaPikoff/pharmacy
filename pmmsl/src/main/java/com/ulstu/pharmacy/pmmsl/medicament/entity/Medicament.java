package com.ulstu.pharmacy.pmmsl.medicament.entity;

import com.ulstu.pharmacy.pmmsl.common.entity.AbstractEntity;
import com.ulstu.pharmacy.pmmsl.medservice.entity.MedicamentMedicalService;
import com.ulstu.pharmacy.pmmsl.pharmacy.entity.PharmacyMedicament;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
/** Медикамент. */
public class Medicament extends AbstractEntity<Long> {

    private String name;

    private String description;

    private String contraindications;

    private String instruction;

    private BigDecimal price;

    @OneToMany
    @EqualsAndHashCode.Exclude
    private List<PharmacyMedicament> pharmacyMedicaments;

    @OneToMany
    @EqualsAndHashCode.Exclude
    private List<MedicamentMedicalService> medicamentMedicalServices;

    @Override
    @SequenceGenerator(name = "medicament_seq", sequenceName = "medicament_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "medicament_seq")
    public Long getId() {
        return super.getId();
    }
}