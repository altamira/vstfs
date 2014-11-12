package com.purchaseorder.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class MailBoxId implements Serializable 
{
	@Column(name="MESSAGE_ID")
	String messageId;
	
	@Column(name="SENDER_ID")
	String senderId;
	
	@Column(name="RECEIVED_DATE")
	Date receivedDate;

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

	public Date getReceivedDate() 
	{
		return receivedDate;
	}

	public void setReceivedDate(Date receivedDate) 
	{
		this.receivedDate = receivedDate;
	}
}
