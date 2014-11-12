package br.com.altamira.erp.entity.model;

import java.io.Serializable;
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
import javax.persistence.JoinTable;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Alessandro
 */
@XmlRootElement
@Entity
@Table(name = "QUOTATION")
@NamedQueries({
    //@NamedQuery(name = "Quotation.getCurrent", query = "SELECT q FROM Quotation q WHERE q.id = (SELECT MAX(qq.id) FROM Quotation qq) AND q.closed_date is null"),
    @NamedQuery(name = "Quotation.list", query = "SELECT q FROM Quotation q ORDER BY q.id")
})
public class Quotation implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "QuotationSequence", sequenceName = "QUOTATION_SEQUENCE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "QuotationSequence")
    @Column(name = "ID")
    private long id;

    @Column(name = "CREATOR_NAME", columnDefinition = "NVARCHAR2", length = 50)
    private String creator_name;

    @Temporal(TemporalType.DATE)
    @Column(name = "CREATED_DATE")
    private Date created_date;

    @Temporal(TemporalType.DATE)
    @Column(name = "CLOSED_DATE")
    private Date closed_date;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "QUOTATION_REQUEST", joinColumns = {
        @JoinColumn(name = "QUOTATION", referencedColumnName = "ID")}, inverseJoinColumns = {
        @JoinColumn(name = "REQUEST", referencedColumnName = "ID")})
    private Set<Request> requests;

    @OneToMany(orphanRemoval=true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderBy("lamination ASC, treatment ASC, thickness ASC, weight ASC")
    @JoinColumn(name = "QUOTATION", insertable = true, nullable = false)
    private Set<QuotationItem> items;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCreatorName() {
        return creator_name;
    }

    public void setCreatorName(String creator) {
        this.creator_name = creator;
    }

    public Date getCreatedDate() {
        return created_date;
    }

    public void setCreatedDate(Date date) {
        this.created_date = date;
    }

    public Date getClosedDate() {
        return closed_date;
    }

    public void setClosedDate(Date date) {
        this.closed_date = date;
    }

    public Set<QuotationItem> getItems() {
        return items;
    }

    public void setItems(Set<QuotationItem> items) {
        this.items = items;
    }

    public Set<Request> getRequests() {
        return requests;
    }

    public void setRequests(Set<Request> requests) {
        this.requests = requests;
    }

//    public static Quotation getCurrentQuotation(EntityManager entityManager) {
//        return (Quotation)entityManager.createNamedQuery("Quotation.getCurrentQuotation").getSingleResult();
//    }
}
