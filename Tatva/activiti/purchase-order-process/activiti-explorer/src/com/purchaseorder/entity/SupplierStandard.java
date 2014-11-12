package com.purchaseorder.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 
 * @author PARTH
 * 
 * Entity class for table 'SUPPLIER_STANDARD'
 *
 */
@Entity
@Table(name="SUPPLIER_STANDARD")
public class SupplierStandard 
{
	@EmbeddedId
	SupplierStandardId supplierStandardId;
	
	@Column(name="STANDARD_AVAILABLE", columnDefinition="char")
	String StandardAvailable;

	public SupplierStandardId getSupplierStandardId() 
	{
		return supplierStandardId;
	}

	public void setSupplierStandardId(SupplierStandardId supplierStandardId) 
	{
		this.supplierStandardId = supplierStandardId;
	}

	public String getStandardAvailable() 
	{
		return StandardAvailable;
	}

	public void setStandardAvailable(String standardAvailable) 
	{
		StandardAvailable = standardAvailable;
	}
	
	

}
