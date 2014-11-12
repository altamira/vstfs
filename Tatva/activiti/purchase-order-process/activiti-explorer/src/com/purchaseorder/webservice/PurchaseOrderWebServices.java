package com.purchaseorder.webservice;

import java.math.BigDecimal;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.purchaseorder.dao.PurchaseOrderDao;
import com.purchaseorder.entity.Material;
import com.purchaseorder.entity.Request;
import com.purchaseorder.entity.RequestItem;
import com.purchaseorder.model.RequestItemModel;
import com.purchaseorder.service.steelpurchase.MaterialService;
import com.purchaseorder.service.steelpurchase.PurchaseOrderService;
import com.purchaseorder.service.steelpurchase.PurchasePlanningService;
import com.purchaseorder.service.steelpurchase.QuotationService;
import com.purchaseorder.service.steelpurchase.RequestService;
import com.purchaseorder.service.steelpurchase.SupplierService;

/**
 * 
 * @author PARTH
 *
 */

@Controller
@RequestMapping("/purchaseOrder")
public class PurchaseOrderWebServices 
{
	String RELOGIN = null;
	
	@Autowired
	RuntimeService runtimeService;
	
	@Autowired
	IdentityService identityService;
	
	@Autowired
	PurchaseOrderDao purchaseOrderDao;
	
	@Autowired
	MaterialService materialService;
	
	@Autowired
	RequestService requestService;
	
	@Autowired
	SupplierService supplierService;
	
	@Autowired
	QuotationService quotationService;
	
	@Autowired
	PurchasePlanningService purchasePlanningService;
	
	@Autowired
	PurchaseOrderService purchaseOrderService;
	
	public PurchaseOrderService getPurchaseOrderService() 
	{
		return purchaseOrderService;
	}

	public void setPurchaseOrderService(PurchaseOrderService purchaseOrderService) 
	{
		this.purchaseOrderService = purchaseOrderService;
	}

	public PurchasePlanningService getPurchasePlanningService() 
	{
		return purchasePlanningService;
	}

	public void setPurchasePlanningService(PurchasePlanningService purchasePlanningService) 
	{
		this.purchasePlanningService = purchasePlanningService;
	}

	public SupplierService getSupplierService() 
	{
		return supplierService;
	}

	public void setSupplierService(SupplierService supplierService) 
	{
		this.supplierService = supplierService;
	}

	public RequestService getRequestService() 
	{
		return requestService;
	}

	public void setRequestService(RequestService requestService) 
	{
		this.requestService = requestService;
	}

	public MaterialService getMaterialService() 
	{
		return materialService;
	}

	public void setMaterialService(MaterialService materialService) 
	{
		this.materialService = materialService;
	}

	public IdentityService getIdentityService() 
	{
		return identityService;
	}

	public void setIdentityService(IdentityService identityService) 
	{
		this.identityService = identityService;
	}

	public PurchaseOrderDao getPurchaseOrderDao() 
	{
		return purchaseOrderDao;
	}

	public void setPurchaseOrderDao(PurchaseOrderDao purchaseOrderDao) 
	{
		this.purchaseOrderDao = purchaseOrderDao;
	}

	public RuntimeService getRuntimeService() 
	{
		return runtimeService;
	}

	public void setRuntimeService(RuntimeService runtimeService) 
	{
		this.runtimeService = runtimeService;
	}

/*	@RequestMapping(value = "/{name}", method = RequestMethod.GET)
	public @ResponseBody Map getName(@PathVariable String name)
	{
		System.out.println("Web service Call");
		String[] str = name.split(",");
		
		Map<String, String> map = new LinkedHashMap<String, String>();
		int i=1;
		for(String string : str)
		{
			map.put("name"+i, string);
			i++;
		}
		
		return map;
	}*/
	
	/**
	 * This method serves as a webservice method handling URI '/'
	 * @return
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public @ResponseBody String getDefaultName() 
	{
		System.out.println("Web service Call");
		return "No names...";
 
	}
	
/*	@RequestMapping(value = "/createProcess", method = RequestMethod.POST)
	public @ResponseBody String createProcessInstance(@RequestParam("processName")String processName)
	{
		ProcessInstance instance = runtimeService.startProcessInstanceById(processName);
		
		return instance.getId();
	}*/
	
