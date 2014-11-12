package com.purchaseorder.model;

import java.math.BigDecimal;
import java.util.Date;

public class RequestReportData 
{
private String materialCode;
	
	private String materialDescription;
	
	private String materialLamination;
	
	private String materialTreatment;
	
	private BigDecimal materialThickness;
	
	private BigDecimal materialWidth;
	
	private BigDecimal materialLength;
	
	private BigDecimal requestWeight;
	
	private Date requestDate;
	
	public String getMaterialCode() 
	{
		return materialCode;
	}

	public void setMaterialCode(String materialCode) 
	{
		this.materialCode = materialCode;
	}

	public String getMaterialDescription() 
	{
		return materialDescription;
	}

	public void setMaterialDescription(String materialDescription) 
	{
		this.materialDescription = materialDescription;
	}

	public String getMaterialLamination() 
	{
		return materialLamination;
	}

	public void setMaterialLamination(String materialLamination) 
	{
		this.materialLamination = materialLamination;
	}

	public String getMaterialTreatment() 
	{
		return materialTreatment;
	}

	public void setMaterialTreatment(String materialTreatment) 
	{
		this.materialTreatment = materialTreatment;
	}

	public BigDecimal getMaterialThickness() 
	{
		return materialThickness;
	}

	public void setMaterialThickness(BigDecimal materialThickness) 
	{
		this.materialThickness = materialThickness;
	}

	public BigDecimal getMaterialWidth() 
	{
		return materialWidth;
	}

	public void setMaterialWidth(BigDecimal materialWidth) 
	{
		this.materialWidth = materialWidth;
	}

	public BigDecimal getMaterialLength() 
	{
		return materialLength;
	}

	public void setMaterialLength(BigDecimal materialLength) 
	{
		this.materialLength = materialLength;
	}

	public BigDecimal getRequestWeight() 
	{
		return requestWeight;
	}

	public void setRequestWeight(BigDecimal requestWeight) 
	{
		this.requestWeight = requestWeight;
	}

	public Date getRequestDate() 
	{
		return requestDate;
	}

	public void setRequestDate(Date requestDate) 
	{
		this.requestDate = requestDate;
	}
}
