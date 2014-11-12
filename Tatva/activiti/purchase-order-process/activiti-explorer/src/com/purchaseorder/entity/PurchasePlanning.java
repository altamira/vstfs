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
 * Entity class for table 'PURCHASE_PLANNING'
 *
 */
@Entity
@Table(name="PURCHASE_PLANNING")
public class PurchasePlanning 
{
	@Id
	@SequenceGenerator(name="PurchasePlanningSequence", sequenceName="PURCHASE_PLANNING_SEQUENCE", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="PurchasePlanningSequence")
	@Column(name="PLANNING_ID")
	private BigDecimal planningId;
	
	@Column(name="QUOTATION_ID")
	private BigDecimal quotationId;
	
	@Temporal(TemporalType.DATE)
	@Column(name="PLANNING_DATE")
	private Date planningDate;
	
	@Column(name="PLANNING_OWNER")
	private String planningOwner;
	
	@Temporal(TemporalType.DATE)
	@Column(name="PLANNING_APPROVE_DATE")
	private Date planningApproveDate;
	
	@Column(name="PLANNING_APPROVE_USER")
	private String planningApproveUser;

	public BigDecimal getPlanningId() {
		return planningId;
	}

	public void setPlanningId(BigDecimal planningId) {
		this.planningId = planningId;
	}

	public BigDecimal getQuotationId() {
		return quotationId;
	}

	public void setQuotationId(BigDecimal quotationId) {
		this.quotationId = quotationId;
	}

	public Date getPlanningDate() {
		return planningDate;
	}

	public void setPlanningDate(Date planningDate) {
		this.planningDate = planningDate;
	}

	public String getPlanningOwner() {
		return planningOwner;
	}

	public void setPlanningOwner(String planningOwner) {
		this.planningOwner = planningOwner;
	}

	public Date getPlanningApproveDate() {
		return planningApproveDate;
	}

	public void setPlanningApproveDate(Date planningApproveDate) {
		this.planningApproveDate = planningApproveDate;
	}

	public String getPlanningApproveUser() {
		return planningApproveUser;
	}

	public void setPlanningApproveUser(String planningApproveUser) {
		this.planningApproveUser = planningApproveUser;
	}
}
