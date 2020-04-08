package com.ulstu.pharmacy.pmmsl.pharmacy.entity;

import com.ulstu.pharmacy.pmmsl.common.entity.AbstractEntity;
import com.ulstu.pharmacy.pmmsl.medicament.entity.Medicament;
import com.ulstu.pharmacy.pmmsl.medservice.entity.MedicamentMedicalService;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
/** Свзяь аптеки и медикамента. */
public class PharmacyMedicament extends AbstractEntity<Long> {

    @ManyToOne
    private Medicament medicament;

    @ManyToOne
    private Pharmacy pharmacy;

    @OneToMany
    @EqualsAndHashCode.Exclude
    private List<MedicamentMedicalService> medicamentMedicalServices;

    @EqualsAndHashCode.Exclude
    private Integer count;

    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp receiptDate;

    @Override
    @SequenceGenerator(name = "pharmacy_medicament_seq", sequenceName = "pharmacy_medicament_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pharmacy_medicament_seq")
    public Long getId() {
        return super.getId();
    }
}