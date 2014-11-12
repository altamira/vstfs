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
 * Entity class for table 'QUOTATION'
 *
 */
@Entity
@Table(name="QUOTATION")
public class Quotation 
{
	@Id
	@SequenceGenerator(name="QuotationSequence", sequenceName="QUOTATION_SEQUENCE", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="QuotationSequence")
	@Column(name="QUOTATION_ID")
	private BigDecimal quotationId;
	
	@Temporal(TemporalType.DATE)
	@Column(name="QUOTATION_DATE")
	private Date quotationDate;
	
	@Column(name="QUOTATION_CREATOR")
	private String quotationCreator;

	public BigDecimal getQuotationId() 
	{
		return quotationId;
	}

	public void setQuotationId(BigDecimal quotationId) 
	{
		this.quotationId = quotationId;
	}

	public Date getQuotationDate() 
	{
		return quotationDate;
	}

	public void setQuotationDate(Date quotationDate) 
	{
		this.quotationDate = quotationDate;
	}

	public String getQuotationCreator() 
	{
		return quotationCreator;
	}

	public void setQuotationCreator(String quotationCreator) 
	{
		this.quotationCreator = quotationCreator;
	}	
}
