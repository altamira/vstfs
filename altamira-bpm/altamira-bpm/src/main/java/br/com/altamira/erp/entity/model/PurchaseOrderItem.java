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
@Table(name = "PURCHASE_ORDER_ITEM")
public class PurchaseOrderItem {
    
    @Id
    @SequenceGenerator(name = "PurchaseOrderItemSequence", sequenceName = "PURCHASE_ORDER_ITEM_SEQUENCE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PurchaseOrderItemSequence")
    @Column(name = "ID")
    private long id;
    
    @Column(name = "PURCHASE_ORDER")
    private long purchaseOrderId;
    
    @Column(name = "PLANNING_ITEM")
    private long planningItemId;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "\"DATE\"")
    private Date date;
    
    @Column(name = "WEIGHT")
    private long weight;
    
    @Column(name = "PRICE")
    private long price;
    
    @Column(name = "TAX")
    private long tax;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(long purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }

    public long getPlanningItemId() {
        return planningItemId;
    }

    public void setPlanningItemId(long planningItemId) {
        this.planningItemId = planningItemId;
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

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public long getTax() {
        return tax;
    }

    public void setTax(long tax) {
        this.tax = tax;
    }
}
