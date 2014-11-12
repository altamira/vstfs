package com.purchaseorder.service.steelpurchase;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.identity.User;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.jdbc.ReturningWork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.transaction.annotation.Transactional;

import com.purchaseorder.dao.PurchaseOrderDao;
import com.purchaseorder.entity.PurchasePlanning;
import com.purchaseorder.entity.PurchasePlanningItem;
import com.purchaseorder.entity.RequestItem;

/**
 * 
 * @author PARTH
 *
 */
public class PurchasePlanningService 
{
	@Autowired
	PurchaseOrderDao purchaseOrderDao;

	@Autowired
	RuntimeService runtimeService;
	
	@Autowired
	IdentityService identityService;
	
	@Autowired
	@PersistenceContext(unitName="pum")
	private EntityManager entityManager;

	public EntityManager getEntityManager() 
	{
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) 
	{
		this.entityManager = entityManager;
	}

	public IdentityService getIdentityService() 
	{
		return identityService;
	}

	public void setIdentityService(IdentityService identityService) 
	{
		this.identityService = identityService;
	}

	public RuntimeService getRuntimeService() 
	{
		return runtimeService;
	}

	public void setRuntimeService(RuntimeService runtimeService) 
	{
		this.runtimeService = runtimeService;
	}

	public PurchaseOrderDao getPurchaseOrderDao() 
	{
		return purchaseOrderDao;
	}

	public void setPurchaseOrderDao(PurchaseOrderDao purchaseOrderDao) 
	{
		this.purchaseOrderDao = purchaseOrderDao;
	}
	
