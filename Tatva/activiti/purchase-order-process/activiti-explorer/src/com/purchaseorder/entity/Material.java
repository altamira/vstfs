package com.purchaseorder.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * 
 * @author PARTH
 * 
 * Entity class for table 'MATERIAL'
 *
 */
@Entity
@Table(name="MATERIAL", uniqueConstraints = @UniqueConstraint(columnNames = "MATERIAL_CODE"))
public class Material 
{
	@Id
	@SequenceGenerator(name="MaterialSequence", sequenceName="MATERIAL_SEQUENCE", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="MaterialSequence")
	@Column(name="MATERIAL_ID")
	private BigDecimal materialId;
	
	@Column(name="MATERIAL_CODE")
	private String materialCode;
	
	@Column(name="MATERIAL_LAMINATION", columnDefinition="char")
	private String materialLamination;
	
	@Column(name="MATERIAL_TREATMENT", columnDefinition="char")
	private String materialTreatment;
	
	@Column(name="MATERIAL_THICKNESS")
	private BigDecimal materialThickness;
	
	@Column(name="MATERIAL_WIDTH")
	private BigDecimal materialWidth;
	
	@Column(name="MATERIAL_LENGTH")
	private BigDecimal materialLength;
	
	@Column(name="MATERIAL_DESCRIPTION")
	private String materialDescription;
	
	@Column(name="MATERIAL_TAX")
	private BigDecimal materialTax;

	public BigDecimal getMaterialTax() {
		return materialTax;
	}

	public void setMaterialTax(BigDecimal materialTax) {
		this.materialTax = materialTax;
	}

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

	public String getMaterialDescription() {
		return materialDescription;
	}

	public void setMaterialDescription(String materialDescription) {
		this.materialDescription = materialDescription;
	}	
	
}
