package com;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.imageio.ImageIO;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import dao.DatabaseConnector;

public class PlanningReportGenerator 
{
	public static void main(String ar[]) throws JRException
	{
		Map<String,Object> parameters = new HashMap<String,Object>();
		InputStream reportStream = null;
		try 
		{
			reportStream = new FileInputStream("D:/iReport/Purchase Order Process Reports/Steel Purchasing - Amazon AWS/Purchase Planning/PurchasePlanning final.jasper");
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		
		DatabaseConnector dc = new DatabaseConnector();
		dc.connectDatabase();
		Connection connection = dc.getConnection();
		
		BigDecimal planningId = new BigDecimal("1");
		parameters.put("PLANNING_ID", planningId);
		
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
		
		parameters.put("USERNAME", "PARTH");
		
		JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, parameters, connection);
		
		JasperExportManager.exportReportToPdfFile(jasperPrint, "D:/REQUEST sample/planning-new.pdf");
	}
}
