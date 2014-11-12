/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.erp.entity.dao;

import br.com.altamira.erp.entity.model.Supplier;
import br.com.altamira.erp.entity.model.SupplierPriceList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author PARTH
 */
public class SupplierDao {

    @PersistenceContext(name = "persistence/altamira-bpm", unitName = "altamira-bpm-PU")
    private EntityManager entityManager;
    
    public List<Supplier> list() {
        return (List<Supplier>) entityManager
                .createNamedQuery("Supplier.list", Supplier.class)
                .getResultList();
    }

    public Supplier find(long id) {
        return entityManager.find(Supplier.class, id);
    }

    public Supplier find(Supplier supplier) {
        return find(supplier.getId());
    }

    public Supplier create(Supplier supplier) {
        entityManager.persist(supplier);

        return supplier;
    }

    public Supplier update(Supplier supplier) {
        return entityManager.merge(supplier);
    }

    public Supplier remove(Supplier supplier) {
        entityManager.remove(supplier);

        return supplier;
    }

}
