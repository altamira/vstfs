package com.purchaseorder.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

@Table(name="PURCHASE_XSD")
@Entity
public class PurchaseXsd
{
	@EmbeddedId
	PurchaseXsdId purchaseXsdId;
	
	@Lob
	@Column(name="XSD_CONTENT")
	byte[] xsdContent;
	
	@Column(name="ROOT")
	String root;
	
	@Column(name="VALID_START_DATE")
	Date validStartDate;

	public PurchaseXsdId getPurchaseXsdId() 
	{
		return purchaseXsdId;
	}
	
	public void setPurchaseXsdId(PurchaseXsdId purchaseXsdId) 
	{
		this.purchaseXsdId = purchaseXsdId;
	}
	
	public byte[] getXsdContent() 
	{
		return xsdContent;
	}

	public void setXsdContent(byte[] xsdContent) 
	{
		this.xsdContent = xsdContent;
	}

	public String getRoot() 
	{
		return root;
	}

	public void setRoot(String root) 
	{
		this.root = root;
	}

	public Date getValidStartDate() 
	{
		return validStartDate;
	}

	public void setValidStartDate(Date validStartDate) 
	{
		this.validStartDate = validStartDate;
	}
}
