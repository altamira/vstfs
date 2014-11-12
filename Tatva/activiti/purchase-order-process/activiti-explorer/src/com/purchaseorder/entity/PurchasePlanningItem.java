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
 * Entity class for table 'PURCHASE_PLANNING_ITEM'
 *
 */
@Entity
@Table(name="PURCHASE_PLANNING_ITEM")
public class PurchasePlanningItem 
{
	@Id
	@SequenceGenerator(name="PlanningItemSequence", sequenceName="PLANNING_ITEM_SEQUENCE", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="PlanningItemSequence")
	@Column(name="PLANNING_ITEM_ID")
	private BigDecimal planningItemId;
	
	@Column(name="PLANNING_ID")
	private BigDecimal planningId;
	
	@Column(name="REQUEST_ITEM_ID")
	private BigDecimal requestItemId;
	
	@Column(name="SUPPLIER_ID")
	private BigDecimal supplierId;
	
	@Column(name="SUPPLIER_WEIGHT")
	private BigDecimal supplierWeight;

	public BigDecimal getPlanningItemId() {
		return planningItemId;
	}

	public void setPlanningItemId(BigDecimal planningItemId) {
		this.planningItemId = planningItemId;
	}

	public BigDecimal getPlanningId() {
		return planningId;
	}

	public void setPlanningId(BigDecimal planningId) {
		this.planningId = planningId;
	}

	public BigDecimal getRequestItemId() {
		return requestItemId;
	}

	public void setRequestItemId(BigDecimal requestItemId) {
		this.requestItemId = requestItemId;
	}

	public BigDecimal getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(BigDecimal supplierId) {
		this.supplierId = supplierId;
	}

	public BigDecimal getSupplierWeight() {
		return supplierWeight;
	}

	public void setSupplierWeight(BigDecimal supplierWeight) {
		this.supplierWeight = supplierWeight;
	}
}
