package com.ulstu.pharmacy.pmmsl.medicament.ejb;

import com.ulstu.pharmacy.pmmsl.common.exception.CrudOperationException;
import com.ulstu.pharmacy.pmmsl.medicament.dao.MedicamentDao;
import com.ulstu.pharmacy.pmmsl.medicament.entity.Medicament;
import com.ulstu.pharmacy.pmmsl.medicament.mapper.MedicamentMapper;
import com.ulstu.pharmacy.pmmsl.medicament.view.MedicamentViewModel;
import lombok.AllArgsConstructor;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
     * Метод создания медикамента.
     *
     * @param name              название медикамента.
     * @param description       описание медикамента.
     * @param contraindications противопоказания.
     * @param instruction       инструкция по применению.
     * @param price             цена медикамнта.
     */
    @Override
    public void create(String name,
                       String description,
                       String contraindications,
                       String instruction,
                       BigDecimal price) throws CrudOperationException {
        String validationErrors = this.validateCreateArgs(name, description, contraindications, instruction, price);
        if(Objects.nonNull(validationErrors)) {
            throw new CrudOperationException(validationErrors);
        } else {
            this.medicamentDao.save(
                    Medicament.builder()
                            .name(name)
                            .contraindications(contraindications)
                            .description(description)
                            .instruction(instruction)
                            .price(price)
                            .build()
            );
        }
    }

    /**
     * Метод изменения медикамента.
     *
     * @param medicamentViewModel
     */
    @Override
    public void update(MedicamentViewModel medicamentViewModel) {

    }

    /**
     * Метод удаления медикамента по его id.
     *
     * @param id
     */
    @Override
    public void delete(Long id) {

    }

    /**
     * Валидация аргументов для создания медикамента.
     * @param name
     * @param description
     * @param contraindications
     * @param instruction
     * @param price
     * @return
     */
    private String validateCreateArgs(String name, String description, String contraindications, String instruction, BigDecimal price) {
        StringBuilder errors = new StringBuilder();

        if (Objects.isNull(name)) {
            errors.append("Name is null; ");
        }
        if (Objects.isNull(description)) {
            errors.append("Description is null; ");
        }
        if (Objects.isNull(contraindications)) {
            errors.append("Contraindications are null; ");
        }
        if (Objects.isNull(instruction)) {
            errors.append("Instruction is null; ");
        }
        if (Objects.isNull(price)) {
            errors.append("Price is null; ");
        } else {
            if (price.signum() == -1) {
                errors.append("Price is negative ; ");
            }
        }

        return errors.toString().isEmpty() ? null : errors.toString();
    }

    private String validateUpdateArgs(Long id, String name, String description, String contraindications, String instruction, BigDecimal price) {
        StringBuilder errors = new StringBuilder();

        if (Objects.isNull(id)) {
            errors.append("Id is null; ");
        } else {
            if (!this.medicamentDao.existsById(id)) {
                errors.append("Medicament with id = " + id + " not exist; ");
            }
        }
        String otherArgsErrors = this.validateCreateArgs(name, description, contraindications, instruction, price);
        if (Objects.nonNull(otherArgsErrors)) {
            errors.append(otherArgsErrors);
        }

        return errors.toString().isEmpty() ? null : errors.toString();
    }
}