	@RequestMapping(value = "/createSteelPurchasingInstance", method = RequestMethod.GET)
	public ModelAndView createSteelPurchasingInstance(HttpServletRequest request, HttpServletResponse response)
	{
		String sessionId = request.getSession().getId();
		ProcessInstance processInstance = null;
		
		try
		{
			identityService.setAuthenticatedUserId("parth");
			processInstance = runtimeService.startProcessInstanceByKey("SteelPurchasingProcess");
		}
		finally
		{
			identityService.setAuthenticatedUserId(null);
		}
		
		String processInstanceId = processInstance.getId();
		
		//Map<String, String> map = new HashMap<String, String>();
		//map.put("sessionId", sessionId);
		//map.put("processInstanceId", processInstanceId);
		
		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("sessionId", sessionId);
		modelMap.addAttribute("processInstanceId", processInstanceId);
		
		//request.setAttribute("requestItemList", new ArrayList<RequestItem>());
		request.getSession().setAttribute("requestItemList", new ArrayList<RequestItemModel>());
		request.getSession().setAttribute("sessionId", sessionId);
		request.getSession().setAttribute("processInstanceId", processInstanceId);
		
		return new ModelAndView("purchaserequest",modelMap);
	}
	
	@RequestMapping(value = "/request", method = RequestMethod.POST)
	public String addPurchaseRequestItem(@RequestParam("materialCode")String materialCode,
									 	 @RequestParam("requestArrivalDate")String requestArrivalDate,
									 	 @RequestParam("requestWeight")BigDecimal request_weight,
									 	 HttpServletRequest request)
	{
		Material material = purchaseOrderDao.getMaterialDetailsByCode(materialCode);
		
		RequestItemModel requestItem = new RequestItemModel();
		requestItem.setMaterialId(material.getMaterialId());
		requestItem.setMaterialCode(material.getMaterialCode());
		requestItem.setMaterialLamination(material.getMaterialLamination());
		requestItem.setMaterialTreatment(material.getMaterialTreatment());
		requestItem.setMaterialThickness(material.getMaterialThickness());
		requestItem.setMaterialWidth(material.getMaterialWidth());
		requestItem.setMaterialLength(material.getMaterialLength());
		requestItem.setRequestArrivalDate(requestArrivalDate);
		requestItem.setRequestWeight(request_weight);
		
		List<RequestItemModel> list = (List<RequestItemModel>) request.getSession().getAttribute("requestItemList");
		list.add(requestItem);
		
		request.getSession().setAttribute("requestItemList", list);
		
		return "purchaserequest";
	}

	@Transactional
	@RequestMapping(value = "/submit_request", method = RequestMethod.POST)
	public String submitPurchaseRequest(HttpServletRequest request, @RequestParam("next_step")String nextStep)
	{
		String processInstanceId = (String)request.getSession().getAttribute("processInstanceId");
		List<RequestItemModel> list = (List<RequestItemModel>) request.getSession().getAttribute("requestItemList");
		
		Request purchaseRequest = new Request();
		purchaseRequest.setRequestDate(new Date());
		purchaseRequest.setRequestCreator("PARTH");
		
		BigDecimal purchaseRequestId = purchaseOrderDao.insertRequest(purchaseRequest);
		
		for(RequestItemModel requestItem : list)
		{
			RequestItem purchaseReqItem = new RequestItem();
			purchaseReqItem.setRequestId(purchaseRequestId);
			purchaseReqItem.setMaterialId(requestItem.getMaterialId());
			
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
			Date reqArrivalDate = null;
			try 
			{
				reqArrivalDate = sdf.parse(requestItem.getRequestArrivalDate());
			} 
			catch (ParseException e) 
			{
				e.printStackTrace();
			}
			
			purchaseReqItem.setRequestArrivalDate(reqArrivalDate);
			purchaseReqItem.setRequestWeight(requestItem.getRequestWeight());
			
			purchaseOrderDao.insertRequestItem(purchaseReqItem);
		}
		
		// store Request Id in activiti process instance variables.
		List<String> requestIdList = (List<String>) runtimeService.getVariable(processInstanceId, "requestIdList");
		
		if(requestIdList != null)
		{
			requestIdList.add(purchaseRequestId.toString());
			runtimeService.setVariable(processInstanceId, "requestIdList", requestIdList);
		}
		else
		{
			requestIdList = new ArrayList<String>();
			requestIdList.add(purchaseRequestId.toString());
			runtimeService.setVariable(processInstanceId, "requestIdList", requestIdList);
		}
		
		if(nextStep.equalsIgnoreCase("request"))
		{
			request.getSession().setAttribute("requestItemList",new ArrayList<RequestItemModel>());
			return "purchaserequest";
		}
		else
		{
			return "purchasequotation";			
		}
		
	}
	
/*
 ***********************************************************************************
 * New Methods that don't uses Sessions & makes use of HTTP BASIC AUTHENTICATION
 ***********************************************************************************
 */
	
