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
 * Entity class for table 'REQUEST'
 *
 */
@Entity
@Table(name="REQUEST")
public class Request 
{
	@Id
	@SequenceGenerator(name="RequestSequence", sequenceName="REQUEST_SEQUENCE", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="RequestSequence")
	@Column(name="REQUEST_ID")
	private BigDecimal requestId;
	
	@Temporal(TemporalType.DATE)
	@Column(name="REQUEST_DATE")
	private Date requestDate;
	
	@Column(name="REQUEST_CREATOR")
	private String requestCreator;
	
//	@OneToMany(fetch=FetchType.LAZY, mappedBy="REQUEST")
//	private Set<RequestItem> requestItemSet = new HashSet<RequestItem>();

	public BigDecimal getRequestId() {
		return requestId;
	}

	public void setRequestId(BigDecimal requestId) {
		this.requestId = requestId;
	}

	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	public String getRequestCreator() {
		return requestCreator;
	}

	public void setRequestCreator(String requestCreator) {
		this.requestCreator = requestCreator;
	}

//	public Set<RequestItem> getRequestItemSet() {
//		return requestItemSet;
//	}
//
//	public void setRequestItemSet(Set<RequestItem> requestItemSet) {
//		this.requestItemSet = requestItemSet;
//	}
}
