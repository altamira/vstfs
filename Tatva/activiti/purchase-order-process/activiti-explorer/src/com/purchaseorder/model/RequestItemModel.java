package com.purchaseorder.model;

import java.math.BigDecimal;

public class RequestItemModel 
{
	private BigDecimal materialId;
	
	private String materialCode;
	
	private String materialLamination;
	
	private String materialTreatment;
	
	private BigDecimal materialThickness;
	
	private BigDecimal materialWidth;
	
	private BigDecimal materialLength;
	
	private String requestArrivalDate;
	
	private BigDecimal requestWeight;

	public BigDecimal getMaterialId() {
		return materialId;
	}

	public void setMaterialId(BigDecimal materialId) {
		this.materialId = materialId;
	}

	public String getMaterialCode() {
		return materialCode;
	}

	public void setMaterialCode(String materialCode) {
		this.materialCode = materialCode;
	}

	public String getMaterialLamination() {
		return materialLamination;
	}

	public void setMaterialLamination(String materialLamination) {
		this.materialLamination = materialLamination;
	}

	public String getMaterialTreatment() {
		return materialTreatment;
	}

	public void setMaterialTreatment(String materialTreatment) {
		this.materialTreatment = materialTreatment;
	}

	public BigDecimal getMaterialThickness() {
		return materialThickness;
	}

	public void setMaterialThickness(BigDecimal materialThickness) {
		this.materialThickness = materialThickness;
	}

	public BigDecimal getMaterialWidth() {
		return materialWidth;
	}

	public void setMaterialWidth(BigDecimal materialWidth) {
		this.materialWidth = materialWidth;
	}

	public BigDecimal getMaterialLength() {
		return materialLength;
	}

	public void setMaterialLength(BigDecimal materialLength) {
		this.materialLength = materialLength;
	}

	public String getRequestArrivalDate() {
		return requestArrivalDate;
	}

	public void setRequestArrivalDate(String requestArrivalDate) {
		this.requestArrivalDate = requestArrivalDate;
	}

	public BigDecimal getRequestWeight() {
		return requestWeight;
	}

	public void setRequestWeight(BigDecimal requestWeight) {
		this.requestWeight = requestWeight;
	}
		
}
