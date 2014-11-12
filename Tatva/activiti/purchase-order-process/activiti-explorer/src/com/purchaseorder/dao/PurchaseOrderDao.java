package com.purchaseorder.dao;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import net.sf.jasperreports.engine.JasperPrint;
import oracle.jdbc.OraclePreparedStatement;
import oracle.xdb.XMLType;

import org.apache.commons.dbcp.DelegatingConnection;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import validator.Xsd;

import com.purchaseorder.entity.DanfeLog;
import com.purchaseorder.entity.Material;
import com.purchaseorder.entity.PlanningReportLog;
import com.purchaseorder.entity.PurchaseOrder;
import com.purchaseorder.entity.PurchaseOrderItem;
import com.purchaseorder.entity.PurchaseOrderReportLog;
import com.purchaseorder.entity.PurchasePlanning;
import com.purchaseorder.entity.PurchasePlanningItem;
import com.purchaseorder.entity.PurchaseXsd;
import com.purchaseorder.entity.Quotation;
import com.purchaseorder.entity.QuotationItem;
import com.purchaseorder.entity.QuotationItemQuote;
import com.purchaseorder.entity.QuotationReportLog;
import com.purchaseorder.entity.QuotationRequest;
import com.purchaseorder.entity.Request;
import com.purchaseorder.entity.RequestItem;
import com.purchaseorder.entity.RequestReportLog;
import com.purchaseorder.entity.Supplier;
import com.purchaseorder.model.Mail;

/**
 * 
 * @author PARTH
 *
 */
public class PurchaseOrderDao 
{
	@Autowired
	@PersistenceContext(unitName="pum")
	private EntityManager entityManager;
	
	/**
	 * This method returns list of xmls stored under PROCNFE table.
	 * @return
	 */
	public List<String> listXmls()
	{
		List<String> xmlList = (List<String>)entityManager.createQuery("SELECT p.xmlName FROM Procnfe p").getResultList();
		
		return xmlList;
	}
	
	/**
	 * 
	 * @param xmlName - Name of xml
	 * @return - Content of xml file
	 */
	public String getXmlContent(String xmlName)
	{
		String xmlContent = (String) entityManager.createQuery("SELECT p.xmlcontent FROM Procnfe p WHERE p.xmlName = :xml_name")
										 .setParameter("xml_name", xmlName).getSingleResult();
		
		return xmlContent;
	}
	
	/**
	 * This method returns byte[] of latest .jasper file content for DANFE report
	 * 
	 * @return - byte[]
	 * @throws SQLException
	 */
	public byte[] getDanfeJasperFile() throws SQLException
	{
		Blob tempBlob = (Blob) entityManager.createNativeQuery("SELECT JASPER_FILE FROM DANFE_REPORT WHERE ROWNUM = 1 ORDER BY REPORT_ID DESC")
												   .getSingleResult();
		
		return tempBlob.getBytes(1, (int)tempBlob.length());
	}
	
	/**
	 * This method returns byte[] of latest Altamira Image content for DANFE report
	 * 
	 * @return - byte[]
	 * @throws SQLException
	 */
	public byte[] getDanfeAltamiraImage() throws SQLException
	{
		Blob tempBlob = (Blob) entityManager.createNativeQuery("SELECT ALTAMIRA_LOGO FROM DANFE_REPORT WHERE ROWNUM = 1")
				   			   						.getSingleResult();
		
		return tempBlob.getBytes(1, (int)tempBlob.length());
	}
	
	/**
	 * Inserts generated DANFE report in database
	 * 
	 * @param print
	 * @return
	 */
	public boolean insertGeneratedReport(JasperPrint print)
	{
		byte[] bArray = null;
		
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			
			oos.writeObject(print);
			oos.close();
			baos.close();
			
			bArray = baos.toByteArray();
		}
		catch(Exception e)
		{
			System.out.println("Error converting JasperPrint object to byte[] array");
			e.printStackTrace();
			return false;
		}
		
