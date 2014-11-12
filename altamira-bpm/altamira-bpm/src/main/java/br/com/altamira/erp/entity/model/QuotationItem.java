package br.com.altamira.erp.entity.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Entity
@Table(name = "QUOTATION_ITEM",
        uniqueConstraints = @UniqueConstraint(columnNames = {"QUOTATION", "LAMINATION", "TREATMENT", "THICKNESS"}))
public class QuotationItem implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "QuotationItemSequence", sequenceName = "QUOTATION_ITEM_SEQUENCE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "QuotationItemSequence")
    @Column(name = "ID")
    private long id;

    @Column(name = "LAMINATION", columnDefinition = "NVARCHAR2", length = 2)
    private String lamination;

    @Column(name = "TREATMENT", columnDefinition = "NVARCHAR2", length = 2)
    private String treatment;

    @Column(name = "THICKNESS")
    private BigDecimal thickness;

    @Column(name = "STANDARD")
    private long standard;

    @Column(name = "WEIGHT")
    private BigDecimal weight;

    @OneToMany(orphanRemoval=true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderBy("id ASC")
    @JoinColumn(name = "QUOTATION_ITEM", insertable = true, nullable = false)
    private Set<QuotationItemQuote> quotes;

    public long getId() {
        return id;
    }

    public void setiId(long id) {
        this.id = id;
    }

    public String getLamination() {
        return lamination;
    }

    public void setLamination(String lamination) {
        this.lamination = lamination;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public BigDecimal getThickness() {
        return thickness;
    }

    public void setThickness(BigDecimal thickness) {
        this.thickness = thickness;
    }

    public long getStandard() {
        return standard;
    }

    public void setStandard(long standard) {
        this.standard = standard;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public Set<QuotationItemQuote> getQuotes() {
        return quotes;
    }

    public void setQuotes(Set<QuotationItemQuote> quotes) {
        this.quotes = quotes;
    }

}
