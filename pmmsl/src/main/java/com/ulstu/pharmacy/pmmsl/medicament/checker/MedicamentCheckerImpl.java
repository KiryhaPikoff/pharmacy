package com.ulstu.pharmacy.pmmsl.medicament.checker;

import com.ulstu.pharmacy.pmmsl.medicament.binding.MedicamentBindingModel;
import com.ulstu.pharmacy.pmmsl.medicament.dao.MedicamentDao;

import javax.inject.Inject;
import java.util.Objects;

public class MedicamentCheckerImpl implements MedicamentChecker {

    private final MedicamentDao medicamentDao;

    @Inject
    public MedicamentCheckerImpl(MedicamentDao medicamentDao) {
        this.medicamentDao = medicamentDao;
    }

    /**
     * Проверка параметров модели, необходимых для создания медикамента.
     *
     * @param medicamentBindingModel
     * @return
     */
    @Override
    public String createCheck(MedicamentBindingModel medicamentBindingModel) {
        StringBuilder errors = new StringBuilder();

        if (Objects.isNull(medicamentBindingModel)) {
            return "MedicamentBindingModel is null; ";
        }

        if (Objects.isNull(medicamentBindingModel.getName())) {
            errors.append("Name is null; ");
        } else {
            if(this.medicamentDao.existByName(medicamentBindingModel.getName())) {
                errors.append("Medicament with a name = ")
                        .append(medicamentBindingModel.getName())
                        .append(" already exist; ");
            }
        }
        if (Objects.isNull(medicamentBindingModel.getDescription())) {
            errors.append("Description is null; ");
        }
        if (Objects.isNull(medicamentBindingModel.getContraindications())) {
            errors.append("Contraindications are null; ");
        }
        if (Objects.isNull(medicamentBindingModel.getInstruction())) {
            errors.append("Instruction is null; ");
        }
        if (Objects.isNull(medicamentBindingModel.getPrice())) {
            errors.append("Price is null; ");
        } else {
            if (medicamentBindingModel.getPrice().signum() == -1) {
                errors.append("Price is negative ; ");
            }
        }

        return errors.toString();
    }

    /**
     * Проверка параметров модели, необходимых для обновления медикамента.
     *
     * @param medicamentBindingModel
     * @return
     */
    @Override
    public String updateCheck(MedicamentBindingModel medicamentBindingModel) {
        StringBuilder errors = new StringBuilder();

        if (Objects.isNull(medicamentBindingModel)) {
            return "MedicamentBindingModel is null; ";
        }

        errors.append(
                this.existById(medicamentBindingModel.getId())
        );

        errors.append(
                this.createCheck(medicamentBindingModel)
        );

        return errors.toString();
    }

    /**
     * Проверка условий, необходимых для удаления медикамета.
     *
     * @param medicamentId id удаляемого медикамента.
     * @return
     */
    @Override
    public String deleteCheck(Long medicamentId) {
        return this.existById(medicamentId);
    }

    private String existById(Long medicamentId) {
        StringBuilder errors = new StringBuilder();

        if(Objects.isNull(medicamentId)) {
            errors.append("Id is null; ");
        } else {
            if(!this.medicamentDao.existsById(medicamentId)) {
                errors.append("Medicament with an id = ")
                        .append(medicamentId)
                        .append(" not exist!");
            }
        }

        return errors.toString();
    }
}