		try
		{
			DanfeLog log = new DanfeLog();
			log.setReportInstance(bArray);

			entityManager.persist(log);
		}
		catch(Exception e)
		{
			System.out.println("Error while inserting generated report in database.");
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	/**
	 * 
	 * This method returns list of xsd stored under PURCHASE_XSD table. 
	 * @return
	 */
	public List<Xsd> getAllXsd()
	{
		List<PurchaseXsd> list = (List<PurchaseXsd>)entityManager.createQuery("SELECT p FROM PurchaseXsd p").getResultList();
		
		List<Xsd> xsdList = new ArrayList<Xsd>();
		
		for(PurchaseXsd xsd : list)
		{
			Xsd tempXsd = new Xsd();
			
			// set XSD_NAME
			tempXsd.setXsdName(xsd.getPurchaseXsdId().getXsdName());
			
			// set XSD_VERSION
			tempXsd.setXsdVersion(xsd.getPurchaseXsdId().getXsdVersion());
			
			// set XSD_CONTENT
			//Blob tempBlob = rs.getBlob("XSD_CONTENT");
			//byte[] bdata = tempBlob.getBytes(1, (int) tempBlob.length());
			byte[] bdata = xsd.getXsdContent();
			
			tempXsd.setXsdContent(new String(bdata));
			
			// set PKG_REVISION
			tempXsd.setPkgRevision(xsd.getPurchaseXsdId().getPkgRevision());
			
			// set ROOT
			tempXsd.setRootElem(xsd.getRoot());
			
			xsdList.add(tempXsd);
		}
		
		return xsdList;
	}
	
	public boolean isMailAlreadyStored(String messageId,
			   						   String senderId,
			   						   String receivedDate)
	{
		String result = null;
		try
		{
			result = (String) entityManager.createNativeQuery("SELECT :x FROM MAILBOX A WHERE A.MESSAGE_ID = :message_id AND A.RECEIVED_DATE = TO_DATE(:date,'dd/mm/yyyy HH24:mi:ss') AND A.SENDER_ID = :sender_id")
										   .setParameter("x", "x")
										   .setParameter("message_id", messageId)
										   .setParameter("date", receivedDate)
										   .setParameter("sender_id", senderId)
										   .getSingleResult();
		}
		catch(NoResultException nre)
		{
			return false;
		}
		
		if(result.equalsIgnoreCase("x"))
		{
			return true;
		}
		return false;
	}
	
	public void storeMail(String messageId,
						  String senderId,
						  String receivedDate,
						  ByteArrayOutputStream mailStream) throws Exception
	{
		entityManager.createNativeQuery("INSERT INTO MAILBOX ( MESSAGE_ID, SENDER_ID, RECEIVED_DATE, MAIL_MESSAGE ) VALUES ( :message_id, :senderId, TO_DATE(:received_date,'dd/mm/yyyy HH24:mi:ss'), :mail_stream  )")
					 .setParameter("message_id", messageId)
					 .setParameter("senderId", senderId)
					 .setParameter("received_date", receivedDate)
					 .setParameter("mail_stream", mailStream.toByteArray())
					 .executeUpdate();
	}
	
	public List<Mail> selectUnProcessedMails()
	{
		TypedQuery<Object[]> query = entityManager.createQuery("SELECT m.mailBoxId.messageId, m.mailBoxId.senderId, m.mailBoxId.receivedDate, m.mailMessage FROM MailBox m WHERE m.isProcessed = :is_processed", Object[].class)
					 							  .setParameter("is_processed", "N");
		
		List<Object[]> results = query.getResultList();
			
		List<Mail> mailList = new ArrayList<Mail>();
		
		for(Object[] result : results)
		{
			try 
			{
				Mail tempMail = new Mail();
				
				// set MESSAGE_ID
				tempMail.setMessageId((String)result[0]);
				
				// set SENDER_ID
				tempMail.setSenderId((String)result[1]);
				
				// set RECEIVED_DATE
				String str = result[2].toString();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date d=null;
				try 
				{
					d = sdf.parse(str);
				} 
				catch (ParseException e) 
				{
					e.printStackTrace();
				}
				sdf.applyPattern("dd/MM/yyyy HH:mm:ss");
				
				tempMail.setReceivedDate(sdf.format(d));
				
				// set MAIL_MESSAGE
				//Blob tempBlob = (Blob) result[4];
				//byte[] bdata = tempBlob.getBytes(1, (int) tempBlob.length());
				byte[] bdata = (byte[]) result[3];
				
				ByteArrayInputStream bais = new ByteArrayInputStream(bdata);
				
				tempMail.setMailMessage(bais);
				
				mailList.add(tempMail);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		
		return mailList;
	}
	
	public void setProcessedFlagForMail(String messageId,
										String senderId,
										String receivedDate,
										String isProcessed)
	{
		entityManager.createNativeQuery("UPDATE MAILBOX SET ISPROCESSED = :is_processed WHERE MESSAGE_ID = :message_id AND SENDER_ID = :sender_id AND RECEIVED_DATE = TO_DATE(:received_date,'dd/mm/yyyy HH24:mi:ss')")
					 .setParameter("is_processed", isProcessed)
					 .setParameter("message_id", messageId)
					 .setParameter("sender_id", senderId)
					 .setParameter("received_date", receivedDate)
					 .executeUpdate();
	}
	
	public void storeXmls(Map<String, String> processedXmlMap) throws Exception
	{
		String xmlName = processedXmlMap.get("XML_NAME");
		String xmlContent = processedXmlMap.get("XML_CONTENT");
		String xsdName = processedXmlMap.get("XSD_NAME");

		xmlName = xmlName.substring(0, (xmlName.length())-4);

		xsdName = xsdName.substring(0, xsdName.indexOf("_"));

		boolean isXmlStored = isXmlAlreadyStored(xmlName, xsdName);

		if(!isXmlStored)
		{
			insertXml(xmlName, xmlContent, xsdName);
		}
		else
		{
			// if xml is already stored then do nothing
		}
	}
	
	private boolean isXmlAlreadyStored(String xmlName, String xsdName)
	{
		String result = null;
		try
		{
			result = (String) entityManager.createNativeQuery("SELECT :x FROM "+xsdName.toUpperCase()+" A WHERE A.XMLNAME = :xml_name")
				  						   .setParameter("x", "x")
				  						   .setParameter("xml_name", xmlName)
				  						   .getSingleResult();
		}
		catch(NoResultException nre)
		{
			return false;
		}
		
		if(result.equalsIgnoreCase("x"))
		{
			return true;
		}
		return false;
	}
	
	private void insertXml(String xmlName, String xmlContent, String xsdName) throws Exception
	{
		Session session = entityManager.unwrap(Session.class);
		
		final String finalxmlContent = xmlContent;
		final String finalxmlName = xmlName;
		final String finalxsdName = xsdName;
		
		session.doWork(new Work() 
		{
			@Override
			public void execute(Connection connection) throws SQLException 
			{
				Connection dconn = ((DelegatingConnection) connection).getInnermostDelegate();
				
				OraclePreparedStatement sqlStatement = null;
				try
				{
					StringBuffer insertSql = new StringBuffer().append(" INSERT INTO "+finalxsdName.toUpperCase()+" ")
															   .append(" VALUES ")
															   .append(" ( ?, ")
															   .append("   ?  ) ");

					XMLType xml = XMLType.createXML(dconn, finalxmlContent);
					
					sqlStatement = (OraclePreparedStatement) dconn.prepareStatement(insertSql.toString());
					sqlStatement.setObject(1, finalxmlName);
					sqlStatement.setObject(2, xml);
					
					sqlStatement.execute();
				}
				finally
				{
					sqlStatement.close();
				}
			}
		});		
	}
	
	@Transactional
	public BigDecimal insertMaterial(Material material)
	{
		entityManager.persist(material);
		
		return material.getMaterialId();
	}
	
	public Material getMaterialDetailsByCode(String materialCode)
	{
		 
		TypedQuery<Material> tq = entityManager.createQuery("SELECT m FROM Material m WHERE m.materialCode = :materialCode", Material.class)
											   .setParameter("materialCode", materialCode);
		
		return tq.getSingleResult();
	}
	
	public Material getMaterialDetailsById(BigDecimal id)
	{
		return entityManager.find(Material.class, id);
	}
	
	public List<Material> getAllMaterils()
	{
		List<Material> materials = (List<Material>)entityManager.createQuery("SELECT m FROM Material m").getResultList();
		
		return materials;
	}
	
	@Transactional
	public void updateMaterial(Material material)
	{
		entityManager.merge(material);
	}
	
	@Transactional
	public void deleteMaterialByCode(String materialCode)
	{
		Material material = getMaterialDetailsByCode(materialCode);
		
		entityManager.remove(material);
	}
	
	@Transactional
	public boolean deleteMaterialById(String materialId)
	{
		Material material = getMaterialDetailsById(new BigDecimal(materialId));
		
		if(material==null)
		{
			return false;
		}
		
		entityManager.remove(material);
		return true;
	}
	
	@Transactional
	public BigDecimal insertRequest(Request purchaseRequest)
	{
		entityManager.persist(purchaseRequest);
		
		return purchaseRequest.getRequestId();
	}
	
	public Request getRequestDetailsById(BigDecimal requestId)
	{
		return entityManager.find(Request.class, requestId);
	}
	
	public List<Request> getAllRequests()
	{
		List<Request> requests = (List<Request>)entityManager.createQuery("SELECT r FROM Request r").getResultList();
		
		return requests;
	}
	
	@Transactional
	public boolean deleteRequestById(String requestId)
	{
		Request request = getRequestDetailsById(new BigDecimal(requestId));
		
		if(request==null)
		{
			return false;
		}
		
		try
		{
			entityManager.remove(request);			
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}
	
	@Transactional
	public BigDecimal insertRequestItem(RequestItem purchaseRequestItem)
	{
		entityManager.persist(purchaseRequestItem);
		
		return purchaseRequestItem.getRequestItemId();
	}
	
	public RequestItem getRequestItemDetailsById(BigDecimal requestItemId)
	{
		return entityManager.find(RequestItem.class, requestItemId);
	}
	
	public List<RequestItem> getAllRequestItems()
	{
		List<RequestItem> requestItems = (List<RequestItem>)entityManager.createQuery("SELECT rt FROM RequestItem rt").getResultList();
		
		return requestItems;
	}
	
	public BigDecimal getRequestIdOfRequestItemId(BigDecimal requestItemId)
	{
		BigDecimal requestId = (BigDecimal) entityManager.createQuery("SELECT rt.requestId FROM RequestItem rt WHERE rt.requestItemId = :requestItemId")
		 		     						.setParameter("requestItemId", requestItemId).getSingleResult();
		
		return requestId;
	}
	
	@Transactional
	public void updateRequestItem(RequestItem requestItem)
	{
		entityManager.merge(requestItem);
	}
	
	@Transactional
	public boolean deleteRequestItemById(BigDecimal requestItemId)
	{
		RequestItem requestItem = getRequestItemDetailsById(requestItemId);
		
		if(requestItem==null)
		{
			return false;
		}
		
		entityManager.remove(requestItem);
		return true;
	}
	
	@Transactional
	public void insertSupplier(Supplier supplier)
	{
		entityManager.persist(supplier);
	}
	
	public boolean isSupplierIdAlreadyStored(BigDecimal supplierId)
	{
		String result = null;
		try
		{
			result = (String) entityManager.createNativeQuery("SELECT :x FROM SUPPLIER S WHERE S.SUPPLIER_ID = :supplier_id")
										   .setParameter("x", "x")
										   .setParameter("supplier_id", supplierId)
										   .getSingleResult();
		}
		catch(NoResultException nre)
		{
			return false;
		}

		if(result.equalsIgnoreCase("x"))
		{
			return true;
		}
		return false;
	}
	
	public boolean isSupplierNameAlreadyStored(String supplierName)
	{
		String result = null;
		try
		{
			result = (String) entityManager.createNativeQuery("SELECT :x FROM SUPPLIER S WHERE S.SUPPLIER_NAME = :supplier_name")
										   .setParameter("x", "x")
										   .setParameter("supplier_name", supplierName)
										   .getSingleResult();
		}
		catch(NoResultException nre)
		{
			return false;
		}

		if(result.equalsIgnoreCase("x"))
		{
			return true;
		}
		return false;
	}
	
	public Supplier getSupplierDetailsById(BigDecimal supplierId)
	{
		return entityManager.find(Supplier.class, supplierId);
	}
	
	public List<Supplier> getAllSuppliers()
	{
		List<Supplier> suppliers = (List<Supplier>)entityManager.createQuery("SELECT s FROM Supplier s").getResultList();
		
		return suppliers;
	}
	
	@Transactional
	public void updatesupplier(Supplier supplier)
	{
		entityManager.merge(supplier);
	}
	
	@Transactional
	public boolean deleteSupplierById(BigDecimal supplierId)
	{
		Supplier supplier = getSupplierDetailsById(supplierId);
		
		if(supplier==null)
		{
			return false;
		}
		
		entityManager.remove(supplier);
		return true;
	}
	
	@Transactional
	public BigDecimal insertQuotation(Quotation quotation)
	{
		entityManager.persist(quotation);
		
		return quotation.getQuotationId();
	}
	
	public Quotation getQuotationDetailsById(BigDecimal quotationId)
	{
		return entityManager.find(Quotation.class, quotationId);
	}
	
	public List<Quotation> getAllQuotations()
	{
		List<Quotation> quotations = (List<Quotation>)entityManager.createQuery("SELECT q FROM Quotation q").getResultList();
		
		return quotations;
	}
	
	@Transactional
	public boolean deleteQuotationById(String quotationId)
	{
		Quotation quotation = getQuotationDetailsById(new BigDecimal(quotationId));
		
		if(quotation==null)
		{
			return false;
		}
		
		try
		{
			entityManager.remove(quotation);			
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}
	
	@Transactional
	public BigDecimal insertQuotationRequest(QuotationRequest quotationRequest)
	{
		entityManager.persist(quotationRequest);
		
		return quotationRequest.getQuotationRequestId();
	}
	
	public QuotationRequest getQuotationRequestDetailsById(BigDecimal quotationRequestId)
	{
		return entityManager.find(QuotationRequest.class, quotationRequestId);
	}
	
	public List<QuotationRequest> getAllQuotationRequests()
	{
		List<QuotationRequest> quotationRequests = (List<QuotationRequest>)entityManager.createQuery("SELECT qr FROM QuotationRequest qr").getResultList();
		
		return quotationRequests;
	}
	
	public BigDecimal getQuotationIdOfQuotationRequestId(BigDecimal quotationRequestId)
	{
		BigDecimal quotationId = (BigDecimal) entityManager.createQuery("SELECT qr.quotationId FROM QuotationRequest qr WHERE qr.quotationRequestId = :quotationRequestId")
		 		     									 .setParameter("quotationRequestId", quotationRequestId).getSingleResult();
		
		return quotationId;
	}
	
	@Transactional
	public boolean deleteQuotationRequestById(BigDecimal quotationrequestId)
	{
		QuotationRequest quotationRequest = getQuotationRequestDetailsById(quotationrequestId);
		
		if(quotationRequest==null)
		{
			return false;
		}
		
		entityManager.remove(quotationRequest);
		return true;
	}
	
	@Transactional
	public void updateQuotationRequest(QuotationRequest quotationRequest)
	{
		entityManager.merge(quotationRequest);
	}
	
	public List selectDetailsForQuotationItem(BigDecimal quotationId)
	{
		StringBuffer selectSql = new StringBuffer().append(" SELECT")
												   .append("   QR.QUOTATION_ID,")
												   .append("   M.MATERIAL_LAMINATION,")
												   .append("   M.MATERIAL_TREATMENT,")
												   .append("   M.MATERIAL_THICKNESS,")
												   .append("   S.STANDARD_ID,")
												   .append("   SUM(RI.REQUEST_WEIGHT) AS TOTAL_WEIGHT")
												   .append(" FROM QUOTATION_REQUEST QR INNER JOIN REQUEST R ON QR.REQUEST_ID = R.REQUEST_ID")
												   .append("                           INNER JOIN REQUEST_ITEM RI ON R.REQUEST_ID = RI.REQUEST_ID")
												   .append("                           INNER JOIN MATERIAL M ON RI.MATERIAL_ID = M.MATERIAL_ID")
												   .append("                           INNER JOIN MATERIAL_STANDARD MS ON M.MATERIAL_ID = MS.MATERIAL_ID")
												   .append("                           INNER JOIN STANDARD S ON MS.STANDARD_ID = S.STANDARD_ID")
												   .append(" WHERE QR.QUOTATION_ID = :quotation_id")
												   .append("   AND MS.STANDARD_ACCEPT = :standard_accept")
												   .append(" GROUP BY QR.QUOTATION_ID,")
												   .append("   M.MATERIAL_LAMINATION,")
												   .append("   M.MATERIAL_TREATMENT,")
												   .append("   M.MATERIAL_THICKNESS,")
												   .append("   S.STANDARD_ID");
		
		
		List<Object[]> list = entityManager.createNativeQuery(selectSql.toString())
					 			 .setParameter("quotation_id", quotationId)
					 			 .setParameter("standard_accept", "Y")
					 			 .getResultList();
		
		return list;
	}
	
	@Transactional
	public BigDecimal insertQuotationItem(QuotationItem quotationItem)
	{
		entityManager.persist(quotationItem);
		
		return quotationItem.getQuotationItemId();
	}
	
	public QuotationItem getQuotationItemDetailsById(BigDecimal quotationItemId)
	{
		return entityManager.find(QuotationItem.class, quotationItemId);
	}
	
	public List<QuotationItem> getAllQuotationItems()
	{
		List<QuotationItem> quotationItems = (List<QuotationItem>)entityManager.createQuery("SELECT qi FROM QuotationItem qi").getResultList();
		
		return quotationItems;
	}
	
	public BigDecimal getQuotationIdOfQuotationItemId(BigDecimal quotationItemId)
	{
		BigDecimal quotationId = (BigDecimal) entityManager.createQuery("SELECT qi.quotationId FROM QuotationItem qi WHERE qi.quotationItemId = :quotationItemId")
		 		     									   .setParameter("quotationItemId", quotationItemId).getSingleResult();
		
		return quotationId;
	}
	
	@Transactional
	public boolean deleteQuotationItemById(BigDecimal quotationItemId)
	{
		QuotationItem quotationItem = getQuotationItemDetailsById(quotationItemId);
		
		if(quotationItem==null)
		{
			return false;
		}
		
		try
		{
			entityManager.remove(quotationItem);			
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}
	
	@Transactional
	public void updateQuotationItem(QuotationItem quotationItem)
	{
		entityManager.merge(quotationItem);
	}
	
	@Transactional
	public BigDecimal insertPurchasePlan(PurchasePlanning purchasePlanning)
	{
		entityManager.persist(purchasePlanning);
		
		return purchasePlanning.getPlanningId();
	}
	
	public PurchasePlanning getPurchasePlanDetailsById(BigDecimal planningId)
	{
		return entityManager.find(PurchasePlanning.class, planningId);
	}
	
	public List<PurchasePlanning> getAllPurchasePlans()
	{
		List<PurchasePlanning> plannings = (List<PurchasePlanning>)entityManager.createQuery("SELECT p FROM PurchasePlanning p").getResultList();
		
		return plannings;
	}
	
	@Transactional
	public boolean deletePurchasePlanById(BigDecimal planningId)
	{
		PurchasePlanning planning = getPurchasePlanDetailsById(planningId);
		
		if(planning==null)
		{
			return false;
		}
		
		try
		{
			entityManager.remove(planning);			
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}
	
	@Transactional
	public void updatePurchasePlanning(PurchasePlanning planning)
	{
		entityManager.merge(planning);
	}
	
	@Transactional
	public BigDecimal insertPurchasePlanItem(PurchasePlanningItem planningItem)
	{
		entityManager.persist(planningItem);
		
		return planningItem.getPlanningItemId();
	}
	
	public PurchasePlanningItem getPurchasePlanItemDetailsById(BigDecimal planningItemId)
	{
		return entityManager.find(PurchasePlanningItem.class, planningItemId);
	}
	
	public List<PurchasePlanningItem> getAllPurchasePlanItems()
	{
		List<PurchasePlanningItem> planningItems = (List<PurchasePlanningItem>)entityManager.createQuery("SELECT pt FROM PurchasePlanningItem pt").getResultList();
		
		return planningItems;
	}
	
	public BigDecimal getPlanningIdOfPlanningItemId(BigDecimal planningItemId)
	{
		BigDecimal planningId = (BigDecimal) entityManager.createQuery("SELECT pt.planningId FROM PurchasePlanningItem pt WHERE pt.planningItemId = :planningItemId")
		 		     						.setParameter("planningItemId", planningItemId).getSingleResult();
		
		return planningId;
	}
	
	@Transactional
	public boolean deletePurchasePlanItemById(BigDecimal planningItemId)
	{		
		PurchasePlanningItem planningItem = getPurchasePlanItemDetailsById(planningItemId);
		
		if(planningItem==null)
		{
			return false;
		}
		
		entityManager.remove(planningItem);
		return true;
	}
	
	@Transactional
	public void updatePurchasePlanItem(PurchasePlanningItem planningItem)
	{
		entityManager.merge(planningItem);
	}
	
	@Transactional
	public BigDecimal insertPurchaseOrder(PurchaseOrder purchaseOrder)
	{
		entityManager.persist(purchaseOrder);
		
		return purchaseOrder.getPurchaseOrderId();
	}
	
	public PurchaseOrder getPurchaseOrderDetailsById(BigDecimal purchaseOrderId)
	{
		return entityManager.find(PurchaseOrder.class, purchaseOrderId);
	}
	
	public List<PurchaseOrder> getAllPurchaseOrders()
	{
		List<PurchaseOrder> purchaseOrders = (List<PurchaseOrder>)entityManager.createQuery("SELECT po FROM PurchaseOrder po").getResultList();
		
		return purchaseOrders;
	}
	
	@Transactional
	public boolean deletePurchaseOrderById(BigDecimal purchaseOrderId)
	{
		PurchaseOrder purchaseOrder = getPurchaseOrderDetailsById(purchaseOrderId);
		
		if(purchaseOrder==null)
		{
			return false;
		}
		
		try
		{
			entityManager.remove(purchaseOrder);			
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}
	
	@Transactional
	public void updatePurchaseOrder(PurchaseOrder purchaseOrder)
	{
		entityManager.merge(purchaseOrder);
	}
	
	@Transactional
	public BigDecimal insertPurchaseOrderItem(PurchaseOrderItem purchaseOrderItem)
	{
		entityManager.persist(purchaseOrderItem);
		
		return purchaseOrderItem.getPurchaseOrderItemId();
	}
	
	public PurchaseOrderItem getPurchaseOrderItemDetailsById(BigDecimal purchaseOrderItemId)
	{
		return entityManager.find(PurchaseOrderItem.class, purchaseOrderItemId);
	}
	
	public List<PurchaseOrderItem> getAllPurchaseOrderItems()
	{
		List<PurchaseOrderItem> purchaseOrderItems = (List<PurchaseOrderItem>)entityManager.createQuery("SELECT pot FROM PurchaseOrderItem pot").getResultList();
		
		return purchaseOrderItems;
	}
	
	public BigDecimal getPurchaseOrderIdOfPurchaseOrderItemId(BigDecimal purchaseOrderItemId)
	{
		BigDecimal purchaseOrderId = (BigDecimal) entityManager.createQuery("SELECT pot.purchaseOrderId FROM PurchaseOrderItem pot WHERE pot.purchaseOrderItemId = :purchaseOrderItemId")
		 		     						.setParameter("purchaseOrderItemId", purchaseOrderItemId).getSingleResult();
		
		return purchaseOrderId;
	}
	
	@Transactional
	public boolean deletePurchaseOrderItemById(BigDecimal purchaseOrderItemId)
	{
		PurchaseOrderItem purchaseOrderItem = getPurchaseOrderItemDetailsById(purchaseOrderItemId);
		
		if(purchaseOrderItem==null)
		{
			return false;
		}
		
		entityManager.remove(purchaseOrderItem);
		return true;
	}
	
	@Transactional
	public void updatePurchaseOrderItem(PurchaseOrderItem purchaseOrderItem)
	{
		entityManager.merge(purchaseOrderItem);
	}
	
	public byte[] getRequestReportJasperFile() throws SQLException
	{
		Blob tempBlob = (Blob) entityManager.createNativeQuery("SELECT JASPER_FILE FROM REQUEST_REPORT WHERE REPORT_ID = (SELECT MAX(REPORT_ID) FROM REQUEST_REPORT)")
											.getSingleResult();
		
		return tempBlob.getBytes(1, (int)tempBlob.length());
	}
	
	public byte[] getRequestReportAltamiraImage() throws SQLException
	{
		Blob tempBlob = (Blob) entityManager.createNativeQuery("SELECT ALTAMIRA_LOGO FROM REQUEST_REPORT WHERE ROWNUM = 1")
				   			   						.getSingleResult();
		
		return tempBlob.getBytes(1, (int)tempBlob.length());
	}
	
	@Transactional
	public boolean insertGeneratedRequestReport(JasperPrint print)
	{
		byte[] bArray = null;
		
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			
			oos.writeObject(print);
			oos.close();
			baos.close();
			
			bArray = baos.toByteArray();
		}
		catch(Exception e)
		{
			System.out.println("Error converting JasperPrint object to byte[] array");
			e.printStackTrace();
			return false;
		}
		
		try
		{
			RequestReportLog log = new RequestReportLog();
			log.setReportInstance(bArray);

			entityManager.persist(log);
		}
		catch(Exception e)
		{
			System.out.println("Error while inserting generated report in database.");
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public List selectRequestReportDataById(BigDecimal requestId)
	{
		StringBuffer selectSql = new StringBuffer().append(" SELECT M.MATERIAL_CODE, ")
				   								   .append("        M.MATERIAL_DESCRIPTION, ")
				   								   .append("        M.MATERIAL_LAMINATION, ")
				   								   .append("        M.MATERIAL_TREATMENT, ")
				   								   .append("        M.MATERIAL_THICKNESS, ")
				   								   .append("        M.MATERIAL_WIDTH, ")
				   								   .append("        M.MATERIAL_LENGTH, ")
				   								   .append("        RT.REQUEST_WEIGHT, ")
				   								   .append("        RT.REQUEST_DATE ")
				   								   .append(" FROM REQUEST R, REQUEST_ITEM RT, MATERIAL M ")
				   								   .append(" WHERE R.REQUEST_ID = RT.REQUEST_ID ")
				   								   .append(" AND RT.MATERIAL_ID = M.MATERIAL_ID ")
				   								   .append(" AND R.REQUEST_ID = :request_id ")
				   								   .append(" ORDER BY MATERIAL_CODE ASC");


		List<Object[]> list = entityManager.createNativeQuery(selectSql.toString())
										   .setParameter("request_id", requestId)
										   .getResultList();

		return list;
	}
	
	public byte[] getQuotationReportJasperFile() throws SQLException
	{
		Blob tempBlob = (Blob) entityManager.createNativeQuery("SELECT JASPER_FILE FROM QUOTATION_REPORT WHERE REPORT_ID = (SELECT MAX(REPORT_ID) FROM QUOTATION_REPORT)")
											.getSingleResult();
		
		return tempBlob.getBytes(1, (int)tempBlob.length());
	}
	
	public byte[] getQuotationReportAltamiraImage() throws SQLException
	{
		Blob tempBlob = (Blob) entityManager.createNativeQuery("SELECT ALTAMIRA_LOGO FROM QUOTATION_REPORT WHERE ROWNUM = 1")
				   			   						.getSingleResult();
		
		return tempBlob.getBytes(1, (int)tempBlob.length());
	}
	
	@Transactional
	public boolean insertGeneratedQuotationReport(JasperPrint print)
	{
		byte[] bArray = null;
		
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			
			oos.writeObject(print);
			oos.close();
			baos.close();
			
			bArray = baos.toByteArray();
		}
		catch(Exception e)
		{
			System.out.println("Error converting JasperPrint object to byte[] array");
			e.printStackTrace();
			return false;
		}
		
		try
		{
			QuotationReportLog log = new QuotationReportLog();
			log.setReportInstance(bArray);

			entityManager.persist(log);
		}
		catch(Exception e)
		{
			System.out.println("Error while inserting generated report in database.");
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public List selectQuotationReportDataById(BigDecimal quotationId)
	{
		StringBuffer selectSql = new StringBuffer().append(" SELECT QI.MATERIAL_LAMINATION, ")
												   .append("        QI.MATERIAL_TREATMENT, ")
												   .append("        QI.MATERIAL_THICKNESS ")
												   .append(" FROM QUOTATION_ITEM QI ")
												   .append(" WHERE QI.QUOTATION_ID = :quotation_id ")
												   .append(" GROUP BY QI.MATERIAL_LAMINATION, QI.MATERIAL_TREATMENT, QI.MATERIAL_THICKNESS ")
												   .append(" ORDER BY QI.MATERIAL_LAMINATION, QI.MATERIAL_TREATMENT, QI.MATERIAL_THICKNESS ");

		List<Object[]> list = entityManager.createNativeQuery(selectSql.toString())
										   .setParameter("quotation_id", quotationId)
										   .getResultList();
		
		return list;
	}
	
	public byte[] getPlanningReportJasperFile() throws SQLException
	{
		Blob tempBlob = (Blob) entityManager.createNativeQuery("SELECT JASPER_FILE FROM PLANNING_REPORT WHERE REPORT_ID = (SELECT MAX(REPORT_ID) FROM PLANNING_REPORT)")
											.getSingleResult();
		
		return tempBlob.getBytes(1, (int)tempBlob.length());
	}
	
	public byte[] getPlanningReportAltamiraImage() throws SQLException
	{
		Blob tempBlob = (Blob) entityManager.createNativeQuery("SELECT ALTAMIRA_LOGO FROM PLANNING_REPORT WHERE REPORT_ID = (SELECT MAX(REPORT_ID) FROM PLANNING_REPORT)")
				   			   				.getSingleResult();
		
		return tempBlob.getBytes(1, (int)tempBlob.length());
	}
	
	@Transactional
	public boolean insertGeneratedPlanningReport(JasperPrint print)
	{
		byte[] bArray = null;
		
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			
			oos.writeObject(print);
			oos.close();
			baos.close();
			
			bArray = baos.toByteArray();
		}
		catch(Exception e)
		{
			System.out.println("Error converting JasperPrint object to byte[] array");
			e.printStackTrace();
			return false;
		}
		
		try
		{
			PlanningReportLog log = new PlanningReportLog();
			log.setReportInstance(bArray);

			entityManager.persist(log);
		}
		catch(Exception e)
		{
			System.out.println("Error while inserting generated report in database.");
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public List selectDistinctSuppliersFromPurchasePlan(BigDecimal planningId)
	{
		StringBuffer selectSql = new StringBuffer().append(" SELECT DISTINCT PPI.SUPPLIER_ID ")
												   .append(" FROM PURCHASE_PLANNING_ITEM PPI ")
												   .append(" WHERE PPI.PLANNING_ID = :planning_id ")
												   .append(" ORDER BY PPI.SUPPLIER_ID ");
		
		return entityManager.createNativeQuery(selectSql.toString())
				 			.setParameter("planning_id", planningId)
				 			.getResultList();
	}
	
	public List selectOrderItemDetailsBySupplierId(BigDecimal planningId,
												   BigDecimal supplierId)
	{
		StringBuffer selectSql = new StringBuffer().append(" SELECT PPI.PLANNING_ITEM_ID,")
												   .append("        RI.REQUEST_DATE AS PURCHASE_ORDER_ITEM_DATE,")
												   .append("        PPI.SUPPLIER_WEIGHT AS PURCHASE_ORDER_ITEM_WEIGHT,")
												   .append("        QIQ.SUPPLIER_PRICE AS PURCHASE_ORDER_ITEM_UNIT_PRICE,") 
												   .append("        M.MATERIAL_TAX AS PURCHASE_ORDER_ITEM_TAX")
												   .append(" FROM PURCHASE_PLANNING_ITEM PPI,")
												   .append("      REQUEST_ITEM RI,")
												   .append("      REQUEST R,")
												   .append("      MATERIAL M,")
												   .append("      QUOTATION_ITEM QI,")
												   .append("      QUOTATION_REQUEST QR,")
												   .append("      QUOTATION_ITEM_QUOTE QIQ")
												   .append(" WHERE PPI.PLANNING_ID = :planning_id")
												   .append(" AND PPI.SUPPLIER_ID = :supplier_id")
												   .append(" AND PPI.REQUEST_ITEM_ID = RI.REQUEST_ITEM_ID")
												   .append(" AND RI.MATERIAL_ID = M.MATERIAL_ID")
												   .append(" AND RI.REQUEST_ID = R.REQUEST_ID")
												   .append(" AND R.REQUEST_ID = QR.REQUEST_ID")
												   .append(" AND QR.QUOTATION_ID = QI.QUOTATION_ID")
												   .append(" AND QIQ.SUPPLIER_ID = PPI.SUPPLIER_ID")
												   .append(" AND QI.MATERIAL_LAMINATION = M.MATERIAL_LAMINATION")
												   .append(" AND QI.MATERIAL_TREATMENT = M.MATERIAL_TREATMENT")
												   .append(" AND QI.MATERIAL_THICKNESS = M.MATERIAL_THICKNESS")
												   .append(" AND QI.QUOTATION_ITEM_ID = QIQ.QUOTATION_ITEM_ID");
		
		return entityManager.createNativeQuery(selectSql.toString())
				 		    .setParameter("planning_id", planningId)
				 		    .setParameter("supplier_id", supplierId)
				 		    .getResultList();
	}
	
	public byte[] getPurchaseOrderReportJasperFile() throws SQLException
	{
		Blob tempBlob = (Blob) entityManager.createNativeQuery("SELECT JASPER_FILE FROM PURCHASE_ORDER_REPORT WHERE REPORT_ID = (SELECT MAX(REPORT_ID) FROM PURCHASE_ORDER_REPORT)")
											.getSingleResult();
		
		return tempBlob.getBytes(1, (int)tempBlob.length());
	}
	
	public byte[] getPurchaseOrderReportAltamiraImage() throws SQLException
	{
		Blob tempBlob = (Blob) entityManager.createNativeQuery("SELECT ALTAMIRA_LOGO FROM PURCHASE_ORDER_REPORT WHERE REPORT_ID = (SELECT MAX(REPORT_ID) FROM PURCHASE_ORDER_REPORT)")
				   			   				.getSingleResult();
		
		return tempBlob.getBytes(1, (int)tempBlob.length());
	}
	
	@Transactional
	public boolean insertGeneratedPurchaseOrderReport(JasperPrint print)
	{
		byte[] bArray = null;
		
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			
			oos.writeObject(print);
			oos.close();
			baos.close();
			
			bArray = baos.toByteArray();
		}
		catch(Exception e)
		{
			System.out.println("Error converting JasperPrint object to byte[] array");
			e.printStackTrace();
			return false;
		}
		
		try
		{
			PurchaseOrderReportLog log = new PurchaseOrderReportLog();
			log.setReportInstance(bArray);

			entityManager.persist(log);
		}
		catch(Exception e)
		{
			System.out.println("Error while inserting generated report in database.");
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public Map getSupplierMailAddressForPurchaseOrder(BigDecimal purchaseOrderId)
	{
		StringBuffer selectSql = new StringBuffer().append(" SELECT PO.PURCHASE_ORDER_ID, ")
												   .append("        S.SUPPLIER_NAME, ")
												   .append("        SC.MAIL_ADDRESS ")
												   .append(" FROM PURCHASE_ORDER PO, ")
												   .append("      SUPPLIER S, ")
												   .append("      SUPPLIER_CONTACT SC ")
												   .append(" WHERE PO.PURCHASE_ORDER_ID = :purchase_order_id ")
												   .append(" AND PO.SUPPLIER_ID = S.SUPPLIER_ID ")
												   .append(" AND S.SUPPLIER_CONTACT_ID = SC.SUPPLIER_CONTACT_ID ");
		
		Object[] result = (Object[]) entityManager.createNativeQuery(selectSql.toString())
					 					  .setParameter("purchase_order_id", purchaseOrderId)
					 					  .getSingleResult();
		
		Map resultMap = new HashMap();
		resultMap.put("PURCHASE_ORDER_ID", result[0]);
		resultMap.put("SUPPLIER_NAME", result[1]);
		resultMap.put("MAIL_ADDRESS", result[2]);
		
		return resultMap;
	}

	public QuotationItem getQuotationIdByQuotationItemId(BigDecimal quotationItemId) 
	{
		return entityManager.find(QuotationItem.class, quotationItemId);
	}

	@Transactional
	public BigDecimal insertQuotationItemQuote(QuotationItemQuote quotationItemQuote) 
	{
		entityManager.persist(quotationItemQuote);
		
		return quotationItemQuote.getQuotationItemQuoteId();
	}

	public List<QuotationItemQuote> getAllQuotationItemQuotes() {
		
		List<QuotationItemQuote> quotationItemQuotes = (List<QuotationItemQuote>)entityManager.createQuery("SELECT qiq FROM QuotationItemQuote qiq").getResultList();
		
		return quotationItemQuotes;
	}

	public QuotationItemQuote getQuotationItemQuoteDetailsById(
			BigDecimal quotationItemQuoteId) {
		
		return entityManager.find(QuotationItemQuote.class, quotationItemQuoteId);
	}

	@Transactional
	public boolean deleteQuotationItemQuoteById(BigDecimal quotationItemQuoteId) {

		QuotationItemQuote quotationItemQuote = getQuotationItemQuoteDetailsById(quotationItemQuoteId);

		if(quotationItemQuote==null)
		{
			return false;
		}

		try
		{
			entityManager.remove(quotationItemQuote);			
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}

	@Transactional
	public void updateQuotationItemQuote(QuotationItemQuote quotationItemQuote) {

		entityManager.merge(quotationItemQuote);
	}
}
