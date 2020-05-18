package com.ulstu.pharmacy.pmmsl.medservice.entity;

import com.ulstu.pharmacy.pmmsl.common.entity.AbstractEntity;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "medical_service")
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@SequenceGenerator(name = "default_gen", sequenceName = "medical_service_seq", allocationSize = 1)
/** Медицинская услуга. */
public class MedicalService extends AbstractEntity<Long> {

    @Column(name = "provision_date", nullable = true)
    private Timestamp provisionDate;

    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            mappedBy = "medicalService")
    private Set<MedicamentMedicalService> medicamentMedicalServices;

    @Transient
    private BigDecimal sumPrice;

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
            : BigDecimal.ZERO;
        }
        return sumPrice;
    }

    public static MedicalService.Builder builder() {
        return new MedicalService.Builder();
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

        public Builder medicamentMedicalServices(Set<MedicamentMedicalService> medicamentMedicalServices) {
            newMedicalService.setMedicamentMedicalServices(medicamentMedicalServices);
            return this;
        }

        public MedicalService build() {
            return newMedicalService;
        }
    }
}