	/**
	 * This method adds Purchase Plan
	 * 
	 * @param jsonObject - JSON request body
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object addPurchasePlanning(String jsonObject,
							   		  Principal principal)
	{
		String name = null;
		Map<String, String> resultMap = new HashMap<String, String>();
		boolean error = false;
		StringBuffer reqFields = new StringBuffer();
		StringBuffer fieldConstrints = new StringBuffer();
		String processId = null;
		String processInitiator = null;

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
		Map<String, String> purchasePlanningProperties = new HashMap<String, String>();
		try 
		{
			purchasePlanningProperties = new ObjectMapper().readValue(jsonObject, new TypeReference<HashMap<String, String>>(){});
		}
		catch (Exception e) 
		{
			resultMap.put("error", "Bad Request");
			resultMap.put("description", "Unable to parse Request Body.");

			return resultMap;
		}
		
		// Check for the mandatory fields
		if(!purchasePlanningProperties.containsKey("processId"))
		{
			if(reqFields.toString().isEmpty())
				reqFields.append("processId");
			else
				reqFields.append(", processId");

			error = true;
		}
		
		if(!purchasePlanningProperties.containsKey("quotationId"))
		{
			if(reqFields.toString().isEmpty())
				reqFields.append("quotationId");
			else
				reqFields.append(", quotationId");
				
			error = true;
		}
		
		if(error)
		{
			resultMap.put("Required Fields", reqFields.toString());
			
			return resultMap;
		}
		
		// check for the process initiator of the process id in Activiti
		try
		{
			processId = purchasePlanningProperties.get("processId");
			processInitiator = (String) runtimeService.getVariable(processId, "initiator");			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not find process initiator for the process Id");
			return resultMap;
		}

		if(!processInitiator.equalsIgnoreCase(name))
		{
			resultMap.put("error", "Only Process Instance Initiator can add Purchase Plan.");
			return resultMap;
		}
		
		BigDecimal quotationId = null;
		Date planningDate = new Date();
		String planningOwner = null;
		Date planningApproveDate = null;
		String planningApproveUser = null;
		
		// check field constraints
		try
		{
			quotationId = new BigDecimal(purchasePlanningProperties.get("quotationId"));
			
			// check if the given quotationId is stored in process variables or not ?
			List<String> quotationIdList = (List<String>) runtimeService.getVariable(processId, "quotationId");

			if(!quotationIdList.contains(quotationId.toString()))
			{
				resultMap.put("error", "Could not found the quotation id in process instance.");
				return resultMap;
			}
		}
		catch(Exception e)
		{
			if(fieldConstrints.toString().isEmpty())
				fieldConstrints.append("Could not parse quotationId");
			else
				fieldConstrints.append(", Could not parse quotationId");
			
			error = true;
		}
		
		if(purchasePlanningProperties.get("planningApproveDate")!=null)
		{
			try
			{
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				planningApproveDate = sdf.parse(purchasePlanningProperties.get("planningApproveDate"));
				
			}
			catch(Exception e)
			{
				if(fieldConstrints.toString().isEmpty())
					fieldConstrints.append("Could not parse planningApproveDate");
				else
					fieldConstrints.append(", Could not parse planningApproveDate");
				
				error = true;
			}
		}
		
		if(purchasePlanningProperties.get("planningApproveUser")!=null)
		{
			planningApproveUser = purchasePlanningProperties.get("planningApproveUser");
			
			
			if(planningApproveUser.length() > 50)
			{
				if(fieldConstrints.toString().isEmpty())
					fieldConstrints.append("planningApproveOwner should not exceed 50 characters");
				else
					fieldConstrints.append(", planningApproveOwner should not exceed 50 characters");
				
				error = true;
			}
			
			List<User> users = identityService.createUserQuery().list();
			boolean userFound = false;
			for(User user : users)
			{
				if(user.getId().equalsIgnoreCase(planningApproveUser))
				{
					userFound = true;
					break;
				}
			}
			
			if(!userFound)
			{
				if(fieldConstrints.toString().isEmpty())
					fieldConstrints.append("planningApproveUser is not valid user");
				else
					fieldConstrints.append(", planningApproveUser is not valid user");
				
				error = true;
			}
		}
		
		if(error)
		{
			resultMap.put("Fields Constraints", fieldConstrints.toString());
			return resultMap;
		}
		
		// store purchase planning id in activiti process instance variables.
		List<String> planningIdList = (List<String>) runtimeService.getVariable(processId, "planningId");
		
		if(planningIdList!=null && planningIdList.size()>=1)
		{
			resultMap.put("error", "Only 1 Purchase Plan can be inserted per process.");
			return resultMap;
		}
		
		PurchasePlanning purchasePlanning = new PurchasePlanning();
		purchasePlanning.setQuotationId(quotationId);
		purchasePlanning.setPlanningDate(planningDate);
		purchasePlanning.setPlanningOwner(processInitiator);
		purchasePlanning.setPlanningApproveDate(planningApproveDate);
		purchasePlanning.setPlanningApproveUser(planningApproveUser);
		
		BigDecimal purchasePlanningId = null;
		try
		{
			purchasePlanningId = purchaseOrderDao.insertPurchasePlan(purchasePlanning);
		}
		catch (Exception e) 
		{
			resultMap.put("error", "Could not insert purchase plan");
			
			return resultMap;
		}
		
		if(planningIdList != null)
		{
			planningIdList.add(purchasePlanningId.toString());
			runtimeService.setVariable(processId, "planningId", planningIdList);
		}
		else
		{
			planningIdList = new ArrayList<String>();
			planningIdList.add(purchasePlanningId.toString());
			runtimeService.setVariable(processId, "planningId", planningIdList);
		}
		
		resultMap.put("success", "Purchase plan inserted successfully");
		resultMap.put("processId", processId);
		resultMap.put("planningId", purchasePlanningId.toString());
		
		return resultMap;
	}
	
	/**
	 * This method fetches Purchase Plan
	 * 
	 * @param id - Purchase Plan ID
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object getPurchasePlanning(String id,
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

		BigDecimal planningId = null;
		try
		{
			planningId = new BigDecimal(id);			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not parse the id provided.");

			return resultMap;
		}

		// get purchase planning details from database 
		try
		{
			PurchasePlanning planning = purchaseOrderDao.getPurchasePlanDetailsById(planningId);

			if(planning!=null)
			{
				resultMap.put("planningId", planning.getPlanningId().toString());
				resultMap.put("quotationId", planning.getQuotationId().toString());
				resultMap.put("planningDate", planning.getPlanningDate().toString());
				resultMap.put("planningOwner", planning.getPlanningOwner());
				
				if(planning.getPlanningApproveDate()!=null)
				{
					resultMap.put("planningApproveDate", planning.getPlanningApproveDate().toString());					
				}
				
				if(planning.getPlanningApproveUser()!=null)
				{
					resultMap.put("planningApproveUser", planning.getPlanningApproveUser());					
				}

				return resultMap;
			}
			else
			{
				resultMap.put("error", "Could not find Purchase Plan");

				return resultMap;
			}
		}
		catch(Exception e)
		{	
			resultMap.put("error", "Error while fetching Purchase Plan details");
		}

		return resultMap;
	}
	
	/**
	 * This method returns all Purchase Plans
	 * 
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object getAllPurchasePlannings(Principal principal)
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
		
		// Fetch all purchase planning stored in database
		try
		{
			List<PurchasePlanning> plannings = purchaseOrderDao.getAllPurchasePlans();

			return plannings;
		}
		catch(Exception e)
		{	
			resultMap.put("error", "Error while fetching Purchase Plan details");
		}
		
		return resultMap;
	}
	
	/**
	 * This method deletes Purchase Plan
	 * 
	 * @param processId - Process ID
	 * @param id - Purchase Plan ID
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object deletePurchasePlanning(String processId,
								  		 String id,
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
		
		// check for the process initiator of the process id in Activiti
		String processInitiator = null;
		try
		{
			processInitiator = (String) runtimeService.getVariable(processId, "initiator");			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not find process initiator for the process Id");

			return resultMap;
		}

		if(!processInitiator.equalsIgnoreCase(name))
		{
			resultMap.put("error", "Only Process Instance Initiator can delete Purchase Plan.");
		}
		
		BigDecimal planningId = null;
		try
		{
			planningId = new BigDecimal(id);			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not parse the id provided.");

			return resultMap;
		}
		
		// check if the given planningId is stored in process variables or not ?
		List<String> planningIdList = (List<String>) runtimeService.getVariable(processId, "planningId");

		if(!planningIdList.contains(planningId.toString()))
		{
			resultMap.put("error", "Could not found the planning id in process instance.");

			return resultMap;
		}
		
		// delete Purchase Plan
		try
		{
			boolean result = purchaseOrderDao.deletePurchasePlanById(planningId);

			if(!result)
				throw new Exception();

			planningIdList.remove(planningId.toString());
			runtimeService.setVariable(processId, "planningId", planningIdList);
			resultMap.put("success", "Purchase Plan deleted having id:"+planningId);
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
			resultMap.put("error", "Could not delete quotation");
			return resultMap;
		}
		
		return resultMap;
	}
	
	/**
	 * This method updates Purchase Plan
	 * 
	 * @param jsonObject - JSON request body
	 * @param processId - Process ID
	 * @param id - Purchase Plan ID
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object updatePurchasePlanning(String jsonObject,
									  	 String processId,
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
		Map<String, String> purchasePlanningProperties = new HashMap<String, String>();
		try 
		{
			purchasePlanningProperties = new ObjectMapper().readValue(jsonObject, new TypeReference<HashMap<String, String>>(){});
		}
		catch (Exception e) 
		{
			resultMap.put("error", "Bad Request");
			resultMap.put("description", "Unable to parse Request Body.");

			return resultMap;
		}

		BigDecimal planningId = null;
		try
		{
			planningId = new BigDecimal(id);			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not parse the provided Purchase Plan id.");

			return resultMap;
		}

		// check for the process initiator of the process id in Activiti
		String processInitiator = null;
		try
		{
			processInitiator = (String) runtimeService.getVariable(processId, "initiator");			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not find process initiator for the process Id");

			return resultMap;
		}

		if(!processInitiator.equalsIgnoreCase(name))
		{
			resultMap.put("error", "Only Process Instance Initiator can update Purchase Plan.");
		}
		
		// check if purchase planning Id is available for update or not
		PurchasePlanning planning = null;
		try
		{
			planning = purchaseOrderDao.getPurchasePlanDetailsById(planningId);

			if(planning==null)
				throw new Exception();
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not find Purchase Plan by id:"+planningId);

			return resultMap;
		}
		
		// check if the given planningId is stored in process variables or not ?
		List<String> planningIdList = (List<String>) runtimeService.getVariable(processId, "planningId");

		if(!planningIdList.contains(planningId.toString()))
		{
			resultMap.put("error", "Could not found the planning id in process instance.");

			return resultMap;
		}
		
		//update Purchase Plan
		BigDecimal quotationId = null;
		Date planningApproveDate = null;
		String planningApproveUser = null;
		
		if(purchasePlanningProperties.get("quotationId")!=null)
		{
			try
			{
				quotationId = new BigDecimal(purchasePlanningProperties.get("quotationId"));
				
				// check if the given quotationId is stored in process variables or not ?
				List<String> quotationIdList = (List<String>) runtimeService.getVariable(processId, "quotationId");

				if(!quotationIdList.contains(quotationId.toString()))
				{
					resultMap.put("error", "Could not found the quotation id in process instance.");
					return resultMap;
				}
				
				planning.setQuotationId(quotationId);
			}
			catch(Exception e)
			{
				if(fieldConstrints.toString().isEmpty())
					fieldConstrints.append("Could not parse quotationId");
				else
					fieldConstrints.append(", Could not parse quotationId");
				
				error = true;
			}
		}
		
		if(purchasePlanningProperties.get("planningApproveDate")!=null)
		{
			try
			{
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				planningApproveDate = sdf.parse(purchasePlanningProperties.get("planningApproveDate"));
				
				planning.setPlanningApproveDate(planningApproveDate);
			}
			catch(Exception e)
			{
				if(fieldConstrints.toString().isEmpty())
					fieldConstrints.append("Could not parse planningApproveDate");
				else
					fieldConstrints.append(", Could not parse planningApproveDate");
				
				error = true;
			}
		}
		
		if(purchasePlanningProperties.get("planningApproveUser")!=null)
		{
			planningApproveUser = purchasePlanningProperties.get("planningApproveUser");
			
			
			if(planningApproveUser.length() > 50)
			{
				if(fieldConstrints.toString().isEmpty())
					fieldConstrints.append("planningApproveOwner should not exceed 50 characters");
				else
					fieldConstrints.append(", planningApproveOwner should not exceed 50 characters");
				
				error = true;
			}
			
			List<User> users = identityService.createUserQuery().list();
			boolean userFound = false;
			for(User user : users)
			{
				if(user.getId().equalsIgnoreCase(planningApproveUser))
				{
					userFound = true;
					break;
				}
			}
			
			if(!userFound)
			{
				if(fieldConstrints.toString().isEmpty())
					fieldConstrints.append("planningApproveUser is not valid user");
				else
					fieldConstrints.append(", planningApproveUser is not valid user");
				
				error = true;
			}
			
			planning.setPlanningApproveUser(planningApproveUser);
		}
		
		if(error)
		{
			resultMap.put("Fields Constraints", fieldConstrints.toString());

			return resultMap;
		}
		
		try
		{
			purchaseOrderDao.updatePurchasePlanning(planning);			
			resultMap.put("success", "Purchase Plan updated successfully");
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not update Purchase Plan.");
			return resultMap;
		}
		
		return resultMap;
	}
	
	/**
	 * This method adds Purchase Plan Item
	 * 
	 * @param jsonObject - JSON request body
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object addPurchasePlanningItem(String jsonObject,
									  	  Principal principal)
	{
		String name = null;
		Map<String, String> resultMap = new HashMap<String, String>();
		boolean error = false;
		StringBuffer reqFields = new StringBuffer();
		StringBuffer fieldConstrints = new StringBuffer();
		String processId = null;
		String processInitiator = null;

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
		Map<String, String> purchasePlanningItemProperties = new HashMap<String, String>();
		try 
		{
			purchasePlanningItemProperties = new ObjectMapper().readValue(jsonObject, new TypeReference<HashMap<String, String>>(){});
		}
		catch (Exception e) 
		{
			resultMap.put("error", "Bad Request");
			resultMap.put("description", "Unable to parse Request Body.");

			return resultMap;
		}
		
		// Check for the mandatory fields
		
		if(!purchasePlanningItemProperties.containsKey("processId"))
		{
			if(reqFields.toString().isEmpty())
				reqFields.append("processId");
			else
				reqFields.append(", processId");
				
			error = true;
		}
		
		if(!purchasePlanningItemProperties.containsKey("planningId"))
		{
			if(reqFields.toString().isEmpty())
				reqFields.append("planningId");
			else
				reqFields.append(", planningId");

			error = true;
		}
		
		if(!purchasePlanningItemProperties.containsKey("requestItemId"))
		{
			if(reqFields.toString().isEmpty())
				reqFields.append("requestItemId");
			else
				reqFields.append(", requestItemId");

			error = true;
		}
		
		if(!purchasePlanningItemProperties.containsKey("supplierId"))
		{
			if(reqFields.toString().isEmpty())
				reqFields.append("supplierId");
			else
				reqFields.append(", supplierId");

			error = true;
		}
		
		if(!purchasePlanningItemProperties.containsKey("supplierWeight"))
		{
			if(reqFields.toString().isEmpty())
				reqFields.append("supplierWeight");
			else
				reqFields.append(", supplierWeight");

			error = true;
		}
		
		if(error)
		{
			resultMap.put("Required Fields", reqFields.toString());
			
			return resultMap;
		}
		
		// check for the process initiator of the process id in Activiti
		try
		{
			processId = purchasePlanningItemProperties.get("processId");
			processInitiator = (String) runtimeService.getVariable(processId, "initiator");			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not find process initiator for the process Id");

			return resultMap;
		}

		if(!processInitiator.equalsIgnoreCase(name))
		{
			resultMap.put("error", "Only Process Instance Initiator can add Purchase Plan Item.");
		}
		
		BigDecimal planningId = null;
		BigDecimal requestItemId = null;
		BigDecimal supplierId = null;
		BigDecimal supplierWeight = null;
		
		// check field constraints
		try
		{
			planningId = new BigDecimal(purchasePlanningItemProperties.get("planningId"));
			
			// check if the given planningId is stored in process variables or not ?
			List<String> planningIdList = (List<String>) runtimeService.getVariable(processId, "planningId");

			if(!planningIdList.contains(planningId.toString()))
			{
				if(fieldConstrints.toString().isEmpty())
					fieldConstrints.append("Could not found the planning id in process instance.");
				else
					fieldConstrints.append(", Could not found the planning id in process instance.");
				
				error = true;
			}
		}
		catch(Exception e)
		{
			if(fieldConstrints.toString().isEmpty())
				fieldConstrints.append("Could not parse planningId");
			else
				fieldConstrints.append(", Could not parse planningId");
			
			error = true;
		}
		
		try
		{
			requestItemId = new BigDecimal(purchasePlanningItemProperties.get("requestItemId"));
			
			// check if given requestItemId exists or not.
			RequestItem requestItem=purchaseOrderDao.getRequestItemDetailsById(requestItemId);
			
			if(requestItem==null) 
			{
				
				if(fieldConstrints.toString().isEmpty())
					fieldConstrints.append("Given request item Id does not exist.");
				else
					fieldConstrints.append(", Given request item Id does not exist.");
				
				error = true;
			}
			else
			{
				//check if given requestItemId belongs to same process 
				List<String> requestIdList = (List<String>) runtimeService.getVariable(processId, "requestId");
				BigDecimal requestId = requestItem.getRequestId();
				
				if(!requestIdList.contains(requestId.toString()))
				{
					if(fieldConstrints.toString().isEmpty())
						fieldConstrints.append("Given requestItemId does not belongs to same process.");
					else
						fieldConstrints.append(", Given requestItemId does not belongs to same process");
					
					error = true;					
				}				
			}
		}
		catch(Exception e)
		{
			if(fieldConstrints.toString().isEmpty())
				fieldConstrints.append("Could not parse requestItemId");
			else
				fieldConstrints.append(", Could not parse requestItemId");

			error = true;
		}
		
		try
		{
			supplierId = new BigDecimal(purchasePlanningItemProperties.get("supplierId"));
			
			// check if given supplierId exists or not.
			boolean supplierAlreadyExists = purchaseOrderDao.isSupplierIdAlreadyStored(supplierId);
			if(!supplierAlreadyExists)
			{
				if(fieldConstrints.toString().isEmpty())
					fieldConstrints.append("Given supplier Id does not exist.");
				else
					fieldConstrints.append(", Given supplier Id does not exist.");
				
				error = true;
			}			
		}
		catch(Exception e)
		{
			if(fieldConstrints.toString().isEmpty())
				fieldConstrints.append("Could not parse supplierId");
			else
				fieldConstrints.append(", Could not parse supplierId");

			error = true;
		}
		
		try
		{
			supplierWeight = new BigDecimal(purchasePlanningItemProperties.get("supplierWeight")).setScale(2, RoundingMode.CEILING);		
		}
		catch(Exception e)
		{
			if(fieldConstrints.toString().isEmpty())
				fieldConstrints.append("Could not parse supplierWeight");
			else
				fieldConstrints.append(", Could not parse supplierWeight");

			error = true;
		}
		
		if(error)
		{
			resultMap.put("Fields Constraints", fieldConstrints.toString());
			
			return resultMap;
		}
		
		PurchasePlanningItem planningItem = new PurchasePlanningItem();
		planningItem.setPlanningId(planningId);
		planningItem.setRequestItemId(requestItemId);
		planningItem.setSupplierId(supplierId);
		planningItem.setSupplierWeight(supplierWeight);
		
		BigDecimal planningItemId = null;
		try
		{
			planningItemId = purchaseOrderDao.insertPurchasePlanItem(planningItem);
		}
		catch (Exception e) 
		{
			resultMap.put("error", "Could not insert Purchase Plan Item.");
			
			return resultMap;
		}
		
		resultMap.put("success", "Purchase Plan Item inserted successfully");
		resultMap.put("planningItemId", planningItemId.toString());	
		
		return resultMap;
	}
	
	/**
	 * This method fetches Purchase Plan Item.
	 * 
	 * @param id - Purchase Plan Item ID
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object getPurchasePlanningItem(String id,
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

		BigDecimal planningItemId = null;
		try
		{
			planningItemId = new BigDecimal(id);			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not parse the id provided.");

			return resultMap;
		}

		// get purchase planning item details from database 
		try
		{
			PurchasePlanningItem planningItem = purchaseOrderDao.getPurchasePlanItemDetailsById(planningItemId);

			if(planningItem!=null)
			{
				resultMap.put("planningItemId", planningItem.getPlanningItemId().toString());
				resultMap.put("planningId", planningItem.getPlanningId().toString());
				resultMap.put("requestItemId", planningItem.getRequestItemId().toString());
				resultMap.put("supplierId", planningItem.getSupplierId().toString());
				resultMap.put("supplierWeight", planningItem.getSupplierWeight().toString());

				return resultMap;
			}
			else
			{
				resultMap.put("error", "Could not find Purchase Plan Item");

				return resultMap;
			}
		}
		catch(Exception e)
		{	
			resultMap.put("error", "Error while fetching Purchase Plan Item details");
		}

		return resultMap;
	}
	
	/**
	 * This mehtod returns all Purchase Plan Items.
	 * 
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object getAllPurchasePlanningItems(Principal principal)
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
		
		// Fetch all purchase planning items stored in database
		try
		{
			List<PurchasePlanningItem> planningItems = purchaseOrderDao.getAllPurchasePlanItems();

			return planningItems;
		}
		catch(Exception e)
		{	
			resultMap.put("error", "Error while fetching Purchase Plan Item details");
		}
		
		return resultMap;
	}
	
	/**
	 * This method deletes Purchase Plan Item.
	 * 
	 * @param processId - Process ID
	 * @param id - Purchase Plan Item ID
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object deletePurchasePlanningItem(String processId,
											 String id,
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
		
		BigDecimal planningItemId = null;
		try
		{
			planningItemId = new BigDecimal(id);			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not parse the provided planningItemId.");
			
			return resultMap;
		}

		// check for the process initiator of the process id in Activiti
		String processInitiator = null;
		try
		{
			processInitiator = (String) runtimeService.getVariable(processId, "initiator");			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not find process initiator for the process Id");

			return resultMap;
		}

		if(!processInitiator.equalsIgnoreCase(name))
		{
			resultMap.put("error", "Only Process Instance Initiator can delete Purchase Plan Item.");
		}
		
		// check if the given planningItemId belongs to any planningId which is stored in process instance variables ?
		List<String> planningIdList = (List<String>) runtimeService.getVariable(processId, "planningId");

		BigDecimal planningId = null;
		try
		{
			planningId = purchaseOrderDao.getPlanningIdOfPlanningItemId(planningItemId);			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not find the relative planning Id of the provided request Item.");
			return resultMap;
		}

		if(!planningIdList.contains(planningId.toString()))
		{
			resultMap.put("error", "Provided planning item id does not belong to planning id in activiti process.");
			return resultMap;
		}
		
		// delete Purchase plan item
		try
		{
			boolean result = purchaseOrderDao.deletePurchasePlanItemById(planningItemId);

			if(!result)
				throw new Exception();

			resultMap.put("success", "Purchase Plan Item deleted having id:"+planningItemId);
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not delete Purchase Plan item.");
			return resultMap;
		}

		return resultMap;
	}
	
	/**
	 * This method updates Purchase Plan Item.
	 * 
	 * @param jsonObject - JSON request body
	 * @param processId - Process ID
	 * @param id - Purchase Plan Item ID
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object updatePurchasePlanningItem(String jsonObject,
									  		 String processId,
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
		Map<String, String> purchasePlanningItemProperties = new HashMap<String, String>();
		try 
		{
			purchasePlanningItemProperties = new ObjectMapper().readValue(jsonObject, new TypeReference<HashMap<String, String>>(){});
		}
		catch (Exception e) 
		{
			resultMap.put("error", "Bad Request");
			resultMap.put("description", "Unable to parse Request Body.");

			return resultMap;
		}

		BigDecimal planningItemId = null;
		try
		{
			planningItemId = new BigDecimal(id);			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not parse the provided planning item id.");

			return resultMap;
		}

		// check for the process initiator of the process id in Activiti
		String processInitiator = null;
		try
		{
			processInitiator = (String) runtimeService.getVariable(processId, "initiator");			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not find process initiator for the process Id");

			return resultMap;
		}

		if(!processInitiator.equalsIgnoreCase(name))
		{
			resultMap.put("error", "Only Process Instance Initiator can update Purchase Plan Item.");
		}
		
		// check if purchase plan item Id is available for update or not
		PurchasePlanningItem planningItem = null;
		try
		{
			planningItem = purchaseOrderDao.getPurchasePlanItemDetailsById(planningItemId);

			if(planningItem==null)
				throw new Exception();
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not find Purchase Plan Item by id:"+planningItemId);

			return resultMap;
		}
		
		// check if the given planningItemId belongs to planningId which is stored in process instance variables ?
		List<String> planningIdList = (List<String>) runtimeService.getVariable(processId, "planningId");

		BigDecimal planningId = purchaseOrderDao.getPlanningIdOfPlanningItemId(planningItemId);

		if(!planningIdList.contains(planningId.toString()))
		{
			resultMap.put("error", "Provided planningItemId does not belong to planningId in activiti process.");

			return resultMap;
		}
		
		BigDecimal requestItemId = null;
		BigDecimal supplierId = null;
		BigDecimal supplierWeight = null;
		
		if(purchasePlanningItemProperties.get("requestItemId")!=null)
		{
			try
			{
				requestItemId = new BigDecimal(purchasePlanningItemProperties.get("requestItemId"));

				// check if given requestItemId exists or not.
				RequestItem requestItem=purchaseOrderDao.getRequestItemDetailsById(requestItemId);

				if(requestItem==null) 
				{
					
					if(fieldConstrints.toString().isEmpty())
						fieldConstrints.append("Given request item Id does not exist.");
					else
						fieldConstrints.append(", Given request item Id does not exist.");
					
					error = true;
				}
				else
				{
					//check if given requestItemId belongs to same process 
					List<String> requestIdList = (List<String>) runtimeService.getVariable(processId, "requestId");
					BigDecimal requestId = requestItem.getRequestId();
					
					if(!requestIdList.contains(requestId.toString()))
					{
						if(fieldConstrints.toString().isEmpty())
							fieldConstrints.append("Given requestItemId does not belongs to same process.");
						else
							fieldConstrints.append(", Given requestItemId does not belongs to same process");
						
						error = true;					
					}
					else
					{
						planningItem.setRequestItemId(requestItemId);
					}
				}
			}
			catch(Exception e)
			{
				if(fieldConstrints.toString().isEmpty())
					fieldConstrints.append("Could not parse requestItem");
				else
					fieldConstrints.append(", Could not parse requestItem");

				error = true;
			}
		}
		
		if(purchasePlanningItemProperties.get("supplierId")!=null)
		{
			try
			{
				supplierId = new BigDecimal(purchasePlanningItemProperties.get("supplierId"));
				
				// check if given supplierId exists or not.
				boolean supplierAvailable = purchaseOrderDao.isSupplierIdAlreadyStored(supplierId);
				if(!supplierAvailable)
				{
					resultMap.put("error", "Given supplier Id does not exist.");
					return resultMap;
				}
				
				planningItem.setSupplierId(supplierId);
			}
			catch(Exception e)
			{
				if(fieldConstrints.toString().isEmpty())
					fieldConstrints.append("Could not parse supplierId");
				else
					fieldConstrints.append(", Could not parse supplierId");

				error = true;
			}
		}
		
		
		if(purchasePlanningItemProperties.get("supplierWeight")!=null)
		{
			try
			{
				supplierWeight = new BigDecimal(purchasePlanningItemProperties.get("supplierWeight")).setScale(2, RoundingMode.CEILING);
				
				planningItem.setSupplierWeight(supplierWeight);
			}
			catch(Exception e)
			{
				if(fieldConstrints.toString().isEmpty())
					fieldConstrints.append("Could not parse supplierWeight");
				else
					fieldConstrints.append(", Could not parse supplierWeight");

				error = true;
			}
		}
		
		if(error)
		{
			resultMap.put("Fields Constraints", fieldConstrints.toString());
			
			return resultMap;
		}
		
		// update purchase plan item
		try
		{
			purchaseOrderDao.updatePurchasePlanItem(planningItem);			
			resultMap.put("success", "Purchase Plan Item updated successfully");
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not update Purchase Plan Item");
			return resultMap;
		}
		
		return resultMap;
	}
	
	
	/**
	 * This method validates Planning ID
	 * 
	 * @param id - Purchase Plan ID
	 * @return - boolean value
	 */
	public boolean isPlanningIdValid(String id)
	{
		BigDecimal planningId = null;
		try
		{
			planningId = new BigDecimal(id);
		}
		catch(Exception e)
		{
			return false;
		}	
		
		PurchasePlanning planning = purchaseOrderDao.getPurchasePlanDetailsById(planningId); 
		
		if(planning==null)
		{
			return false;
		}
		else
		{
			return true;
		}		
	}
	
