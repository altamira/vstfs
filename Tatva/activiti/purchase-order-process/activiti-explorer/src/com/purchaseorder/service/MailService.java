package com.purchaseorder.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.activation.DataSource;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.ProcessEngineConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.ByteArrayDataSource;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author PARTH
 *
 */
public class MailService 
{
	@Autowired
	ProcessEngineConfiguration processEngineConfiguration;
	
	public ProcessEngineConfiguration getProcessEngineConfiguration() 
	{
		return processEngineConfiguration;
	}

	public void setProcessEngineConfiguration(ProcessEngineConfiguration processEngineConfiguration) 
	{
		this.processEngineConfiguration = processEngineConfiguration;
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
	public void sendMail(String to, 
						 String cc, 
						 String bcc, 
						 String subject, 
						 String text, 
						 Map<String, InputStream> emailAttachmentList) throws EmailException
	{
		String hostName = processEngineConfiguration.getMailServerHost();
		int smtpPort = processEngineConfiguration.getMailServerPort();
		String userName = processEngineConfiguration.getMailServerUsername();
		String password = processEngineConfiguration.getMailServerPassword();
		boolean ssl = processEngineConfiguration.getMailServerUseSSL();
		boolean tls = processEngineConfiguration.getMailServerUseTLS();
		
		if(StringUtils.isEmpty(hostName) || 
				StringUtils.isEmpty(userName) ||
				StringUtils.isEmpty(password))
		{
			throw new ActivitiException("Mail Server not Configured properly.");
		}
		
		MultiPartEmail email = new MultiPartEmail();
		
		// Set body text of E-Mail
		email.setMsg(StringUtils.defaultIfEmpty(text, ""));
		
		// Set From address
		email.setFrom(userName);
		
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
		email.setHostName(hostName);
		email.setSmtpPort(smtpPort);
		email.setAuthentication(userName, password);
		email.setTLS(tls);

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
