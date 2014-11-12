package com.purchaseorder.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import validator.XmlValidator;
import validator.Xsd;

import com.purchaseorder.dao.PurchaseOrderDao;
import com.purchaseorder.model.Mail;
import com.sun.mail.imap.IMAPFolder;

/**
 * 
 * @author PARTH
 *
 */
public class PurchaseOrderMailboxProcessor
{
	Properties props;
	Store store;
	Folder inbox;
	List<Xsd> xsdList;
	List<Map> downloadedMails;
	List<Message> processedMessage;
	
	@Autowired
	private PurchaseOrderDao purchaseOrderDao;
	
	@Autowired
	private ProcessAttachments processAttachments;
	
	public PurchaseOrderDao getPurchaseOrderDao() 
	{
		return purchaseOrderDao;
	}

	public void setPurchaseOrderDao(PurchaseOrderDao purchaseOrderDao) 
	{
		this.purchaseOrderDao = purchaseOrderDao;
	}
	
	public ProcessAttachments getProcessAttachments() 
	{
		return processAttachments;
	}
	
	public void setProcessAttachments(ProcessAttachments processAttachments) 
	{
		this.processAttachments = processAttachments;
	}

	/**
	 * This method performs processing of mailbox. 
	 * It downloads & stores mails to database from mailbox.
	 * After storing storing new mails, it performs processing of mails. 
	 * 
	 */
	@Transactional
	public void execute() 
	{
		// connect mailbox
		try 
		{
			connectMailbox();
		} 
		catch (MessagingException e) 
		{
			System.out.println("Could not connect mailbox: ");
			e.printStackTrace();
		}
		
		// Fetch all available XSDs from database
		xsdList = purchaseOrderDao.getAllXsd();
		
		XmlValidator.setXsdList(xsdList);
		
		downloadedMails = new ArrayList<Map>();
		processedMessage = new ArrayList<Message>();
		
		int totalMails =0;
		try 
		{
			totalMails = inbox.getMessageCount();
		}
		catch (MessagingException e) 
		{
			System.out.println("ERROR at counting Mail Messages: ");
			e.printStackTrace();
		}
		
		// download all mails
		long startTime = System.currentTimeMillis();
		for( int i=1 ; i<=totalMails ; i++ )
		{
			System.out.println("MESSAGE #"+i);
			try 
			{
				Message message = inbox.getMessage(i);

				downloadMail(message);
				processedMessage.add(message);
			}
			catch (MessagingException | ParseException | IOException e) 
			{
				System.out.println("ERROR at reading mail no: "+i);
				e.printStackTrace();
			}
			catch (Exception e)
			{
				System.out.println("ERROR while downloading mail: ");
				e.printStackTrace();
			}
		}
		System.out.println("Download completed for "+totalMails+" mails in "+(System.currentTimeMillis() - startTime)/1000+" seconds");
		
		// disconnect mailbox
		try 
		{
			if(!processedMessage.isEmpty())
			{
				Message processedMessages[] = processedMessage.toArray(new Message[processedMessage.size()]);
				inbox.setFlags(processedMessages,new Flags(Flag.SEEN), true);
			}

			inbox.close(true);
			store.close();
		}
		catch (MessagingException e1) 
		{
			e1.printStackTrace();
		}
		
		// store downloaded mails in database table
		startTime = System.currentTimeMillis();
		int i=1;
		for(Map mailMessage : downloadedMails)
		{
			try
			{
				System.out.println("MESSAGE #"+i);
				storeMail(mailMessage);

			}
			catch(Exception e)
			{
				System.out.println("ERROR while storing mail");
				e.printStackTrace();
			}
			i++;
		}
		System.out.println("Storing completed for "+totalMails+" mails in "+(System.currentTimeMillis() - startTime)/1000+" seconds");
	
		// process Mails
		try
		{
			processMails();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Could not process mails");
		}
	}
	
	/**
	 * This method connects mailbox.
	 * 
	 * @throws MessagingException
	 */
	private void connectMailbox() throws MessagingException
	{
		// load mail properties
		props = new Properties();
		props.put("mail.imap.host", "mail.altamira.com.br");
		props.put("mail.store.protocol", "imaps");
		props.put("mail.imap.port", 993);
		props.put("mail.imap.ssl.trust", "true");
		
		// Create the session and get the store for read the mail
		Session session = Session.getDefaultInstance(props);

		store = session.getStore("imaps");
		store.connect("mail.altamira.com.br","tests.nfe@altamira.com.br", "testsNFe");

		// Mention the folder name which you want to read
		inbox = store.getFolder("INBOX");

		// Open the inbox using store
		inbox.open(IMAPFolder.READ_WRITE);
	}
	
	/**
	 * This method downloads particular mail-message from mailbox by fetching
	 * message id, sender id, received date and mail-message.
	 * 
	 * @param message - Mail Message
	 * @throws Exception
	 */
	private void downloadMail(Message message) throws Exception
	{
		// Get the message id
		String messageId = null;
		try
		{
			messageId = message.getHeader("Message-Id")[0];
		}
		catch(NullPointerException e)
		{
			messageId = "[SPAM]"+message.getSubject();
		}
		
		// Get the sender id
		Address[] froms = message.getFrom();
		String senderId = froms == null ? null : ((InternetAddress) froms[0]).getAddress();
		
		// Get the received date
		String dateString = message.getHeader("Date")[0];
		
		SimpleDateFormat sdf1 = null;
		Date date = null;
		try
		{
			sdf1 = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", new Locale("en", "US"));
			date = sdf1.parse(dateString);
		}
		catch(Exception e)
		{
			sdf1 = new SimpleDateFormat("d MMM yyyy HH:mm:ss");
			date = sdf1.parse(dateString);
		}
		
		sdf1.applyPattern("dd/MM/yyyy HH:mm:ss");
		String formattedDateString = sdf1.format(date);
		
		int bytes = message.getSize();
		long start = System.currentTimeMillis();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		message.writeTo(baos);
		long end = System.currentTimeMillis();
		double time = (double)(end-start)/1000;
		System.out.println("Download speed:"+ Math.round( ( ((double)bytes / 131072) / time ) *1000.0 )/1000.0+" Mbps" );
		
		// store the mail in arraylist
		Map messageMap = new HashMap();
		messageMap.put("MESSAGE_ID", messageId);
		messageMap.put("SENDER_ID", senderId);
		messageMap.put("RECEIVED_DATE", formattedDateString);
		messageMap.put("MAIL_MESSAGE", baos);	
		
		downloadedMails.add(messageMap);
		
	}
	
