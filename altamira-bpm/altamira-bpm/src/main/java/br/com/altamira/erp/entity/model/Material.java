package br.com.altamira.erp.entity.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Alessandro
 */
@XmlRootElement
@Entity
@Table(name = "MATERIAL", 
       uniqueConstraints=@UniqueConstraint(columnNames={"LAMINATION", "TREATMENT", "THICKNESS", "WIDTH", "LENGTH"}))
@NamedQueries({
    @NamedQuery( name = "Material.find", query = "SELECT m FROM Material m WHERE m.lamination = :lamination AND m.treatment = :treatment AND m.thickness = :thickness AND m.width = :width AND m.length = :length"),
    @NamedQuery( name = "Material.list", query = "SELECT m FROM Material m ORDER BY m.id")
})
public class Material implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "MaterialSequence", sequenceName = "MATERIAL_SEQUENCE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MaterialSequence")
    @Column(name = "ID")
    private long id;

    @Column(name = "LAMINATION", columnDefinition = "char")
    private String lamination;

    @Column(name = "TREATMENT", columnDefinition = "char")
    private String treatment;

    @Column(name = "THICKNESS")
    private BigDecimal thickness;

    @Column(name = "WIDTH")
    private BigDecimal width;

    @Column(name = "LENGTH")
    private BigDecimal length;
    
    @Column(name = "TAX")
    private BigDecimal tax;

    @OneToMany(mappedBy = "material")
    private List<RequestItem> requestItems;

    @XmlElement(required = true)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLamination() {
        return this.lamination;
    }

    public void setLamination(String lamination) {
        this.lamination = lamination;
    }

    public String getTreatment() {
        return this.treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public BigDecimal getThickness() {
        return this.thickness;
    }

    public void setThickness(BigDecimal thickness) {
        this.thickness = thickness;
    }

    public BigDecimal getWidth() {
        return this.width;
    }

    public void setWidth(BigDecimal width) {
        this.width = width;
    }

    public BigDecimal getLength() {
        return this.length;
    }

    public void setLength(BigDecimal length) {
        this.length = length;
    }
    
    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }
    
}
