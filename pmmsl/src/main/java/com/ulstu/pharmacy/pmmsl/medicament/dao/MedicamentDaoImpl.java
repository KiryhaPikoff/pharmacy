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
    public boolean existByName(String medicamentName) {
        long notesCount = (long) this.entityManager
                .createQuery("SELECT COUNT(m.name) FROM Medicament m" +
                        " WHERE m.name = :name")
                .setParameter("name", medicamentName)
                .getSingleResult();
        return notesCount != 0;
    }
}