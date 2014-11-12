package com.purchaseorder.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

@Table(name="MAILBOX")
@Entity
public class MailBox 
{
	@EmbeddedId
	MailBoxId mailBoxId;
	
	@Lob
	@Column(name="MAIL_MESSAGE")
	byte[] mailMessage;
	
	@Column(name="ISPROCESSED", length=1, columnDefinition="CHAR")
	String isProcessed;

	public MailBoxId getMailBoxId() 
	{
		return mailBoxId;
	}

	public void setMailBoxId(MailBoxId mailBoxId) 
	{
		this.mailBoxId = mailBoxId;
	}

	public byte[] getMailMessage() 
	{
		return mailMessage;
	}

	public void setMailMessage(byte[] mailMessage) 
	{
		this.mailMessage = mailMessage;
	}

	public String getIsProcessed() 
	{
		return isProcessed;
	}

	public void setIsProcessed(String isProcessed) 
	{
		this.isProcessed = isProcessed;
	}
}
