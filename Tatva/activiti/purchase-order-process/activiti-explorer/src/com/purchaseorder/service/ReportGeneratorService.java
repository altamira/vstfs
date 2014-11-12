package com.purchaseorder.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.query.JRXPathQueryExecuterFactory;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;

import com.purchaseorder.dao.PurchaseOrderDao;

/**
 * 
 * @author PARTH
 *
 */
public class ReportGeneratorService implements JavaDelegate 
{

	@Autowired
	private PurchaseOrderDao purchaseOrderDao;
	
	public PurchaseOrderDao getPurchaseOrderDao() 
	{
		return purchaseOrderDao;
	}

	public void setPurchaseOrderDao(PurchaseOrderDao purchaseOrderDao) 
	{
		this.purchaseOrderDao = purchaseOrderDao;
	}

	/**
	 * Java method implementation for BPMN service task 'Generate reports for selected items'.
	 */
	@Override
	public void execute(DelegateExecution execution) throws Exception 
	{
		System.out.println("Report-Generation service task executed.");
		
		String xmlListStr = (String) execution.getVariable("xmllist");
		
		xmlListStr = xmlListStr.replace("[", "");
		xmlListStr = xmlListStr.replace("]", "");
		
		String[] xmlNames = xmlListStr.split(",");
		
		Map<String, JasperPrint> reportList = new HashMap<String, JasperPrint>();
		byte[] danfeJasper = purchaseOrderDao.getDanfeJasperFile();
		byte[] danfeAltamiraimage = purchaseOrderDao.getDanfeAltamiraImage();
		byte[] pdf = null;
		
		for(String xmlName : xmlNames)
		{
			xmlName = xmlName.trim();
			JasperPrint print = null;
			
			try
			{
				// get the xml content from database
				String xmlContent = purchaseOrderDao.getXmlContent(xmlName);

				// get the document object
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document document = builder.parse(new ByteArrayInputStream(xmlContent.getBytes("UTF-8")));

				Map params = new HashMap();
				params.put(JRXPathQueryExecuterFactory.PARAMETER_XML_DATA_DOCUMENT, document);

				Locale locale = new Locale.Builder().setLanguage("pt").setRegion("BR").build();
				params.put("REPORT_LOCALE", locale);

				BufferedImage imfg=null;
				try 
				{
					InputStream in = new ByteArrayInputStream(danfeAltamiraimage);
					imfg = ImageIO.read(in);
				} 
				catch (Exception e1) 
				{
					e1.printStackTrace();
				}

				params.put("altamira_logo", imfg);

				ByteArrayInputStream bais = new ByteArrayInputStream(danfeJasper);

				print = JasperFillManager.fillReport(bais, params);
				
				reportList.put(xmlName, print);
			}
			catch(Exception e)
			{
				System.out.println("Report could not generated");
				e.printStackTrace();
			}
			finally
			{
				try
				{
					if(print!=null)
					{
						// store generated report in database
						purchaseOrderDao.insertGeneratedReport(print);
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
					System.out.println("Could not insert generated report in database.");
				}
			}
			
		}
		
		// Store reportList in process variable
		execution.setVariable("reportList", reportList);
		execution.setVariable("pid", execution.getProcessInstanceId());
	}

}