	@RequestMapping(value="/createProcessInstance", method=RequestMethod.POST)
	public @ResponseBody Object createSteelPurchasingInstance(Principal principal)
	{
		String name = null;
		ProcessInstance processInstance = null;
		Map<String, String> map = new HashMap<String, String>();
		
		try
		{
			name = principal.getName();			
		}
		catch (NullPointerException e) 
		{
			// Do Nothing
		}
		
		if(name!=null)
		{
			try
			{
				identityService.setAuthenticatedUserId(principal.getName());
				processInstance = runtimeService.startProcessInstanceByKey("SteelPurchasingProcess");
				
				map.put("processId", processInstance.getId());
				map.put("processInitiator", name);
			}
			finally
			{
				identityService.setAuthenticatedUserId(null);
			}
		}
		else
		{
			map.put("error", "HTTP 401 Unauthorized");
			map.put("description", "Access Denied: Authorization required");
			
			return map;
		}
		
		return map;
	}
	
	@RequestMapping(value="/welcome", method = RequestMethod.GET)
	public String printWelcome(ModelMap model, Principal principal) 
	{
		String name = null;
		
		try
		{
			name = principal.getName();			
		}
		catch (NullPointerException e) 
		{
			Map<String, String> map = new HashMap<String, String>();
			map.put("error", "HTTP 401 Unauthorized");
			map.put("description", "Access Denied: Authorization required");
			
			return null;
		}
		
		model.addAttribute("username", name);

		return "hello";
	}
 
	@RequestMapping(value="/login")
	public String login() 
	{
//		if(RELOGIN!=null && RELOGIN.equals("Y")) a way that will ne
//		{
//			RELOGIN = null;
//			throw new BadCredentialsException("Bad Credentials");
//		}
//		
//		return "forward:welcome";
		
		return "login";
 
	}
	
	@RequestMapping(value="/relogin")
	public String relogin() 
	{
//		RELOGIN = "Y";
//		return "redirect:login";
		
		return "login";
	}
	
	@RequestMapping(value="/addMaterial", method=RequestMethod.POST)
	public @ResponseBody Object addMaterial(@RequestBody String jsonObject,
											Principal principal)
	{
		return materialService.addMaterial(jsonObject, principal);
	}
	
	@RequestMapping(value="/material", method=RequestMethod.GET)
	public @ResponseBody Object getMaterial(@RequestParam(value="id", required=false) String materialId,
											@RequestParam(value="code", required=false) String materialCode,
											Principal principal)
	{
		if(materialId!=null)
		{
			return materialService.getMaterialById(materialId, principal);			
		}
		else if(materialCode!=null)
		{
			return materialService.getMaterialByCode(materialCode, principal);
		}
		else
		{
			return null;
		}
	}
	
	@RequestMapping(value="/getMaterials", method=RequestMethod.GET)
	public @ResponseBody Object getMaterials(Principal principal)
	{
		return materialService.getAllMaterials(principal);
	}
	
	@RequestMapping(value="/updateMaterial/{id}", method=RequestMethod.PUT)
	public @ResponseBody Object updateMaterial(@RequestBody String jsonObject,
											   @PathVariable String id,
											   Principal principal)
	{
		return materialService.updateMaterial(jsonObject, id,principal);
	}
	
	@RequestMapping(value="/deleteMaterial/{id}", method=RequestMethod.DELETE)
	public @ResponseBody Object deleteMaterial(@PathVariable String id,
											   Principal principal)
	{
		return materialService.deleteMaterial(id, principal);
	}
	
	@RequestMapping(value="/addRequest", method=RequestMethod.POST)
	public @ResponseBody Object addRequest(@RequestBody String jsonObject,
										   Principal principal)
	{
		return requestService.addRequest(jsonObject, principal);
	}
	
