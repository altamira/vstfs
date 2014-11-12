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
 * Entity class for table 'PURCHASE_ORDER'
 *
 */
@Entity
@Table(name="PURCHASE_ORDER")
public class PurchaseOrder 
{
	@Id
	@SequenceGenerator(name="PurchaseOrderSequence", sequenceName="PURCHASE_ORDER_SEQUENCE", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="PurchaseOrderSequence")
	@Column(name="PURCHASE_ORDER_ID")
	private BigDecimal purchaseOrderId;
	
	@Column(name="PURCHASE_PLANNING_ID")
	private BigDecimal purchasePlanningId;
	
	@Column(name="SUPPLIER_ID")
	private BigDecimal supplierId;
	
	@Temporal(TemporalType.DATE)
	@Column(name="PURCHASE_ORDER_DATE")
	private Date purchaseOrderDate;
	
	@Column(name="COMMENTS")
	private String comments;

	public BigDecimal getPurchaseOrderId() {
		return purchaseOrderId;
	}

	public void setPurchaseOrderId(BigDecimal purchaseOrderId) {
		this.purchaseOrderId = purchaseOrderId;
	}

	public BigDecimal getPurchasePlanningId() {
		return purchasePlanningId;
	}

	public void setPurchasePlanningId(BigDecimal purchasePlanningId) {
		this.purchasePlanningId = purchasePlanningId;
	}

	public BigDecimal getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(BigDecimal supplierId) {
		this.supplierId = supplierId;
	}

	public Date getPurchaseOrderDate() {
		return purchaseOrderDate;
	}

	public void setPurchaseOrderDate(Date purchaseOrderDate) {
		this.purchaseOrderDate = purchaseOrderDate;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
	
}
