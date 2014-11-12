package com.purchaseorder.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * 
 * @author PARTH
 * 
 * Entity class for table 'QUOTATION_ITEM'
 *
 */
@Entity
@Table(name="QUOTATION_ITEM")
public class QuotationItem 
{
	@Id
	@SequenceGenerator(name="QuotationITemSequence", sequenceName="QUOTATION_ITEM_SEQUENCE", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="QuotationITemSequence")
	@Column(name="QUOTATION_ITEM_ID")
	private BigDecimal quotationItemId;
	
	@Column(name="QUOTATION_ID")
	private BigDecimal quotationId;
	
	@Column(name="MATERIAL_LAMINATION", columnDefinition="char")
	private String materialLamination;
	
	@Column(name="MATERIAL_TREATMENT", columnDefinition="char")
	private String materialTreatment;
	
	@Column(name="MATERIAL_THICKNESS")
	private BigDecimal materialThickness;
	
	@Column(name="MATERIAL_STANDARD")
	private BigDecimal materialStandard;
	
	@Column(name="REQUEST_WEIGHT")
	private BigDecimal requestWeight;
	
	public BigDecimal getQuotationItemId() {
		return quotationItemId;
	}

	public void setQuotationItemId(BigDecimal quotationItemId) {
		this.quotationItemId = quotationItemId;
	}

	public BigDecimal getQuotationId() {
		return quotationId;
	}

	public void setQuotationId(BigDecimal quotationId) {
		this.quotationId = quotationId;
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

	public BigDecimal getMaterialStandard() {
		return materialStandard;
	}

	public void setMaterialStandard(BigDecimal materialStandard) {
		this.materialStandard = materialStandard;
	}
	
	public BigDecimal getRequestWeight() {
		return requestWeight;
	}

	public void setRequestWeight(BigDecimal requestWeight) {
		this.requestWeight = requestWeight;
	}
}
