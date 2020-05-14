package medicament;

import com.ulstu.pharmacy.pmmsl.medicament.binding.MedicamentBindingModel;
import com.ulstu.pharmacy.pmmsl.medicament.ejb.MedicamentEjbLocal;
import com.ulstu.pharmacy.pmmsl.medicament.view.MedicamentViewModel;
import helper.MessagesHelper;
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

    private String s;

    @PostConstruct
    public void initValues() {
        this.medicaments = medicamentEjb.getAll();
        this.selectedMedicament = MedicamentViewModel.builder().build();
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

    public void createOrUpdate() {
        try {
            this.medicamentEjb.createOrUpdate(
                    MedicamentBindingModel.builder()
                            .id(selectedMedicament.getId())
                            .name(selectedMedicament.getName())
                            .price(selectedMedicament.getPrice())
                            .description(selectedMedicament.getDescription())
                            .contraindications(selectedMedicament.getContraindications())
                            .instruction(selectedMedicament.getInstruction())
                            .build()
            );
        } catch (Exception ex) {
            MessagesHelper.errorMessage(ex);
        }
    }

    public String getS() {
        System.out.println("GET " + s);
        return s;
    }

    public void setS(String s) {
        System.out.println("SET " + s);
        this.s = s;
    }
}