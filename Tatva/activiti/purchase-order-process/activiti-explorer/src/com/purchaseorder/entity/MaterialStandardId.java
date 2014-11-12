package com.purchaseorder.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class MaterialStandardId implements Serializable
{
	@Column(name="MATERIAL_ID")
	BigDecimal materialId;
	
	@Column(name="STANDARD_ID")
	BigDecimal standardId;

	public BigDecimal getMaterialId() 
	{
		return materialId;
	}

	public void setMaterialId(BigDecimal materialId) 
	{
		this.materialId = materialId;
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
