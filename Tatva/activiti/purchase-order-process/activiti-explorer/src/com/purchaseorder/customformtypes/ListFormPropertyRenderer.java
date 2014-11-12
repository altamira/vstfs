package com.purchaseorder.customformtypes;

import java.util.List;

import org.activiti.engine.form.FormProperty;
import org.activiti.explorer.Messages;
import org.activiti.explorer.ui.form.AbstractFormPropertyRenderer;
import org.springframework.beans.factory.annotation.Autowired;

import com.purchaseorder.dao.PurchaseOrderDao;
import com.vaadin.ui.Field;
import com.vaadin.ui.ListSelect;

/**
 * @author PARTH
 * 
 * Form property renderer for list type.
 *
 */
public class ListFormPropertyRenderer extends AbstractFormPropertyRenderer 
{
	private static final long serialVersionUID = 1L;
	
	@Autowired
	private PurchaseOrderDao purchaseOrderDao;
	
	public PurchaseOrderDao getPurchaseOrderDao() 
	{
		return purchaseOrderDao;
	}

	public void setPurchaseOrderDao(PurchaseOrderDao purchaseOrderDao) 
	{
		this.purchaseOrderDao = purchaseOrderDao;
	}

	public ListFormPropertyRenderer()
	{
		super(ListFormType.class);
	}

	@Override
	public Field getPropertyField(FormProperty formProperty) 
	{
		ListSelect list = new ListSelect(getPropertyLabel(formProperty));
		list.setRequired(formProperty.isRequired());
		list.setRequiredError(getMessage(Messages.FORM_FIELD_REQUIRED, getPropertyLabel(formProperty)));
		list.setEnabled(formProperty.isWritable());
		list.setMultiSelect(true);
		list.setRows(15);
		
		// Fill ListSelect
		List<String> nfeList = purchaseOrderDao.listXmls();
		
		for(String xmlName : nfeList)
		{
			list.addItem(xmlName);
			list.setItemCaption(xmlName, xmlName);
		}
		
		return list;
	}

}
