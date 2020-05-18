package medservice;

import com.ulstu.pharmacy.pmmsl.medicament.view.MedicamentViewModel;
import com.ulstu.pharmacy.pmmsl.medservice.ejb.MedicalServiceEjbLocal;
import com.ulstu.pharmacy.pmmsl.medservice.view.MedicalServiceViewModel;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import java.util.Set;


@Getter
@Setter
@RequestScoped
@ManagedBean
public class MedicalServiceViewBean {

    private MedicalServiceEjbLocal medicalServiceEjb;

    private Long medicalServiceId;

    private MedicalServiceViewModel medicalService;

    @Inject
    public void setMedicalServiceEjb(MedicalServiceEjbLocal medicalServiceEjb) {
        this.medicalServiceEjb = medicalServiceEjb;
    }

    public void initMedicalServiceById() {
        this.medicalService = medicalServiceEjb
                .getByIds(Set.of(medicalServiceId)).stream()
                .findFirst()
                .orElse(MedicalServiceViewModel.builder().build());
    }
}