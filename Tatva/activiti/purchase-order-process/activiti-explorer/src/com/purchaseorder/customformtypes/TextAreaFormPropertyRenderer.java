package com.purchaseorder.customformtypes;

import org.activiti.engine.form.FormProperty;
import org.activiti.explorer.Messages;
import org.activiti.explorer.ui.form.AbstractFormPropertyRenderer;

import com.vaadin.ui.Field;
import com.vaadin.ui.TextArea;

/**
 * @author PARTH
 * 
 * Form property renderer for textarea.
 *
 */
public class TextAreaFormPropertyRenderer extends AbstractFormPropertyRenderer 
{
	private static final long serialVersionUID = 1L;
	
	
	public TextAreaFormPropertyRenderer() 
	{
		super(TextAreaFormType.class);
	}

	@Override
	public Field getPropertyField(FormProperty formProperty) 
	{
		TextArea textarea = new TextArea(getPropertyLabel(formProperty));
		textarea.setWidth("500px");
		textarea.setHeight("200px");
		textarea.setRequired(formProperty.isRequired());
		textarea.setEnabled(formProperty.isWritable());
		textarea.setRequiredError(getMessage(Messages.FORM_FIELD_REQUIRED, getPropertyLabel(formProperty)));
		
		if(formProperty.getValue() != null)
		{
			textarea.setValue(formProperty.getValue());
		}
		
		return textarea;
	}

}