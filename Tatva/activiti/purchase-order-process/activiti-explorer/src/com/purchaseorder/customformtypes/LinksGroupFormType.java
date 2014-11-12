package com.purchaseorder.customformtypes;

import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.form.AbstractFormType;

/**
 * @author PARTH
 *
 * Form type that shows links on Activiti form.
 */
public class LinksGroupFormType extends AbstractFormType
{
	public static final String TYPE_NAME = "links";

	@Override
	public String getName() 
	{
		return TYPE_NAME;
	}

	@Override
	public Object convertFormValueToModelValue(String formValue) 
	{
		if(formValue != null)
		{
			return formValue;
		}
		
		return null;
	}

	@Override
	public String convertModelValueToFormValue(Object modelValue) 
	{
		if(modelValue==null)
		{
			return null;
		}
		
	    if (!(modelValue instanceof byte[])) 
	    {
	        throw new ActivitiIllegalArgumentException("This form type only support process definitions, but is " + modelValue.getClass());
	    }
		
		return modelValue.toString();
	}

}
