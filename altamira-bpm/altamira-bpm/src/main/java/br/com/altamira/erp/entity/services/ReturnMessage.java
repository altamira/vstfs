package br.com.altamira.erp.entity.services;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ReturnMessage {
	
	private long error;
	private String message;
	private Object data;
	
	@XmlElement(required=true)
	public long getError() {
		return error;
	}
	
	public void setError(long error) {
		this.error = error;
	}
	
	@XmlElement(required=true)
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
}
