package com.purchaseorder.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 
 * @author PARTH
 * 
 * Entity class for table 'MATERIAL_STANDARD'
 *
 */
@Entity
@Table(name="MATERIAL_STANDARD")
public class MaterialStandard 
{
	@EmbeddedId
	MaterialStandardId matrialStandardId;
	
	@Column(name="STANDARD_ACCEPT", columnDefinition="char")
	String StandardAccept;

	public MaterialStandardId getMatrialStandardId() 
	{
		return matrialStandardId;
	}

	public void setMatrialStandardId(MaterialStandardId matrialStandardId) 
	{
		this.matrialStandardId = matrialStandardId;
	}

	public String getStandardAccept() 
	{
		return StandardAccept;
	}

	public void setStandardAccept(String standardAccept) 
	{
		StandardAccept = standardAccept;
	}
	
	

}