	@RequestMapping(value="/getRequest/{id}", method=RequestMethod.GET)
	public @ResponseBody Object getRequest(@PathVariable String id,
										   Principal principal)
	{
		return requestService.getRequest(id, principal);
	}
	
	@RequestMapping(value="/getRequests", method=RequestMethod.GET)
	public @ResponseBody Object getRequests(Principal principal)
	{
		return requestService.getAllRequests(principal);
	}
	
	@RequestMapping(value="/deleteRequest/{processId}/{requestId}", method=RequestMethod.DELETE)
	public @ResponseBody Object deleteRequest(@PathVariable String processId,
											  @PathVariable String requestId,
											  Principal principal)
	{
		return requestService.deleteRequest(processId,requestId, principal);
	}
	
	@RequestMapping(value="/addRequestItem", method=RequestMethod.POST)
	public @ResponseBody Object addRequestItem(@RequestBody String jsonObject,
			   								   Principal principal)
	{
		return requestService.addRequestItem(jsonObject, principal);
	}
	
	@RequestMapping(value="/getRequestItem/{requestItemId}", method=RequestMethod.GET)
	public @ResponseBody Object getRequestItem(@PathVariable String requestItemId,
			   							   	   Principal principal)
	{
		return requestService.getRequestItem(requestItemId, principal);
	}
	
	@RequestMapping(value="/getRequestItems", method=RequestMethod.GET)
	public @ResponseBody Object getRequestItems(Principal principal)
	{
		return requestService.getAllRequestItems(principal);
	}
	
	@RequestMapping(value="/deleteRequestItem/{processId}/{requestItemId}", method=RequestMethod.DELETE)
	public @ResponseBody Object deleteRequestItem(@PathVariable String processId,
												  @PathVariable String requestItemId,
												  Principal principal)
	{
		return requestService.deleteRequestItem(processId, requestItemId, principal);
	}
	
	@RequestMapping(value="/updateRequestItem/{processId}/{requestItemId}", method=RequestMethod.PUT)
	public @ResponseBody Object updateRequestItem(@RequestBody String jsonObject,
												  @PathVariable String processId,
												  @PathVariable String requestItemId,
												  Principal principal)
	{
		//return materialService.updateMaterial(jsonObject, id,principal);
		return requestService.updateRequestItem(jsonObject, processId, requestItemId,principal);
	}
	
	@RequestMapping(value="/addSupplier", method=RequestMethod.POST)
	public @ResponseBody Object addSupplier(@RequestBody String jsonObject,
											Principal principal)
	{
		return supplierService.addSupplier(jsonObject, principal);
	}
	
	@RequestMapping(value="/getSupplier/{supplierId}", method=RequestMethod.GET)
	public @ResponseBody Object getSupplier(@PathVariable String supplierId,
											Principal principal)
	{
		return supplierService.getSupplier(supplierId, principal);
	}
	
	@RequestMapping(value="/getSuppliers", method=RequestMethod.GET)
	public @ResponseBody Object getSuppliers(Principal principal)
	{
		return supplierService.getAllSuppliers(principal);
	}
	
	@RequestMapping(value="/updateSupplier/{supplierId}", method=RequestMethod.PUT)
	public @ResponseBody Object updateSupplier(@RequestBody String jsonObject,
											   @PathVariable String supplierId,
											   Principal principal)
	{
		return supplierService.updateSupplier(jsonObject, supplierId,principal);
	}
	
	@RequestMapping(value="/deleteSupplier/{supplierId}", method=RequestMethod.DELETE)
	public @ResponseBody Object deleteSupplier(@PathVariable String supplierId,
											   Principal principal)
	{
		return supplierService.deleteSupplier(supplierId, principal);
	}
	
	@RequestMapping(value="/addQuotation", method=RequestMethod.POST)
	public @ResponseBody Object addQuotation(@RequestBody String jsonObject,
											 Principal principal)
	{
		return quotationService.addQuotation(jsonObject, principal);
	}
	
	@RequestMapping(value="/getQuotation/{id}", method=RequestMethod.GET)
	public @ResponseBody Object getQuotation(@PathVariable String id,
										     Principal principal)
	{
		return quotationService.getQuotation(id, principal);
	}
	
	@RequestMapping(value="/getQuotations", method=RequestMethod.GET)
	public @ResponseBody Object getQuotations(Principal principal)
	{
		return quotationService.getAllQuotations(principal);
	}
	
