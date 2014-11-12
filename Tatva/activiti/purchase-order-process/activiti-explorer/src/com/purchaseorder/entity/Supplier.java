package com.purchaseorder.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * 
 * @author PARTH
 * 
 * Entity class for table 'SUPPLIER'
 *
 */
@Entity
@Table(name="SUPPLIER", uniqueConstraints=@UniqueConstraint(columnNames="SUPPLIER_NAME"))
public class Supplier 
{
	@Id
	@Column(name="SUPPLIER_ID")
	private BigDecimal supplierId;
	
	@Column(name="SUPPLIER_NAME")
	private String supplierName;
	
	@Column(name="SUPPLIER_CONTACT_ID")
	private BigDecimal supplierContactId;

	public BigDecimal getSupplierContactId() 
	{
		return supplierContactId;
	}

	public void setSupplierContactId(BigDecimal supplierContactId) 
	{
		this.supplierContactId = supplierContactId;
	}

	public BigDecimal getSupplierId() 
	{
		return supplierId;
	}

	public void setSupplierId(BigDecimal supplierId) 
	{
		this.supplierId = supplierId;
	}

	public String getSupplierName() 
	{
		return supplierName;
	}

	public void setSupplierName(String supplierName) 
	{
		this.supplierName = supplierName;
	}
}
