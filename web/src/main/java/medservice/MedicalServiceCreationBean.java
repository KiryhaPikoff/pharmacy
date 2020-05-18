package medservice;

import com.ulstu.pharmacy.pmmsl.medicament.binding.MedicamentCountBindingModel;
import com.ulstu.pharmacy.pmmsl.medicament.ejb.MedicamentEjbLocal;
import com.ulstu.pharmacy.pmmsl.medicament.view.MedicamentViewModel;
import com.ulstu.pharmacy.pmmsl.medservice.ejb.MedicalServiceEjbLocal;
import com.ulstu.pharmacy.pmmsl.medservice.view.MedicalServiceViewModel;
import com.ulstu.pharmacy.pmmsl.pharmacy.ejb.PharmacyEjbLocal;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@RequestScoped
@ManagedBean
public class MedicalServiceCreationBean {

    private MedicalServiceEjbLocal medicalServiceEjb;

    private PharmacyEjbLocal pharmacyEjb;

    private Map<MedicamentViewModel, Integer> medicamentsInstock;

    private Map<MedicamentViewModel, Integer> selectedMedicalServices;

    private Integer count;

    @PostConstruct
    public void initValues() {
        this.medicamentsInstock = pharmacyEjb.getPharmacyMedicamentsInStock();
        this.selectedMedicalServices = new LinkedHashMap<>();
    }

    @Inject
    public void setMedicalServiceEjb(MedicalServiceEjbLocal medicalServiceEjb) {
        this.medicalServiceEjb = medicalServiceEjb;
    }

    @Inject
    public void setMedicamentEjb(PharmacyEjbLocal pharmacyEjb) {
        this.pharmacyEjb = pharmacyEjb;
    }

    public void addToSelected(MedicamentViewModel medicament) {
        if (selectedMedicalServices.containsKey(medicament)) {
            selectedMedicalServices.put(
                    medicament,
                    selectedMedicalServices.get(medicament) + 1);
        } else {
            selectedMedicalServices.put(medicament, 1);
        }
    }

    public void removeFromSelected(MedicamentViewModel medicament) {
        selectedMedicalServices.remove(medicament);
    }

    public void changeCount(MedicamentViewModel medicament) {
        selectedMedicalServices.put(medicament, this.count);
    }
}