	@RequestMapping(value="/deleteQuotation/{processId}/{quotationId}", method=RequestMethod.DELETE)
	public @ResponseBody Object deleteQuotation(@PathVariable String processId,
											    @PathVariable String quotationId,
											    Principal principal)
	{
		return quotationService.deleteQuotation(processId,quotationId, principal);
	}
	
	@RequestMapping(value="/addQuotationRequest", method=RequestMethod.POST)
	public @ResponseBody Object addQuotationRequest(@RequestBody String jsonObject,
			   								   		Principal principal)
	{
		return quotationService.addQuotationRequest(jsonObject, principal);
	}
	
	@RequestMapping(value="/getQuotationRequest/{quotationRequestId}", method=RequestMethod.GET)
	public @ResponseBody Object getQuotationRequest(@PathVariable String quotationRequestId,
			   							   	   		Principal principal)
	{
		return quotationService.getQuotationRequest(quotationRequestId, principal);
	}
	
	@RequestMapping(value="/getQuotationRequests", method=RequestMethod.GET)
	public @ResponseBody Object getQuotationRequests(Principal principal)
	{
		return quotationService.getAllQuotationRequests(principal);
	}
	
	@RequestMapping(value="/deleteQuotationRequest/{processId}/{quotationRequestId}", method=RequestMethod.DELETE)
	public @ResponseBody Object deleteQuotationRequest(@PathVariable String processId,
												  	   @PathVariable String quotationRequestId,
												  	   Principal principal)
	{
		return quotationService.deleteQuotationRequest(processId, quotationRequestId, principal);
	}
	
	@RequestMapping(value="/updateQuotationRequest/{processId}/{quotationRequestId}", method=RequestMethod.PUT)
	public @ResponseBody Object updateQuotationRequest(@RequestBody String jsonObject,
												  	   @PathVariable String processId,
												  	   @PathVariable String quotationRequestId,
												  	   Principal principal)
	{
		return quotationService.updateQuotationRequest(jsonObject, processId, quotationRequestId,principal);
	}
	
	@RequestMapping(value="/addQuotationItem", method=RequestMethod.POST)
	public @ResponseBody Object addQuotationItem(@RequestBody String jsonObject,
			   								   	 Principal principal)
	{
		return quotationService.addQuotationItem(jsonObject, principal);
	}
	
	@RequestMapping(value="/getQuotationItem/{quotationItemId}", method=RequestMethod.GET)
	public @ResponseBody Object getQuotationItem(@PathVariable String quotationItemId,
			   							   	   	 Principal principal)
	{
		return quotationService.getQuotationItem(quotationItemId, principal);
	}
	
	@RequestMapping(value="/getQuotationItems", method=RequestMethod.GET)
	public @ResponseBody Object getQuotationItems(Principal principal)
	{
		return quotationService.getAllQuotationItems(principal);
	}
	
	@RequestMapping(value="/deleteQuotationItem/{processId}/{quotationItemId}", method=RequestMethod.DELETE)
	public @ResponseBody Object deleteQuotationItem(@PathVariable String processId,
												  	@PathVariable String quotationItemId,
												  	Principal principal)
	{
		return quotationService.deleteQuotationItem(processId, quotationItemId, principal);
	}
	
	@RequestMapping(value="/updateQuotationItem/{processId}/{quotationItemId}", method=RequestMethod.PUT)
	public @ResponseBody Object updateQuotationItem(@RequestBody String jsonObject,
												    @PathVariable String processId,
												    @PathVariable String quotationItemId,
												    Principal principal)
	{
		return quotationService.updateQuotationItem(jsonObject, processId, quotationItemId,principal);
	}
	
	@RequestMapping(value="/addQuotationItemQuote", method=RequestMethod.POST)
	public @ResponseBody Object addQuotationItemQuote(@RequestBody String jsonObject,
			   								   	 	  Principal principal)
	{
		return quotationService.addQuotationItemQuote(jsonObject, principal);
	}
	
	@RequestMapping(value="/getQuotationItemQuotes", method=RequestMethod.GET)
	public @ResponseBody Object getQuotationItemQuotes(Principal principal)
	{
		return quotationService.getAllQuotationItemQuotes(principal);
	}
	
