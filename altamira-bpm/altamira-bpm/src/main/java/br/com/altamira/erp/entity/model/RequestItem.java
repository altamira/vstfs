package br.com.altamira.erp.entity.model;

import java.io.Serializable;
import java.math.BigDecimal;
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

@XmlRootElement
@Entity
@Table(name = "REQUEST_ITEM",
        uniqueConstraints = @UniqueConstraint(columnNames = {"REQUEST", "MATERIAL", "ARRIVAL_DATE"}))
public class RequestItem implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "RequestItemSequence", sequenceName = "REQUEST_ITEM_SEQUENCE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RequestItemSequence")
    @Column(name = "ID")
    private long id;

    @Temporal(TemporalType.DATE)
    @Column(name = "ARRIVAL_DATE")
    private Date arrival_date;

    @Column(name = "WEIGHT")
    private BigDecimal weight;

    @ManyToOne
    @JoinColumn(name = "MATERIAL")
    private Material material;

    public RequestItem() {

    }

    public RequestItem(Material material, Date arrival_date, BigDecimal weight) {
        super();
        this.material = material;
        this.arrival_date = arrival_date;
        this.weight = weight;
    }

    public long getId() {
        return id;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Date getArrival() {
        return arrival_date;
    }

    public void setArrival(Date date) {
        this.arrival_date = date;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }
    
    @Override
    public boolean equals (Object object) {
        boolean result = false;
        
        if (object == null || object.getClass() != getClass()) {
            result = false;
        } else {
            RequestItem requestItem = (RequestItem) object;
            if (this.id == requestItem.getId()) {
                result = true;
            }
        }
        
        return result;
    }
}
