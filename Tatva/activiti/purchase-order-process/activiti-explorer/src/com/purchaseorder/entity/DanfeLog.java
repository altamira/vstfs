package com.purchaseorder.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * 
 * @author PARTH
 * 
 * Entity class for table 'DANFE_LOG'
 *
 */
@Entity
@Table(name="DANFE_LOG")
public class DanfeLog 
{
	@Id
	@Column(name="REPORT_INSTANCE")
	@Lob
	private byte[] reportInstance;
	
	@Column(name="GENERATED_DATETIME", insertable=false)
	private Date dateTime;

	public byte[] getReportInstance() 
	{
		return reportInstance;
	}

	public void setReportInstance(byte[] reportInstance) 
	{
		this.reportInstance = reportInstance;
	}

	public Date getDateTime() 
	{
		return dateTime;
	}

	public void setDateTime(Date dateTime) 
	{
		this.dateTime = dateTime;
	}
}
