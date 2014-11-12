package com.purchaseorder.model;

import java.io.ByteArrayInputStream;

public class Mail 
{
	private String messageId;
	
	private String senderId;

	private String receivedDate;

	private ByteArrayInputStream mailMessage;


	public ByteArrayInputStream getMailMessage() 
	{
		return mailMessage;
	}

	public void setMailMessage(ByteArrayInputStream mailMessage) 
	{
		this.mailMessage = mailMessage;
	}

	public String getMessageId() 
	{
		return messageId;
	}

	public void setMessageId(String messageId) 
	{
		this.messageId = messageId;
	}

	public String getSenderId() 
	{
		return senderId;
	}

	public void setSenderId(String senderId) 
	{
		this.senderId = senderId;
	}

	public String getReceivedDate() 
	{
		return receivedDate;
	}

	public void setReceivedDate(String receivedDate) 
	{
		this.receivedDate = receivedDate;
	}
}
