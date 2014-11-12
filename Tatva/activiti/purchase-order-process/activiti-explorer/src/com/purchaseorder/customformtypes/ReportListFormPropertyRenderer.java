package com.purchaseorder.customformtypes;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.form.FormProperty;
import org.activiti.explorer.Messages;
import org.activiti.explorer.ui.form.AbstractFormPropertyRenderer;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.ui.Field;
import com.vaadin.ui.ListSelect;

/**
 * @author PARTH
 * 
 * Form property renderer for pdflist type
 *
 */
public class ReportListFormPropertyRenderer extends AbstractFormPropertyRenderer 
{
	private static final long serialVersionUID = 1L;
	
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
	
	public ReportListFormPropertyRenderer() 
	{
		super(ReportListFormType.class);
	}

	@Override
	public Field getPropertyField(FormProperty formProperty) 
	{
		String pid = formProperty.getValue();
		
		String reportList = (String)runtimeService.getVariable(pid, "xmllist");
		
		reportList = reportList.replace("[", "");
		reportList = reportList.replace("]", "");
		
		String[] reportNames = reportList.split(",");
		
		ListSelect list = new ListSelect(getPropertyLabel(formProperty));
		list.setRequired(formProperty.isRequired());
		list.setRequiredError(getMessage(Messages.FORM_FIELD_REQUIRED, getPropertyLabel(formProperty)));
		list.setEnabled(formProperty.isWritable());
		list.setMultiSelect(true);
		list.setRows(5);
		
		// Fill ListSelect
		for(String tempString : reportNames)
		{
			list.addItem(tempString+".pdf");
			list.setItemCaption(tempString+".pdf", tempString+".pdf");
			list.select(tempString+".pdf");
		}
		
		return list;
	}

}
