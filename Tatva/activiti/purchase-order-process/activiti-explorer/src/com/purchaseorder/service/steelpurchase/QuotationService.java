package com.purchaseorder.service.steelpurchase;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.sql.Connection;
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
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.jdbc.ReturningWork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.transaction.annotation.Transactional;

import com.purchaseorder.dao.PurchaseOrderDao;
import com.purchaseorder.entity.Quotation;
import com.purchaseorder.entity.QuotationItem;
import com.purchaseorder.entity.QuotationItemQuote;
import com.purchaseorder.entity.QuotationRequest;

/**
 * 
 * @author PARTH
 *
 */
public class QuotationService 
{
	@Autowired
	PurchaseOrderDao purchaseOrderDao;

	@Autowired
	RuntimeService runtimeService;
	
	@Autowired
	@PersistenceContext(unitName="pum")
	private EntityManager entityManager;

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
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
	 * This method adds Quotation.
	 * 
	 * @param jsonObject - JSON request body
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object addQuotation(String jsonObject,
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

		// Parse json object to HashMap
		Map<String, String> quotationProperties = new HashMap<String, String>();
		try 
		{
			quotationProperties = new ObjectMapper().readValue(jsonObject, new TypeReference<HashMap<String, String>>(){});
		}
		catch (Exception e) 
		{
			resultMap.put("error", "Bad Request");
			resultMap.put("description", "Unable to parse Request Body.");

			return resultMap;
		}

		// check for the process initiator of the process id in Activiti
		String processId = null;
		String processInitiator = null;
		try
		{
			processId = quotationProperties.get("processId");
			processInitiator = (String) runtimeService.getVariable(processId, "initiator");			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not find process initiator for the process Id");

			return resultMap;
		}

		if(!processInitiator.equalsIgnoreCase(name))
		{
			resultMap.put("error", "Only Process Instance Initiator can add Quotation.");
			return resultMap;
		}

		// store Quotation Id in activiti process instance variables.
		List<String> quotationIdList = (List<String>) runtimeService.getVariable(processId, "quotationId");
		
		if(quotationIdList!=null && quotationIdList.size()>=1)
		{
			resultMap.put("error", "Only 1 quotation can be added per process.");
			return resultMap;
		}
		
		Quotation quotation = new Quotation();
		quotation.setQuotationDate(new Date());
		quotation.setQuotationCreator(processInitiator);

		BigDecimal quotationId = null;
		try
		{
			quotationId = purchaseOrderDao.insertQuotation(quotation);			
		}
		catch (Exception e) 
		{
			resultMap.put("error", "Could not insert quotation");

			return resultMap;
		}


		if(quotationIdList != null)
		{
			quotationIdList.add(quotationId.toString());
			runtimeService.setVariable(processId, "quotationId", quotationIdList);
		}
		else
		{
			quotationIdList = new ArrayList<String>();
			quotationIdList.add(quotationId.toString());
			runtimeService.setVariable(processId, "quotationId", quotationIdList);
		}

		resultMap.put("success", "Quotation inserted successfully");
		resultMap.put("processId", processId);
		resultMap.put("quotationId", quotationId.toString());	

		return resultMap;		
	}

