package com.ulstu.pharmacy.pmmsl.common.dao;

import com.ulstu.pharmacy.pmmsl.common.entity.AbstractEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.Optional;

/**
 * Абстрактный класс описывающий общие операции со всеми сущностями.
 *
 * @param <Entity>
 * @param <Id>
 */
public abstract class AbstractDao<Entity extends AbstractEntity, Id extends Serializable> {

    @PersistenceContext(unitName = "Pharmacy")
    protected EntityManager entityManager;

    private Class<Entity> entityClass;

    public AbstractDao(Class<Entity> entityClass) {
        this.entityClass = entityClass;
    }

    public Class<Entity> getEntityClass() {
        return entityClass;
    }

    public Optional<Entity> findById(Id id) {
        Entity entity = entityManager.find(entityClass, id);
        return Optional.of(entity);
    }

    public void save(Entity entity) {
        entityManager.persist(entity);
    }

    public void update(Entity entity) {
        entityManager.merge(entity);
    }

    public void deleteById(Id id) {
        Entity entity = entityManager.find(entityClass, id);
        delete(entity);
    }

    public void delete(Entity entity) {
        if (entity != null) {
            entityManager.remove(entity);
        }
    }

    public boolean existsById(Id id) {
        return entityManager.find(entityClass, id) != null;
    }

    public TypedQuery<Entity> namedQuery(String queryName) {
        return entityManager.createNamedQuery(queryName, entityClass);
    }
}