	/**
	 * This method returns report output stream.
	 * 
	 * @param request - HTTPServletRequest object
	 * @param response - HTTPServletResponse object
	 * @param planningId - Purchase Plan ID
	 * @param userName - HTTP Basic Auth logged in User ID
	 */
	@Transactional
	public void getPlanningReportInPdf(HttpServletRequest request, 
									   HttpServletResponse response, 
									   BigDecimal planningId,
									   String userName)
	{
		// generate report
		JasperPrint jasperPrint = null;
		
		try
		{
			byte[] planningReportJasper = purchaseOrderDao.getPlanningReportJasperFile();
			byte[] planningReportAltamiraimage = purchaseOrderDao.getPlanningReportAltamiraImage();
			byte[] pdf = null;
						
			final ByteArrayInputStream reportStream = new ByteArrayInputStream(planningReportJasper);
			final Map<String, Object> parameters = new HashMap<String, Object>();
			
			parameters.put("PLANNING_ID", planningId);
			
			Locale locale = new Locale.Builder().setLanguage("pt").setRegion("BR").build();
			parameters.put("REPORT_LOCALE", locale);
			
			BufferedImage imfg=null;
			try 
			{
				InputStream in = new ByteArrayInputStream(planningReportAltamiraimage);
				imfg = ImageIO.read(in);
			} 
			catch (Exception e1) 
			{
				e1.printStackTrace();
			}
			
			parameters.put("altamira_logo", imfg);
			parameters.put("USERNAME", userName.toUpperCase());
			
			Session session = entityManager.unwrap(Session.class);
			
			jasperPrint = session.doReturningWork(new ReturningWork<JasperPrint>() 
			{
				@Override
				public JasperPrint execute(Connection connection) 
				{
					JasperPrint jasperPrint = null;
					
					try
					{
						jasperPrint = JasperFillManager.fillReport(reportStream, parameters, connection);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
					
					return jasperPrint;
				}
				
			});
			
			pdf = JasperExportManager.exportReportToPdf(jasperPrint);
			
			ByteArrayInputStream pdfStream = new ByteArrayInputStream(pdf);
			
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition","inline; filename=PurchasePlanningReport.pdf");
			
			OutputStream os = response.getOutputStream();
			IOUtils.copy(pdfStream, os);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(jasperPrint!=null)
				{
					// store generated report in database
					purchaseOrderDao.insertGeneratedPlanningReport(jasperPrint);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				System.out.println("Could not insert generated report in database.");
			}
		}
	}
}