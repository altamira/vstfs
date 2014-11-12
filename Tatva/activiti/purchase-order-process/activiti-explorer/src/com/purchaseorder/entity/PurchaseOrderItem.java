package com.purchaseorder.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 
 * @author PARTH
 * 
 * Entity class for table 'PURCHASE_ORDER_ITEM'
 *
 */
@Entity
@Table(name="PURCHASE_ORDER_ITEM")
public class PurchaseOrderItem 
{
	@Id
	@SequenceGenerator(name="PurchaseOrderItemSequence", sequenceName="PURCHASE_ORDER_ITEM_SEQUENCE", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="PurchaseOrderItemSequence")
	@Column(name="PURCHASE_ORDER_ITEM_ID")
	private BigDecimal purchaseOrderItemId;
	
	@Column(name="PURCHASE_ORDER_ID")
	private BigDecimal purchaseOrderId;
	
	@Column(name="PLANNING_ITEM_ID")
	private BigDecimal planningItemId;
	
	@Temporal(TemporalType.DATE)
	@Column(name="PURCHASE_ORDER_ITEM_DATE")	
	private Date purchaseOrderItemDate;
	
	@Column(name="PURCHASE_ORDER_ITEM_WEIGHT")
	private BigDecimal purchaseOrderItemWeight;
	
	@Column(name="PURCHASE_ORDER_ITEM_UNIT_PRICE")
	private BigDecimal purchaseOrderItemUnitPrice;
	
	@Column(name="PURCHASE_ORDER_ITEM_TAX")
	private BigDecimal purchaseOrderItemTax;

	public BigDecimal getPurchaseOrderItemId() {
		return purchaseOrderItemId;
	}

	public void setPurchaseOrderItemId(BigDecimal purchaseOrderItemId) {
		this.purchaseOrderItemId = purchaseOrderItemId;
	}

	public BigDecimal getPurchaseOrderId() {
		return purchaseOrderId;
	}

	public void setPurchaseOrderId(BigDecimal purchaseOrderId) {
		this.purchaseOrderId = purchaseOrderId;
	}

	public BigDecimal getPlanningItemId() {
		return planningItemId;
	}

	public void setPlanningItemId(BigDecimal planningItemId) {
		this.planningItemId = planningItemId;
	}

	public Date getPurchaseOrderItemDate() {
		return purchaseOrderItemDate;
	}

	public void setPurchaseOrderItemDate(Date purchaseOrderItemDate) {
		this.purchaseOrderItemDate = purchaseOrderItemDate;
	}

	public BigDecimal getPurchaseOrderItemWeight() {
		return purchaseOrderItemWeight;
	}

	public void setPurchaseOrderItemWeight(BigDecimal purchaseOrderItemWeight) {
		this.purchaseOrderItemWeight = purchaseOrderItemWeight;
	}

	public BigDecimal getPurchaseOrderItemUnitPrice() {
		return purchaseOrderItemUnitPrice;
	}

	public void setPurchaseOrderItemUnitPrice(BigDecimal purchaseOrderItemUnitPrice) {
		this.purchaseOrderItemUnitPrice = purchaseOrderItemUnitPrice;
	}

	public BigDecimal getPurchaseOrderItemTax() {
		return purchaseOrderItemTax;
	}

	public void setPurchaseOrderItemTax(BigDecimal purchaseOrderItemTax) {
		this.purchaseOrderItemTax = purchaseOrderItemTax;
	}
	
	
}
