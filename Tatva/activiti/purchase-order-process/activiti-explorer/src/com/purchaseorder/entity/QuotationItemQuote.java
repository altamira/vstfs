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
 * Entity class for table 'QUOTATION_ITEM_QUOTE'
 *
 */
@Entity
@Table(name="QUOTATION_ITEM_QUOTE")
public class QuotationItemQuote {

	@Id
	@SequenceGenerator(name="QuotationItemQuoteSequence",sequenceName="QUOTATION_ITEM_QUOTE_SEQUENCE",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE,generator="QuotationItemQuoteSequence")
	@Column(name="QUOTATION_ITEM_QUOTE_ID")
	private BigDecimal quotationItemQuoteId;
	
	@Column(name="QUOTATION_ITEM_ID")
	private BigDecimal quotationItemId;
	
	@Column(name="SUPPLIER_ID")
	private BigDecimal supplierId;
	
	@Column(name="SUPPLIER_PRICE")
	private BigDecimal supplierPrice;
	
	@Column(name="SUPPLIER_WEIGHT")
	private BigDecimal supplierWeight;
	
	@Column(name="SUPPLIER_STANDARD")
	private String supplierStandard;

	public BigDecimal getQuotationItemQuoteId() {
		return quotationItemQuoteId;
	}

	public void setQuotationItemQuoteId(BigDecimal quotationItemQuoteId) {
		this.quotationItemQuoteId = quotationItemQuoteId;
	}

	public BigDecimal getQuotationItemId() {
		return quotationItemId;
	}

	public void setQuotationItemId(BigDecimal quotationItemId) {
		this.quotationItemId = quotationItemId;
	}

	public BigDecimal getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(BigDecimal supplierId) {
		this.supplierId = supplierId;
	}

	public BigDecimal getSupplierPrice() {
		return supplierPrice;
	}

	public void setSupplierPrice(BigDecimal supplierPrice) {
		this.supplierPrice = supplierPrice;
	}

	public BigDecimal getSupplierWeight() {
		return supplierWeight;
	}

	public void setSupplierWeight(BigDecimal supplierWeight) {
		this.supplierWeight = supplierWeight;
	}

	public String getSupplierStandard() {
		return supplierStandard;
	}

	public void setSupplierStandard(String supplierStandard) {
		this.supplierStandard = supplierStandard;
	}
}
