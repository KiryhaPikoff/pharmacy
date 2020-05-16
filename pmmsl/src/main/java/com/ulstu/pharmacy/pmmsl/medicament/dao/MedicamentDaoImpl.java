package com.ulstu.pharmacy.pmmsl.medicament.dao;

import com.ulstu.pharmacy.pmmsl.medicament.entity.Medicament;

import java.util.List;

public class MedicamentDaoImpl extends MedicamentDao {

    @Override
    public List<Medicament> getAll() {
        return this.entityManager
                .createQuery("SELECT m FROM Medicament m")
                .getResultList();
    }

    @Override
    public boolean existByNameAndNotId(String medicamentName, Long medicamentId) {
        long notesCount = (long) this.entityManager
                .createQuery("SELECT COUNT(m.name) FROM Medicament m" +
                        " WHERE m.name = :name AND m.id <> :id")
                .setParameter("name", medicamentName)
                .setParameter("id", medicamentId)
                .getSingleResult();
        return notesCount != 0;
    }
}