package com.purchaseorder.service.steelpurchase;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.activiti.engine.RuntimeService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaSystemException;

import com.purchaseorder.dao.PurchaseOrderDao;
import com.purchaseorder.entity.Material;
import com.purchaseorder.entity.Request;
import com.purchaseorder.entity.RequestItem;
import com.purchaseorder.model.RequestReportData;

/**
 * 
 * @author PARTH
 *
 */
public class RequestService 
{
	@Autowired
	PurchaseOrderDao purchaseOrderDao;
	
	@Autowired
	RuntimeService runtimeService;
	
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
	 * This method adds Request.
	 * 
	 * @param jsonObject - JSON request body
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object addRequest(String jsonObject,
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
		Map<String, String> requestProperties = new HashMap<String, String>();
		try 
		{
			requestProperties = new ObjectMapper().readValue(jsonObject, new TypeReference<HashMap<String, String>>(){});
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
			processId = requestProperties.get("processId");
			processInitiator = (String) runtimeService.getVariable(processId, "initiator");			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not find process initiator for the process Id");
			
			return resultMap;
		}
		
		if(!processInitiator.equalsIgnoreCase(name))
		{
			resultMap.put("error", "Only Process Instance Initiator can add Request.");
		}
		
		Request request = new Request();
		request.setRequestDate(new Date());
		request.setRequestCreator(processInitiator);
		
		BigDecimal requestId = null;
		try
		{
			requestId = purchaseOrderDao.insertRequest(request);			
		}
		catch (Exception e) 
		{
			resultMap.put("error", "Could not insert request");
			
			return resultMap;
		}
		
		// store Request Id in activiti process instance variables.
		List<String> requestIdList = (List<String>) runtimeService.getVariable(processId, "requestId");

		if(requestIdList != null)
		{
			requestIdList.add(requestId.toString());
			runtimeService.setVariable(processId, "requestId", requestIdList);
		}
		else
		{
			requestIdList = new ArrayList<String>();
			requestIdList.add(requestId.toString());
			runtimeService.setVariable(processId, "requestId", requestIdList);
		}
		
		resultMap.put("success", "Request inserted successfully");
		resultMap.put("processId", processId);
		resultMap.put("requestId", requestId.toString());	
		
		return resultMap;
	}
	