	/**
	 * This method stores all mails in database that are downloaded from mailbox.
	 * 
	 * @param downloadedMail - Map of downloaded mail messages
	 * @throws Exception
	 */
	private void storeMail(Map downloadedMail) throws Exception
	{
		String messageId = (String) downloadedMail.get("MESSAGE_ID");
		String senderId = (String) downloadedMail.get("SENDER_ID");
		String receivedDate = (String) downloadedMail.get("RECEIVED_DATE");
		ByteArrayOutputStream mailMessage = (ByteArrayOutputStream) downloadedMail.get("MAIL_MESSAGE");
		
		// check that mail already exists in database
		boolean isMailStored = purchaseOrderDao.isMailAlreadyStored(messageId, senderId, receivedDate);
		
		// store mail in database, if it does not exists
		if(!isMailStored)
		{
			purchaseOrderDao.storeMail(messageId, senderId, receivedDate, mailMessage);
		}
	}
	
	/**
	 * This message does processing for unprocessed mails stored in database
	 */
	private void processMails()
	{
		// Fetch all Unprocessed Mails from database
		List<Mail> mails = purchaseOrderDao.selectUnProcessedMails();
		
		for(Mail tempMail : mails)
		{
			ByteArrayInputStream bais = tempMail.getMailMessage();
			
			MimeMessage tempMsg=null;
			try 
			{
				tempMsg = new MimeMessage(Session.getDefaultInstance(props), bais);
			}
			catch (MessagingException e) 
			{
				System.out.println("ERROR while reading stored mail");
				e.printStackTrace();
			}
			
			boolean deleteMessage = processMessageContent(tempMsg);
			
			if(deleteMessage)
			{
				purchaseOrderDao.setProcessedFlagForMail(tempMail.getMessageId(), 
											   tempMail.getSenderId(), 
											   tempMail.getReceivedDate(), 
											   "Y");
			}
		}
	}
	
	/**
	 * This method performs processing of a Mail message fetched from database.
	 * 
	 * @param msg - Mail message
	 * @return
	 */
	private boolean processMessageContent(Message msg)
	{
		// set this flag to false when any exception is thrown as a result of following
		// 1) XML Syntax Validation
		// 2) XML Schema Validation
		// 3) Exception while processing 
		// default value is set to true
		boolean deleteMessage = true;
		boolean mimemessage = false;
		MimeMessage message=null;
		
		try
		{
			String contentType = msg.getContentType();
			//System.out.println(contentType);
			
			if(contentType.contains("iso-8859-10"))
			{
				contentType = contentType.replace("iso-8859-10", "iso-8859-1");
				message = new MimeMessage((MimeMessage)msg);
				message.setHeader("Content-Type", contentType);
				message.saveChanges();
				mimemessage = true;
			}
			else if(contentType.contains("charset=\"\""))
			{
				contentType = contentType.replace("charset=\"\"", "charset=\"iso-8859-1\"");	
				message = new MimeMessage((MimeMessage)msg);
				message.setHeader("Content-Type", contentType);
				message.saveChanges();
				mimemessage = true;
			}
			
			Object obj = null;
			if(mimemessage)
			{
				obj = message.getContent();
			}
			else
			{
				obj = msg.getContent();
			}
			
			if(obj instanceof String)
			{
				//System.out.println("simple message: "+obj);
			}
			else if(obj instanceof Multipart )
			{
				Multipart mp = (Multipart) obj;
				int count = mp.getCount();
				
				for (int i = 0; i < count; i++)
				{
					BodyPart bodyPart = mp.getBodyPart(i);
					
					if(!Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) 
					{
						continue; // dealing with attachments only
						
					}
					
					try
					{
						processBodyPart(bodyPart);
					}
					catch(XMLValidationException e)
					{
						deleteMessage = false;
					}
				}
			}
			else
			{
				deleteMessage = false;
			}
		}
		catch (Exception ex)
		{
			System.out.println("Exception arise at get Content:");
			ex.printStackTrace();
			
			deleteMessage = false;
		}
		
		return deleteMessage;
	}
	
	/**
	 * This method fetches XML from the mail's body part.
	 * 
	 * @param part
	 * @throws XMLValidationException
	 */
	private void processBodyPart(BodyPart part) throws XMLValidationException
	{
		String fileName=null;
		try 
		{
			fileName = part.getFileName();
		} 
		catch (MessagingException e) 
		{
			e.printStackTrace();
		}
		
		String extension = "";
		int i = fileName.lastIndexOf('.');
		if(i > 0)
		{
			extension = fileName.substring(i+1);
		}
		
		if(extension.equalsIgnoreCase("xml"))
		{
			Map<String, String> processedXMLMap = processAttachments.processXML(part, fileName);
		}
	}
	
}
