package br.com.altamira.erp.entity.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Entity
@Table(name = "SUPPLIER_IN_STOCK")
public class SupplierInStock implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "SupplierInStockSequence", sequenceName = "SUPPLIER_IN_STOCK_SEQUENCE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SupplierInStockSequence")
    @Column(name = "ID")
    private long id;

    @Column(name = "WIDTH")
    private BigDecimal width;

    @Column(name = "LENGTH", nullable = true)
    private BigDecimal length;

    @Column(name = "WEIGHT")
    private BigDecimal weight;

    public long getId() {
        return id;
    }

    public BigDecimal getWidth() {
        return width;
    }

    public void setWidth(BigDecimal width) {
        this.width = width;
    }

    public BigDecimal getLength() {
        return length;
    }

    public void seLength(BigDecimal length) {
        this.length = length;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

}
