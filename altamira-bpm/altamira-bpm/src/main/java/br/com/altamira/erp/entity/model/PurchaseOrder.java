/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.erp.entity.model;

import java.util.Date;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author PARTH
 */

@XmlRootElement
@Entity
@Table(name = "PURCHASE_ORDER")
public class PurchaseOrder {
 
    @Id
    @SequenceGenerator(name = "PurchaseOrderSequence", sequenceName = "PURCHASE_ORDER_SEQUENCE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PurchaseOrderSequence")
    @Column(name = "ID")
    private long id;
    
    @Column(name = "PURCHASE_PLANNING")
    private long purchasePlanningId;
    
    @Column(name = "SUPPLIER")
    private long supplierId;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "CREATED_DATE")
    private Date createdDate;
    
    @Column(name = "COMMENTS", columnDefinition = "CLOB")
    private String comments;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPurchasePlanningId() {
        return purchasePlanningId;
    }

    public void setPurchasePlanningId(long purchasePlanningId) {
        this.purchasePlanningId = purchasePlanningId;
    }

    public long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(long supplierId) {
        this.supplierId = supplierId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
    
}
