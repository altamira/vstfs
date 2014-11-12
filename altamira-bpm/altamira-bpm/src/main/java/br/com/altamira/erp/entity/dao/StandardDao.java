/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.altamira.erp.entity.dao;

import br.com.altamira.erp.entity.model.Standard;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Alessandro
 */
public class StandardDao {
    
    @PersistenceContext(name = "persistence/altamira-bpm", unitName = "altamira-bpm-PU")
    private EntityManager entityManager;
    
    public List<Standard> list() {
        return (List<Standard>) entityManager
                .createNamedQuery("Standard.list", Standard.class)
                .getResultList();
    }

    public Standard find(long id) {
        return entityManager.find(Standard.class, id);
    }

    public Standard find(Standard standard) {
        return find(standard.getId());
    }

    public Standard create(Standard standard) {
        entityManager.persist(standard);

        return standard;
    }

    public Standard update(Standard standard) {
        return entityManager.merge(standard);
    }

    public Standard remove(Standard standard) {
        entityManager.remove(standard);

        return standard;
    }    
}
