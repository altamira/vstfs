package com.purchaseorder.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.activation.DataSource;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.ByteArrayDataSource;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;

/**
 * 
 * @author PARTH
 *
 */
public class ReportMailService implements JavaDelegate 
{
	final String HOST_NAME = "mail.altamira.com.br";
	final int SMTP_PORT = 587;
	final String USERNAME = "tests.nfe@altamira.com.br";
	final String PASSWORD = "testsNFe";

	/**
	 * Java method implementation for BPMN service task 'Send e-mail of generated reports'.
	 *  
	 */
	@Override
	public void execute(DelegateExecution execution) throws Exception 
	{
		System.out.println("Report-Mail service task executed");
		
		String to = (String) execution.getVariable("to");
		String cc = (String) execution.getVariable("cc");
		String bcc = (String) execution.getVariable("bcc");
		String subject = (String) execution.getVariable("subject");
		String text = (String) execution.getVariable("text");
		
		String attachments = (String) execution.getVariable("pid");
		attachments = attachments.replace("[", "");
		attachments = attachments.replace("]", "");
		
		String pdfList[] = attachments.split(",");
		
		Map<String, JasperPrint> reportList = (Map<String, JasperPrint>) execution.getVariable("reportList");
		
		Map<String, InputStream> emailAttachmentList = new HashMap<String, InputStream>(); 
		
		for(String pdf : pdfList)
		{
			pdf = pdf.trim();
			JasperPrint print = reportList.get(pdf.substring(0, pdf.lastIndexOf(".pdf")));
			ByteArrayInputStream bais = new ByteArrayInputStream(JasperExportManager.exportReportToPdf(print));
			
			emailAttachmentList.put(pdf, bais);
		}
		
		sendMail(to, cc, bcc, subject, text, emailAttachmentList);
	}
	
	/**
	 * This method sends email of reports as attachments to specified To, Cc & Bcc with specified subject & body text.
	 * 
	 * @param to - To address
	 * @param cc - Cc address
	 * @param bcc - Bcc address
	 * @param subject - Subject of the E-mail
	 * @param text - Body text for e-mail
	 * @param emailAttachmentList - Map of reports
	 * @throws EmailException
	 */
	private void sendMail(String to, String cc, String bcc, String subject, 
						  String text, Map<String, InputStream> emailAttachmentList) throws EmailException
	{
		MultiPartEmail email = new MultiPartEmail();
		
		// Set body text of E-Mail
		email.setMsg(StringUtils.defaultIfEmpty(text, ""));
		
		// Set From address
		email.setFrom(USERNAME);
		
		// Add To address
		String[] toAddress = splitAndTrim(to);
		if(toAddress != null)
		{
			for(String tempTo : toAddress)
			{
				if(!StringUtils.isBlank(tempTo))
				email.addTo(tempTo);
			}
		}
		
		// Add Cc address
		String[] ccAddress = splitAndTrim(cc);
		if(ccAddress != null)
		{
			for(String tempCc : ccAddress)
			{
				if(!StringUtils.isBlank(tempCc))
				email.addCc(tempCc);
			}
		}
		
		// Add Bcc address
		String[] bccAddress = splitAndTrim(bcc);
		if(bccAddress != null)
		{
			for(String tempBcc : bccAddress)
			{
				if(!StringUtils.isBlank(tempBcc))
				email.addBcc(tempBcc);
			}
		}
		
		// Add subject
		email.setSubject(StringUtils.defaultIfEmpty(subject, ""));
		
		// Add attachments
		for(String key : emailAttachmentList.keySet())
		{
			InputStream is = emailAttachmentList.get(key);
			DataSource source = null;
			
			try 
			{
				source = new ByteArrayDataSource(is, "application/pdf");
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			
			email.attach(source, key, "");
		}
		
		// Set mail server properties
		email.setHostName(HOST_NAME);
		email.setSmtpPort(SMTP_PORT);
		email.setAuthentication(USERNAME, PASSWORD);
		email.setTLS(true);
		
		// Send mail
		email.send();
		
	}
	
	/**
	 * This method splits the string by ',' character and trims particular splitted string & returns array of string.
	 * 
	 * @param str - Input String
	 * @return String[] - String array of splitted and trimmed strings
	 */
	private String[] splitAndTrim(String str)
	{
		if (str != null) 
		{
			String[] splittedStrings = str.split(",");
			
			for (int i = 0; i < splittedStrings.length; i++) 
			{
				splittedStrings[i] = splittedStrings[i].trim();
			}
			
			return splittedStrings;
		}
		
		return null;
	}

}
