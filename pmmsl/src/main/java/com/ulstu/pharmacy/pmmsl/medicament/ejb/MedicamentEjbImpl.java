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
        Optional<Medicament> medicament = medicamentDao.findById(id);

        return medicament.map(medicamentMapper::toViewModel).orElse(null);
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
        if(this.isArgsValidForCreate(name, description, contraindications, instruction, price)) {
            /*Medicament medicament = Medicament
                    .builder()
                    .name(name)
                    .
        } else {
*/
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

    private boolean isArgsValidForCreate(String name, String description, String contraindications, String instruction, BigDecimal price) {
        return  Objects.nonNull(name) &&
                !this.medicamentDao.existByName(name) &&
                Objects.nonNull(description) &&
                Objects.nonNull(contraindications) &&
                Objects.nonNull(instruction) &&
                Objects.nonNull(price) &&
                price.signum() == 1; // Число положительное.
    }
}
