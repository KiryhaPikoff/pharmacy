package com.ulstu.pharmacy.web.medservice;

import com.ulstu.pharmacy.pmmsl.medicament.binding.MedicamentCountBindingModel;
import com.ulstu.pharmacy.pmmsl.medicament.view.MedicamentViewModel;
import com.ulstu.pharmacy.pmmsl.medservice.ejb.MedicalServiceEjbLocal;
import com.ulstu.pharmacy.pmmsl.pharmacy.ejb.PharmacyEjbLocal;
import com.ulstu.pharmacy.web.helper.MessagesHelper;
import com.ulstu.pharmacy.web.medservice.model.MedicamentCountViewModel;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@ViewScoped
@ManagedBean
public class MedicalServiceCreationBean {

    private MedicalServiceEjbLocal medicalServiceEjb;

    private PharmacyEjbLocal pharmacyEjb;

    private List<MedicamentCountViewModel> medicamentsInStock;

    private List<MedicamentCountViewModel> selectedMedicaments;

    private MedicamentCountViewModel selectedMedicament;

    private Integer count;

    @PostConstruct
    public void initValues() {
        this.medicamentsInStock = this.toList(
                pharmacyEjb.getPharmacyMedicamentsInStock()
        );
        this.selectedMedicaments = new LinkedList<>();
        this.count = 0;
    }

    @Inject
    public void setMedicalServiceEjb(MedicalServiceEjbLocal medicalServiceEjb) {
        this.medicalServiceEjb = medicalServiceEjb;
    }

    @Inject
    public void setMedicamentEjb(PharmacyEjbLocal pharmacyEjb) {
        this.pharmacyEjb = pharmacyEjb;
    }

    public void addToSelected() {
        MedicamentCountViewModel medicamentFromStock = this.getMedicamentFromStock(selectedMedicament);
        if (medicamentFromStock.getCount() > 0) {
            medicamentFromStock.setCount(medicamentFromStock.getCount() - this.count);
            if (selectedMedicaments.contains(selectedMedicament)) {
                MedicamentCountViewModel foundMedicament = this.getMedicamentFromSelected(selectedMedicament);
                foundMedicament.setCount(foundMedicament.getCount() + this.count);
            } else {
                selectedMedicaments.add(
                        MedicamentCountViewModel.builder()
                                .medicament(selectedMedicament.getMedicament())
                                .count(this.count)
                                .build()
                );
            }
        }
    }

    private MedicamentCountViewModel getMedicamentFromSelected(MedicamentCountViewModel medicament) {
        return selectedMedicaments.stream()
                .filter(medicamentInList -> medicamentInList.equals(medicament))
                .findFirst().get();
    }

    private MedicamentCountViewModel getMedicamentFromStock(MedicamentCountViewModel medicament) {
        return medicamentsInStock.stream()
                .filter(medicamentInList -> medicamentInList.equals(medicament))
                .findFirst().get();
    }

    public void removeFromSelected(MedicamentCountViewModel medicament) {
        selectedMedicaments.remove(medicament);
        medicamentsInStock.stream()
                .filter(medicamentInList -> medicamentInList.equals(medicament))
                .findFirst()
                .ifPresent(foundMedicament -> foundMedicament.setCount(foundMedicament.getCount() + medicament.getCount()));
    }

    public String createMedicalService() {
        try {
            if (this.selectedMedicaments.isEmpty()) {
                throw new RuntimeException(new Throwable("Список медикаментов пустой! Выберите медикаменты."));
            }
            this.medicalServiceEjb.create(this.toSet(this.selectedMedicaments));
            return "successMedicalServiceCreation";
        } catch (Exception ex) {
            MessagesHelper.errorMessage(ex);
        }
       return "fail";
    }

    private List<MedicamentCountViewModel> toList(Map<MedicamentViewModel, Integer> medicamentsWithCount) {
        return medicamentsWithCount.entrySet().stream()
                .map(medicamentWithCount ->
                        MedicamentCountViewModel.builder()
                                .medicament(medicamentWithCount.getKey())
                                .count(medicamentWithCount.getValue())
                                .build())
                .collect(Collectors.toList());
    }

    private Set<MedicamentCountBindingModel> toSet(List<MedicamentCountViewModel> medicamentsWithCount) {
        return medicamentsWithCount.stream()
                .map(medicamentCount ->
                        MedicamentCountBindingModel.builder()
                                .medicamentId(medicamentCount.getMedicament().getId())
                                .count(medicamentCount.getCount())
                                .build())
                .collect(Collectors.toSet());
    }
}