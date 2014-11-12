package com.purchaseorder.entity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import oracle.jdbc.OracleResultSet;
import oracle.sql.OPAQUE;
import oracle.xdb.XMLType;

import org.apache.commons.dbcp.DelegatingResultSet;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * 
 * @author PARTH
 * 
 * Implementation for Hibernate-XMLTYPE usertype 
 *
 */
public class HibernateXMLType implements UserType 
{
	@Override
    public int[] sqlTypes() 
	{
        return new int[] {oracle.xdb.XMLType._SQL_TYPECODE};
    }
	
	@Override
    public Class returnedClass() 
	{
        return String.class;
    }
	
	@Override
    public int hashCode(Object o) 
	{
        return o.hashCode();
    }
	
	@Override
    public Object assemble(Serializable cached, Object owner) 
	{
        try 
        {
            return cached;
        } 
        catch (Exception e) 
        {
            throw new HibernateException("Could not assemble String to Document", e);
        }
    }
	
	@Override
    public Serializable disassemble(Object o) 
	{
        try 
        {
            return (Serializable) o;
        } 
        catch (Exception e)
        {
            throw new HibernateException("Could not disassemble Document to Serializable", e);
        }
    }
	
	@Override
    public Object replace(Object original, Object target, Object owner) 
	{
        return deepCopy(original);
    }
	
	@Override
    public boolean equals(Object o1, Object o2) 
	{
        return Objects.equals(o1, o2);
    }
	
	@Override
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor impl, Object owner) throws SQLException 
    {
		XMLType xmlType = null;
		String doc = null;
		try 
		{
			OPAQUE op = null;
			OracleResultSet ors = null;
			
			if (rs instanceof OracleResultSet) 
			{
				ors = (OracleResultSet)rs;
			} 
			else if(rs instanceof DelegatingResultSet)
			{
				DelegatingResultSet drs = (DelegatingResultSet) rs;
				ors = (OracleResultSet) drs.getInnermostDelegate();
			}
			else
			{
				throw new UnsupportedOperationException("ResultSet needs to be of type OracleResultSet");
			}
			
			op = ors.getOPAQUE(names[0]);
			if(op != null) 
			{
				xmlType = XMLType.createXML ( op );
				doc = xmlType.getStringVal();
			}

		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally 
		{
			if (null != xmlType) 
			{
				xmlType.close();
			}
		}
		
		return doc;
    }
	
	@Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws SQLException 
    {
        XMLType xmlType = null;
        try 
        {
            if (value != null) 
            {
                xmlType = XMLType.createXML(st.getConnection(), stringToDom((String) value));
            }
            st.setObject(index, xmlType);
        } 
        catch (Exception e) 
        {
			e.printStackTrace();
		} 
        finally 
        {
            if (xmlType != null) 
            {
                xmlType.close();
            }
        }
    }
	
	@Override
	public boolean isMutable() 
	{
		return false;
	}
	
	@Override
	public Object deepCopy(Object value)
	{
		return value;
	}
	
	public static String domToString(Document _document)throws TransformerException
	{
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		DOMSource source = new DOMSource(_document);
		StringWriter sw=new StringWriter();
		StreamResult result = new StreamResult(sw);
		transformer.transform(source, result);
		return sw.toString();
	}
	
	public static Document stringToDom(String xmlSource)throws SAXException, ParserConfigurationException, IOException
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(new ByteArrayInputStream(xmlSource.getBytes("UTF-8")));
	}
}
