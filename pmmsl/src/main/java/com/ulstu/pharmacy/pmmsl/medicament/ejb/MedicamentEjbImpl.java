package com.ulstu.pharmacy.pmmsl.medicament.ejb;

import com.ulstu.pharmacy.pmmsl.common.exception.CrudOperationException;
import com.ulstu.pharmacy.pmmsl.medicament.binding.MedicamentBindingModel;
import com.ulstu.pharmacy.pmmsl.medicament.checker.MedicamentChecker;
import com.ulstu.pharmacy.pmmsl.medicament.dao.MedicamentDao;
import com.ulstu.pharmacy.pmmsl.medicament.mapper.MedicamentMapper;
import com.ulstu.pharmacy.pmmsl.medicament.view.MedicamentViewModel;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Stateless
public class MedicamentEjbImpl implements MedicamentEjbLocal {

    private final MedicamentDao medicamentDao;

    private final MedicamentMapper medicamentMapper;

    private final MedicamentChecker medicamentChecker;

    @Inject
    public MedicamentEjbImpl(MedicamentDao medicamentDao,
                             MedicamentMapper medicamentMapper,
                             MedicamentChecker medicamentChecker) {
        this.medicamentDao = medicamentDao;
        this.medicamentMapper = medicamentMapper;
        this.medicamentChecker = medicamentChecker;
    }

    /**
     * Метод получения медикамента по его id.
     *
     * @param id
     */
    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public MedicamentViewModel getById(Long id) {
        return medicamentDao.findById(id)
                .map(medicamentMapper::toViewModel)
                .orElse(MedicamentViewModel.builder().build());
    }

    /**
     * Метод получения списка всех медикаментов.
     */
    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
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
    @Transactional(Transactional.TxType.SUPPORTS)
    public void createOrUpdate(MedicamentBindingModel medicamentBindingModel) {
        if (Objects.isNull(medicamentBindingModel)) {
            throw new CrudOperationException("MedicamentBindingModel is null; ");
        }
        if (Objects.isNull(medicamentBindingModel.getId())) {
            String errors = this.medicamentChecker.createCheck(medicamentBindingModel);
            if(!errors.isBlank()) {
                throw new CrudOperationException(errors);
            }
            this.create(medicamentBindingModel);
        } else {
            String errors = this.medicamentChecker.updateCheck(medicamentBindingModel);
            if(!errors.isBlank()) {
                throw new CrudOperationException(errors);
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
    @Transactional(Transactional.TxType.SUPPORTS)
    public void delete(Long id) {
        String validationErrors = this.medicamentChecker.deleteCheck(id);
        if (!validationErrors.isBlank()) {
            throw new CrudOperationException(validationErrors);
        }
        this.medicamentDao.deleteById(id);
    }

    /**
     * Метод сохранения медикамента в БД.
     * @param medicamentBindingModel
     */
    private void create(MedicamentBindingModel medicamentBindingModel) {
        this.medicamentDao.save(
                this.medicamentMapper.toEntity(medicamentBindingModel)
        );
    }

    /**
     * Метод обновления медикамента в БД.
     * @param medicamentBindingModel
     */
    private void update(MedicamentBindingModel medicamentBindingModel) {
        this.medicamentDao.update(
                this.medicamentMapper.toEntity(medicamentBindingModel)
        );
    }
}
