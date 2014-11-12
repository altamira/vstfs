/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.erp.entity.model;

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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author PARTH
 */

@XmlRootElement
@Entity
@Table(name = "PURCHASE_PLANNING_ITEM")
public class PurchasePlanningItem {
    
    @Id
    @SequenceGenerator(name = "PlanningItemSequence", sequenceName = "PLANNING_ITEM_SEQUENCE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PlanningItemSequence")
    @Column(name = "ID")
    private long id;
    
    @ManyToOne
    @JoinColumn(name = "REQUEST_ITEM")
    private RequestItem requestItem;
    
    @ManyToOne
    @JoinColumn(name = "SUPPLIER")
    private Supplier supplier;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "\"DATE\"")
    private Date date;
    
    @Column(name = "WEIGHT")
    private long weight;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public RequestItem getRequestItem() {
        return requestItem;
    }

    public void setRequestItem(RequestItem requestItem) {
        this.requestItem = requestItem;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getWeight() {
        return weight;
    }

    public void setWeight(long weight) {
        this.weight = weight;
    }
    
}
