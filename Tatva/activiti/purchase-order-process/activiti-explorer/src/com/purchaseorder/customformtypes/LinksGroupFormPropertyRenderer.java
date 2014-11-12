package com.purchaseorder.customformtypes;

import java.util.Map;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.form.FormProperty;
import org.activiti.explorer.ui.form.AbstractFormPropertyRenderer;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.ui.Field;

/**
 * @author PARTH
 */
public class LinksGroupFormPropertyRenderer extends AbstractFormPropertyRenderer
{

	private static final long serialVersionUID = 1L;
	
	@Autowired
	LinksGroup linkgroup;
	
	@Autowired
	RuntimeService runtimeService;

	public RuntimeService getRuntimeService() 
	{
		return runtimeService;
	}

	public void setRuntimeService(RuntimeService runtimeService) 
	{
		this.runtimeService = runtimeService;
	}

	public LinksGroup getLinkgroup() 
	{
		return linkgroup;
	}

	public void setLinkgroup(LinksGroup linkgroup) 
	{
		this.linkgroup = linkgroup;
	}

	public LinksGroupFormPropertyRenderer()
	{
		super(LinksGroupFormType.class);
	}

	@Override
	public Field getPropertyField(FormProperty formProperty) 
	{
		String pid = formProperty.getValue();
		
		Map reportList = (Map) runtimeService.getVariable(pid, "reportList");
		
		linkgroup.removeAllComponents();
		linkgroup.load(reportList);
		return linkgroup;
	}

}
