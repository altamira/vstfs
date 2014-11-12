package com.purchaseorder.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * 
 * @author PARTH
 *
 * Entity class for table 'PLANNING_REPORT'
 */
@Entity
@Table(name="PLANNING_REPORT")
public class PlanningReport 
{
	@Id
	@Column(name="REPORT_ID")
	@SequenceGenerator(name="PlanningReportSequence", sequenceName="PLANNING_REPORT_SEQUENCE")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="PlanningReportSequence")
	private long reportId;
	
	@Column(name="JASPER_FILE")
	@Lob
	private byte[] jasperFile;
	
	@Column(name="JRXML_FILE")
	@Lob
	private byte[] jrxmlFile;
	
	@Column(name="ALTAMIRA_LOGO")
	@Lob
	private byte[] altamiraLogo;
	
	@Column(name="GENERATED_DATETIME")
	private Date dateTime;

	public long getReportId() 
	{
		return reportId;
	}

	public void setReportId(long reportId) 
	{
		this.reportId = reportId;
	}

	public byte[] getJasperFile() 
	{
		return jasperFile;
	}

	public void setJasperFile(byte[] jasperFile) 
	{
		this.jasperFile = jasperFile;
	}

	public byte[] getJrxmlFile() 
	{
		return jrxmlFile;
	}

	public void setJrxmlFile(byte[] jrxmlFile) 
	{
		this.jrxmlFile = jrxmlFile;
	}

	public byte[] getAltamiraLogo() 
	{
		return altamiraLogo;
	}

	public void setAltamiraLogo(byte[] altamiraLogo) 
	{
		this.altamiraLogo = altamiraLogo;
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