	/**
	 * This method fetches Request Details.
	 * 
	 * @param id - Request ID
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object getRequest(String id,
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
		
		BigDecimal requestId = null;
		try
		{
			requestId = new BigDecimal(id);			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not parse the id provided.");
			
			return resultMap;
		}
		
		// get request details from database 
		try
		{
			Request request = purchaseOrderDao.getRequestDetailsById(requestId);

			if(request!=null)
			{
				resultMap.put("requestId", request.getRequestId().toString());
				resultMap.put("requestDate", request.getRequestDate().toString());
				resultMap.put("requestCreator", request.getRequestCreator());

				return resultMap;
			}
			else
			{
				resultMap.put("error", "Could not find request");
				
				return resultMap;
			}
		}
		catch(Exception e)
		{	
			resultMap.put("error", "Error while fetching request details");
		}
		
		return resultMap;
	}
	
	/**
	 * This method returns all Requests.
	 * 
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object getAllRequests(Principal principal)
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
		
		// Fetch all requests stored in database
		try
		{
			List<Request> requests = purchaseOrderDao.getAllRequests();

			return requests;
		}
		catch(Exception e)
		{	
			resultMap.put("error", "Error while fetching request details");
		}
		
		return resultMap;
	}
	
	/**
	 * This method deletes Request.
	 * 
	 * @param processId - Process ID 
	 * @param requestId - Request ID
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object deleteRequest(String processId,
								String requestId,
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
			resultMap.put("error", "Only Process Instance Initiator can delete Request.");
		}
		
		// check if the given requestId is stored in process variables or not ?
		List<String> requestIdList = (List<String>) runtimeService.getVariable(processId, "requestId");
		
		if(!requestIdList.contains(requestId))
		{
			resultMap.put("error", "Could not found the request id in process instance.");
			
			return resultMap;
		}
		
		// delete request
		try
		{
			boolean result = purchaseOrderDao.deleteRequestById(requestId);
						
			if(!result)
				throw new Exception();
				
			requestIdList.remove(requestId);
			runtimeService.setVariable(processId, "requestId", requestIdList);
			resultMap.put("success", "Request deleted having id:"+requestId);
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
			resultMap.put("error", "Could not delete request");
			return resultMap;
		}
		
		return resultMap;
	}
	
	/**
	 * This method adds Request Item.
	 * 
	 * @param jsonObject - JSON request body
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object addRequestItem(String jsonObject,
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
		Map<String, String> requestItemProperties = new HashMap<String, String>();
		try 
		{
			requestItemProperties = new ObjectMapper().readValue(jsonObject, new TypeReference<HashMap<String, String>>(){});
		}
		catch (Exception e) 
		{
			resultMap.put("error", "Bad Request");
			resultMap.put("description", "Unable to parse Request Body.");

			return resultMap;
		}
		
		// Check for the mandatory fields
		if(!requestItemProperties.containsKey("processId"))
		{
			if(reqFields.toString().isEmpty())
				reqFields.append("processId");
			else
				reqFields.append(", processId");
				
			error = true;
		}
		
		if(!requestItemProperties.containsKey("requestId"))
		{
			if(reqFields.toString().isEmpty())
				reqFields.append("requestId");
			else
				reqFields.append(", requestId");
				
			error = true;
		}
		
		if(!requestItemProperties.containsKey("materialId"))
		{
			if(reqFields.toString().isEmpty())
				reqFields.append("materialId");
			else
				reqFields.append(", materialId");
				
			error = true;
		}
		
		if(!requestItemProperties.containsKey("requestArrivalDate"))
		{
			if(reqFields.toString().isEmpty())
				reqFields.append("requestArrivalDate");
			else
				reqFields.append(", requestArrivalDate");
				
			error = true;
		}
		
		if(!requestItemProperties.containsKey("requestWeight"))
		{
			if(reqFields.toString().isEmpty())
				reqFields.append("requestWeight");
			else
				reqFields.append(", requestWeight");
				
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
			processId = requestItemProperties.get("processId");
			processInitiator = (String) runtimeService.getVariable(processId, "initiator");
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not find process initiator for the process Id");
			
			return resultMap;
		}
		
		if(!processInitiator.equalsIgnoreCase(name))
		{
			resultMap.put("error", "Only Process Instance Initiator can add Request.");
		}
		
		BigDecimal requestId = null;
		BigDecimal materialId = null;
		Date requestArrivalDate = null;
		BigDecimal requestWeight = null;
		
		// check field constraints
		try
		{
			requestId = new BigDecimal(requestItemProperties.get("requestId"));
		}
		catch(Exception e)
		{
			if(fieldConstrints.toString().isEmpty())
				fieldConstrints.append("Could not parse requestId");
			else
				fieldConstrints.append(", Could not parse requestId");
			
			error = true;
		}
		
		try
		{
			materialId = new BigDecimal(requestItemProperties.get("materialId"));
		}
		catch(Exception e)
		{
			if(fieldConstrints.toString().isEmpty())
				fieldConstrints.append("Could not parse materialId");
			else
				fieldConstrints.append(", Could not parse materialId");
			
			error = true;
		}
		
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			requestArrivalDate = sdf.parse(requestItemProperties.get("requestArrivalDate"));
		}
		catch(Exception e)
		{
			if(fieldConstrints.toString().isEmpty())
				fieldConstrints.append("Could not parse requestArrivalDate");
			else
				fieldConstrints.append(", Could not parse requestArrivalDate");
			
			error = true;
		}
		
		try
		{
			requestWeight = new BigDecimal(requestItemProperties.get("requestWeight")).setScale(2, RoundingMode.CEILING);
		}
		catch(Exception e)
		{
			if(fieldConstrints.toString().isEmpty())
				fieldConstrints.append("Could not parse requestWeight");
			else
				fieldConstrints.append(", Could not parse requestWeight");
			
			error = true;
		}
		
		if(error)
		{
			resultMap.put("Fields Constraints", fieldConstrints.toString());
			return resultMap;
		}
		
		// check if requestId is registered in activiti process
		List<String> requestIdList = (List<String>) runtimeService.getVariable(processId, "requestId");
		
		if(!requestIdList.contains(requestId.toString()))
		{
			resultMap.put("error", "Could not find the provided request id in process instance.");
			return resultMap;
		}
		
		// check if material id exists
		Material material = purchaseOrderDao.getMaterialDetailsById(materialId);
		
		if(material==null)
		{
			resultMap.put("error", "Provided material id does not exist.");
			return resultMap;
		}
		
		RequestItem requestItem = new RequestItem();
		requestItem.setRequestId(requestId);
		requestItem.setMaterialId(materialId);
		requestItem.setRequestArrivalDate(requestArrivalDate);
		requestItem.setRequestWeight(requestWeight);
		
		BigDecimal requestItemId = null;
		try
		{
			requestItemId = purchaseOrderDao.insertRequestItem(requestItem);
		}
		catch (Exception e) 
		{
			resultMap.put("error", "Could not insert request");
			
			return resultMap;
		}
		
		resultMap.put("success", "Request Item inserted successfully");
		resultMap.put("requestItemId", requestItemId.toString());	
		
		return resultMap;
	}
	
	/**
	 * This method fetches Request Item.
	 * 
	 * @param id - Request Item ID
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object getRequestItem(String id,
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
		
		BigDecimal requestItemId = null;
		try
		{
			requestItemId = new BigDecimal(id);			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not parse the request item id provided.");
			
			return resultMap;
		}
		
		// get request item details from database 
		try
		{
			RequestItem requestItem = purchaseOrderDao.getRequestItemDetailsById(requestItemId);

			if(requestItem!=null)
			{
				
				resultMap.put("requestItemId", requestItem.getRequestItemId().toString());
				resultMap.put("requestId", requestItem.getRequestId().toString());
				resultMap.put("materialId", requestItem.getMaterialId().toString());
				resultMap.put("requestArrivalDate", requestItem.getRequestArrivalDate().toString());
				resultMap.put("requestWeight", requestItem.getRequestWeight().toString());

				return resultMap;
			}
			else
			{
				resultMap.put("error", "Could not find request item");
				
				return resultMap;
			}
		}
		catch(Exception e)
		{	
			resultMap.put("error", "Error while fetching request item details");
		}
		
		return resultMap;
	}
	
	/**
	 * This method returns all Request Items.
	 * 
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object getAllRequestItems(Principal principal)
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
		
		// Fetch all requests stored in database
		try
		{
			List<RequestItem> requestItems = purchaseOrderDao.getAllRequestItems();

			return requestItems;
		}
		catch(Exception e)
		{	
			resultMap.put("error", "Error while fetching request item details");
		}
		
		return resultMap;
	}
	
	/**
	 * This method deletes Request Item.
	 * 
	 * @param processId - Process ID
	 * @param id - Request Item ID
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object deleteRequestItem(String processId,
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
		
		BigDecimal requestItemId = null;
		try
		{
			requestItemId = new BigDecimal(id);			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not parse the provided requestItemId.");
			
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
			resultMap.put("error", "Only Process Instance Initiator can delete Request Item.");
		}
		
		// check if the given requestItemId belongs to any requestId which is stored in process instance variables ?
		List<String> requestIdList = (List<String>) runtimeService.getVariable(processId, "requestId");
		
		BigDecimal requestId = null;
		try
		{
			requestId = purchaseOrderDao.getRequestIdOfRequestItemId(requestItemId);			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not find the relative request Id of the provided request Item.");
			return resultMap;
		}
		
		if(!requestIdList.contains(requestId.toString()))
		{
			resultMap.put("error", "Provided request item id does not belong to request id in activiti process.");
			return resultMap;
		}
		
		// delete request item
		try
		{
			boolean result = purchaseOrderDao.deleteRequestItemById(requestItemId);
			
			if(!result)
				throw new Exception();
			
			resultMap.put("success", "Request Item deleted having id:"+requestItemId);
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not delete request item.");
			return resultMap;
		}
		
		return resultMap;
	}
	
	/**
	 * This method updtes Request Item.
	 * 
	 * @param jsonObject - JSON request body
	 * @param processId - Process ID
	 * @param id - Request Item ID
	 * @param principal - HTTP Basic Authentication principal object
	 * @return - JSON Response body
	 */
	public Object updateRequestItem(String jsonObject,
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
		Map<String, String> requestItemProperties = new HashMap<String, String>();
		try 
		{
			requestItemProperties = new ObjectMapper().readValue(jsonObject, new TypeReference<HashMap<String, String>>(){});
		}
		catch (Exception e) 
		{
			resultMap.put("error", "Bad Request");
			resultMap.put("description", "Unable to parse Request Body.");

			return resultMap;
		}
		
		BigDecimal requestItemId = null;
		try
		{
			requestItemId = new BigDecimal(id);			
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not parse the providedrequest item id.");
			
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
			resultMap.put("error", "Only Process Instance Initiator can update Request Item.");
		}
		
		// check if request Item Id is available for update or not
		RequestItem requestItem = null;
		try
		{
			requestItem = purchaseOrderDao.getRequestItemDetailsById(requestItemId);
			
			if(requestItem==null)
				throw new Exception();
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not find request Item by id:"+requestItemId);
			
			return resultMap;
		}
		
		// check if the given requestItemId belongs to any requestId which is stored in process instance variables ?
		List<String> requestIdList = (List<String>) runtimeService.getVariable(processId, "requestId");

		BigDecimal requestId = purchaseOrderDao.getRequestIdOfRequestItemId(requestItemId);

		if(!requestIdList.contains(requestId.toString()))
		{
			resultMap.put("error", "Provided request item id does not belong to request id in activiti process.");

			return resultMap;
		}
		
		// update request item
		BigDecimal materialId = null;
		Date requestArrivalDate = null;
		BigDecimal requestWeight = null;
		
		if(requestItemProperties.get("materialId")!=null)
		{
			try
			{
				materialId = new BigDecimal(requestItemProperties.get("materialId"));
				
				// check if given materialId exists or not.
				Material material = purchaseOrderDao.getMaterialDetailsById(materialId);
				if(material==null)
				{
					resultMap.put("error", "Given material Id does not exist.");
					return resultMap;
				}
				
				requestItem.setMaterialId(materialId);
			}
			catch(Exception e)
			{
				if(fieldConstrints.toString().isEmpty())
					fieldConstrints.append("Could not parse materialId");
				else
					fieldConstrints.append(", Could not parse materialId");

				error = true;
			}
		}
		
		if(requestItemProperties.get("requestArrivalDate")!=null)
		{
			try
			{
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				requestArrivalDate = sdf.parse(requestItemProperties.get("requestArrivalDate"));
				requestItem.setRequestArrivalDate(requestArrivalDate);
			}
			catch(Exception e)
			{
				if(fieldConstrints.toString().isEmpty())
					fieldConstrints.append("Could not parse requestArrivalDate");
				else
					fieldConstrints.append(", Could not parse requestArrivalDate");
				
				error = true;
			}
		}
		
		if(requestItemProperties.get("requestWeight")!=null)
		{
			try
			{
				requestWeight = new BigDecimal(requestItemProperties.get("requestWeight")).setScale(2, RoundingMode.CEILING);
				requestItem.setRequestWeight(requestWeight);
			}
			catch(Exception e)
			{
				if(fieldConstrints.toString().isEmpty())
					fieldConstrints.append("Could not parse requestWeight");
				else
					fieldConstrints.append(", Could not parse requestWeight");

				error = true;
			}
		}
		
		if(error)
		{
			resultMap.put("Fields Constraints", fieldConstrints.toString());
			
			return resultMap;
		}
		
		// update request item id
		try
		{
			purchaseOrderDao.updateRequestItem(requestItem);			
			resultMap.put("success", "Request Item updated successfully");
		}
		catch(Exception e)
		{
			resultMap.put("error", "Could not update request item.");
			return resultMap;
		}

		return resultMap;
		
	}
	
