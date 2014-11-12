package com.purchaseorder.service.steelpurchase;

import java.math.BigDecimal;
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

import com.purchaseorder.dao.PurchaseOrderDao;
import com.purchaseorder.entity.Supplier;

/**
 * 
 * @author PARTH
 *
 */
public class SupplierService 
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
	 * This method adds Supplier.
	 * 
	 * @param jsonObject - JSON request body
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object addSupplier(String jsonObject,
							  Principal principal)
	{
		String name = null;
		Map<String, String> resultMap = new HashMap<String, String>();
		StringBuffer reqFields = new StringBuffer();
		StringBuffer fieldConstrints = new StringBuffer();
		boolean error = false;
		
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
		Map<String, String> supplierProperties = new HashMap<String, String>();
		try 
		{
			supplierProperties = new ObjectMapper().readValue(jsonObject, new TypeReference<HashMap<String, String>>(){});
		}
		catch (Exception e) 
		{
			resultMap.put("error", "Bad Request");
			resultMap.put("description", "Unable to parse Request Body.");

			return resultMap;
		}
		
		// Check for the mandatory fields
		if(!supplierProperties.containsKey("supplierId"))
		{
			if(reqFields.toString().isEmpty())
				reqFields.append("supplierId");
			else
				reqFields.append(", supplierId");
				
			error = true;
		}
		
		if(!supplierProperties.containsKey("supplierName"))
		{
			if(reqFields.toString().isEmpty())
				reqFields.append("supplierName");
			else
				reqFields.append(", supplierName");
				
			error = true;
		}
		
		if(error)
		{
			resultMap.put("Required Fields", reqFields.toString());
			
			return resultMap;
		}
		
		BigDecimal supplierId = null;
		String supplierName = supplierProperties.get("supplierName");
		
		// check field constraints
		try
		{
			supplierId = new BigDecimal(supplierProperties.get("supplierId"));
		}
		catch(Exception e)
		{
			if(fieldConstrints.toString().isEmpty())
				fieldConstrints.append("Could not parse supplierId");
			else
				fieldConstrints.append(", Could not parse supplierId");
			
			error = true;
		}
		
		if(supplierName.length() > 50)
		{
			if(fieldConstrints.toString().isEmpty())
				fieldConstrints.append("supplierName should not exceed 50 characters");
			else
				fieldConstrints.append(", supplierName should not exceed 50 characters");
			
			error = true;
		}
		
		if(error)
		{
			resultMap.put("Fields Constraints", fieldConstrints.toString());
			
			return resultMap;
		}
		
		//check if supplier id is already present
		boolean supplierIdAlreadyStored = purchaseOrderDao.isSupplierIdAlreadyStored(supplierId);
		if(supplierIdAlreadyStored)
		{
			resultMap.put("error", "Supplier id already exists.");
			return resultMap;
		}
		
		// check if supplier name is already present
		boolean supplierNameAlreadyStored = purchaseOrderDao.isSupplierNameAlreadyStored(supplierName);
		if(supplierNameAlreadyStored)
		{
			resultMap.put("error", "Supplier name already exists.");
			return resultMap;
		}
		
		// insert new supplier
		Supplier supplier = new Supplier();
		supplier.setSupplierId(supplierId);
		supplier.setSupplierName(supplierName);
		
		try
		{
			purchaseOrderDao.insertSupplier(supplier);
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not insert supplier");
			
			return resultMap;
		}
				
		resultMap.put("success", "Material inserted successfully");
		resultMap.put("supplierId", supplierId.toString());
		resultMap.put("supplierName", supplierName);
		
		return resultMap;
	}
	
	/**
	 * This method fetches Supplier.
	 * 
	 * @param id - Supplier ID
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object getSupplier(String id,
							  Principal principal)
	{
		String name = null;
		Map<String, String> resultMap = new HashMap<String, String>();
		StringBuffer reqFields = new StringBuffer();
		StringBuffer fieldConstrints = new StringBuffer();
		boolean error = false;
		
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
		
		BigDecimal supplierId = null;
		try
		{
			supplierId = new BigDecimal(id);			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not parse the provided supplierId.");
			return resultMap;
		}
		
		// Check if supplierId already exists or not
		try
		{
			Supplier supplier = purchaseOrderDao.getSupplierDetailsById(supplierId);
			
			if(supplier!=null)
			{
				resultMap.put("supplierId", supplier.getSupplierId() .toString());
				resultMap.put("supplierName", supplier.getSupplierName());
				
				return resultMap;
			}
			else
			{
				resultMap.put("error", "Could not find supplier");
				return resultMap;
			}
			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Error while fetching supplier details");
		}
		
		return resultMap;
	}
	
	/**
	 * This method returns all Suppliers.
	 * 
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object getAllSuppliers(Principal principal)
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
		
		// Fetch all suppliers stored in database
		try
		{
			List<Supplier> suppliers = purchaseOrderDao.getAllSuppliers();

			return suppliers;
		}
		catch(Exception e)
		{	
			resultMap.put("error", "Error while fetching supplier details");
		}
		
		return resultMap;
	}
	
	/**
	 * This method updates Supplier.
	 * 
	 * @param jsonObject - JSON request body
	 * @param id - Supplier ID
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object updateSupplier(String jsonObject,
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
		Map<String, String> supplierProperties = new HashMap<String, String>();
		try 
		{
			supplierProperties = new ObjectMapper().readValue(jsonObject, new TypeReference<HashMap<String, String>>(){});
		}
		catch (Exception e) 
		{
			resultMap.put("error", "Bad Request");
			resultMap.put("description", "Unable to parse Request Body.");

			return resultMap;
		}
		
		BigDecimal supplierId = null;
		try
		{
			supplierId = new BigDecimal(id);			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not parse the supplierId provided.");
			
			return resultMap;
		}
		
		// check if supplier Id is available for update or not
		Supplier supplier = null;
		try
		{
			supplier = purchaseOrderDao.getSupplierDetailsById(supplierId);

			if(supplier==null)
				throw new Exception();
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not find supplier by id:"+supplierId);

			return resultMap;
		}
		
		String supplierName = null;
		
		if(supplierProperties.get("supplierName")!=null)
		{
			supplierName = supplierProperties.get("supplierName");
			
			if(supplierName.length() > 50)
			{
				if(fieldConstrints.toString().isEmpty())
					fieldConstrints.append("supplierName should not exceed 50 characters");
				else
					fieldConstrints.append(", supplierName should not exceed 50 characters");
				
				error = true;
			}
			
			supplier.setSupplierName(supplierName);
		}
		
		if(error)
		{
			resultMap.put("Fields Constraints", fieldConstrints.toString());
			
			return resultMap;
		}
		
		// update supplier
		try
		{
			purchaseOrderDao.updatesupplier(supplier);
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not update supplier");

			return resultMap;
		}

		resultMap.put("success", "Supplier updated successfully");
		return resultMap;
	}
	
	/**
	 * This method deletes Supplier.
	 * 
	 * @param id - Supplier ID
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object deleteSupplier(String id,
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
		
		BigDecimal supplierId = null;
		try
		{
			supplierId = new BigDecimal(id);			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not parse the provided supplierId.");
			
			return resultMap;
		}

		try
		{
			boolean result = purchaseOrderDao.deleteSupplierById(supplierId);

			if(!result)
				throw new Exception();

			resultMap.put("success", "Supplier deleted having id:"+id);
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
			resultMap.put("error", "Could not delete supplier");
			return resultMap;
		}

		return resultMap;
	}
	
}
