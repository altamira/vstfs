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

import org.activiti.engine.RuntimeService;
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
import com.purchaseorder.entity.PurchaseOrder;
import com.purchaseorder.entity.PurchaseOrderItem;
import com.purchaseorder.entity.PurchasePlanning;
import com.purchaseorder.entity.PurchasePlanningItem;

/**
 * 
 * @author PARTH
 *
 */
public class PurchaseOrderService 
{
	@Autowired
	PurchaseOrderDao purchaseOrderDao;

	@Autowired
	RuntimeService runtimeService;
	
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
	 * This method adds Purchase Order
	 * 
	 * @param jsonObject - JSON request body
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object addPurchaseOrder(String jsonObject,
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
		Map<String, String> purchaseOrderProperties = new HashMap<String, String>();
		try 
		{
			purchaseOrderProperties = new ObjectMapper().readValue(jsonObject, new TypeReference<HashMap<String, String>>(){});
		}
		catch (Exception e) 
		{
			resultMap.put("error", "Bad Request");
			resultMap.put("description", "Unable to parse Request Body.");

			return resultMap;
		}
		
		// Check for the mandatory fields
		if(!purchaseOrderProperties.containsKey("processId"))
		{
			if(reqFields.toString().isEmpty())
				reqFields.append("processId");
			else
				reqFields.append(", processId");

			error = true;
		}
		
		if(!purchaseOrderProperties.containsKey("planningId"))
		{
			if(reqFields.toString().isEmpty())
				reqFields.append("planningId");
			else
				reqFields.append(", planningId");

			error = true;
		}
		
		if(!purchaseOrderProperties.containsKey("supplierId"))
		{
			if(reqFields.toString().isEmpty())
				reqFields.append("supplierId");
			else
				reqFields.append(", supplierId");

			error = true;
		}
		
		if(!purchaseOrderProperties.containsKey("purchaseOrderDate"))
		{
			if(reqFields.toString().isEmpty())
				reqFields.append("purchaseOrderDate");
			else
				reqFields.append(", purchaseOrderDate");

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
			processId = purchaseOrderProperties.get("processId");
			processInitiator = (String) runtimeService.getVariable(processId, "initiator");			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not find process initiator for the process Id");

			return resultMap;
		}

		if(!processInitiator.equalsIgnoreCase(name))
		{
			resultMap.put("error", "Only Process Instance Initiator can add Purchase Order.");
		}
		
		BigDecimal planningId = null;
		BigDecimal supplierId = null;
		Date purchaseOrderDate = null;
		String comments = null;
		
		// check field constraints
		try
		{
			planningId = new BigDecimal(purchaseOrderProperties.get("planningId"));
			
			// check if the given planningId is stored in process variables or not ?
			List<String> planningIdList = (List<String>) runtimeService.getVariable(processId, "planningId");

			if(!planningIdList.contains(planningId.toString()))
			{
				resultMap.put("error", "Could not found the planning id in process instance.");
				return resultMap;
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
			supplierId = new BigDecimal(purchaseOrderProperties.get("supplierId"));
			
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
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			purchaseOrderDate = sdf.parse(purchaseOrderProperties.get("purchaseOrderDate"));
			
		}
		catch(Exception e)
		{
			if(fieldConstrints.toString().isEmpty())
				fieldConstrints.append("Could not parse purchaseOrderDate");
			else
				fieldConstrints.append(", Could not parse purchaseOrderDate");
			
			error = true;
		}
		
		if(purchaseOrderProperties.get("comments")!=null)
		{
			comments = purchaseOrderProperties.get("comments");
			
			if(comments.length() > 500)
			{
				if(fieldConstrints.toString().isEmpty())
					fieldConstrints.append("comments should not exceed 500 characters");
				else
					fieldConstrints.append(", comments should not exceed 500 characters");
				
				error = true;
			}
		}
		
		if(error)
		{
			resultMap.put("Fields Constraints", fieldConstrints.toString());
			
			return resultMap;
		}
		
		PurchaseOrder purchaseOrder = new PurchaseOrder();
		purchaseOrder.setPurchasePlanningId(planningId);
		purchaseOrder.setSupplierId(supplierId);
		purchaseOrder.setPurchaseOrderDate(purchaseOrderDate);
		purchaseOrder.setComments(comments);
		
		BigDecimal purchaseOrderId = null;
		try
		{
			purchaseOrderId = purchaseOrderDao.insertPurchaseOrder(purchaseOrder);
		}
		catch (Exception e) 
		{
			resultMap.put("error", "Could not insert Purchase Order.");
			
			return resultMap;
		}
		
		// store purchase order id in activiti process instance variables.
		List<String> purchaseOrderIdList = (List<String>) runtimeService.getVariable(processId, "purchaseOrderId");

		if(purchaseOrderIdList != null)
		{
			purchaseOrderIdList.add(purchaseOrderId.toString());
			runtimeService.setVariable(processId, "purchaseOrderId", purchaseOrderIdList);
		}
		else
		{
			purchaseOrderIdList = new ArrayList<String>();
			purchaseOrderIdList.add(purchaseOrderId.toString());
			runtimeService.setVariable(processId, "purchaseOrderId", purchaseOrderIdList);
		}
		
		resultMap.put("success", "Purchase Order inserted successfully");
		resultMap.put("purchaseOrderId", purchaseOrderId.toString());	
		
		return resultMap;
	}
	
	/**
	 * This method fetches Purchase Order
	 * 
	 * @param id - Purchase Order ID
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object getPurchaseOrder(String id,
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

		BigDecimal purchaseOrderId = null;
		try
		{
			purchaseOrderId = new BigDecimal(id);			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not parse the id provided.");

			return resultMap;
		}

		// get purchase order details from database 
		try
		{
			PurchaseOrder purchaseOrder = purchaseOrderDao.getPurchaseOrderDetailsById(purchaseOrderId);

			if(purchaseOrder!=null)
			{
				resultMap.put("purchaseOrderId", purchaseOrder.getPurchaseOrderId().toString());
				resultMap.put("purchasePlanningId", purchaseOrder.getPurchasePlanningId().toString());
				resultMap.put("supplierId", purchaseOrder.getSupplierId().toString());
				resultMap.put("purchaseOrderDate", purchaseOrder.getPurchaseOrderDate().toString());

				return resultMap;
			}
			else
			{
				resultMap.put("error", "Could not find Purchase Order");

				return resultMap;
			}
		}
		catch(Exception e)
		{	
			resultMap.put("error", "Error while fetching Purchase Order details");
		}

		return resultMap;
	}
	
	/**
	 * This method returns all Purchase Orders
	 * 
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object getAllPurchaseOrders(Principal principal)
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
		
		// Fetch all purchase orders stored in database
		try
		{
			List<PurchaseOrder> purchaseOrders = purchaseOrderDao.getAllPurchaseOrders();

			return purchaseOrders;
		}
		catch(Exception e)
		{	
			resultMap.put("error", "Error while fetching Purchase Order details");
		}
		
		return resultMap;
	}
	
	/**
	 * This method deletes Purchase Order
	 * 
	 * @param processId - Process Id
	 * @param id - Purchase Order ID
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object deletePurchaseOrder(String processId,
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
			resultMap.put("error", "Only Process Instance Initiator can delete Purchase Order.");
		}

		BigDecimal purchaseOrderId = null;
		try
		{
			purchaseOrderId = new BigDecimal(id);			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not parse the id provided.");

			return resultMap;
		}

		// check if the given purchaseOrderId is stored in process variables or not ?
		List<String> purchaseOrderIdList = (List<String>) runtimeService.getVariable(processId, "purchaseOrderId");

		if(!purchaseOrderIdList.contains(purchaseOrderId.toString()))
		{
			resultMap.put("error", "Could not found the purchase order id in process instance.");

			return resultMap;
		}

		// delete Purchase Order
		try
		{
			boolean result = purchaseOrderDao.deletePurchaseOrderById(purchaseOrderId);

			if(!result)
				throw new Exception();

			purchaseOrderIdList.remove(purchaseOrderId.toString());
			runtimeService.setVariable(processId, "purchaseOrderId", purchaseOrderIdList);
			resultMap.put("success", "Purchase Order deleted having id:"+purchaseOrderId);
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
			resultMap.put("error", "Could not delete Purchase Order.");
			return resultMap;
		}

		return resultMap;
	}
	
	/**
	 * This method updates Purchase Order.
	 * 
	 * @param jsonObject - JSON request body
	 * @param processId - Process ID
	 * @param id - Purchase Order ID
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object updatePurchaseOrder(String jsonObject,
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
		Map<String, String> purchaseOrderProperties = new HashMap<String, String>();
		try 
		{
			purchaseOrderProperties = new ObjectMapper().readValue(jsonObject, new TypeReference<HashMap<String, String>>(){});
		}
		catch (Exception e) 
		{
			resultMap.put("error", "Bad Request");
			resultMap.put("description", "Unable to parse Request Body.");

			return resultMap;
		}

		BigDecimal purchaseOrderId = null;
		try
		{
			purchaseOrderId = new BigDecimal(id);			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not parse the provided Purchase Order id.");

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
			resultMap.put("error", "Only Process Instance Initiator can update Purchase Order.");
		}
		
		// check if purchase order Id is available for update or not
		PurchaseOrder purchaseOrder = null;
		try
		{
			purchaseOrder = purchaseOrderDao.getPurchaseOrderDetailsById(purchaseOrderId);

			if(purchaseOrder==null)
				throw new Exception();
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not find Purchase Order by id:"+purchaseOrderId);

			return resultMap;
		}
		
		// check if the given purchaseOrderId is stored in process variables or not ?
		List<String> purchaseOrderIdList = (List<String>) runtimeService.getVariable(processId, "purchaseOrderId");

		if(!purchaseOrderIdList.contains(purchaseOrderId.toString()))
		{
			resultMap.put("error", "Could not found the provided purchase order id in process instance.");

			return resultMap;
		}
		
		//update Purchase Order
		BigDecimal purchasePlanningId = null;
		BigDecimal supplierId = null;
		Date purchaseOrderDate = null;
		String comments = null;
		
		if(purchaseOrderProperties.get("planningId")!=null)
		{
			try
			{
				purchasePlanningId = new BigDecimal(purchaseOrderProperties.get("planningId"));
				
				// check if the given planningId is stored in process variables or not ?
				List<String> planningIdList = (List<String>) runtimeService.getVariable(processId, "planningId");

				if(!planningIdList.contains(purchasePlanningId.toString()))
				{
					resultMap.put("error", "Could not found the planning id in process instance.");
					return resultMap;
				}
				
				purchaseOrder.setPurchasePlanningId(purchasePlanningId);
			}
			catch(Exception e)
			{
				if(fieldConstrints.toString().isEmpty())
					fieldConstrints.append("Could not parse planningId");
				else
					fieldConstrints.append(", Could not parse planningId");
				
				error = true;
			}
		}
		
		if(purchaseOrderProperties.get("supplierId")!=null)
		{
			try
			{
				supplierId = new BigDecimal(purchaseOrderProperties.get("supplierId"));
				
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
				
				purchaseOrder.setSupplierId(supplierId);
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
		
		if(purchaseOrderProperties.get("purchaseOrderDate")!=null)
		{
			try
			{
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				purchaseOrderDate = sdf.parse(purchaseOrderProperties.get("purchaseOrderDate"));
				
				purchaseOrder.setPurchaseOrderDate(purchaseOrderDate);
			}
			catch(Exception e)
			{
				if(fieldConstrints.toString().isEmpty())
					fieldConstrints.append("Could not parse purchaseOrderDate");
				else
					fieldConstrints.append(", Could not parse purchaseOrderDate");
				
				error = true;
			}
		}
		
		if(purchaseOrderProperties.get("comments")!=null)
		{
			comments = purchaseOrderProperties.get("comments");
			
			if(comments.length() > 500)
			{
				if(fieldConstrints.toString().isEmpty())
					fieldConstrints.append("comments should not exceed 500 characters");
				else
					fieldConstrints.append(", comments should not exceed 500 characters");
				
				error = true;
			}
			
			purchaseOrder.setComments(comments);
		}
		
		if(error)
		{
			resultMap.put("Fields Constraints", fieldConstrints.toString());

			return resultMap;
		}
		
		try
		{
			purchaseOrderDao.updatePurchaseOrder(purchaseOrder);			
			resultMap.put("success", "Purchase Order updated successfully");
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not update Purchase Order.");
			return resultMap;
		}
		
		return resultMap;
	}
	
	/**
	 * This method adds Purchase Order Item
	 * 
	 * @param jsonObject - JSON request body
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object addPurchaseOrderItem(String jsonObject,
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
		Map<String, String> purchaseOrderItemProperties = new HashMap<String, String>();
		try 
		{
			purchaseOrderItemProperties = new ObjectMapper().readValue(jsonObject, new TypeReference<HashMap<String, String>>(){});
		}
		catch (Exception e) 
		{
			resultMap.put("error", "Bad Request");
			resultMap.put("description", "Unable to parse Request Body.");

			return resultMap;
		}
		
		// Check for the mandatory fields
		if(!purchaseOrderItemProperties.containsKey("processId"))
		{
			if(reqFields.toString().isEmpty())
				reqFields.append("processId");
			else
				reqFields.append(", processId");

			error = true;
		}
		
		if(!purchaseOrderItemProperties.containsKey("purchaseOrderId"))
		{
			if(reqFields.toString().isEmpty())
				reqFields.append("purchaseOrderId");
			else
				reqFields.append(", purchaseOrderId");

			error = true;
		}
		
		if(!purchaseOrderItemProperties.containsKey("planningItemId"))
		{
			if(reqFields.toString().isEmpty())
				reqFields.append("planningItemId");
			else
				reqFields.append(", planningItemId");

			error = true;
		}
		
		if(!purchaseOrderItemProperties.containsKey("purchaseOrderItemDate"))
		{
			if(reqFields.toString().isEmpty())
				reqFields.append("purchaseOrderItemDate");
			else
				reqFields.append(", purchaseOrderItemDate");

			error = true;
		}
		
		if(!purchaseOrderItemProperties.containsKey("purchaseOrderItemWeight"))
		{
			if(reqFields.toString().isEmpty())
				reqFields.append("purchaseOrderItemWeight");
			else
				reqFields.append(", purchaseOrderItemWeight");

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
			processId = purchaseOrderItemProperties.get("processId");
			processInitiator = (String) runtimeService.getVariable(processId, "initiator");			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not find process initiator for the process Id");

			return resultMap;
		}

		if(!processInitiator.equalsIgnoreCase(name))
		{
			resultMap.put("error", "Only Process Instance Initiator can add Purchase Order Item.");
		}
		
		BigDecimal purchaseOrderId = null;
		BigDecimal planningItemId = null;
		Date purchaseOrderItemDate = null;
		BigDecimal purchaseOrderItemWeight = null;
		BigDecimal purchaseOrderItemUnitPrice = null;		
		BigDecimal purchaseOrderItemTax = null;
		
		// check field constraints
		try
		{
			purchaseOrderId = new BigDecimal(purchaseOrderItemProperties.get("purchaseOrderId"));

			// check if the given purchaseOrderId is stored in process variables or not ?
			List<String> purchaseOrderIdList = (List<String>) runtimeService.getVariable(processId, "purchaseOrderId");

			if(!purchaseOrderIdList.contains(purchaseOrderId.toString()))
			{
				if(fieldConstrints.toString().isEmpty())
					fieldConstrints.append("Could not found the purchase order id in process instance.");
				else
					fieldConstrints.append(", Could not found the purchase order id in process instance.");

				error = true;
			}
		}
		catch(Exception e)
		{
			if(fieldConstrints.toString().isEmpty())
				fieldConstrints.append("Could not parse purchaseOrderId");
			else
				fieldConstrints.append(", Could not parse purchaseOrderId");

			error = true;
		}
		
		try
		{
			planningItemId = new BigDecimal(purchaseOrderItemProperties.get("planningItemId"));
			
			// check if given planningId exists or not.
			PurchasePlanningItem planningItem = purchaseOrderDao.getPurchasePlanItemDetailsById(planningItemId);
			
			if(planningItem==null)
			{
				if(fieldConstrints.toString().isEmpty())
					fieldConstrints.append("Given planningItemId does not exist.");
				else
					fieldConstrints.append(", Given planningItemId does not exist.");
				
				error = true;
			}
			else
			{
				//check if given planningItemId belongs to same process 
				List<String> planningIdList = (List<String>) runtimeService.getVariable(processId, "planningId");
				BigDecimal planningId = planningItem.getPlanningId();
				
				if(!planningIdList.contains(planningId.toString()))
				{
					if(fieldConstrints.toString().isEmpty())
						fieldConstrints.append("Given planningItemId does not belongs to same process.");
					else
						fieldConstrints.append(", Given planningItemId does not belongs to same process.");
					
					error = true;
				}
			}
		}
		catch(Exception e)
		{
			if(fieldConstrints.toString().isEmpty())
				fieldConstrints.append("Could not parse planningItemId");
			else
				fieldConstrints.append(", Could not parse planningItemId");

			error = true;
		}
		
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			purchaseOrderItemDate = sdf.parse(purchaseOrderItemProperties.get("purchaseOrderItemDate"));
			
		}
		catch(Exception e)
		{
			if(fieldConstrints.toString().isEmpty())
				fieldConstrints.append("Could not parse purchaseOrderItemDate");
			else
				fieldConstrints.append(", Could not parse purchaseOrderItemDate");
			
			error = true;
		}
		
		try
		{
			purchaseOrderItemWeight = new BigDecimal(purchaseOrderItemProperties.get("purchaseOrderItemWeight")).setScale(2, RoundingMode.CEILING);		
		}
		catch(Exception e)
		{
			if(fieldConstrints.toString().isEmpty())
				fieldConstrints.append("Could not parse purchaseOrderItemWeight");
			else
				fieldConstrints.append(", Could not parse purchaseOrderItemWeight");

			error = true;
		}
		
		if(purchaseOrderItemProperties.get("purchaseOrderItemUnitPrice")!=null)
		{
			try
			{
				purchaseOrderItemUnitPrice = new BigDecimal(purchaseOrderItemProperties.get("purchaseOrderItemUnitPrice")).setScale(2, RoundingMode.CEILING);		
			}
			catch(Exception e)
			{
				if(fieldConstrints.toString().isEmpty())
					fieldConstrints.append("Could not parse purchaseOrderItemUnitPrice");
				else
					fieldConstrints.append(", Could not parse purchaseOrderItemUnitPrice");

				error = true;
			}
		}
		
		if(purchaseOrderItemProperties.get("purchaseOrderItemTax")!=null)
		{
			try
			{
				purchaseOrderItemTax = new BigDecimal(purchaseOrderItemProperties.get("purchaseOrderItemTax")).setScale(2, RoundingMode.CEILING);		
			}
			catch(Exception e)
			{
				if(fieldConstrints.toString().isEmpty())
					fieldConstrints.append("Could not parse purchaseOrderItemTax");
				else
					fieldConstrints.append(", Could not parse purchaseOrderItemTax");

				error = true;
			}
		}
		
		if(error)
		{
			resultMap.put("Fields Constraints", fieldConstrints.toString());
			
			return resultMap;
		}
		
		PurchaseOrderItem purchaseOrderItem = new PurchaseOrderItem();
		purchaseOrderItem.setPurchaseOrderId(purchaseOrderId);
		purchaseOrderItem.setPlanningItemId(planningItemId);
		purchaseOrderItem.setPurchaseOrderItemDate(purchaseOrderItemDate);
		purchaseOrderItem.setPurchaseOrderItemWeight(purchaseOrderItemWeight);
		purchaseOrderItem.setPurchaseOrderItemUnitPrice(purchaseOrderItemUnitPrice);
		purchaseOrderItem.setPurchaseOrderItemTax(purchaseOrderItemTax);
		
		BigDecimal purchaseOrderItemId = null;
		try
		{
			purchaseOrderItemId = purchaseOrderDao.insertPurchaseOrderItem(purchaseOrderItem);
		}
		catch (Exception e) 
		{
			resultMap.put("error", "Could not insert Purchase Order Item.");
			
			return resultMap;
		}
		
		resultMap.put("success", "Purchase Order Item inserted successfully");
		resultMap.put("purchaseOrderItemId", purchaseOrderItemId.toString());
		
		return resultMap;
	}
	
	/**
	 * This method fetches Purchase Order Item
	 * 
	 * @param id - Purchase Order Item ID
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object getPurchaseOrderItem(String id,
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

		BigDecimal purchaseOrderItemId = null;
		try
		{
			purchaseOrderItemId = new BigDecimal(id);			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not parse the id provided.");

			return resultMap;
		}

		// get purchase order item details from database 
		try
		{
			PurchaseOrderItem purchaseOrderItem = purchaseOrderDao.getPurchaseOrderItemDetailsById(purchaseOrderItemId);

			if(purchaseOrderItem!=null)
			{
				resultMap.put("purchaseOrderItemId", purchaseOrderItem.getPurchaseOrderItemId().toString());
				resultMap.put("purchaseOrderId", purchaseOrderItem.getPurchaseOrderId().toString());
				resultMap.put("planningItemId", purchaseOrderItem.getPlanningItemId().toString());
				resultMap.put("purchaseOrderItemDate", purchaseOrderItem.getPurchaseOrderItemDate().toString());
				resultMap.put("purchaseOrderItemWeight", purchaseOrderItem.getPurchaseOrderItemWeight().toString());
				
				if(purchaseOrderItem.getPurchaseOrderItemUnitPrice()!=null)
				{
					resultMap.put("purchaseOrderItemUnitPrice", purchaseOrderItem.getPurchaseOrderItemUnitPrice().toString());					
				}
				
				if(purchaseOrderItem.getPurchaseOrderItemTax()!=null)
				{
					resultMap.put("purchaseOrderItemTax", purchaseOrderItem.getPurchaseOrderItemTax().toString());					
				}
				

				return resultMap;
			}
			else
			{
				resultMap.put("error", "Could not find Purchase Order Item");

				return resultMap;
			}
		}
		catch(Exception e)
		{	
			resultMap.put("error", "Error while fetching Purchase Order Item details");
		}

		return resultMap;
	}
	
	/**
	 * This method reutrns all Purchase Order Items
	 * 
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object getAllPurchaseOrderItems(Principal principal)
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
		
		// Fetch all purchase order items stored in database
		try
		{
			List<PurchaseOrderItem> purchaseOrderItems = purchaseOrderDao.getAllPurchaseOrderItems();

			return purchaseOrderItems;
		}
		catch(Exception e)
		{	
			resultMap.put("error", "Error while fetching Purchase Order details");
		}
		
		return resultMap;
	}
	
	/**
	 * This method deletes Purchase Order Item.
	 * 
	 * @param processId - Process ID
	 * @param id - Purchase Order Item ID
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object deletePurchaseOrderItem(String processId,
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
		
		BigDecimal purchaseOrderItemId = null;
		try
		{
			purchaseOrderItemId = new BigDecimal(id);			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not parse the provided purchaseOrderItemId.");
			
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
			resultMap.put("error", "Only Process Instance Initiator can delete Purchase Order Item.");
		}
		
		// check if the given purchaseOrderItemId belongs to any purchaseOrderId which is stored in process instance variables ?
		List<String> purchaseOrderIdList = (List<String>) runtimeService.getVariable(processId, "purchaseOrderId");

		BigDecimal purchaseOrderId = null;
		try
		{
			purchaseOrderId = purchaseOrderDao.getPurchaseOrderIdOfPurchaseOrderItemId(purchaseOrderItemId);			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not find the relative planning Id of the provided purchase order item.");
			return resultMap;
		}

		if(!purchaseOrderIdList.contains(purchaseOrderId.toString()))
		{
			resultMap.put("error", "Provided purchase order item id does not belong to purchase order id in activiti process.");
			return resultMap;
		}
		
		// delete Purchase order item
		try
		{
			boolean result = purchaseOrderDao.deletePurchaseOrderItemById(purchaseOrderItemId);

			if(!result)
				throw new Exception();

			resultMap.put("success", "Purchase Order Item deleted having id:"+purchaseOrderItemId);
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not delete Purchase Order Item.");
			return resultMap;
		}
		
		return resultMap;
	}
	
	/**
	 * This method updates Purchase Order Item.
	 * 
	 * @param jsonObject - JSON request body
	 * @param processId - Process ID
	 * @param id - Purchase Order Item ID
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object updatePurchaseOrderItem(String jsonObject,
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
		Map<String, String> purchaseOrderItemProperties = new HashMap<String, String>();
		try 
		{
			purchaseOrderItemProperties = new ObjectMapper().readValue(jsonObject, new TypeReference<HashMap<String, String>>(){});
		}
		catch (Exception e) 
		{
			resultMap.put("error", "Bad Request");
			resultMap.put("description", "Unable to parse Request Body.");

			return resultMap;
		}
		
		BigDecimal purchaseOrderItemId = null;
		try
		{
			purchaseOrderItemId = new BigDecimal(id);			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not parse the provided purchase order item id.");

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
			resultMap.put("error", "Only Process Instance Initiator can update Purchase Order Item.");
		}
		
		// check if purchase order item Id is available for update or not
		PurchaseOrderItem purchaseOrderItem = null;
		try
		{
			purchaseOrderItem = purchaseOrderDao.getPurchaseOrderItemDetailsById(purchaseOrderItemId);

			if(purchaseOrderItem==null)
				throw new Exception();
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not find Purchase Order Item by id:"+purchaseOrderItemId);

			return resultMap;
		}
		
		// check if the given purchaseOrderItemId belongs to purchaseOrderId which is stored in process instance variables ?
		List<String> purchaseOrderIdList = (List<String>) runtimeService.getVariable(processId, "purchaseOrderId");

		BigDecimal purchaseOrderId = purchaseOrderDao.getPurchaseOrderIdOfPurchaseOrderItemId(purchaseOrderItemId);

		if(!purchaseOrderIdList.contains(purchaseOrderId.toString()))
		{
			resultMap.put("error", "Provided purchaseOrderItemId does not belong to purchaseOrderId in activiti process.");

			return resultMap;
		}
		
		BigDecimal planningItemId = null;
		Date purchaseOrderItemDate = null;
		BigDecimal purchaseOrderItemWeight = null;
		BigDecimal purchaseOrderItemUnitPrice = null;
		BigDecimal purchaseOrderItemTax = null;
		
		if(purchaseOrderItemProperties.get("planningItemId")!=null)
		{
			try
			{
				planningItemId = new BigDecimal(purchaseOrderItemProperties.get("planningItemId"));
				
				// check if given planningId exists or not.
				PurchasePlanningItem planningItem = purchaseOrderDao.getPurchasePlanItemDetailsById(planningItemId);
				
				if(planningItem==null)
				{
					if(fieldConstrints.toString().isEmpty())
						fieldConstrints.append("Given planningItemId does not exist.");
					else
						fieldConstrints.append(", Given planningItemId does not exist.");
					
					error = true;
				}
				else
				{
					//check if given planningItemId belongs to same process 
					List<String> planningIdList = (List<String>) runtimeService.getVariable(processId, "planningId");
					BigDecimal planningId = planningItem.getPlanningId();
					
					if(!planningIdList.contains(planningId.toString()))
					{
						if(fieldConstrints.toString().isEmpty())
							fieldConstrints.append("Given planningItemId does not belongs to same process.");
						else
							fieldConstrints.append(", Given planningItemId does not belongs to same process.");
						
						error = true;
					}
					else
					{
						purchaseOrderItem.setPlanningItemId(planningItemId);						
					}
				}
			}
			catch(Exception e)
			{
				if(fieldConstrints.toString().isEmpty())
					fieldConstrints.append("Could not parse planningItemId");
				else
					fieldConstrints.append(", Could not parse planningItemId");

				error = true;
			}
		}
		
		if(purchaseOrderItemProperties.get("purchaseOrderItemDate")!=null)
		{
			try
			{
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				purchaseOrderItemDate = sdf.parse(purchaseOrderItemProperties.get("purchaseOrderItemDate"));
				purchaseOrderItem.setPurchaseOrderItemDate(purchaseOrderItemDate);
			}
			catch(Exception e)
			{
				if(fieldConstrints.toString().isEmpty())
					fieldConstrints.append("Could not parse purchaseOrderItemDate");
				else
					fieldConstrints.append(", Could not parse purchaseOrderItemDate");
				
				error = true;
			}
		}
		
		if(purchaseOrderItemProperties.get("purchaseOrderItemWeight")!=null)
		{
			try
			{
				purchaseOrderItemWeight = new BigDecimal(purchaseOrderItemProperties.get("purchaseOrderItemWeight")).setScale(2, RoundingMode.CEILING);
				
				purchaseOrderItem.setPurchaseOrderItemWeight(purchaseOrderItemWeight);
			}
			catch(Exception e)
			{
				if(fieldConstrints.toString().isEmpty())
					fieldConstrints.append("Could not parse purchaseOrderItemWeight");
				else
					fieldConstrints.append(", Could not parse purchaseOrderItemWeight");

				error = true;
			}
		}
		
		if(purchaseOrderItemProperties.get("purchaseOrderItemUnitPrice")!=null)
		{
			try
			{
				purchaseOrderItemUnitPrice = new BigDecimal(purchaseOrderItemProperties.get("purchaseOrderItemUnitPrice")).setScale(2, RoundingMode.CEILING);
				
				purchaseOrderItem.setPurchaseOrderItemUnitPrice(purchaseOrderItemUnitPrice);
			}
			catch(Exception e)
			{
				if(fieldConstrints.toString().isEmpty())
					fieldConstrints.append("Could not parse purchaseOrderItemUnitPrice");
				else
					fieldConstrints.append(", Could not parse purchaseOrderItemUnitPrice");

				error = true;
			}
		}
		
		if(purchaseOrderItemProperties.get("purchaseOrderItemTax")!=null)
		{
			try
			{
				purchaseOrderItemTax = new BigDecimal(purchaseOrderItemProperties.get("purchaseOrderItemTax")).setScale(2, RoundingMode.CEILING);
				
				purchaseOrderItem.setPurchaseOrderItemTax(purchaseOrderItemTax);
			}
			catch(Exception e)
			{
				if(fieldConstrints.toString().isEmpty())
					fieldConstrints.append("Could not parse purchaseOrderItemTax");
				else
					fieldConstrints.append(", Could not parse purchaseOrderItemTax");

				error = true;
			}
		}
		
		if(error)
		{
			resultMap.put("Fields Constraints", fieldConstrints.toString());
			
			return resultMap;
		}
		
		// update Purchase Order Item
		try
		{
			purchaseOrderDao.updatePurchaseOrderItem(purchaseOrderItem);			
			resultMap.put("success", "Purchase Order Item updated successfully");
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not update Purchase Plan Item");
			return resultMap;
		}
		
		return resultMap;
	}
	
	
	/**
	 * This method adds all Purchase Orders
	 * 
	 * @param id - Planning ID
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object addPurchaseOrders(String id,
			   					    Principal principal)
	{
		String name = null;
		Map<String, Object> resultMap = new HashMap<String, Object>();
	
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
			resultMap.put("error", "Could not parse the provided purchase order item id.");

			return resultMap;
		}
		
		// check if planningId exists or not
		PurchasePlanning planning = purchaseOrderDao.getPurchasePlanDetailsById(planningId);

		if(planning==null)
		{
			resultMap.put("error", "Given planningId does not exist.");
			return resultMap;
		}
		
		List<BigDecimal> supplierList = purchaseOrderDao.selectDistinctSuppliersFromPurchasePlan(planningId);
		Map<BigDecimal, List> purchaseOrderMap = new HashMap<BigDecimal, List>(); 
		
		try
		{
			for(BigDecimal supplierId : supplierList)
			{
				PurchaseOrder purchaseOrder = new PurchaseOrder();
				purchaseOrder.setPurchasePlanningId(planningId);
				purchaseOrder.setSupplierId(supplierId);
				purchaseOrder.setPurchaseOrderDate(new Date());
				purchaseOrder.setComments(null);
				
				List<Object[]> list = purchaseOrderDao.selectOrderItemDetailsBySupplierId(planningId, supplierId);
				
				BigDecimal purchaseOrderId = purchaseOrderDao.insertPurchaseOrder(purchaseOrder);
				List<BigDecimal> purchaseOrderItemList = new ArrayList<BigDecimal>();
				
				for(Object[] rs : list)
				{
					BigDecimal planningItemId = (BigDecimal) rs[0];
					Date purchaseOrderItemDate = (Date) rs[1];
					BigDecimal purchaseOrderItemWeight = ((BigDecimal) rs[2]);
					BigDecimal purchaseOrderItemUnitPrice = (BigDecimal) rs[3];
					BigDecimal purchaseOrderItemTax = (BigDecimal)rs[4];
					
					PurchaseOrderItem purchaseOrderItem = new PurchaseOrderItem();
					purchaseOrderItem.setPurchaseOrderId(purchaseOrderId);
					purchaseOrderItem.setPlanningItemId(planningItemId);
					purchaseOrderItem.setPurchaseOrderItemDate(purchaseOrderItemDate);
					purchaseOrderItem.setPurchaseOrderItemWeight(purchaseOrderItemWeight);
					purchaseOrderItem.setPurchaseOrderItemUnitPrice(purchaseOrderItemUnitPrice);
					purchaseOrderItem.setPurchaseOrderItemTax(purchaseOrderItemTax);
					
					BigDecimal purchaseOrderItemId = purchaseOrderDao.insertPurchaseOrderItem(purchaseOrderItem);
					
					purchaseOrderItemList.add(purchaseOrderItemId);
				}
				
				purchaseOrderMap.put(purchaseOrderId, purchaseOrderItemList);
			}
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not insert order item");
			return resultMap;
		}
		
		resultMap.put("success", "Purchase Order inserted successfully");
		resultMap.put("purchaseOrders", purchaseOrderMap);		
		
		return resultMap;
	}
	
	/**
	 * This method validates Purchase Order ID
	 * 
	 * @param id - Purchase Order ID
	 * @return - boolean value
	 */
	public boolean isPurchaseOrderIdValid(String id)
	{
		BigDecimal purchaseOrderId = null;
		try
		{
			purchaseOrderId = new BigDecimal(id);
		}
		catch(Exception e)
		{
			return false;
		}
		
		PurchaseOrder order = purchaseOrderDao.getPurchaseOrderDetailsById(purchaseOrderId);
		
		if(order==null)
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
	 * @param purchaseOrderId - Purchase Order ID
	 * @param userName - HTTP Basic Auth logged in User ID
	 */
	@Transactional
	public void getPurchaseOrderReportInPdf(HttpServletRequest request, 
									   		HttpServletResponse response, 
									   		BigDecimal purchaseOrderId,
									   		String userName)
	{
		// generate report
		JasperPrint jasperPrint = null;
		
		try
		{
			byte[] purchaseOrderReportJasper = purchaseOrderDao.getPurchaseOrderReportJasperFile();
			byte[] purchaseOrderReportAltamiraimage = purchaseOrderDao.getPurchaseOrderReportAltamiraImage();
			byte[] pdf = null;
						
			final ByteArrayInputStream reportStream = new ByteArrayInputStream(purchaseOrderReportJasper);
			final Map<String, Object> parameters = new HashMap<String, Object>();
			
			parameters.put("PURCHASE_ORDER_ID", purchaseOrderId);
			
			Date purchaseOrderDate = purchaseOrderDao.getPurchaseOrderDetailsById(purchaseOrderId).getPurchaseOrderDate();
			
			parameters.put("PURCHASE_ORDER_DATE", purchaseOrderDate);
			parameters.put("USERNAME", userName.toUpperCase());
			
			Locale locale = new Locale.Builder().setLanguage("pt").setRegion("BR").build();
			parameters.put("REPORT_LOCALE", locale);
			
			BufferedImage imfg=null;
			try 
			{
				InputStream in = new ByteArrayInputStream(purchaseOrderReportAltamiraimage);
				imfg = ImageIO.read(in);
			} 
			catch (Exception e1) 
			{
				e1.printStackTrace();
			}
			
			parameters.put("altamira_logo", imfg);
			
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
			response.setHeader("Content-Disposition","inline; filename=PurchaseOrderReport.pdf");
			
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
					purchaseOrderDao.insertGeneratedPurchaseOrderReport(jasperPrint);
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