	/**
	 * This method fetches Quotation details.
	 * 
	 * @param id - Quotation ID
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object getQuotation(String id,
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

		BigDecimal quotationId = null;
		try
		{
			quotationId = new BigDecimal(id);			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not parse the id provided.");

			return resultMap;
		}

		// get quotation details from database 
		try
		{
			Quotation quotation = purchaseOrderDao.getQuotationDetailsById(quotationId);

			if(quotation!=null)
			{
				resultMap.put("quotationId", quotation.getQuotationId().toString());
				resultMap.put("quotationDate", quotation.getQuotationDate().toString());
				resultMap.put("quotationCreator", quotation.getQuotationCreator());

				return resultMap;
			}
			else
			{
				resultMap.put("error", "Could not find quotation");

				return resultMap;
			}
		}
		catch(Exception e)
		{	
			resultMap.put("error", "Error while fetching quotation details");
		}

		return resultMap;
	}
	
	/**
	 * This method returns all Quotations.
	 * 
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object getAllQuotations(Principal principal)
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
		
		// Fetch all quotations stored in database
		try
		{
			List<Quotation> quotations = purchaseOrderDao.getAllQuotations();

			return quotations;
		}
		catch(Exception e)
		{	
			resultMap.put("error", "Error while fetching quotation details");
		}
		
		return resultMap;
	}
	
	/**
	 * This method deletes Quotation.
	 * 
	 * @param processId - Process ID
	 * @param quotationId - Quotation ID
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object deleteQuotation(String processId,
								  String quotationId,
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
			resultMap.put("error", "Only Process Instance Initiator can delete Quotation.");
		}

		// check if the given quotationId is stored in process variables or not ?
		List<String> quotationIdList = (List<String>) runtimeService.getVariable(processId, "quotationId");

		if(!quotationIdList.contains(quotationId.toString()))
		{
			resultMap.put("error", "Could not found the quotation id in process instance.");

			return resultMap;
		}

		// delete quotation
		try
		{
			boolean result = purchaseOrderDao.deleteQuotationById(quotationId);

			if(!result)
				throw new Exception();

			quotationIdList.remove(quotationId);
			runtimeService.setVariable(processId, "quotationId", quotationIdList);
			resultMap.put("success", "Quotation deleted having id:"+quotationId);
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
	 * This method adds Quotation Request.
	 * 
	 * @param jsonObject - JSON request body
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object addQuotationRequest(String jsonObject,
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
		Map<String, String> quoationRequestProperties = new HashMap<String, String>();
		try 
		{
			quoationRequestProperties = new ObjectMapper().readValue(jsonObject, new TypeReference<HashMap<String, String>>(){});
		}
		catch (Exception e) 
		{
			resultMap.put("error", "Bad Request");
			resultMap.put("description", "Unable to parse Request Body.");

			return resultMap;
		}
		
		// Check for the mandatory fields
		if(!quoationRequestProperties.containsKey("processId"))
		{
			if(reqFields.toString().isEmpty())
				reqFields.append("processId");
			else
				reqFields.append(", processId");

			error = true;
		}
		
		if(!quoationRequestProperties.containsKey("quotationId"))
		{
			if(reqFields.toString().isEmpty())
				reqFields.append("quotationId");
			else
				reqFields.append(", quotationId");

			error = true;
		}
		
		if(!quoationRequestProperties.containsKey("requestId"))
		{
			if(reqFields.toString().isEmpty())
				reqFields.append("requestId");
			else
				reqFields.append(", requestId");

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
			processId = quoationRequestProperties.get("processId");
			processInitiator = (String) runtimeService.getVariable(processId, "initiator");
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not find process initiator for the process Id");

			return resultMap;
		}

		if(!processInitiator.equalsIgnoreCase(name))
		{
			resultMap.put("error", "Only Process Instance Initiator can add Quotation Request.");
		}
		
		BigDecimal quotationId = null;
		BigDecimal requestId = null;
		
		// check field constraints
		try
		{
			quotationId = new BigDecimal(quoationRequestProperties.get("quotationId"));
		}
		catch(Exception e)
		{
			if(fieldConstrints.toString().isEmpty())
				fieldConstrints.append("Could not parse quotationId");
			else
				fieldConstrints.append(", Could not parse quotationId");

			error = true;
		}
		
		try
		{
			requestId = new BigDecimal(quoationRequestProperties.get("requestId"));
		}
		catch(Exception e)
		{
			if(fieldConstrints.toString().isEmpty())
				fieldConstrints.append("Could not parse requestId");
			else
				fieldConstrints.append(", Could not parse requestId");

			error = true;
		}
		
		if(error)
		{
			resultMap.put("Fields Constraints", fieldConstrints.toString());
			return resultMap;
		}
		
		// check if quotationId is registered in activiti process
		List<String> quotationIdList = (List<String>) runtimeService.getVariable(processId, "quotationId");
		
		if(!quotationIdList.contains(quotationId.toString()))
		{
			resultMap.put("error", "Could not find the provided quotation id in process instance.");
			return resultMap;
		}
		
		// check if requestId is registered in activiti process
		List<String> requestIdList = (List<String>) runtimeService.getVariable(processId, "requestId");

		if(!requestIdList.contains(requestId.toString()))
		{
			resultMap.put("error", "Could not find the provided request id in process instance.");
			return resultMap;
		}
		
		QuotationRequest quotationRequest = new QuotationRequest();
		quotationRequest.setQuotationId(quotationId);
		quotationRequest.setRequestId(requestId);
		
		BigDecimal quotationRequestId = null;
		try
		{
			quotationRequestId = purchaseOrderDao.insertQuotationRequest(quotationRequest);
		}
		catch (Exception e) 
		{
			resultMap.put("error", "Could not insert Quotation Request");
			
			return resultMap;
		}
		
		resultMap.put("success", "Quotation Request inserted successfully");
		resultMap.put("quotationRequestId", quotationRequestId.toString());
		
		return resultMap;
	}
	
	/**
	 * This method fetches Quotation Request.
	 * 
	 * @param id - Quotation Request ID
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object getQuotationRequest(String id,
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
		
		BigDecimal quotationRequestId = null;
		try
		{
			quotationRequestId = new BigDecimal(id);			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not parse the Quotation Request Id provided.");
			
			return resultMap;
		}
		
		// get quotation request details from database 
		try
		{
			QuotationRequest quotationRequest = purchaseOrderDao.getQuotationRequestDetailsById(quotationRequestId);

			if(quotationRequest!=null)
			{

				resultMap.put("quotationRequestId", quotationRequest.getQuotationRequestId().toString());
				resultMap.put("quotationId", quotationRequest.getQuotationId().toString());
				resultMap.put("requestId", quotationRequest.getRequestId().toString());

				return resultMap;
			}
			else
			{
				resultMap.put("error", "Could not find Quotation Request");

				return resultMap;
			}
		}
		catch(Exception e)
		{	
			resultMap.put("error", "Error while fetching Quotation Request details");
		}

		return resultMap;
	}
	
	/**
	 * This method returns all Quotation Requests.
	 * 
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object getAllQuotationRequests(Principal principal)
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
		
		// Fetch all quotation requests stored in database
		try
		{
			List<QuotationRequest> quotationRequests = purchaseOrderDao.getAllQuotationRequests();

			return quotationRequests;
		}
		catch(Exception e)
		{	
			resultMap.put("error", "Error while fetching Quotation Request details");
		}

		return resultMap;
	}
	
	/**
	 * This method deletes Quotation Request.
	 * 
	 * @param processId - Process ID
	 * @param id - Quotation Request ID
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object deleteQuotationRequest(String processId,
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
		
		BigDecimal quotationRequestId = null;
		try
		{
			quotationRequestId = new BigDecimal(id);			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not parse the provided quotationRequestId.");
			
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
			resultMap.put("error", "Only Process Instance Initiator can delete Quotation Request.");
		}
		
		// check if the given quotationRequestId belongs to quotationId which is stored in process instance variables ?
		List<String> quotationIdList = (List<String>) runtimeService.getVariable(processId, "quotationId");

		BigDecimal quotationId = null;
		try
		{
			quotationId = purchaseOrderDao.getQuotationIdOfQuotationRequestId(quotationRequestId);			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not find the relative Quotation Id of the provided Quotation Request.");
			return resultMap;
		}

		if(!quotationIdList.contains(quotationId.toString()))
		{
			resultMap.put("error", "Provided quotation request id does not belong to quotation id in activiti process.");
			return resultMap;
		}
		
		// delete request item
		try
		{
			boolean result = purchaseOrderDao.deleteQuotationRequestById(quotationRequestId);

			if(!result)
				throw new Exception();

			resultMap.put("success", "Quotation Request deleted having id:"+quotationRequestId);
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not delete quotation request.");
			return resultMap;
		}

		return resultMap;		
	}
	
	/**
	 * This method updates Quotation Request.
	 * 
	 * @param jsonObject - JSON request body
	 * @param processId - Process ID
	 * @param id - Quotation Request ID
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object updateQuotationRequest(String jsonObject,
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
		Map<String, String> quotationRequestProperties = new HashMap<String, String>();
		try 
		{
			quotationRequestProperties = new ObjectMapper().readValue(jsonObject, new TypeReference<HashMap<String, String>>(){});
		}
		catch (Exception e) 
		{
			resultMap.put("error", "Bad Request");
			resultMap.put("description", "Unable to parse Request Body.");

			return resultMap;
		}
		
		BigDecimal quotationrequestId = null;
		try
		{
			quotationrequestId = new BigDecimal(id);			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not parse the provided quotation request id.");
			
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
			resultMap.put("error", "Only Process Instance Initiator can update Quotation Request.");
		}
		
		// check if quotation request Id is available for update or not
		QuotationRequest quotationRequest = null;
		try
		{
			quotationRequest = purchaseOrderDao.getQuotationRequestDetailsById(quotationrequestId);

			if(quotationRequest==null)
				throw new Exception();
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not find Quotation Request by id:"+quotationrequestId);

			return resultMap;
		}
		
		// check if the given quotationRequestId belongs to quotationId which is stored in process instance variables ?
		List<String> quotationIdList = (List<String>) runtimeService.getVariable(processId, "quotationId");

		BigDecimal quotationId = purchaseOrderDao.getQuotationIdOfQuotationRequestId(quotationrequestId);

		if(!quotationIdList.contains(quotationId.toString()))
		{
			resultMap.put("error", "Provided quotation request id does not belong to quotation id in activiti process.");

			return resultMap;
		}
		
		// update quotation request
		BigDecimal requestId = null;
		
		if(quotationRequestProperties.get("requestId")!=null)
		{
			try
			{
				requestId = new BigDecimal(quotationRequestProperties.get("requestId"));
				
				// check if requestId is registered in activiti process
				List<String> requestIdList = (List<String>) runtimeService.getVariable(processId, "requestId");

				if(!requestIdList.contains(requestId.toString()))
				{
					resultMap.put("error", "Could not find the provided request id in process instance.");
					return resultMap;
				}
								
				quotationRequest.setRequestId(requestId);
			}
			catch(Exception e)
			{
				if(fieldConstrints.toString().isEmpty())
					fieldConstrints.append("Could not parse requestId");
				else
					fieldConstrints.append(", Could not parse requestId");

				error = true;
			}
		}
		
		if(error)
		{
			resultMap.put("Fields Constraints", fieldConstrints.toString());
			
			return resultMap;
		}
		
		try
		{
			purchaseOrderDao.updateQuotationRequest(quotationRequest);			
			resultMap.put("success", "Quotation Request updated successfully");
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not update Quotation Request.");
			return resultMap;
		}

		return resultMap;
	}
	
	/**
	 * This method adds Quotation Items.
	 * 
	 * @param jsonObject - JSON request body
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object addQuotationItem(String jsonObject,
								   Principal principal)
	{
		String name = null;
		Map<String, String> resultMap = new HashMap<String, String>();
		boolean error = false;
		StringBuffer reqFields = new StringBuffer();
		StringBuffer fieldConstrints = new StringBuffer();
		StringBuffer success = new StringBuffer();
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
		Map<String, String> quoationItemProperties = new HashMap<String, String>();
		try 
		{
			quoationItemProperties = new ObjectMapper().readValue(jsonObject, new TypeReference<HashMap<String, String>>(){});
		}
		catch (Exception e) 
		{
			resultMap.put("error", "Bad Request");
			resultMap.put("description", "Unable to parse Request Body.");

			return resultMap;
		}
		
		// Check for the mandatory fields
		if(!quoationItemProperties.containsKey("quotationId"))
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
			processId = quoationItemProperties.get("processId");
			processInitiator = (String) runtimeService.getVariable(processId, "initiator");
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not find process initiator for the process Id");

			return resultMap;
		}

		if(!processInitiator.equalsIgnoreCase(name))
		{
			resultMap.put("error", "Only Process Instance Initiator can add Quotation Item.");
		}
		
		BigDecimal quotationId = null;
		
		// check field constraints
		try
		{
			quotationId = new BigDecimal(quoationItemProperties.get("quotationId"));
		}
		catch(Exception e)
		{
			if(fieldConstrints.toString().isEmpty())
				fieldConstrints.append("Could not parse quotationId");
			else
				fieldConstrints.append(", Could not parse quotationId");

			error = true;
		}

		if(error)
		{
			resultMap.put("Fields Constraints", fieldConstrints.toString());
			return resultMap;
		}
		
		// check if quotationId is registered in activiti process
		List<String> quotationIdList = (List<String>) runtimeService.getVariable(processId, "quotationId");
		
		if(!quotationIdList.contains(quotationId.toString()))
		{
			resultMap.put("error", "Could not find the provided quotation id in process instance.");
			return resultMap;
		}
		
		List<Object[]> list = purchaseOrderDao.selectDetailsForQuotationItem(quotationId);
		
		List<QuotationItem> quotationItemList = new ArrayList<QuotationItem>();
		
		for(Object[] result : list)
		{
			QuotationItem quotationItem = new QuotationItem();
			
			quotationItem.setQuotationId(quotationId);
			quotationItem.setMaterialLamination((String)result[1]);
			quotationItem.setMaterialTreatment((String)result[2]);
			quotationItem.setMaterialThickness(new BigDecimal(result[3].toString()));
			quotationItem.setMaterialStandard(new BigDecimal(result[4].toString()));
			quotationItem.setRequestWeight(new BigDecimal(result[5].toString()));
			
			quotationItemList.add(quotationItem);
		}
		
		// insert all quotation items
		for(QuotationItem quotationItem : quotationItemList)
		{
			try
			{
				BigDecimal quotationItemId = purchaseOrderDao.insertQuotationItem(quotationItem);
				
				if(success.toString().isEmpty())
					success.append(quotationItemId.toString());
				else
					success.append(", "+quotationItemId.toString());
			}
			catch(Exception e)
			{
				resultMap.put("error", "Error occured while insretion");
				return resultMap;
			}
		}
		
		resultMap.put("success", "Quotation Items inserted successfully");
		resultMap.put("Quotation Item Id(s)", success.toString());
		
		return resultMap;
	}
	
	/**
	 * This method fetches Quotation Item details.
	 * 
	 * @param id - Quotation Item ID
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object getQuotationItem(String id,
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

		BigDecimal quotationItemId = null;
		try
		{
			quotationItemId = new BigDecimal(id);			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not parse the quotation item id provided.");

			return resultMap;
		}

		// get quotation item details from database 
		try
		{
			QuotationItem quotationItem = purchaseOrderDao.getQuotationItemDetailsById(quotationItemId); 

			if(quotationItem!=null)
			{

				resultMap.put("quotationItemId", quotationItem.getQuotationItemId().toString());
				resultMap.put("quotationId", quotationItem.getQuotationId().toString());
				resultMap.put("materialLamination", quotationItem.getMaterialLamination());
				resultMap.put("materialTreatment", quotationItem.getMaterialTreatment());
				resultMap.put("materialThickness", quotationItem.getMaterialThickness().toString());
				resultMap.put("materialStandard", quotationItem.getMaterialStandard().toString());
				resultMap.put("requestWeight", quotationItem.getRequestWeight().toString());

				return resultMap;
			}
			else
			{
				resultMap.put("error", "Could not find quotation item");

				return resultMap;
			}
		}
		catch(Exception e)
		{	
			resultMap.put("error", "Error while fetching quotation item details");
		}

		return resultMap;
	}
	
	/**
	 * This method returns all Quotation Items.
	 * 
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object getAllQuotationItems(Principal principal)
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
		
		// Fetch all quotation Items stored in database
		try
		{
			List<QuotationItem> quotationItems = purchaseOrderDao.getAllQuotationItems();

			return quotationItems;
		}
		catch(Exception e)
		{	
			resultMap.put("error", "Error while fetching quotation item details");
		}
		
		return resultMap;
	}
	
	/**
	 * This method deletes Quotation Item.
	 * 
	 * @param processId - Process ID
	 * @param id - Quotation Item ID
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object deleteQuotationItem(String processId,
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

		BigDecimal quotationItemId = null;
		try
		{
			quotationItemId = new BigDecimal(id);			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not parse the provided quotationItemId.");

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
			resultMap.put("error", "Only Process Instance Initiator can delete Quotation Item.");
		}

		// check if the given quotationItemId belongs to any quotationId which is stored in process instance variables ?
		List<String> quotationIdList = (List<String>) runtimeService.getVariable(processId, "quotationId");

		BigDecimal quotationId = null;
		try
		{
			quotationId = purchaseOrderDao.getQuotationIdOfQuotationItemId(quotationItemId);			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not find the relative quotation Id of the provided Quotation Item.");
			return resultMap;
		}

		if(!quotationIdList.contains(quotationId.toString()))
		{
			resultMap.put("error", "Provided quotation item id does not belong to quotation id in activiti process.");
			return resultMap;
		}

		// delete quotation item
		try
		{
			boolean result = purchaseOrderDao.deleteQuotationItemById(quotationItemId);

			if(!result)
				throw new Exception();

			resultMap.put("success", "Quotation Item deleted having id:"+quotationItemId);
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
			resultMap.put("error", "Could not delete Quotation Item");
			return resultMap;
		}

		return resultMap;
	}
	
	/**
	 * This method updates Quotation Item.
	 * 
	 * @param jsonObject - JSON request body
	 * @param processId - Process ID
	 * @param id - Quotation Item ID
	 * @param principal -HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object updateQuotationItem(String jsonObject,
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
		Map<String, String> quotationItemProperties = new HashMap<String, String>();
		try 
		{
			quotationItemProperties = new ObjectMapper().readValue(jsonObject, new TypeReference<HashMap<String, String>>(){});
		}
		catch (Exception e) 
		{
			resultMap.put("error", "Bad Request");
			resultMap.put("description", "Unable to parse Request Body.");

			return resultMap;
		}

		BigDecimal quotationItemId = null;
		try
		{
			quotationItemId = new BigDecimal(id);			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not parse the provided quotation item id.");

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
			resultMap.put("error", "Only Process Instance Initiator can update Quotation Item.");
		}

		// check if quotation item Id is available for update or not
		QuotationItem quotationItem = null;
		try
		{
			quotationItem = purchaseOrderDao.getQuotationItemDetailsById(quotationItemId);

			if(quotationItem==null)
				throw new Exception();
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not find Quotation Item by id:"+quotationItemId);

			return resultMap;
		}

		// check if the given quotationItemId belongs to quotationId which is stored in process instance variables ?
		List<String> quotationIdList = (List<String>) runtimeService.getVariable(processId, "quotationId");

		BigDecimal quotationId = purchaseOrderDao.getQuotationIdOfQuotationItemId(quotationItemId);

		if(!quotationIdList.contains(quotationId.toString()))
		{
			resultMap.put("error", "Provided quotation item id does not belong to quotation id in activiti process.");

			return resultMap;
		}

		if(error)
		{
			resultMap.put("Fields Constraints", fieldConstrints.toString());

			return resultMap;
		}

		try
		{
			purchaseOrderDao.updateQuotationItem(quotationItem);			
			resultMap.put("success", "Quotation Item updated successfully");
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not update Quotation Item.");
			return resultMap;
		}

		return resultMap;
	}
	
	/**
	 * This method validates Quotation ID.
	 * 
	 * @param id - Quotation ID
	 * @return - boolean value
	 */
	public boolean isQuotationIdValid(String id)
	{
		BigDecimal quotationId = null;
		try
		{
			quotationId = new BigDecimal(id);
		}
		catch(Exception e)
		{
			return false;
		}	
		
		Quotation quotation = purchaseOrderDao.getQuotationDetailsById(quotationId);
		
		if(quotation==null)
		{
			return false;
		}
		else
		{
			return true;
		}		
	}
	
