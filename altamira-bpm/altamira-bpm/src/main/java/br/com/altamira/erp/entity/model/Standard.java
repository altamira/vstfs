package br.com.altamira.erp.entity.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Entity
@Table(name = "STANDARD", uniqueConstraints = @UniqueConstraint(columnNames = "NAME"))
@NamedQueries({
    @NamedQuery(name = "Standard.list", query = "SELECT s FROM Standard s ORDER BY s.id")
})
public class Standard implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "StandardSequence", sequenceName = "STANDARD_SEQUENCE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "StandardSequence")
    @Column(name = "ID")
    private long id;

    @Column(name = "NAME", columnDefinition = "NVARCHAR2", length = 20)
    private String name;

    @Column(name = "DESCRIPTION", columnDefinition = "CLOB")
    private String description;

    public long getId() {
        return id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
