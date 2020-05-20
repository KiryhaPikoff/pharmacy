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

    private List<MedicamentCountViewModel> medicamentsInstock;

    private List<MedicamentCountViewModel> selectedMedicaments;

    private Integer count;

    @PostConstruct
    public void initValues() {
        this.medicamentsInstock = this.toList(
                pharmacyEjb.getPharmacyMedicamentsInStock()
        );
        this.selectedMedicaments = new LinkedList<>();
    }

    @Inject
    public void setMedicalServiceEjb(MedicalServiceEjbLocal medicalServiceEjb) {
        this.medicalServiceEjb = medicalServiceEjb;
    }

    @Inject
    public void setMedicamentEjb(PharmacyEjbLocal pharmacyEjb) {
        this.pharmacyEjb = pharmacyEjb;
    }

    public void addToSelected(MedicamentCountViewModel medicament) {
        System.out.println("COUNT " + this.count);
        System.out.println("ADDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD");
        if (selectedMedicaments.contains(medicament)) {
            selectedMedicaments.stream()
                    .filter(medicamentInList -> medicamentInList.equals(medicament))
                    .findFirst()
                    .ifPresent(foundMedicament -> foundMedicament.setCount(foundMedicament.getCount() + 1));
        } else {
            selectedMedicaments.add(
                    MedicamentCountViewModel.builder()
                            .medicament(medicament.getMedicament())
                            .count(medicament.getCount())
                            .build()
            );
        }
    }

    public void removeFromSelected(MedicamentCountViewModel medicament) {
        selectedMedicaments.remove(medicament);
    }

    public String createMedicalService() {
        try {
            this.medicalServiceEjb.create(this.toSet(this.selectedMedicaments));
        } catch (Exception ex) {
            MessagesHelper.errorMessage(ex);
        }
        return "successMedicalServiceCreation";
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