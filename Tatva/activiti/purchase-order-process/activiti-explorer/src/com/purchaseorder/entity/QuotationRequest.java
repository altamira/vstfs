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
 * Entity class for table 'QUOTATION_REQUEST'
 *
 */
@Entity
@Table(name="QUOTATION_REQUEST")
public class QuotationRequest 
{
	@Id
	@SequenceGenerator(name="QuotationRequestSequence", sequenceName="QUOTATION_REQUEST_SEQUENCE", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="QuotationRequestSequence")
	@Column(name="QUOTATION_REQUEST_ID")
	private BigDecimal quotationRequestId;
	
	@Column(name="QUOTATION_ID")
	private BigDecimal quotationId;
	
	@Column(name="REQUEST_ID")
	private BigDecimal requestId;

	public BigDecimal getQuotationRequestId() {
		return quotationRequestId;
	}

	public void setQuotationRequestId(BigDecimal quotationRequestId) {
		this.quotationRequestId = quotationRequestId;
	}

	public BigDecimal getQuotationId() {
		return quotationId;
	}

	public void setQuotationId(BigDecimal quotationId) {
		this.quotationId = quotationId;
	}

	public BigDecimal getRequestId() {
		return requestId;
	}

	public void setRequestId(BigDecimal requestId) {
		this.requestId = requestId;
	}
	
	
}
