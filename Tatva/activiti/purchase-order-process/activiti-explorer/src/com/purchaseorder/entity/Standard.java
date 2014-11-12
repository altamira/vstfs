package com.purchaseorder.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * 
 * @author PARTH
 * 
 * Entity class for table 'STANDARD'
 *
 */
@Entity
@Table(name="STANDARD", uniqueConstraints=@UniqueConstraint(columnNames="STANDARD_NAME"))
public class Standard 
{
	@Id
	@Column(name="STANDARD_ID")
	private BigDecimal standardId;
	
	@Column(name="STANDARD_NAME")
	private String standardName;
	
	@Column(name="STANDARD_DESCRIPTION", columnDefinition="CLOB")
	private String standardDescription;

	public BigDecimal getStandardId() 
	{
		return standardId;
	}

	public void setStandardId(BigDecimal standardId) 
	{
		this.standardId = standardId;
	}

	public String getStandardName() 
	{
		return standardName;
	}

	public void setStandardName(String standardName) 
	{
		this.standardName = standardName;
	}

	public String getStandardDescription() 
	{
		return standardDescription;
	}

	public void setStandardDescription(String standardDescription) 
	{
		this.standardDescription = standardDescription;
	} 
}
