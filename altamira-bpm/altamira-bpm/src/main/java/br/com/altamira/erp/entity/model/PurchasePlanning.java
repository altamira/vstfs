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
import javax.persistence.OrderBy;
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
@Table(name = "PURCHASE_PLANNING")
public class PurchasePlanning {
    
    @Id
    @SequenceGenerator(name = "PurchasePlanningSequence", sequenceName = "PURCHASE_PLANNING_SEQUENCE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PurchasePlanningSequence")
    @Column(name = "ID")
    private long id;
    
    @ManyToOne
    @JoinColumn(name = "QUOTATION")
    private Quotation quotation;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "CREATED_DATE")
    private Date created_date;
    
    @Column(name = "CREATOR_NAME", columnDefinition = "NVARCHAR2", length = 50)
    private String creator_name;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "APPROVE_DATE")
    private Date approve_date;
    
    @Column(name = "APPROVE_USER", columnDefinition = "NVARCHAR2", length = 50)
    private String approve_user;
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "PLANNING", insertable = true, nullable = false)
    private Set<PurchasePlanningItem> plannintItems;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Quotation getQuotation() {
        return quotation;
    }

    public void setQuotation(Quotation quotation) {
        this.quotation = quotation;
    }

    public Date getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Date created_date) {
        this.created_date = created_date;
    }

    public String getCreator_name() {
        return creator_name;
    }

    public void setCreator_name(String creator_name) {
        this.creator_name = creator_name;
    }

    public Date getApprove_date() {
        return approve_date;
    }

    public void setApprove_date(Date approve_date) {
        this.approve_date = approve_date;
    }

    public String getApprove_user() {
        return approve_user;
    }

    public void setApprove_user(String approve_user) {
        this.approve_user = approve_user;
    }

    public Set<PurchasePlanningItem> getPlannintItems() {
        return plannintItems;
    }

    public void setPlannintItems(Set<PurchasePlanningItem> plannintItems) {
        this.plannintItems = plannintItems;
    }
    
}
