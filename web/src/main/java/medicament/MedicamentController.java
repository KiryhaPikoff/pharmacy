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
import java.util.Objects;

@Getter
@Setter
@ViewScoped
@ManagedBean
public class MedicamentController implements Serializable {

    private MedicamentEjbLocal medicamentEjb;

    private List<MedicamentViewModel> medicaments;

    private MedicamentViewModel selectedMedicament;

    private Long idForUpdate;

    @PostConstruct
    public void initValues() {
        this.medicaments = medicamentEjb.getAll();
        this.selectedMedicament = MedicamentViewModel.builder().build();
    }

    @Inject
    public void setMedicamentEjb(MedicamentEjbLocal medicamentEjb) {
        this.medicamentEjb = medicamentEjb;
    }

    public String deleteMedicament() {
        this.medicamentEjb.delete(
                selectedMedicament.getId()
        );
        MessagesHelper.infoMessage("Медикамент" + selectedMedicament.getName() + " успешно удален!");
        return "refresh";
    }

    public String createOrUpdate() {
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
            return "fail";
        }
        MessagesHelper.infoMessage("Медикамент успешно добавлен/обновлен!");
        return "success";
    }

    public void initMedicamentById() {
        if(this.idForUpdate == null) {
            this.selectedMedicament = MedicamentViewModel.builder().build();
        } else {
            this.selectedMedicament = this.medicamentEjb.getById(
                    this.idForUpdate
            );
        }
    }
}