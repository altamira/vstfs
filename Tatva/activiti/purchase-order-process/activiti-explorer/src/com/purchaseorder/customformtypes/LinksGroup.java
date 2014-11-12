package com.purchaseorder.customformtypes;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.activiti.explorer.ExplorerApp;
import org.activiti.explorer.I18nManager;

import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.terminal.StreamResource;
import com.vaadin.ui.Field;
import com.vaadin.ui.Link;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * @author PARTH
 * 
 * LinksGroup field type displays group of links that are able to open application/pdf page in new browser tab.
 *
 */
public class LinksGroup extends VerticalLayout implements Field 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected TextField wrappedField;                                                           
	
	protected I18nManager i18nManager;

	/**
	 * Creates links for each Map elements
	 * 
	 * @param reportList
	 */
	public void load(final Map<String, JasperPrint> reportList)
	{
		setSpacing(true);
		
		StreamResource resource[] = new StreamResource[reportList.size()];
		Link links[] = new Link[reportList.size()];
		
		int temp=0;
		for(final String key : reportList.keySet())
		{
			resource[temp] = new StreamResource(new StreamResource.StreamSource() 
			{
				@Override
				public InputStream getStream() 
				{
			        try 
			        {
			        	JasperPrint print = (JasperPrint) reportList.get(key);
						
						return new ByteArrayInputStream(JasperExportManager.exportReportToPdf(print));
					} 
			        catch (Exception e) 
			        {
						e.printStackTrace();
					}
			        
			        return null;
				}
				
			}, key+".pdf", ExplorerApp.get());
			
			resource[temp].setMIMEType("application/pdf");
			links[temp] = new Link(key+".pdf", resource[temp]);
			links[temp].setTargetName("_blank");
			
			addComponent(links[temp]);
			
			temp++;
		}
		
		// Invisible textfield, only used as wrapped field
		wrappedField = new TextField();
		wrappedField.setVisible(false);
		addComponent(wrappedField);
	}

	public boolean isInvalidCommitted() 
	{
		return wrappedField.isInvalidCommitted();
	}

	public void setInvalidCommitted(boolean isCommitted) 
	{
		wrappedField.setInvalidCommitted(isCommitted);
	}

	public void commit() throws SourceException, InvalidValueException 
	{
		wrappedField.commit();
	}

	public void discard() throws SourceException 
	{
		wrappedField.discard();
	}

	public boolean isWriteThrough() 
	{
		return wrappedField.isWriteThrough();
	}

	public void setWriteThrough(boolean writeThrough) throws SourceException, InvalidValueException 
	{
		wrappedField.setWriteThrough(true);
	}

	public boolean isReadThrough() 
	{
		return wrappedField.isReadThrough();
	}

	public void setReadThrough(boolean readThrough) throws SourceException 
	{
		wrappedField.setReadThrough(readThrough);
	}

	public boolean isModified() 
	{
		return wrappedField.isModified();
	}

	public void addValidator(Validator validator) 
	{
		wrappedField.addValidator(validator);
	}

	public void removeValidator(Validator validator) 
	{
		wrappedField.removeValidator(validator);
	}

	public Collection<Validator> getValidators() 
	{
		return wrappedField.getValidators();
	}

	public boolean isValid() 
	{
		return wrappedField.isValid();
	}

	public void validate() throws InvalidValueException 
	{
		wrappedField.validate();
	}

	public boolean isInvalidAllowed() 
	{
		return wrappedField.isInvalidAllowed();
	}

	public void setInvalidAllowed(boolean invalidValueAllowed) throws UnsupportedOperationException 
	{
		wrappedField.setInvalidAllowed(invalidValueAllowed);
	}

	public Object getValue() 
	{
		return wrappedField.getValue();
	}

	public void setValue(Object newValue) throws ReadOnlyException, ConversionException 
	{
		wrappedField.setValue(newValue);
	}

	protected Object getSelectedUserLabel() 
	{
		return wrappedField.getValue();
	}

	public Class< ? > getType() 
	{
		return wrappedField.getType();
	}

	public void addListener(ValueChangeListener listener) 
	{
		wrappedField.addListener(listener);
	}

	public void removeListener(ValueChangeListener listener) 
	{
		wrappedField.removeListener(listener);
	}

	public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) 
	{
		wrappedField.valueChange(event);
	}

	public void setPropertyDataSource(Property newDataSource) 
	{
		wrappedField.setPropertyDataSource(newDataSource);
	}

	public Property getPropertyDataSource() 
	{
		return wrappedField.getPropertyDataSource();
	}

	public int getTabIndex() 
	{
		return wrappedField.getTabIndex();
	}

	public void setTabIndex(int tabIndex) 
	{
		wrappedField.setTabIndex(tabIndex);
	}

	public boolean isRequired() 
	{
		return wrappedField.isRequired();
	}

	public void setRequired(boolean required) 
	{
		wrappedField.setRequired(required);
	}

	public void setRequiredError(String requiredMessage) 
	{
		wrappedField.setRequiredError(requiredMessage);
	}

	public String getRequiredError() 
	{
		return wrappedField.getRequiredError();
	}

	@Override
	public void focus() 
	{
		wrappedField.focus();
	}

}
