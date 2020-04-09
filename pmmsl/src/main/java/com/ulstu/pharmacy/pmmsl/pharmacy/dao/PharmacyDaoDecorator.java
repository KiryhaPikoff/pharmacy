package com.ulstu.pharmacy.pmmsl.pharmacy.dao;

import com.ulstu.pharmacy.pmmsl.common.annotation.MainImplementation;
import com.ulstu.pharmacy.pmmsl.common.exception.DatabaseOperationException;
import com.ulstu.pharmacy.pmmsl.pharmacy.entity.Pharmacy;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@MainImplementation
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class PharmacyDaoDecorator extends PharmacyDao {

    private final PharmacyDao pharmacyDao;

    private final Logger logger = LoggerFactory.getLogger(PharmacyDaoDecorator.class);

    @Override
    public Class<Pharmacy> getEntityClass() {
        return super.getEntityClass();
    }

    @Override
    public Optional<Pharmacy> findById(Long id) {
        try {
            logger.debug("Invocation findById() method with an id = " + id);
            return super.findById(id);
        } catch (Exception e) {
            logger.error("Fail invocation findById() with an id = " + id  + " method with cause: ", e.getCause());
            throw new DatabaseOperationException(e.getCause());
        }
    }

    @Override
    public void save(Pharmacy entity) {
        try {
            logger.debug("Invocation save() method with a value = " + entity);
            super.save(entity);
        } catch (Exception e) {
            logger.error("Fail invocation save() with a value = " + entity  + " method with cause: ", e.getCause());
            throw new DatabaseOperationException(e.getCause());
        }
    }

    @Override
    public void update(Pharmacy entity) {
        try {
            logger.debug("Invocation update() method with a value = " + entity);
            super.update(entity);
        } catch (Exception e) {
            logger.error("Fail invocation update() with a value = " + entity  + " method with cause: ", e.getCause());
            throw new DatabaseOperationException(e.getCause());
        }
    }

    @Override
    public void deleteById(Long id) {
        try {
            logger.debug("Invocation deleteById() method with an id = " + id);
            super.deleteById(id);
        } catch (Exception e) {
            logger.error("Fail invocation deleteById() with an id = " + id  + " method with cause: ", e.getCause());
            throw new DatabaseOperationException(e.getCause());
        }
    }

    @Override
    public void delete(Pharmacy entity) {
        try {
            logger.debug("Invocation delete() method with a value = " + entity);
            super.delete(entity);
        } catch (Exception e) {
            logger.error("Fail invocation delete() with a value = " + entity  + " method with cause: ", e.getCause());
            throw new DatabaseOperationException(e.getCause());
        }
    }

    @Override
    public boolean existsById(Long id) {
        try {
            logger.debug("Invocation existsById() method with an id = " + id);
            return super.existsById(id);
        } catch (Exception e) {
            logger.error("Fail invocation existsById() with an id = " + id  + " method with cause: ", e.getCause());
            throw new DatabaseOperationException(e.getCause());
        }
    }

    @Override
    public TypedQuery<Pharmacy> namedQuery(String queryName) {
        try {
            logger.debug("Invocation namedQuery() method with a name: " + queryName);
            return super.namedQuery(queryName);
        } catch (Exception e) {
            logger.error("Fail invocation namedQuery() with a name " + queryName +
                    " method with cause: ", e.getCause());
            throw new DatabaseOperationException(e.getCause());
        }
    }

    @Override
    public List<Pharmacy> getAll() {
        try {
            logger.debug("Invocation getAll() method");
            return this.pharmacyDao.getAll();
        } catch (Exception e) {
            logger.error("Fail invocation getAll() method with cause: ", e.getCause());
            throw new DatabaseOperationException(e.getCause());
        }
    }
}