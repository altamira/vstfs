package br.com.altamira.erp.entity.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
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
@Table(name = "REQUEST")
@NamedQueries({
    @NamedQuery(name = "Request.getCurrent", query = "SELECT r FROM Request r WHERE r.id = (SELECT MAX(rr.id) FROM Request rr WHERE rr.send_date IS NULL)"),
    @NamedQuery(name = "Request.list", query = "SELECT r FROM Request r ORDER BY r.id")
})
public class Request implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "RequestSequence", sequenceName = "REQUEST_SEQUENCE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RequestSequence")
    @Column(name = "ID")
    private long id;

    @Temporal(TemporalType.DATE)
    @Column(name = "CREATED_DATE")
    private Date created_date;

    @Temporal(TemporalType.DATE)
    @Column(name = "SEND_DATE")
    private Date send_date;
    
    @Column(name = "CREATOR_NAME", columnDefinition = "NVARCHAR2", length = 50)
    private String creator_name;

    @OneToMany(orphanRemoval=true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "REQUEST", insertable = true, nullable = false)
    private List<RequestItem> items;

    public Request() {

    }

    public Request(Date created_date, String creator_name, List<RequestItem> items) {
        super();
        this.created_date = created_date;
        this.creator_name = creator_name;
        this.items = items;
    }

    public long getId() {
        return id;
    }

    public Date getCreated() {
        return created_date;
    }

    public void setCreated(Date date) {
        this.created_date = date;
    }

    public Date getSendDate() {
        return send_date;
    }

    public void setSendDate(Date date) {
        this.send_date = date;
    }

    public String getCreator() {
        return creator_name;
    }

    public void setCreator(String name) {
        this.creator_name = name;
    }

    public List<RequestItem> getItems() {
        return items;
    }

    public void setItems(List<RequestItem> items) {
        this.items = items;
    }

}
