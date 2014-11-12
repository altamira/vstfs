package com.purchaseorder.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author PARTH
 * 
 * Entity class for table 'SUPPLIER_CONTACT'
 *
 */
@Entity
@Table(name="SUPPLIER_CONTACT")
public class SupplierContact 
{
	@Id
	@Column(name="SUPPLIER_CONTACT_ID")
	private BigDecimal supplierContactId;
	
	@Column(name="CONTACT_NAME")
	private String contactName;
	
	@Column(name="MAIL_ADDRESS")
	private String mailAddress;

	public BigDecimal getSupplierContactId() 
	{
		return supplierContactId;
	}

	public void setSupplierContactId(BigDecimal supplierContactId) 
	{
		this.supplierContactId = supplierContactId;
	}

	public String getContactName() 
	{
		return contactName;
	}

	public void setContactName(String contactName) 
	{
		this.contactName = contactName;
	}

	public String getMailAddress() 
	{
		return mailAddress;
	}

	public void setMailAddress(String mailAddress) 
	{
		this.mailAddress = mailAddress;
	}	
}
