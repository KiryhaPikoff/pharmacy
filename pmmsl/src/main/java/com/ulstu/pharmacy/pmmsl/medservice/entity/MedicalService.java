package com.ulstu.pharmacy.pmmsl.medservice.entity;

import com.ulstu.pharmacy.pmmsl.common.entity.AbstractEntity;
import com.ulstu.pharmacy.pmmsl.medicament.entity.Medicament;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
/** Медицинская услуга. */
public class MedicalService extends AbstractEntity<Long> {

    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp provisionDate;

    @OneToMany
    private List<MedicamentMedicalService> medicamentMedicalServices;

    @Transient
    private BigDecimal sumPrice;

    @Override
    @SequenceGenerator(name = "medical_service_seq", sequenceName = "medical_service_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "medical_service_seq")
    public Long getId() {
        return super.getId();
    }

    /**
     * Метод подсчитывающий цену медицинской услуги.
     *
     * @return
     */
    public BigDecimal getSumPrice() {
        if (Objects.isNull(sumPrice)) {
            sumPrice = Objects.nonNull(medicamentMedicalServices) && !medicamentMedicalServices.isEmpty() ?
                    medicamentMedicalServices.stream()
                    .map(medicamentMedicalService ->
                            new BigDecimal(
                                    medicamentMedicalService.getPrice().intValue() *
                                            medicamentMedicalService.getCount()
                            )
                    )
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
            : null;
        }
        return sumPrice;
    }

    public static class Builder {
        private MedicalService newMedicalService;

        public Builder() {
            newMedicalService = new MedicalService();
        }

        public Builder id(Long id) {
            newMedicalService.setId(id);
            return this;
        }

        public Builder provisionDate(Timestamp provisionDate) {
            newMedicalService.setProvisionDate(provisionDate);
            return this;
        }

        public Builder medicamentMedicalServices(List<MedicamentMedicalService> medicamentMedicalServices) {
            newMedicalService.setMedicamentMedicalServices(medicamentMedicalServices);
            return this;
        }

        public MedicalService build() {
            return newMedicalService;
        }
    }
}