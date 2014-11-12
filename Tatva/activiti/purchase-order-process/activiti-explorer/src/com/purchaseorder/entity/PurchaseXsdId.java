package com.purchaseorder.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class PurchaseXsdId implements Serializable
{
	@Column(name="XSD_NAME")
	String xsdName;
	
	@Column(name="XSD_VERSION")
	String xsdVersion;
	
	@Column(name="PKG_REVISION")
	String pkgRevision;
	
	@Column(name="PACKAGE_TYPE")
	String packageType;
	
	public String getXsdName() 
	{
		return xsdName;
	}

	public void setXsdName(String xsdName) 
	{
		this.xsdName = xsdName;
	}
	
	public String getXsdVersion() 
	{
		return xsdVersion;
	}

	public void setXsdVersion(String xsdVersion) 
	{
		this.xsdVersion = xsdVersion;
	}
	
	public String getPkgRevision() 
	{
		return pkgRevision;
	}

	public void setPkgRevision(String pkgRevision) 
	{
		this.pkgRevision = pkgRevision;
	}

	public String getPackageType() 
	{
		return packageType;
	}

	public void setPackageType(String packageType) 
	{
		this.packageType = packageType;
	}
}
