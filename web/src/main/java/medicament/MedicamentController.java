package medicament;

import com.ulstu.pharmacy.pmmsl.medicament.ejb.MedicamentEjbLocal;
import com.ulstu.pharmacy.pmmsl.medicament.view.MedicamentViewModel;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ViewScoped
@ManagedBean
public class MedicamentController implements Serializable {


    private MedicamentEjbLocal medicamentEjb;

    private List<MedicamentViewModel> medicaments;

    private MedicamentViewModel selectedMedicament;

    @PostConstruct
    public void initValues() {
        this.medicaments = medicamentEjb.getAll();
    }

    @Inject
    public void setMedicamentEjb(MedicamentEjbLocal medicamentEjb) {
        this.medicamentEjb = medicamentEjb;
    }

    public void deleteMedicament() {
        this.medicamentEjb.delete(
                selectedMedicament.getId()
        );
    }
}