package com.purchaseorder.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

/**
 * 
 * @author PARTH
 * 
 * Entity class for table 'PROCNFE'
 *
 */
@Entity
@TypeDef(name="HibernateXMLType", typeClass=HibernateXMLType.class)
public class Procnfe 
{
	@Id
	private String xmlName;

	@Type(type="HibernateXMLType")
	@Column(name="XMLCONTENT", columnDefinition="XMLTYPE")
	private String xmlcontent;

	public String getXmlName() 
	{
		return xmlName;
	}

	public void setXmlName(String xmlName) 
	{
		this.xmlName = xmlName;
	}

	public String getXmlcontent() 
	{
		return xmlcontent;
	}

	public void setXmlcontent(String xmlcontent) 
	{
		this.xmlcontent = xmlcontent;
	}
	
}
