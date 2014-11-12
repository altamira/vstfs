package com;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.imageio.ImageIO;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import dao.DatabaseConnector;


public class QuotationReportGenerator 
{
	public static void main(String ar[]) throws JRException
	{
		Map<String,Object> parameters = new HashMap<String,Object>();
		InputStream reportStream = null;
		try 
		{
			reportStream = new FileInputStream("D:/iReport/Purchase Order Process Reports/Steel Purchasing - Amazon AWS/Quotation/after ddl change/quotation-new.jasper");
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		
		BigDecimal quotationId = new BigDecimal("1");
		
		DatabaseConnector dc = new DatabaseConnector();
		dc.connectDatabase();
		Connection connection = dc.getConnection();
		
		List<BigDecimal> priceList = new ArrayList<BigDecimal>();
		
		priceList.add(new BigDecimal("3000"));
		priceList.add(new BigDecimal("3200"));
		priceList.add(new BigDecimal("3400"));
		priceList.add(new BigDecimal("3600"));
		priceList.add(new BigDecimal("3800"));
		priceList.add(new BigDecimal("4000"));
		priceList.add(new BigDecimal("4200"));
				
		parameters.put("QUOTATION_ID", quotationId);
		parameters.put("PRICELIST", priceList);
		parameters.put("PRICELIST_CODE", "BM");
		parameters.put("QUOTATION_DATE", new Date());
		parameters.put("USERNAME", "PARTH");
		
		Locale locale = new Locale.Builder().setLanguage("pt").setRegion("BR").build();
		parameters.put("REPORT_LOCALE", locale);
		
		BufferedImage imfg=null;
		try 
		{
			imfg = ImageIO.read(new File("D://logo_altamira.png"));
		} 
		catch (IOException e1) 
		{
			e1.printStackTrace();
		}
		
		parameters.put("altamira_logo", imfg);
		
		JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, parameters, connection);
		
		JasperExportManager.exportReportToPdfFile(jasperPrint, "D:/REQUEST sample/quotation-new.pdf");
	}
}
