package br.com.altamira.erp.entity.model.wbccad;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Alessandro
 */
@XmlRootElement
@Entity
@Table(name = "PRDORC", schema = "dbo")
public class Material implements Serializable {

    @Id
    @Column(name = "PRODUTO")
    private String code;

    @Column(name = "DESCRICAO")
    private String description;

    @XmlElement(required = true)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @XmlElement(required = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
