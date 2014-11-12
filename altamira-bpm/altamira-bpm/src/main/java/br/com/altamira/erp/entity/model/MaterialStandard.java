package br.com.altamira.erp.entity.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Entity
@Table(name="MATERIAL_STANDARD")
public class MaterialStandard implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

//	@EmbeddedId
//	MaterialStandardId matrialStandardId;
//	
//	@Column(name="STANDARD_ACCEPT", columnDefinition="char")
//	String StandardAccept;
//
//	public MaterialStandardId getMatrialStandardId() 
//	{
//		return matrialStandardId;
//	}
//
//	public void setMatrialStandardId(MaterialStandardId matrialStandardId) 
//	{
//		this.matrialStandardId = matrialStandardId;
//	}

//	public String getStandardAccept() 
//	{
//		return StandardAccept;
//	}
//
//	public void setStandardAccept(String standardAccept) 
//	{
//		StandardAccept = standardAccept;
//	}
	
	

}
