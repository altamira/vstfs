package com.purchaseorder.customformtypes;

import org.activiti.engine.form.AbstractFormType;

/**
 * @author PARTH
 *
 * Form type that displayes list on Activiti form
 */
public class ListFormType extends AbstractFormType 
{

	public static final String TYPE_NAME = "list";
	
	@Override
	public String getName() 
	{
		return TYPE_NAME;
	}

	@Override
	public Object convertFormValueToModelValue(String propertyValue) 
	{
		return propertyValue;
	}

	@Override
	public String convertModelValueToFormValue(Object modelValue) 
	{
		if(modelValue==null)
		{
			return null;
		}
		
		return modelValue.toString();
	}

}