	@RequestMapping(value="/getQuotationItemQuote/{quotationItemQuoteId}", method=RequestMethod.GET)
	public @ResponseBody Object getQuotationItemQuote(@PathVariable String quotationItemQuoteId,
			   							   	   	 	  Principal principal)
	{
		return quotationService.getQuotationItemQuote(quotationItemQuoteId, principal);
	}
	
	
	@RequestMapping(value="/deleteQuotationItemQuote/{processId}/{quotationItemQuoteId}", method=RequestMethod.DELETE)
	public @ResponseBody Object deleteQuotationItemQuote(@PathVariable String processId,
														 @PathVariable String quotationItemQuoteId,
														 Principal principal)
	{
		return quotationService.deleteQuotationItemQuote(processId, quotationItemQuoteId, principal);
	}
	
	
	
	@RequestMapping(value="/updateQuotationItemQuote/{processId}/{quotationItemQuoteId}", method=RequestMethod.PUT)
	public @ResponseBody Object updateQuotationItemQuote(@RequestBody String jsonObject,
												    	 @PathVariable String processId,
												    	 @PathVariable String quotationItemQuoteId,
												    	 Principal principal)
	{
		return quotationService.updateQuotationItemQuote(jsonObject, processId, quotationItemQuoteId,principal);
	}
	
	
	
	
	@RequestMapping(value="/addPurchasePlanning", method=RequestMethod.POST)
	public @ResponseBody Object addPurchasePlanning(@RequestBody String jsonObject,
			   								   	 	Principal principal)
	{
		return purchasePlanningService.addPurchasePlanning(jsonObject, principal);
	}
	
	@RequestMapping(value="/getPurchasePlanning/{planningId}", method=RequestMethod.GET)
	public @ResponseBody Object getPurchasePlanning(@PathVariable String planningId,
			   							   	   	 	Principal principal)
	{
		return purchasePlanningService.getPurchasePlanning(planningId, principal);
	}
	
	@RequestMapping(value="/getPurchasePlannings", method=RequestMethod.GET)
	public @ResponseBody Object getPurchasePlannings(Principal principal)
	{
		return purchasePlanningService.getAllPurchasePlannings(principal);
	}
	
	@RequestMapping(value="/deletePurchasePlanning/{processId}/{planningId}", method=RequestMethod.DELETE)
	public @ResponseBody Object deletePurchasePlanning(@PathVariable String processId,
												  	   @PathVariable String planningId,
												  	   Principal principal)
	{
		return purchasePlanningService.deletePurchasePlanning(processId, planningId, principal);
	}
	
	@RequestMapping(value="/updatePurchasePlanning/{processId}/{planningId}", method=RequestMethod.PUT)
	public @ResponseBody Object updatePurchasePlanning(@RequestBody String jsonObject,
												       @PathVariable String processId,
												       @PathVariable String planningId,
												       Principal principal)
	{
		return purchasePlanningService.updatePurchasePlanning(jsonObject, processId, planningId,principal);
	}
	
	@RequestMapping(value="/addPurchasePlanningItem", method=RequestMethod.POST)
	public @ResponseBody Object addPurchasePlanningItem(@RequestBody String jsonObject,
			   								   	 		Principal principal)
	{
		return purchasePlanningService.addPurchasePlanningItem(jsonObject, principal);
	}
	
	@RequestMapping(value="/getPurchasePlanningItem/{planningItemId}", method=RequestMethod.GET)
	public @ResponseBody Object getPurchasePlanningItem(@PathVariable String planningItemId,
			   							   	   	 		Principal principal)
	{
		return purchasePlanningService.getPurchasePlanningItem(planningItemId, principal);
	}
	
	@RequestMapping(value="/getPurchasePlanningItems", method=RequestMethod.GET)
	public @ResponseBody Object getPurchasePlanningItems(Principal principal)
	{
		return purchasePlanningService.getAllPurchasePlanningItems(principal);
	}
	
	@RequestMapping(value="/deletePurchasePlanningItem/{processId}/{planningItemId}", method=RequestMethod.DELETE)
	public @ResponseBody Object deletePurchasePlanningItem(@PathVariable String processId,
												  	  	   @PathVariable String planningItemId,
												  	  	   Principal principal)
	{
		return purchasePlanningService.deletePurchasePlanningItem(processId, planningItemId, principal);
	}
	
