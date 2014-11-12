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
 * Entity class for table 'REQUEST_ITEM'
 *
 */
@Entity
@Table(name="REQUEST_ITEM")
public class RequestItem 
{
	@Id
	@SequenceGenerator(name="RequestItemSequence", sequenceName="REQUEST_ITEM_SEQUENCE", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="RequestItemSequence")
	@Column(name="REQUEST_ITEM_ID")
	private BigDecimal requestItemId;
	
//	@ManyToOne(fetch=FetchType.LAZY)
//	@JoinColumn(name="REQUEST_ID", nullable=false)
//	private Request request;
	
	@Column(name="REQUEST_ID")
	private BigDecimal requestId;
	
	@Column(name="MATERIAL_ID")
	private BigDecimal materialId;
	
	@Temporal(TemporalType.DATE)
	@Column(name="REQUEST_DATE")
	private Date requestArrivalDate;
	
	@Column(name="REQUEST_WEIGHT")
	private BigDecimal requestWeight;

	public BigDecimal getRequestItemId() {
		return requestItemId;
	}

	public void setRequestItemId(BigDecimal requestItemId) {
		this.requestItemId = requestItemId;
	}

//	public Request getRequest() {
//		return request;
//	}
//
//	public void setRequest(Request request) {
//		this.request = request;
//	}

	public BigDecimal getRequestId() {
		return requestId;
	}

	public void setRequestId(BigDecimal requestId) {
		this.requestId = requestId;
	}

	public BigDecimal getMaterialId() {
		return materialId;
	}

	public void setMaterialId(BigDecimal materialId) {
		this.materialId = materialId;
	}

	public Date getRequestArrivalDate() {
		return requestArrivalDate;
	}

	public void setRequestArrivalDate(Date requestArrivalDate) {
		this.requestArrivalDate = requestArrivalDate;
	}

	public BigDecimal getRequestWeight() {
		return requestWeight;
	}

	public void setRequestWeight(BigDecimal requestWeight) {
		this.requestWeight = requestWeight;
	}
}