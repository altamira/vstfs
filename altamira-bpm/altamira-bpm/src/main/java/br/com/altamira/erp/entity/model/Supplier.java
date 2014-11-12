package br.com.altamira.erp.entity.model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Entity
@Table(name = "SUPPLIER", uniqueConstraints = @UniqueConstraint(columnNames = "NAME"))
@NamedQueries({
    @NamedQuery(name = "Supplier.list", query = "SELECT s FROM Supplier s ORDER BY s.id")
})
public class Supplier implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "SupplierSequence", sequenceName = "SUPPLIER_SEQUENCE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SupplierSequence")
    @Column(name = "ID")
    private long id;

    @Column(name = "NAME", columnDefinition = "NVARCHAR2", length = 50)
    private String name;

    @OneToMany(mappedBy = "supplier")
    @OrderBy("id ASC")
    private List<QuotationItemQuote> quotationItemQuote;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
