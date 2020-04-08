package com.ulstu.pharmacy.pmmsl.medservice.entity;

import com.ulstu.pharmacy.pmmsl.common.entity.AbstractEntity;
import com.ulstu.pharmacy.pmmsl.medicament.entity.Medicament;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
/** Связь медикамента и медицинской услуги. */
public class MedicamentMedicalService extends AbstractEntity<Long> {

    @ManyToOne
    private Medicament medicament;

    @ManyToOne
    private MedicalService medicalService;

    private Integer count;

    private BigDecimal price;

    @Override
    @SequenceGenerator(name = "medicament_medical_service_seq", sequenceName = "medicament_medical_service_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "medicament_medical_service_seq")
    public Long getId() {
        return super.getId();
    }
}