package com.purchaseorder.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class SupplierStandardId implements Serializable
{
	@Column(name="SUPPLIER_ID")
	BigDecimal supplierId;
	
	@Column(name="STANDARD_ID")
	BigDecimal standardId;

	public BigDecimal getSupplierId() 
	{
		return supplierId;
	}

	public void setSupplierId(BigDecimal supplierId) 
	{
		this.supplierId = supplierId;
	}

	public BigDecimal getStandardId() 
	{
		return standardId;
	}

	public void setStandardId(BigDecimal standardId) 
	{
		this.standardId = standardId;
	}
	
}
