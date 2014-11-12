/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.altamira.erp.entity.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Alessandro
 */
@XmlRootElement
@Entity
@Table(name = "USER_PREFERENCE"/*, uniqueConstraints = @UniqueConstraint(columnNames = "NAME")*/)
@NamedQueries({
    @NamedQuery(name = "User.list", query = "SELECT u FROM User u ORDER BY u.id")
})
public class User {
private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "NAME", columnDefinition = "NVARCHAR2", length = 64)
    private String name;

    @Column(name = "PREFERENCES", columnDefinition = "CLOB")
    private String preference;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPreference() {
        return this.preference;
    }

    public void setPreference(String preference) {
        this.preference = preference;
    }    
}
