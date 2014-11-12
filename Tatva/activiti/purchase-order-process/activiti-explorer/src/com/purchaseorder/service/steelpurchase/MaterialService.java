package com.purchaseorder.service.steelpurchase;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.transaction.annotation.Transactional;

import com.purchaseorder.dao.PurchaseOrderDao;
import com.purchaseorder.entity.Material;

/**
 * 
 * @author PARTH
 *
 */
public class MaterialService 
{
	@Autowired
	PurchaseOrderDao purchaseOrderDao;
	
	public PurchaseOrderDao getPurchaseOrderDao() 
	{
		return purchaseOrderDao;
	}

	public void setPurchaseOrderDao(PurchaseOrderDao purchaseOrderDao) 
	{
		this.purchaseOrderDao = purchaseOrderDao;
	}
	
	/**
	 * This method adds material 
	 * 
	 * @param jsonObject - JSON request body
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	@Transactional
	public Object addMaterial(String jsonObject,
							  Principal principal)
	{
		String name = null;
		Map<String, String> resultMap = new HashMap<String, String>();
		boolean error = false;
		StringBuffer reqFields = new StringBuffer();
		StringBuffer fieldConstrints = new StringBuffer();
		
		// HTTP Basic Authentication
		try
		{
			name = principal.getName();			
		}
		catch (NullPointerException e) 
		{
			resultMap.put("error", "HTTP 401 Unauthorized");
			resultMap.put("description", "Access Denied: Authorization required");
			
			return resultMap;
		}
		
		// Parse json object to HashMap
		Map<String, String> materialProperties = new HashMap<String, String>();
		try 
		{
			materialProperties = new ObjectMapper().readValue(jsonObject, new TypeReference<HashMap<String, String>>(){});
		} 
		catch (Exception e) 
		{
			resultMap.put("error", "Bad Request");
			resultMap.put("description", "Unable to parse Request Body.");
			
			return resultMap;
		}
		
		// Check for the mandatory fields
		if(!materialProperties.containsKey("materialCode"))
		{
			if(reqFields.toString().isEmpty())
				reqFields.append("materialCode");
			else
				reqFields.append(", materialCode");
				
			error = true;
		}
		
		if(!materialProperties.containsKey("materialLamination"))
		{
			if(reqFields.toString().isEmpty())
				reqFields.append("materialLamination");
			else
				reqFields.append(", materialLamination");
			
			error = true;
		}
		
		if(!materialProperties.containsKey("materialTreatment"))
		{
			if(reqFields.toString().isEmpty())
				reqFields.append("materialTreatment");
			else
				reqFields.append(", materialTreatment");
			
			error = true;
		}
		
		if(!materialProperties.containsKey("materialThickness"))
		{
			if(reqFields.toString().isEmpty())
				reqFields.append("materialThickness");
			else
				reqFields.append(", materialThickness");
			
			error = true;
		}
		
		if(!materialProperties.containsKey("materialWidth"))
		{
			if(reqFields.toString().isEmpty())
				reqFields.append("materialWidth");
			else
				reqFields.append(", materialWidth");
			
			error = true;
		}
		
		if(error)
		{
			resultMap.put("Required Fields", reqFields.toString());
			
			return resultMap;
		}
		
		String materialCode = materialProperties.get("materialCode");
		String materialLamination = materialProperties.get("materialLamination");
		String materialTreatment = materialProperties.get("materialTreatment");
		BigDecimal materialThickness = null;
		BigDecimal materialWidth = null;
		BigDecimal materialLength = null;
		String materialDescription = null;
		BigDecimal materialTax=null;
		
		// Check field constraints
		try
		{
			materialThickness = new BigDecimal(materialProperties.get("materialThickness")).setScale(2, RoundingMode.CEILING);
		}
		catch(Exception e)
		{
			if(fieldConstrints.toString().isEmpty())
				fieldConstrints.append("Could not parse materialThickness");
			else
				fieldConstrints.append(", Could not parse materialThickness");
			
			error = true;
		}
		
		try
		{
			materialWidth = new BigDecimal(materialProperties.get("materialWidth")).setScale(2, RoundingMode.CEILING);			
		}
		catch(Exception e)
		{
			if(fieldConstrints.toString().isEmpty())
				fieldConstrints.append("Could not parse materialWidth");
			else
				fieldConstrints.append(", Could not parse materialWidth");
			
			error = true;
		}
		
		if(materialProperties.get("materialTax")!=null)
		{
			try
			{
				materialTax = new BigDecimal(materialProperties.get("materialTax")).setScale(2, RoundingMode.CEILING);
			}
			catch(Exception e)
			{
				if(fieldConstrints.toString().isEmpty())
					fieldConstrints.append("Could not parse materialTax");
				else
					fieldConstrints.append(", Could not parse materialTax");
				
				error = true;
			}
		}
		
		
		if(materialProperties.get("materialLength")!=null)
		{
			try
			{
				materialLength = new BigDecimal(materialProperties.get("materialLength")).setScale(2, RoundingMode.CEILING);							
			}
			catch(Exception e)
			{
				if(fieldConstrints.toString().isEmpty())
					fieldConstrints.append("Could not parse materialLength");
				else
					fieldConstrints.append(", Could not parse materialLength");
				
				error = true;
			}
		}
		
		if(materialProperties.get("materialDescription")!=null)
		{
			materialDescription = materialProperties.get("materialDescription"); 
			
			if(materialDescription.length() > 100)
			{
				if(fieldConstrints.toString().isEmpty())
					fieldConstrints.append("materialDescription should not exceed 100 characters");
				else
					fieldConstrints.append(", materialDescription should not exceed 100 characters");
				
				error = true;
			}
		}
		
		if(materialCode.length() > 20)
		{
			if(fieldConstrints.toString().isEmpty())
				fieldConstrints.append("materialCode should not exceed 20 characters");
			else
				fieldConstrints.append(", materialCode should not exceed 20 characters");
			
			error = true;
		}
		
		if(materialLamination.length() > 2)
		{
			if(fieldConstrints.toString().isEmpty())
				fieldConstrints.append("materialLamination should not exceed 2 characters");
			else
				fieldConstrints.append(", materialLamination should not exceed 2 characters");
			
			error = true;
		}
		
		if(materialTreatment.length() > 2)
		{
			if(fieldConstrints.toString().isEmpty())
				fieldConstrints.append("materialTreatment should not exceed 2 characters");
			else
				fieldConstrints.append(", materialTreatment should not exceed 2 characters");
			
			error = true;
		}
		
		
		
		if(error)
		{
			resultMap.put("Fields Constraints", fieldConstrints.toString());
			
			return resultMap;
		}
		
		// Check if materialCode already exists or not
		try
		{
			Material material = purchaseOrderDao.getMaterialDetailsByCode(materialCode);
			
			if(material!=null)
			{
				resultMap.put("error", "Material Code already exists");
				
				return resultMap;
			}
		}
		catch(Exception e){	}
		
		// insert new Material 
		Material material = new Material();
		material.setMaterialCode(materialCode);
		material.setMaterialLamination(materialLamination);
		material.setMaterialTreatment(materialTreatment);
		material.setMaterialThickness(materialThickness);
		material.setMaterialWidth(materialWidth);
		material.setMaterialLength(materialLength);
		material.setMaterialDescription(materialDescription);
		material.setMaterialTax(materialTax);
		
		BigDecimal materialId = null;
		
		try
		{
			materialId = purchaseOrderDao.insertMaterial(material);
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not insert material");
			
			return resultMap;
		}
		
		resultMap.put("success", "Material inserted successfully");
		resultMap.put("materialId", materialId.toString());
		resultMap.put("materialCode", materialCode);
		
		return resultMap;
	}
	
	/**
	 * This method fetches material by material id
	 * 
	 * @param id - material id
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object getMaterialById(String id,
							  	  Principal principal)
	{
		String name = null;
		Map<String, String> resultMap = new HashMap<String, String>();

		// HTTP Basic Authentication
		try
		{
			name = principal.getName();			
		}
		catch (NullPointerException e) 
		{
			resultMap.put("error", "HTTP 401 Unauthorized");
			resultMap.put("description", "Access Denied: Authorization required");

			return resultMap;
		}
		
		BigDecimal materialId = null;
		try
		{
			materialId = new BigDecimal(id);			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not parse the id provided.");
			
			return resultMap;
		}
		
		// Check if materialId already exists or not
		try
		{
			Material material = purchaseOrderDao.getMaterialDetailsById(materialId);

			if(material!=null)
			{
				resultMap.put("materialId", material.getMaterialId().toString());
				resultMap.put("materialCode", material.getMaterialCode());
				resultMap.put("materialLamination", material.getMaterialLamination());				
				resultMap.put("materialTreatment", material.getMaterialTreatment());				
				resultMap.put("materialThickness", material.getMaterialThickness().toString());				
				resultMap.put("materialWidth", material.getMaterialWidth().toString());	
				
				if(material.getMaterialLength()!=null)
				{
					resultMap.put("materialLength", material.getMaterialLength().toString());									
				}
				
				if(material.getMaterialDescription()!=null)
				{
					resultMap.put("materialDescription", material.getMaterialDescription());
				}
				
				if(material.getMaterialTax()!=null)
				{
					resultMap.put("materialTax", material.getMaterialTax().toString());	
				}

				return resultMap;
			}
			else
			{
				resultMap.put("error", "Could not find material");
				
				return resultMap;
			}
		}
		catch(Exception e)
		{	
			resultMap.put("error", "Error while fetching material details");
		}
		
		return resultMap;
	}
	
	/**
	 * This method fetches material by material code.
	 * 
	 * @param materialCode - Material Code
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object getMaterialByCode(String materialCode,
							  		Principal principal)
	{
		String name = null;
		Map<String, String> resultMap = new HashMap<String, String>();

		// HTTP Basic Authentication
		try
		{
			name = principal.getName();			
		}
		catch (NullPointerException e) 
		{
			resultMap.put("error", "HTTP 401 Unauthorized");
			resultMap.put("description", "Access Denied: Authorization required");

			return resultMap;
		}

		// Check if materialCode already exists or not
		try
		{
			Material material = purchaseOrderDao.getMaterialDetailsByCode(materialCode);

			if(material!=null)
			{
				resultMap.put("materialId", material.getMaterialId().toString());
				resultMap.put("materialCode", material.getMaterialCode());
				resultMap.put("materialLamination", material.getMaterialLamination());				
				resultMap.put("materialTreatment", material.getMaterialTreatment());				
				resultMap.put("materialThickness", material.getMaterialThickness().toString());				
				resultMap.put("materialWidth", material.getMaterialWidth().toString());			

				if(material.getMaterialLength()!=null)
				{
					resultMap.put("materialLength", material.getMaterialLength().toString());									
				}
				
				if(material.getMaterialDescription()!=null)
				{
					resultMap.put("materialDescription", material.getMaterialDescription());
				}
				
				if(material.getMaterialTax()!=null)
				{
					resultMap.put("materialTax", material.getMaterialTax().toString());	
				}

				return resultMap;
			}
			else
			{
				resultMap.put("error", "Could not find material");

				return resultMap;
			}
		}
		catch(Exception e)
		{	
			resultMap.put("error", "Error while fetching material details");
		}

		return resultMap;
	}
	
	/**
	 * This method returns all materials
	 * 
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object getAllMaterials(Principal principal)
	{
		String name = null;
		Map<String, String> resultMap = new HashMap<String, String>();

		// HTTP Basic Authentication
		try
		{
			name = principal.getName();			
		}
		catch (NullPointerException e) 
		{
			resultMap.put("error", "HTTP 401 Unauthorized");
			resultMap.put("description", "Access Denied: Authorization required");

			return resultMap;
		}
		
		// Fetch all materials stored in database
		try
		{
			List<Material> materials = purchaseOrderDao.getAllMaterils();

			return materials;
		}
		catch(Exception e)
		{	
			resultMap.put("error", "Error while fetching material details");
		}
		
		return resultMap;
	}
	
	/**
	 * This method updates material
	 * 
	 * @param jsonObject - JSON request body
	 * @param id - Material ID
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object updateMaterial(String jsonObject,
								 String id,
								 Principal principal)
	{
		String name = null;
		Map<String, String> resultMap = new HashMap<String, String>();
		boolean error = false;
		StringBuffer fieldConstrints = new StringBuffer();

		// HTTP Basic Authentication
		try
		{
			name = principal.getName();			
		}
		catch (NullPointerException e) 
		{
			resultMap.put("error", "HTTP 401 Unauthorized");
			resultMap.put("description", "Access Denied: Authorization required");

			return resultMap;
		}
		
		// Parse json object to HashMap
		Map<String, String> materialProperties = new HashMap<String, String>();
		try 
		{
			materialProperties = new ObjectMapper().readValue(jsonObject, new TypeReference<HashMap<String, String>>(){});
		}
		catch (Exception e) 
		{
			resultMap.put("error", "Bad Request");
			resultMap.put("description", "Unable to parse Request Body.");

			return resultMap;
		}
		
		BigDecimal materialId = null;
		try
		{
			materialId = new BigDecimal(id);			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not parse the id provided.");
			
			return resultMap;
		}
		
		// check if material Id is available for update or not
		Material material = null;
		try
		{
			material = purchaseOrderDao.getMaterialDetailsById(materialId);
			
			if(material==null)
				throw new Exception();
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not find material by id:"+id);
			
			return resultMap;
		}
		
		String materialCode = null;
		String materialLamination = null;
		String materialTreatment = null;
		BigDecimal materialThickness = null;
		BigDecimal materialWidth = null;
		BigDecimal materialLength = null;
		String materialDescription = null;
		BigDecimal materialTax=null;
		
		if(materialProperties.get("materialCode")!=null)
		{
			materialCode = materialProperties.get("materialCode");
			material.setMaterialCode(materialCode);
		}
		
		if(materialProperties.get("materialLamination")!=null)
		{
			materialLamination = materialProperties.get("materialLamination");
			
			if(materialLamination.length() > 2)
			{
				if(fieldConstrints.toString().isEmpty())
					fieldConstrints.append("materialLamination should not exceed 2 characters");
				else
					fieldConstrints.append(", materialLamination should not exceed 2 characters");
				
				error = true;
			}
			
			material.setMaterialLamination(materialLamination);
		}
		
		if(materialProperties.get("materialTreatment")!=null)
		{
			materialTreatment = materialProperties.get("materialTreatment");
			
			if(materialTreatment.length() > 2)
			{
				if(fieldConstrints.toString().isEmpty())
					fieldConstrints.append("materialTreatment should not exceed 2 characters");
				else
					fieldConstrints.append(", materialTreatment should not exceed 2 characters");
				
				error = true;
			}
			
			material.setMaterialTreatment(materialTreatment);
		}
		
		if(materialProperties.get("materialThickness")!=null)
		{
			try
			{
				materialThickness = new BigDecimal(materialProperties.get("materialThickness")).setScale(2, RoundingMode.CEILING);
				material.setMaterialThickness(materialThickness);
			}
			catch(Exception e)
			{
				if(fieldConstrints.toString().isEmpty())
					fieldConstrints.append("Could not parse materialThickness");
				else
					fieldConstrints.append(", Could not parse materialThickness");

				error = true;
			}
		}
		
		if(materialProperties.get("materialWidth")!=null)
		{
			try
			{
				materialWidth = new BigDecimal(materialProperties.get("materialWidth")).setScale(2, RoundingMode.CEILING);
				material.setMaterialWidth(materialWidth);
			}
			catch(Exception e)
			{
				if(fieldConstrints.toString().isEmpty())
					fieldConstrints.append("Could not parse materialWidth");
				else
					fieldConstrints.append(", Could not parse materialWidth");

				error = true;
			}
		}
		
		if(materialProperties.get("materialLength")!=null)
		{
			try
			{
				materialLength = new BigDecimal(materialProperties.get("materialLength")).setScale(2, RoundingMode.CEILING);
				material.setMaterialLength(materialLength);
			}
			catch(Exception e)
			{
				if(fieldConstrints.toString().isEmpty())
					fieldConstrints.append("Could not parse materialLength");
				else
					fieldConstrints.append(", Could not parse materialLength");

				error = true;
			}
		}
		
		if(materialProperties.get("materialDescription")!=null)
		{
			materialDescription = materialProperties.get("materialDescription");
			
			if(materialDescription.length() > 100)
			{
				if(fieldConstrints.toString().isEmpty())
					fieldConstrints.append("materialDescription should not exceed 100 characters");
				else
					fieldConstrints.append(", materialDescription should not exceed 100 characters");
				
				error = true;
			}
			
			material.setMaterialDescription(materialDescription);
		}
		
		if(materialProperties.get("materialTax")!=null)
		{
			try
			{
				materialTax = new BigDecimal(materialProperties.get("materialTax")).setScale(2, RoundingMode.CEILING);
				material.setMaterialTax(materialTax);
			}
			catch(Exception e)
			{
				if(fieldConstrints.toString().isEmpty())
					fieldConstrints.append("Could not parse materialTax");
				else
					fieldConstrints.append(", Could not parse materialTax");

				error = true;
			}
		}
		
		if(error)
		{
			resultMap.put("Fields Constraints", fieldConstrints.toString());
			
			return resultMap;
		}
		
		// update material
		try
		{
			purchaseOrderDao.updateMaterial(material);			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not update material");
			
			return resultMap;
		}
		
		resultMap.put("success", "Material updated successfully");
		return resultMap;
	}
	
	/**
	 * This method deletes material.
	 * 
	 * @param id - Material ID
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object deleteMaterial(String id,
								 Principal principal)
	{
		String name = null;
		Map<String, String> resultMap = new HashMap<String, String>();

		// HTTP Basic Authentication
		try
		{
			name = principal.getName();			
		}
		catch (NullPointerException e) 
		{
			resultMap.put("error", "HTTP 401 Unauthorized");
			resultMap.put("description", "Access Denied: Authorization required");

			return resultMap;
		}
		
		try
		{
			boolean result = purchaseOrderDao.deleteMaterialById(id);
			
			if(!result)
				throw new Exception();
				
			resultMap.put("success", "Material deleted having id:"+id);
		}
		catch(ConstraintViolationException cve)
		{
			resultMap.put("error", "Cannot delete: child records found.");
			return resultMap;
		}
		catch(JpaSystemException jse)
		{
			if(StringUtils.containsIgnoreCase(jse.getCause().toString(), "ConstraintViolationException"))
			{
				resultMap.put("error", "Cannot delete: child records found.");
				return resultMap;
			}
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not delete material");
			return resultMap;
		}
		
		return resultMap;
	}
}
