package br.com.altamira.erp.entity.model;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Entity
@Table(name = "QUOTATION_ITEM_QUOTE")
public class QuotationItemQuote implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "QuotationItemQuoteSequence", sequenceName = "QUOTATION_ITEM_QUOTE_SEQUENCE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "QuotationItemQuoteSequence")
    @Column(name = "ID")
    private long id;

    @Column(name = "STANDARD")
    private String standard;

    @Column(name = "PRICE")
    private BigDecimal price;

    @Column(name = "WEIGHT")
    private BigDecimal weight;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderBy("id ASC")
    @JoinColumn(name = "QUOTATION_ITEM_QUOTE", insertable = true, nullable = false)
    private Set<SupplierInStock> stocks;

    @ManyToOne
    @JoinColumn(name = "SUPPLIER")
    private Supplier supplier;

    public QuotationItemQuote() {

    }

    public QuotationItemQuote(Supplier supplier, String standard, BigDecimal price, BigDecimal weight) {
        super();
        this.supplier = supplier;
        this.standard = standard;
        this.price = price;
        this.weight = weight;
    }

    public long getId() {
        return id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public String getStandard() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public Set<SupplierInStock> getStocks() {
        return stocks;
    }

    public void setStocks(Set<SupplierInStock> stocks) {
        this.stocks = stocks;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

}
