/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.erp.entity.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author PARTH
 */

@XmlRootElement
@Entity
@Table(name = "SUPPLIER_PRICE_LIST",
        uniqueConstraints = @UniqueConstraint(columnNames = {"SUPPLIER", "MATERIAL"}))
public class SupplierPriceList implements Serializable {
    
    @Id
    @SequenceGenerator(name = "SupplierPriceListSequence", sequenceName = "SUPPLIER_PRICE_LIST_SEQUENCE",allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SupplierPriceListSequence")
    @Column(name = "ID")
    private long id;
    
    @ManyToOne
    @JoinColumn(name="SUPPLIER")
    private Supplier supplier;
//    @Column(name="SUPPLIER_ID")
//    private long supplierId;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "CHANGE_DATE")
    private Date changeDate;
    
    @ManyToOne
    @JoinColumn(name="MATERIAL")
    private Material material;
//    @Column(name="MATERIAL_ID")
//    private long materialId;
    
    @Column(name="PRICE")
    private long price;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

     public Date getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

}
