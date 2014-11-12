/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.altamira.erp.entity.dao;

import br.com.altamira.erp.entity.model.Material;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Alessandro
 */
public class MaterialDao {

    @PersistenceContext(name = "persistence/altamira-bpm", unitName = "altamira-bpm-PU")
    private EntityManager entityManager;

    public List<Material> list() {
        return (List<Material>)entityManager
                .createNamedQuery("Material.list", Material.class)
                .getResultList(); 
    }
    
    public Material find(Material material) {
        return find(material.getLamination(), material.getTreatment(), material.getThickness(), material.getWidth(), material.getLength());
    }

    public Material find(String lamination, String treatment, BigDecimal  thickness, BigDecimal width, BigDecimal length) {
        List<br.com.altamira.erp.entity.model.Material> materials;
        
        materials = (List<Material>)entityManager.createNamedQuery("Material.find")
                .setParameter("lamination", lamination)
                .setParameter("treatment", treatment)
                .setParameter("thickness", thickness)
                .setParameter("width", width)
                .setParameter("length", length).getResultList();
        
        if (materials.isEmpty()) {
            return null;
        }
         
        return materials.get(0);
    }    

    public Material find(long id) {
        return entityManager.find(Material.class, id);
    }

    public Material create(Material material) {
        entityManager.persist(material);
        
        entityManager.flush();
        
        return material;
    }

    public Material update(Material material) {
        return entityManager.merge(material);
    }

    public Material remove(Material material) {
        entityManager.remove(entityManager.contains(material) ? material : entityManager.merge(material));
        
        return material;
    }
}
