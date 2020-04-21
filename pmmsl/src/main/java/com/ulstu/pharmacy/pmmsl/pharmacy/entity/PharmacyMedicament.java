package com.ulstu.pharmacy.pmmsl.pharmacy.entity;

import com.ulstu.pharmacy.pmmsl.common.entity.AbstractEntity;
import com.ulstu.pharmacy.pmmsl.medicament.entity.Medicament;
import com.ulstu.pharmacy.pmmsl.medservice.entity.MedicamentMedicalService;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    private Timestamp receiptDate;

    @Override
    @SequenceGenerator(name = "pharmacy_medicament_seq", sequenceName = "pharmacy_medicament_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pharmacy_medicament_seq")
    public Long getId() {
        return super.getId();
    }

    public static class Builder {
        private PharmacyMedicament newPharmacyMedicament;

        public Builder() {
            newPharmacyMedicament = new PharmacyMedicament();
        }

        public Builder id(Long id) {
            newPharmacyMedicament.setId(id);
            return this;
        }

        public Builder medicament(Medicament medicament) {
            newPharmacyMedicament.setMedicament(medicament);
            return this;
        }

        public Builder pharmacy(Pharmacy pharmacy) {
            newPharmacyMedicament.setPharmacy(pharmacy);
            return this;
        }

        public Builder medicamentMedicalServices(List<MedicamentMedicalService> medicamentMedicalServices) {
            newPharmacyMedicament.setMedicamentMedicalServices(medicamentMedicalServices);
            return this;
        }

        public Builder count(Integer count) {
            newPharmacyMedicament.setCount(count);
            return this;
        }

        public Builder receiptDate(Timestamp receiptDate) {
            newPharmacyMedicament.setReceiptDate(receiptDate);
            return this;
        }

        public PharmacyMedicament build() {
            return newPharmacyMedicament;
        }
    }
}