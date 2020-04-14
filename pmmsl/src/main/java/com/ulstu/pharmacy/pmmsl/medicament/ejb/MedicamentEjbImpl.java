package com.ulstu.pharmacy.pmmsl.medicament.ejb;

import com.ulstu.pharmacy.pmmsl.common.exception.CrudOperationException;
import com.ulstu.pharmacy.pmmsl.medicament.binding.MedicamentBindingModel;
import com.ulstu.pharmacy.pmmsl.medicament.dao.MedicamentDao;
import com.ulstu.pharmacy.pmmsl.medicament.entity.Medicament;
import com.ulstu.pharmacy.pmmsl.medicament.mapper.MedicamentMapper;
import com.ulstu.pharmacy.pmmsl.medicament.view.MedicamentViewModel;
import lombok.AllArgsConstructor;

import javax.inject.Inject;
import javax.websocket.OnClose;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@AllArgsConstructor(onConstructor = @__({@Inject}))
public class MedicamentEjbImpl implements MedicamentEjbRemote {

    private final MedicamentDao medicamentDao;

    private final MedicamentMapper medicamentMapper;

    /**
     * Метод получения медикамента по его id.
     *
     * @param id
     */
    @Override
    public MedicamentViewModel getById(Long id) {
        return medicamentDao.findById(id)
                .map(medicamentMapper::toViewModel)
                .orElse(MedicamentViewModel.builder().build());
    }

    /**
     * Метод получения списка всех медикаментов.
     */
    @Override
    public List<MedicamentViewModel> getAll() {
        return medicamentDao.getAll().stream()
                .map(medicamentMapper::toViewModel)
                .collect(Collectors.toList());
    }

    /**
     * Метод предназначени для сохранения (если id = null) или
     * обновления (id отличен от null) медикамента.
     *
     * @param medicamentBindingModel
     */
    @Override
    public void addOrUpdate(MedicamentBindingModel medicamentBindingModel) {
        if(Objects.isNull(medicamentBindingModel)) {
            throw new CrudOperationException("MedicamentBindingModel is null!");
        }

        if(Objects.isNull(medicamentBindingModel.getId())) {
            String validationErrors = this.validateCreateArgs(medicamentBindingModel);
            if(Objects.nonNull(validationErrors)) {
                throw new CrudOperationException(validationErrors);
            }
            this.add(medicamentBindingModel);
        } else {
            String validationErrors = this.validateUpdateArgs(medicamentBindingModel);
            if(Objects.nonNull(validationErrors)) {
                throw new CrudOperationException(validationErrors);
            }
            this.update(medicamentBindingModel);
        }
    }

    /**
     * Метод удаления медикамента по его id.
     *
     * @param id
     */
    @Override
    public void delete(Long id) {

        if(Objects.isNull(id)) {
            throw new CrudOperationException("Id is null");
        }

        if(!this.medicamentDao.existsById(id)) {
            throw new CrudOperationException("Medicament with an id = " + id + " not exist!");
        } else {
            this.medicamentDao.deleteById(id);
        }
    }

    /**
     * Метод сохранения медикамента в БД.
     * @param medicamentBindingModel
     */
    private void add(MedicamentBindingModel medicamentBindingModel) {
        this.medicamentDao.save(
                Medicament.builder()
                        .name(medicamentBindingModel.getName())
                        .description(medicamentBindingModel.getDescription())
                        .instruction(medicamentBindingModel.getInstruction())
                        .contraindications(medicamentBindingModel.getContraindications())
                        .price(medicamentBindingModel.getPrice())
                        .build()
        );
    }

    /**
     * Метод обновления медикамента в БД.
     * @param medicamentBindingModel
     */
    private void update(MedicamentBindingModel medicamentBindingModel) {
        Medicament updateMedicament = Medicament.builder()
                .name(medicamentBindingModel.getName())
                .description(medicamentBindingModel.getDescription())
                .instruction(medicamentBindingModel.getInstruction())
                .contraindications(medicamentBindingModel.getContraindications())
                .price(medicamentBindingModel.getPrice())
                .build();
        updateMedicament.setId(medicamentBindingModel.getId());
        this.medicamentDao.update(updateMedicament);
    }

    /**
     * Валидация аргументов для создания медикамента.
     * @return если строка null - нет ошибок. В противном случае все ошибки перечислены.
     */
    private String validateCreateArgs(MedicamentBindingModel medicamentBindingModel) {
        StringBuilder errors = new StringBuilder();

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

        return errors.toString().isEmpty() ? null : errors.toString();
    }

    /**
     * Валидация аргументов для изменения медикамента.
     * @return если строка null - нет ошибок. В противном случае все ошибки перечислены.
     */
    private String validateUpdateArgs(MedicamentBindingModel medicamentBindingModel) {
        StringBuilder errors = new StringBuilder();

        if (Objects.isNull(medicamentBindingModel.getId())) {
            errors.append("Id is null; ");
        } else {
            if (this.medicamentDao.existsById(medicamentBindingModel.getId())) {
                errors.append("Medicament with an id = ")
                        .append(medicamentBindingModel.getId())
                        .append(" not exist; ");
            }
        }
        String otherArgsErrors = this.validateCreateArgs(medicamentBindingModel);
        if (Objects.nonNull(otherArgsErrors)) {
            errors.append(otherArgsErrors);
        }

        return errors.toString().isEmpty() ? null : errors.toString();
    }
}