	/**
	 * This method validates Request ID.
	 * 
	 * @param id - Request ID.
	 * @return - boolean value
	 */
	public boolean isRequestIdValid(String id)
	{
		BigDecimal requestId = null;
		try
		{
			requestId = new BigDecimal(id);
		}
		catch(Exception e)
		{
			return false;
		}	
		
		Request request = purchaseOrderDao.getRequestDetailsById(requestId);
		
		if(request==null)
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
	 * @param requestId - Request ID
	 * @param userName - HTTP Basic Auth logged in User ID
	 */
	public void getRequestReportInPdf(HttpServletRequest request, 
									  HttpServletResponse response, 
									  BigDecimal requestId,
									  String userName)
	{
		// generate report
		JasperPrint jasperPrint = null;
		
		try 
		{
			byte[] requestReportJasper = purchaseOrderDao.getRequestReportJasperFile();
			byte[] requestReportAltamiraimage = purchaseOrderDao.getRequestReportAltamiraImage();
			byte[] pdf = null;
			
			ByteArrayInputStream reportStream = new ByteArrayInputStream(requestReportJasper);
			Map<String,Object> parameters = new HashMap<String,Object>();
			
			List<Object[]> list = purchaseOrderDao.selectRequestReportDataById(requestId);
			
			Vector requestReportList = new Vector();
			List<Date> dateList = new ArrayList<Date>();
			
			String lastMaterialCode = "";
			int count = 0;
			BigDecimal sumRequestWeight = new BigDecimal(0);
			BigDecimal totalWeight = new BigDecimal(0);
			
			RequestReportData r = new RequestReportData();
			r.setMaterialCode(null);
			r.setMaterialDescription(null);
			r.setMaterialLamination(null);
			r.setMaterialLength(null);
			r.setMaterialThickness(null);
			r.setMaterialTreatment(null);
			r.setMaterialWidth(null);
			r.setRequestDate(null);
			r.setRequestWeight(null);
			
			requestReportList.add(r);
			
			for(Object[] rs : list)
			{
				RequestReportData rr = new RequestReportData();

				String currentMaterialCode = (String)rs[0];

				if(lastMaterialCode.equalsIgnoreCase(currentMaterialCode))
				{
					rr.setRequestWeight(new BigDecimal(rs[7].toString()));
					rr.setRequestDate((Date)rs[8]);
					
					// copy REQUEST_DATE into dateList
					dateList.add((Date)rs[8]);

					System.out.println(new BigDecimal(rs[7].toString()));
					totalWeight = totalWeight.add(new BigDecimal(rs[7].toString()));
					sumRequestWeight = sumRequestWeight.add(new BigDecimal(rs[7].toString()));
					count++;
				}
				else
				{
					rr.setMaterialCode((String)rs[0]);
					rr.setMaterialDescription(StringUtils.defaultIfEmpty((String)rs[1], null));
					rr.setMaterialLamination(StringUtils.defaultIfEmpty((String)rs[2], null));
					rr.setMaterialTreatment(StringUtils.defaultIfEmpty((String)rs[3], null));
					rr.setMaterialThickness(new BigDecimal(StringUtils.defaultIfEmpty(rs[4].toString(), null)));
					rr.setMaterialWidth(new BigDecimal(StringUtils.defaultIfEmpty(rs[5].toString(), null)));
					
					if(rs[6]!=null)
					rr.setMaterialLength(new BigDecimal(StringUtils.defaultIfEmpty(rs[6].toString(), null)));
					
					rr.setRequestWeight(new BigDecimal(StringUtils.defaultIfEmpty(rs[7].toString(), null)));
					rr.setRequestDate((Date)rs[8]);

					// copy REQUEST_DATE into dateList
					dateList.add((Date)rs[8]);
					
					totalWeight = totalWeight.add(new BigDecimal(rs[7].toString()));
					lastMaterialCode = currentMaterialCode;

					if(count!=0)
					{
						RequestReportData addition = new RequestReportData();
						addition.setRequestWeight(sumRequestWeight);

						requestReportList.add(addition);
					}

					sumRequestWeight = new BigDecimal(rs[7].toString());
					count=0;
				}

				requestReportList.add(rr);
			}
			
			if(count>0)
			{
				RequestReportData addition = new RequestReportData();
				addition.setRequestWeight(sumRequestWeight);
				
				requestReportList.add(addition);
			}
			
			BufferedImage imfg=null;
			try 
			{
				InputStream in = new ByteArrayInputStream(requestReportAltamiraimage);
				imfg = ImageIO.read(in);
			} 
			catch (Exception e1) 
			{
				e1.printStackTrace();
			}
			
			Collections.sort(dateList);

			parameters.put("REQUEST_START_DATE", dateList.get(0));
			parameters.put("REQUEST_END_DATE", dateList.get(dateList.size()-1));
			parameters.put("REQUEST_ID", requestId);
			parameters.put("TOTAL_WEIGHT", totalWeight);
			parameters.put("altamira_logo", imfg);
			parameters.put("USERNAME", userName.toUpperCase());
			
			Locale locale = new Locale.Builder().setLanguage("pt").setRegion("BR").build();
			parameters.put("REPORT_LOCALE", locale);
			
			JRDataSource dataSource = new JRBeanCollectionDataSource(requestReportList, false);
			
			jasperPrint = JasperFillManager.fillReport(reportStream, parameters, dataSource);
			
			pdf = JasperExportManager.exportReportToPdf(jasperPrint);
			
			ByteArrayInputStream pdfStream = new ByteArrayInputStream(pdf);
			
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition","inline; filename=PurchaseRequestReport.pdf");
			
			OutputStream os = response.getOutputStream();
			IOUtils.copy(pdfStream, os);
			
		}
		catch (Exception e) 
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
					purchaseOrderDao.insertGeneratedRequestReport(jasperPrint);
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
