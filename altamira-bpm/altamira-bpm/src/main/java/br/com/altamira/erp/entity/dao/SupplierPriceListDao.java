/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
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
 * @author Alessandro
 */
public class SupplierPriceListDao {
    
    @PersistenceContext(name = "persistence/altamira-bpm", unitName = "altamira-bpm-PU")
    private EntityManager entityManager;

    public SupplierPriceList find(SupplierPriceList supplierPriceList) {

        List<SupplierPriceList> supplierPriceLists;

        supplierPriceLists = entityManager.createQuery("SELECT spl FROM SupplierPriceList spl WHERE spl.id = :id AND spl.supplier = :supplier AND spl.material = :material AND spl.price = :price")
                .setParameter("id", supplierPriceList.getId())
                .setParameter("supplier", supplierPriceList.getSupplier())
                //.setParameter("changeDate", supplierPriceList.getChangeDate(), TemporalType.DATE)
                .setParameter("material", supplierPriceList.getMaterial())
                .setParameter("price", supplierPriceList.getPrice())
                .getResultList();

        if (supplierPriceLists.isEmpty()) {
            return null;
        }

        return supplierPriceLists.get(0);
    }

    public SupplierPriceList find(long id) {
        return entityManager.find(SupplierPriceList.class, id);
    }

    public List<SupplierPriceList> find(Supplier supplier) {
        List<SupplierPriceList> supplierPriceLists;

        supplierPriceLists = entityManager.createQuery("SELECT spl FROM SupplierPriceList spl WHERE spl.supplier = :supplier")
                .setParameter("supplier", supplier)
                .getResultList();

        if (supplierPriceLists == null || supplierPriceLists.isEmpty()) {
            return null;
        }

        return supplierPriceLists;
    }

    public SupplierPriceList find(Supplier supplier, long supplierPriceListId) {
        List<SupplierPriceList> supplierPriceList = entityManager.createQuery("SELECT spl FROM SupplierPriceList spl WHERE spl.supplier = :supplier AND spl.id = :supplierPriceListId")
                .setParameter("supplier", supplier)
                .setParameter("supplierPriceListId", supplierPriceListId)
                .getResultList();

        if (supplierPriceList.isEmpty()) {
            return null;
        }

        return supplierPriceList.get(0);
    }

    public SupplierPriceList create(SupplierPriceList supplierPriceList) {
        entityManager.persist(supplierPriceList);
        
        entityManager.flush();

        return supplierPriceList;
    }

    public SupplierPriceList update(SupplierPriceList supplierPriceList) {
        
        entityManager.merge(supplierPriceList);
        
        entityManager.flush();
        
        return supplierPriceList;
    }

    public void remove(SupplierPriceList supplierPriceList) {
        entityManager.remove(supplierPriceList);
        entityManager.flush();
    }


}