	/**
	 * This method adds Quotation Item Quote.
	 * 
	 * @param jsonObject - JSON request body
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object addQuotationItemQuote(String jsonObject,
										Principal principal)
	{
		String name = null;
		Map<String, String> resultMap = new HashMap<String, String>();
		boolean error = false;
		StringBuffer reqFields = new StringBuffer();
		StringBuffer fieldConstrints = new StringBuffer();
		StringBuffer success = new StringBuffer();
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
		Map<String, String> quoationItemQuoteProperties = new HashMap<String, String>();
		try 
		{
			quoationItemQuoteProperties = new ObjectMapper().readValue(jsonObject, new TypeReference<HashMap<String, String>>(){});
		}
		catch (Exception e) 
		{
		resultMap.put("error", "Bad Request");
		resultMap.put("description", "Unable to parse Request Body.");
		
		return resultMap;
		}
		
		// Check for the mandatory fields
		if(!quoationItemQuoteProperties.containsKey("processId"))
		{
			if(reqFields.toString().isEmpty())
				reqFields.append("processId");
			else
				reqFields.append(", processId");
			
			error = true;
		}

		if(!quoationItemQuoteProperties.containsKey("quotationItemId"))
		{
			if(reqFields.toString().isEmpty())
				reqFields.append("quotationItemId");
			else
				reqFields.append(", quotationItemId");
		
			error = true;
		}
		
		if(!quoationItemQuoteProperties.containsKey("supplierId"))
		{
			if(reqFields.toString().isEmpty())
				reqFields.append("supplierId");
			else
				reqFields.append(", supplierId");
			
				error = true;
		}
		
		if(!quoationItemQuoteProperties.containsKey("supplierWeight"))
		{
			if(reqFields.toString().isEmpty())
				reqFields.append("supplierWeight");
			else
				reqFields.append(", supplierWeight");
			
			error = true;
		}
		
		if(!quoationItemQuoteProperties.containsKey("supplierPrice"))
		{
			if(reqFields.toString().isEmpty())
				reqFields.append("supplierPrice");
			else
				reqFields.append(", supplierPrice");
			
			error = true;
		}
		
		if(!quoationItemQuoteProperties.containsKey("supplierStandard"))
		{
			if(reqFields.toString().isEmpty())
				reqFields.append("supplierStandard");
			else
				reqFields.append(", supplierStandard");
			
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
			processId = quoationItemQuoteProperties.get("processId");
			processInitiator = (String) runtimeService.getVariable(processId, "initiator");
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not find process initiator for the process Id");
		
			return resultMap;
		}
		
		if(!processInitiator.equalsIgnoreCase(name))
		{
			resultMap.put("error", "Only Process Instance Initiator can add Quotation Item Quote.");
		}
		
		BigDecimal quotationItemId = null;
		BigDecimal supplierId=null;
		BigDecimal supplierWeight=null;
		BigDecimal supplierPrice=null;
		String supplierStandard=null;
		
		// check field constraints
		try
		{
			quotationItemId = new BigDecimal(quoationItemQuoteProperties.get("quotationItemId"));
			
			QuotationItem quotationItem= purchaseOrderDao.getQuotationIdByQuotationItemId(quotationItemId);
			BigDecimal quotationId =null;
			
			if(quotationItem==null)
			{
				if(fieldConstrints.toString().isEmpty())
					fieldConstrints.append("Given quotation item Id does not exist.");
				else
					fieldConstrints.append(", Given quotation item Id does not exist.");
				
				error = true;
			}
			else 
			{
				//check if given quotationItemId belongs to same process 
				quotationId=quotationItem.getQuotationId();
				List<String> quotationIdList = (List<String>) runtimeService.getVariable(processId, "quotationId");
				
				if(!quotationIdList.contains(quotationId.toString()))
				{
					if(fieldConstrints.toString().isEmpty())
						fieldConstrints.append("Could not find the provided quotationItemId id in process instance.");
					else
						fieldConstrints.append(", Could not find the provided quotationItemId id in process instance.");
					
					error = true;
				}
			}
		}
		catch(Exception e)
		{
			if(fieldConstrints.toString().isEmpty())
				fieldConstrints.append("Could not parse quotationItemId");
			else
				fieldConstrints.append(", Could not parse quotationItemId");

			error = true;
		}
		
		try
		{
			supplierId= new BigDecimal(quoationItemQuoteProperties.get("supplierId"));
			
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
		catch (Exception e) 
		{
			if(fieldConstrints.toString().isEmpty())
				fieldConstrints.append("Could not parse supplierId");
			else
				fieldConstrints.append(", Could not parse supplierId");

			error = true;
		}
		
		try
		{
			supplierWeight=new BigDecimal(quoationItemQuoteProperties.get("supplierWeight")).setScale(2, RoundingMode.CEILING);
		}
		catch (Exception e) 
		{
			if(fieldConstrints.toString().isEmpty())
				fieldConstrints.append("Could not parse supplierWeight");
			else
				fieldConstrints.append(", Could not parse supplierWeight");

			error = true;
		}
		
		
		try
		{
			supplierPrice=new BigDecimal(quoationItemQuoteProperties.get("supplierPrice")).setScale(2, RoundingMode.CEILING);
		}
		catch (Exception e) 
		{
			if(fieldConstrints.toString().isEmpty())
				fieldConstrints.append("Could not parse supplierPrice");
			else
				fieldConstrints.append(", Could not parse supplierPrice");

			error = true;
		}
		
		try
		{
			supplierStandard=quoationItemQuoteProperties.get("supplierStandard");
			
			if(supplierStandard.length() > 20)
			{
				if(fieldConstrints.toString().isEmpty())
					fieldConstrints.append("supplierStandard should not exceed 20 characters");
				else
					fieldConstrints.append(", supplierStandard should not exceed 20 characters");
				
				error = true;
			}
		}
		catch (Exception e) 
		{
			if(fieldConstrints.toString().isEmpty())
				fieldConstrints.append("Could not parse supplierStandard");
			else
				fieldConstrints.append(", Could not parse supplierStandard");
			
			error = true;
		}
		
		if(error)
		{
			resultMap.put("Fields Constraints", fieldConstrints.toString());
			return resultMap;
		}
		
		// insert quotation item quote
		QuotationItemQuote quotationItemQuote = new QuotationItemQuote();
		quotationItemQuote.setQuotationItemId(quotationItemId);
		quotationItemQuote.setSupplierId(supplierId);
		quotationItemQuote.setSupplierWeight(supplierWeight);
		quotationItemQuote.setSupplierPrice(supplierPrice);
		quotationItemQuote.setSupplierStandard(supplierStandard);
		
		BigDecimal quotationItemQuoteId = null;
		try
		{
			quotationItemQuoteId=purchaseOrderDao.insertQuotationItemQuote(quotationItemQuote);
		}
		catch(JpaSystemException jse)
		{
			if(StringUtils.containsIgnoreCase(jse.getCause().toString(), "ConstraintViolationException"))
			{
				resultMap.put("error", "Cannot insert: Supplier entry already present for specified Quotation Item Id.");
				return resultMap;
			}
			else
			{
				resultMap.put("error", "Could not insert Quotation Item Quote");
				return resultMap;
			}
		}
		catch (Exception e) 
		{
			resultMap.put("error", "Could not insert Quotation Item Quote");
			return resultMap;
		}
		
		resultMap.put("success", "Quotation Item Quote inserted successfully");
		resultMap.put("Quotation Item Quote Id", quotationItemQuoteId.toString());
		
		return resultMap;
	}

	/**
	 * This method returns all Quotation Item Quotes.
	 * 
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object getAllQuotationItemQuotes(Principal principal) 
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
		
		// Fetch all quotation Items stored in database
		try
		{
			List<QuotationItemQuote> quotationItemQuotes = purchaseOrderDao.getAllQuotationItemQuotes();

			return quotationItemQuotes;
		}
		catch(Exception e)
		{	
			resultMap.put("error", "Error while fetching quotation item quotes details");
		}
		
		return resultMap;
	}
	
	/**
	 * This method fetches Quotation Item Quote.
	 * 
	 * @param id - Quotation Item Quote ID
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object getQuotationItemQuote(String id,
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

		BigDecimal quotationItemQuoteId = null;
		try
		{
			quotationItemQuoteId = new BigDecimal(id);			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not parse the quotation item quote id provided.");
			return resultMap;
		}

		// get quotation item details from database 
		try
		{
			QuotationItemQuote quotationItemQuote = purchaseOrderDao.getQuotationItemQuoteDetailsById(quotationItemQuoteId); 

			if(quotationItemQuote!=null)
			{

				resultMap.put("quotationItemQuoteId", quotationItemQuote.getQuotationItemQuoteId().toString());
				resultMap.put("quotationItemId", quotationItemQuote.getQuotationItemId().toString());
				resultMap.put("supplierId", quotationItemQuote.getSupplierId().toString());
				resultMap.put("supplierPrice", quotationItemQuote.getSupplierPrice().toString());
				resultMap.put("supplierWeight", quotationItemQuote.getSupplierWeight().toString());
				resultMap.put("supplierStandard", quotationItemQuote.getSupplierStandard());

				return resultMap;
			}
			else
			{
				resultMap.put("error", "Could not find quotation item quote");

				return resultMap;
			}
		}
		catch(Exception e)
		{	
			resultMap.put("error", "Error while fetching quotation item quote details");
		}

		return resultMap;
	}

	/**
	 * This method deletes Quotation Item Quote.
	 * 
	 * @param processId - Process ID
	 * @param id - Quotation Item Quote ID
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object deleteQuotationItemQuote(String processId,
										   String id, Principal principal) 
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

		BigDecimal quotationItemQuoteId = null;
		try
		{
			quotationItemQuoteId = new BigDecimal(id);			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not parse the provided quotationItemQuoteId.");

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
			resultMap.put("error", "Only Process Instance Initiator can delete Quotation Item Quote.");
		}

		// check if the given quotationItemId belongs to quotationId which is stored in process instance variables.
		List<String> quotationIdList = (List<String>) runtimeService.getVariable(processId, "quotationId");

		QuotationItemQuote quotationItemQuote =null;
		BigDecimal quotationItemId=null;
		BigDecimal quotationId = null;
		try
		{
			quotationItemQuote = purchaseOrderDao.getQuotationItemQuoteDetailsById(quotationItemQuoteId);
			quotationItemId = quotationItemQuote.getQuotationItemId();
			quotationId = purchaseOrderDao.getQuotationIdOfQuotationItemId(quotationItemId);			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not find the relative quotation Id of the provided Quotation Item Quote.");
			return resultMap;
		}

		if(!quotationIdList.contains(quotationId.toString()))
		{
			resultMap.put("error", "Provided quotation item quote id does not belong to quotation id in activiti process.");
			return resultMap;
		}

		// delete quotation item quote
		try
		{
			boolean result = purchaseOrderDao.deleteQuotationItemQuoteById(quotationItemQuoteId);

			if(!result)
				throw new Exception();

			resultMap.put("success", "Quotation Item Quote deleted having id:"+quotationItemQuoteId);
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
			resultMap.put("error", "Could not delete Quotation Item Quote");
			return resultMap;
		}

		return resultMap;
	}

	/**
	 * This method updates Quotation Item Quote.
	 * 
	 * @param jsonObject - JSON request body
	 * @param processId - Process ID
	 * @param id - Quotation Item Quote ID
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object updateQuotationItemQuote(String jsonObject, 
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
		Map<String, String> quotationItemQuoteProperties = new HashMap<String, String>();
		try 
		{
			quotationItemQuoteProperties = new ObjectMapper().readValue(jsonObject, new TypeReference<HashMap<String, String>>(){});
		}
		catch (Exception e) 
		{
			resultMap.put("error", "Bad Request");
			resultMap.put("description", "Unable to parse Request Body.");

			return resultMap;
		}

		BigDecimal quotationItemQuoteId = null;
		try
		{
			quotationItemQuoteId = new BigDecimal(id);			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not parse the provided quotation item quote id.");

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
			resultMap.put("error", "Only Process Instance Initiator can update Quotation Item Quote.");
		}

		// check if quotation item Id is available for update or not
		QuotationItemQuote quotationItemQuote = null;
		try
		{
			quotationItemQuote = purchaseOrderDao.getQuotationItemQuoteDetailsById(quotationItemQuoteId);

			if(quotationItemQuote==null)
				throw new Exception();
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not find Quotation Item Quote by id:"+quotationItemQuoteId);

			return resultMap;
		}
		
		// check if the given quotationItemId belongs to quotationId which is stored in process instance variables ?
		List<String> quotationIdList = (List<String>) runtimeService.getVariable(processId, "quotationId");

		BigDecimal quotationId = purchaseOrderDao.getQuotationIdOfQuotationItemId(quotationItemQuote.getQuotationItemId());

		if(!quotationIdList.contains(quotationId.toString()))
		{
			resultMap.put("error", "Provided quotation item quote id does not belong to quotation id in activiti process.");
			return resultMap;
		}
		
		BigDecimal quotationItemId = null;
		BigDecimal supplierId = null;
		BigDecimal supplierWeight = null;
		BigDecimal supplierPrice=null;
		String supplierStandard=null;
		
		if(quotationItemQuoteProperties.get("quotationItemId")!=null)
		{
			try
			{
				quotationItemId = new BigDecimal(quotationItemQuoteProperties.get("quotationItemId"));

				// check if given quotationItemId exists or not.
				QuotationItem quotationItem=purchaseOrderDao.getQuotationItemDetailsById(quotationItemId);

				if(quotationItem==null) 
				{
					if(fieldConstrints.toString().isEmpty())
						fieldConstrints.append("Given quotation item Id does not exist.");
					else
						fieldConstrints.append(", Given quotation item Id does not exist.");
					
					error = true;
				}
				else
				{
					//check if given quotationItemId belongs to same process 
					quotationIdList = (List<String>) runtimeService.getVariable(processId, "quotationId");

					quotationId = purchaseOrderDao.getQuotationIdOfQuotationItemId(quotationItemId);

					if(!quotationIdList.contains(quotationId.toString()))
					{
						if(fieldConstrints.toString().isEmpty())
							fieldConstrints.append("Given quotationItemQuoteId does not belongs to same process.");
						else
							fieldConstrints.append(", Given quotationItemQuoteId does not belongs to same process");
						
						error = true;					
					}
					else
					{
						quotationItemQuote.setQuotationItemId(quotationItemId);
					}
				}
			}
			catch(Exception e)
			{
				if(fieldConstrints.toString().isEmpty())
					fieldConstrints.append("Could not parse quotationItemId");
				else
					fieldConstrints.append(", Could not parse quotationItemId");

				error = true;
			}
		}
		
		if(quotationItemQuoteProperties.get("supplierId")!=null)
		{
			try
			{
				supplierId = new BigDecimal(quotationItemQuoteProperties.get("supplierId"));
				
				// check if given supplierId exists or not.
				boolean supplierAvailable = purchaseOrderDao.isSupplierIdAlreadyStored(supplierId);
				if(!supplierAvailable)
				{
					resultMap.put("error", "Given supplier Id does not exist.");
					return resultMap;
				}
				
				quotationItemQuote.setSupplierId(supplierId);
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
		
		if(quotationItemQuoteProperties.get("supplierWeight")!=null)
		{
			try
			{
				supplierWeight = new BigDecimal(quotationItemQuoteProperties.get("supplierWeight")).setScale(2, RoundingMode.CEILING);
				
				quotationItemQuote.setSupplierWeight(supplierWeight);
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
		
		if(quotationItemQuoteProperties.get("supplierPrice")!=null)
		{
			try
			{
				supplierPrice = new BigDecimal(quotationItemQuoteProperties.get("supplierPrice")).setScale(2, RoundingMode.CEILING);
				
				quotationItemQuote.setSupplierPrice(supplierPrice);
			}
			catch(Exception e)
			{
				if(fieldConstrints.toString().isEmpty())
					fieldConstrints.append("Could not parse supplierPrice");
				else
					fieldConstrints.append(", Could not parse supplierPrice");

				error = true;
			}
		}
		
		if(quotationItemQuoteProperties.get("supplierStandard")!=null)
		{
			try
			{
				supplierStandard =quotationItemQuoteProperties.get("supplierStandard");
				
				if(supplierStandard.length() > 20)
				{
					if(fieldConstrints.toString().isEmpty())
						fieldConstrints.append("supplierStandard should not exceed 20 characters");
					else
						fieldConstrints.append(", supplierStandard should not exceed 20 characters");
					
					error = true;
				}
				
				quotationItemQuote.setSupplierStandard(supplierStandard);
			}
			catch(Exception e)
			{
				if(fieldConstrints.toString().isEmpty())
					fieldConstrints.append("Could not parse supplierStandard");
				else
					fieldConstrints.append(", Could not parse supplierStandard");

				error = true;
			}
		}
		
		if(error)
		{
			resultMap.put("Fields Constraints", fieldConstrints.toString());
			return resultMap;
		}
		
		// update Quotation Item Quote
		try
		{
			purchaseOrderDao.updateQuotationItemQuote(quotationItemQuote);			
			resultMap.put("success", "Quotation Item Quote updated successfully");
		}
		catch(JpaSystemException jse)
		{
			if(StringUtils.containsIgnoreCase(jse.getCause().toString(), "ConstraintViolationException"))
			{
				resultMap.put("error", "Cannot update: Supplier entry already present for specified Quotation Item Id.");
				return resultMap;
			}
			else
			{
				resultMap.put("error", "Could not insert Quotation Item Quote");
				return resultMap;
			}
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not update Quotation Item Quote");
			return resultMap;
		}
		
		return resultMap;
	}
	
	/**
	 * This method reutrns report output stream.
	 * 
	 * @param request - HTTPServletRequest object
	 * @param response - HTTPServletResponse object
	 * @param quotationId - Quotation ID
	 * @param userName - HTTP Basic Auth logged in User ID
	 */
	@Transactional
	public void getQuotationReportInPdf(HttpServletRequest request, 
									  	HttpServletResponse response, 
									  	BigDecimal quotationId,
									  	String userName)
	{
		// generate report
		JasperPrint jasperPrint = null;
		
		try
		{
			byte[] quotationReportJasper = purchaseOrderDao.getQuotationReportJasperFile();
			byte[] quotationReportAltamiraimage = purchaseOrderDao.getQuotationReportAltamiraImage();
			byte[] pdf = null;
			
			final ByteArrayInputStream reportStream = new ByteArrayInputStream(quotationReportJasper);
			final Map<String, Object> parameters = new HashMap<String, Object>();
			
			List<Object[]> list = purchaseOrderDao.selectQuotationReportDataById(quotationId);
			List<BigDecimal> priceList = new ArrayList<BigDecimal>();
			
			// set pricelist for quotation items
			for(Object[] rs : list)
			{
				String materialLamination = rs[0].toString();
				String materialTreatment = rs[1].toString();
				String materialThickness = rs[2].toString();
				
				String str = "http://localhost:8082/activiti-explorer/webservice/purchaseOrder/test/priceList?lamination="+materialLamination+"&treatment="+materialTreatment+"&thickness="+materialThickness;
				
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet get = new HttpGet(str);
				get.addHeader("accept", "application/json");
				
				HttpResponse httpResponse = httpClient.execute(get);
				
				BufferedReader br = new BufferedReader(new InputStreamReader((httpResponse.getEntity().getContent())));

				StringBuffer jsonObject=new StringBuffer();
				String json = new String();
				System.out.println("Output from Server .... \n");
				
				while ((json = br.readLine()) != null) 
				{
					jsonObject.append(json);
					System.out.println(jsonObject);
				}
				
				// parse json response
				Map<String, Object> quotationItem = new HashMap<String, Object>();
				try 
				{
					quotationItem = new ObjectMapper().readValue(jsonObject.toString(), new TypeReference<HashMap<String, Object>>(){});
				}
				catch (Exception e) 
				{
					e.printStackTrace();
				}
				
				List<Map> materialList = (List<Map>) quotationItem.get("materials");
				
				Map<String, String> map = materialList.get(0);
				String avgPrice = map.get("averageprice");
				
				priceList.add(new BigDecimal(avgPrice));
				
			}
			
			parameters.put("QUOTATION_ID", quotationId);
			parameters.put("PRICELIST", priceList);
			
			// set current pricelist code
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet get = new HttpGet("http://localhost:8082/activiti-explorer/webservice/purchaseOrder/test/current");
			get.addHeader("accept", "application/json");
			
			HttpResponse httpResponse = httpClient.execute(get);
			
			BufferedReader br = new BufferedReader(new InputStreamReader((httpResponse.getEntity().getContent())));

			StringBuffer jsonObject = new StringBuffer();
			String json = new String();
			System.out.println("Output from Server .... \n");
			while ((json = br.readLine()) != null) 
			{
				jsonObject.append(json);
				System.out.println(jsonObject);
			}
			
			httpClient.getConnectionManager().shutdown();
			
			// parse json response
			Map<String, String> currentPriceList = new HashMap<String, String>();
			try 
			{
				currentPriceList = new ObjectMapper().readValue(jsonObject.toString(), new TypeReference<HashMap<String, String>>(){});
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			parameters.put("PRICELIST_CODE", currentPriceList.get("pricelist"));
			
			// set quotation Date
			Quotation quotation = purchaseOrderDao.getQuotationDetailsById(quotationId);
			parameters.put("QUOTATION_DATE", quotation.getQuotationDate());
			
			Locale locale = new Locale.Builder().setLanguage("pt").setRegion("BR").build();
			parameters.put("REPORT_LOCALE", locale);
			
			parameters.put("USERNAME", userName.toUpperCase());
			
			BufferedImage imfg=null;
			try 
			{
				InputStream in = new ByteArrayInputStream(quotationReportAltamiraimage);
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
			response.setHeader("Content-Disposition","inline; filename=QuotationReport.pdf");
			
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
					purchaseOrderDao.insertGeneratedQuotationReport(jasperPrint);
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
