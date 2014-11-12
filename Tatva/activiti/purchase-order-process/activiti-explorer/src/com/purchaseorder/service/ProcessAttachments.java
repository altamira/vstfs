package com.purchaseorder.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import validator.XmlValidator;
import validator.Xsd;

import com.purchaseorder.dao.PurchaseOrderDao;
import com.sun.mail.util.BASE64DecoderStream;
import com.sun.mail.util.QPDecoderStream;

/**
 * 
 * @author PARTH
 *
 */
public class ProcessAttachments 
{
	List<Xsd> xsdList;
	Map<String, String> processedXmlMap;
	
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

	public ProcessAttachments(List<Xsd> xsdList)
	{
		this.xsdList = xsdList;
	}
	
	/**
	 * This method performs extracts XML from mail body part & performs validations on it.
	 * And stores xml in database if it is valid.
	 * 
	 * @param part
	 * @param fileName
	 * @return
	 * @throws XMLValidationException
	 */
	public Map<String, String> processXML(BodyPart part, String fileName) throws XMLValidationException
	{
		processedXmlMap = new HashMap<String, String>();
		
		String xmlcontent = null;
		
		try 
		{
			if((part.getContent()) instanceof BASE64DecoderStream)
			{
				StringBuilder xml = new StringBuilder();
				
				BASE64DecoderStream base64DecoderStream = (BASE64DecoderStream) part.getContent();
				
				int bytesRead;
				while( ( bytesRead = base64DecoderStream.read() ) != -1 )
				{
					xml.append((char)bytesRead);
				}
				
				xmlcontent = xml.toString();
				//System.out.println(xmlcontent);
			}
			else if((part.getContent()) instanceof QPDecoderStream)
			{
				StringBuffer xml = new StringBuffer();
				
				QPDecoderStream qpDecoderStream = (QPDecoderStream)part.getContent();
				
				int bytesRead;
				while( ( bytesRead = qpDecoderStream.read() ) != -1 )
				{
					xml.append((char)bytesRead);
				}
				
				xmlcontent = xml.toString();
				//System.out.println(xmlcontent);
			}
			else
			{
				xmlcontent = (String)(part.getContent());
			}
		} 
		catch (IOException | MessagingException e1) 
		{
			e1.printStackTrace();
		}
		
		XmlValidator validator = new XmlValidator();
		
		// XML Syntax validation
		Document document=null;
		try
		{
			//document = validateXMLSyntax(xmlcontent);
			document = validator.validateXMLSyntax(xmlcontent);
		}
		catch(ParserConfigurationException | SAXException | IOException ex)
		{
			System.out.println("\t:: XML not well formatted");
			ex.printStackTrace();
			
			throw new XMLValidationException();
		}
		
		// XML Schema validation
		try
		{
			//String xsd = validateXMLSchema(document);
			String xsd = validator.validateXMLSchema(document);
			System.out.println("XML file: "+fileName+" is well known and its XSD is :"+xsd);
			
			processedXmlMap.put("XML_NAME", fileName);
			processedXmlMap.put("XML_CONTENT", xmlcontent);
			processedXmlMap.put("XSD_NAME", xsd);
			
		}
		catch(Exception e)
		{
			System.out.println("ERROR: XML not well known \t"+e);
			e.printStackTrace();
			
			throw new XMLValidationException();
		}
		
		// Store XML if XML is valid
		try
		{
			purchaseOrderDao.storeXmls(processedXmlMap);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
			throw new XMLValidationException();
		}
		
		return processedXmlMap;
	}
}