	@RequestMapping(value="/updatePurchasePlanningItem/{processId}/{planningItemId}", method=RequestMethod.PUT)
	public @ResponseBody Object updatePurchasePlanningItem(@RequestBody String jsonObject,
												       	   @PathVariable String processId,
												       	   @PathVariable String planningItemId,
												       	   Principal principal)
	{
		return purchasePlanningService.updatePurchasePlanningItem(jsonObject, processId, planningItemId,principal);
	}
	
	@RequestMapping(value="/addPurchaseOrder", method=RequestMethod.POST)
	public @ResponseBody Object addPurchaseOrder(@RequestBody String jsonObject,
			   								   	 Principal principal)
	{
		return purchaseOrderService.addPurchaseOrder(jsonObject, principal);
	}
	
	@RequestMapping(value="/order", method=RequestMethod.GET)
	public @ResponseBody Object getPurchaseOrder(@RequestParam("id") String purchaseOrderId,
												 Principal principal)
	{
		return purchaseOrderService.getPurchaseOrder(purchaseOrderId, principal);
	}
	
	@RequestMapping(value="/getPurchaseOrders", method=RequestMethod.GET)
	public @ResponseBody Object getPurchaseOrders(Principal principal)
	{
		return purchaseOrderService.getAllPurchaseOrders(principal);
	}
	
	@RequestMapping(value="/deletePurchaseOrder/{processId}/{purchaseOrderId}", method=RequestMethod.DELETE)
	public @ResponseBody Object deletePurchaseOrder(@PathVariable String processId,
												  	@PathVariable String purchaseOrderId,
												  	Principal principal)
	{
		return purchaseOrderService.deletePurchaseOrder(processId, purchaseOrderId, principal);
	}
	
	@RequestMapping(value="/updatePurchaseOrder/{processId}/{purchaseOrderId}", method=RequestMethod.PUT)
	public @ResponseBody Object updatePurchaseOrder(@RequestBody String jsonObject,
												    @PathVariable String processId,
												    @PathVariable String purchaseOrderId,
												    Principal principal)
	{
		return purchaseOrderService.updatePurchaseOrder(jsonObject, processId, purchaseOrderId,principal);
	}
	
	@RequestMapping(value="/addPurchaseOrderItem", method=RequestMethod.POST)
	public @ResponseBody Object addPurchaseOrderItem(@RequestBody String jsonObject,
			   								   	 	 Principal principal)
	{
		return purchaseOrderService.addPurchaseOrderItem(jsonObject, principal);
	}
	
	@RequestMapping(value="/orderItem", method=RequestMethod.GET)
	public @ResponseBody Object getPurchaseOrderItem(@RequestParam("id") String purchaseOrderItemId,
												 	 Principal principal)
	{
		return purchaseOrderService.getPurchaseOrderItem(purchaseOrderItemId, principal);
	}
	
	@RequestMapping(value="/getPurchaseOrderItems", method=RequestMethod.GET)
	public @ResponseBody Object getPurchaseOrderItems(Principal principal)
	{
		return purchaseOrderService.getAllPurchaseOrderItems(principal);
	}
	
	@RequestMapping(value="/deletePurchaseOrderItem/{processId}/{purchaseOrderItemId}", method=RequestMethod.DELETE)
	public @ResponseBody Object deletePurchaseOrderItem(@PathVariable String processId,
												  		@PathVariable String purchaseOrderItemId,
												  		Principal principal)
	{
		return purchaseOrderService.deletePurchaseOrderItem(processId, purchaseOrderItemId, principal);
	}
	
	@RequestMapping(value="/updatePurchaseOrderItem/{processId}/{purchaseOrderItemId}", method=RequestMethod.PUT)
	public @ResponseBody Object updatePurchaseOrderItem(@RequestBody String jsonObject,
												    	@PathVariable String processId,
												    	@PathVariable String purchaseOrderItemId,
												    	Principal principal)
	{
		return purchaseOrderService.updatePurchaseOrderItem(jsonObject, processId, purchaseOrderItemId,principal);
	}
	
	@RequestMapping(value="/test/priceList", method=RequestMethod.GET)
	public @ResponseBody Object getPriceList(@RequestParam(value="lamination", required=false) String lamination,
											 @RequestParam(value="treatment", required=false) String treatment,
											 @RequestParam(value="thickness", required=false) String thickness)
	{
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		resultMap.put("pricelist", "BL");
		
		List<Map> materialList = new ArrayList<Map>();
		
		Map<String, String> material = new HashMap<String, String>();
		material.put("code", "WBO00233");
		material.put("description", "AÇO LAMINADO FINA FRIO 2,65mm");
		material.put("baseprice", "2368");
		material.put("averageprice", new BigDecimal(thickness).multiply(new BigDecimal(1000)).toString());
		material.put("tax", "18");
		
		materialList.add(material);
		
		resultMap.put("materials", materialList);
		
		return resultMap;
	}
	
