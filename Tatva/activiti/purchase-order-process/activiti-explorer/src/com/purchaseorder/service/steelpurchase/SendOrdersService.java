package com.purchaseorder.service.steelpurchase;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.activiti.engine.IdentityService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.impl.identity.Authentication;
import org.hibernate.Session;
import org.hibernate.jdbc.ReturningWork;
import org.springframework.beans.factory.annotation.Autowired;

import com.purchaseorder.dao.PurchaseOrderDao;
import com.purchaseorder.service.MailService;

/**
 * 
 * @author PARTH
 *
 */
public class SendOrdersService implements JavaDelegate 
{
	@Autowired
	private PurchaseOrderDao purchaseOrderDao;
	
	@Autowired
	private MailService mailService;
	
	@Autowired
	private IdentityService identityService;
	
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

	public MailService getMailService() 
	{
		return mailService;
	}

	public void setMailService(MailService mailService) 
	{
		this.mailService = mailService;
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
	 * Java method implementation for BPMN service task 'SendOrdersServiceTask'.
	 */
	@Override
	public void execute(DelegateExecution execution) throws Exception 
	{
		System.out.println("Send Orders To Suppliers service task execution started...");
		
		List<String> purchaseOrderIdList = (List<String>) execution.getVariable("purchaseOrderId");
		
		for(String str : purchaseOrderIdList)
		{
			BigDecimal purchaseOrderId = new BigDecimal(str);
			
			JasperPrint print = generatePurchaseOrderReport(purchaseOrderId);
			ByteArrayInputStream bais = new ByteArrayInputStream(JasperExportManager.exportReportToPdf(print));
			
			Map<String, InputStream> emailAttachmentList = new HashMap<String, InputStream>();
			emailAttachmentList.put("PurchaseOrderReport", bais);
			
			Map supplierInfo = purchaseOrderDao.getSupplierMailAddressForPurchaseOrder(purchaseOrderId);
			
			String message = "Please find the attachment for the Purchase Order";
			
			String subject = "Purchase Order ID :" +
							 new DecimalFormat("#00000").format((BigDecimal)supplierInfo.get("PURCHASE_ORDER_ID")) +
							 " Supplier: " + 
							 (String)supplierInfo.get("SUPPLIER_NAME");
			
			mailService.sendMail((String)supplierInfo.get("MAIL_ADDRESS"), 
								 null, 
								 null,
								 subject,
								 message, 
								 emailAttachmentList);
		}
	}
	
	/**
	 * This methdo generates Purchase Order Report.
	 * 
	 * @param purchaseOrderId - Purchase Order ID
	 * @return - JasperPrint object
	 */
	private JasperPrint generatePurchaseOrderReport(BigDecimal purchaseOrderId)
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
			parameters.put("USERNAME", Authentication.getAuthenticatedUserId());

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
		
		return jasperPrint;
	}
}