	@RequestMapping(value="/test/current", method=RequestMethod.GET)
	public @ResponseBody Object getCurrentPriceList()
	{
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		resultMap.put("pricelist", "BL");
		
		return resultMap;
	}
	
	@RequestMapping(value="/temp/addPurchaseOrder/{planningId}", method=RequestMethod.POST)
	public @ResponseBody Object addPurchaseOrders(@PathVariable String planningId,
			   								   	  Principal principal)
	{
		return purchaseOrderService.addPurchaseOrders(planningId, principal);
	}
	
/*
******************
* Report Methods
******************
*/	
	
	@RequestMapping(value="/report/request/{id}")
	public @ResponseBody Object getRequestReportInPdf(HttpServletRequest request, 
									  				  HttpServletResponse response,
									  				  Principal principal,
									  				  @PathVariable String id)
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
		
		// check if requestid is valid or not
		boolean valid = requestService.isRequestIdValid(id);
		
		BigDecimal requestId = new BigDecimal(id);
		
		if(valid)
		{
			requestService.getRequestReportInPdf(request,
												 response,
												 requestId,
												 name);
			
			return null;
		}
		else
		{
			resultMap.put("error", "Request ID not found.");
			return resultMap;
		}
	}
	
	@RequestMapping(value="/report/quotation/{id}")
	public @ResponseBody Object getQuotationReportInPdf(HttpServletRequest request, 
			  											HttpServletResponse response,
			  											Principal principal,
			  											@PathVariable String id)
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
		
		// check if quotationid is valid or not
		boolean valid = quotationService.isQuotationIdValid(id);
		
		BigDecimal quotationId = new BigDecimal(id);
		
		if(valid)
		{
			quotationService.getQuotationReportInPdf(request, 
													 response, 
													 quotationId,
													 name);
			
			return null;
		}
		else
		{
			resultMap.put("error", "Quotation ID not found.");
			return resultMap;
		}
	}
	
	@RequestMapping(value="/report/planning/{id}")
	public @ResponseBody Object getPlanningReportInPdf(HttpServletRequest request, 
			  						   				   HttpServletResponse response,
			  						   				   Principal principal,
			  						   				   @PathVariable String id)
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
		
		// check if planningId is valid or not
		boolean valid = purchasePlanningService.isPlanningIdValid(id);
		
		BigDecimal planningId = new BigDecimal(id);
		
		if(valid)
		{
			purchasePlanningService.getPlanningReportInPdf(request, 
													 	   response, 
													 	   planningId,
													 	   name);
			
			return null;
		}
		else
		{
			resultMap.put("error", "Purchase Planning ID not found.");
			return resultMap;
		}
	}
	
	@RequestMapping(value="/report/order/{id}")
	public @ResponseBody Object getPurchaseOrderReportInPdf(HttpServletRequest request, 
			  						   						HttpServletResponse response,
			  						   						Principal principal,
			  						   						@PathVariable String id)
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
		
		// check if purchaseOrderId is valid or not
		boolean valid = purchaseOrderService.isPurchaseOrderIdValid(id);
		
		BigDecimal purchaseOrderId = new BigDecimal(id);
		
		if(valid)
		{
			purchaseOrderService.getPurchaseOrderReportInPdf(request, 
															 response, 
															 purchaseOrderId,
															 principal.getName());
			
			return null;
		}
		else
		{
			resultMap.put("error", "Purchase Order ID not found.");
			return resultMap;
		}
	}
	
	
/*	@RequestMapping(value="/pdfmethod/{requestId}", produces="application/pdf")
	public void getPdf(HttpServletRequest request, HttpServletResponse response, @PathVariable String requestId)
	{
		response.setContentType("application/pdf");
		File file = new File("D:/DANFE-Report.pdf");
		
		FileInputStream fis=null;
		try 
		{
			fis = new FileInputStream(file);
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		
		OutputStream os;
		try 
		{
			os = response.getOutputStream();
			IOUtils.copy(fis, os);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}*/
}
