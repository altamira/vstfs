package com;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.imageio.ImageIO;

import dao.DatabaseConnector;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import oracle.jdbc.OracleResultSet;


public class RequestReportGenerator 
{
	public static void main(String ar[]) throws JRException
	{
		Map<String,Object> parameters = new HashMap<String,Object>();
		InputStream reportStream = null;
		try 
		{
			reportStream = new FileInputStream("D:/iReport/Purchase Order Process Reports/Steel Purchasing - Amazon AWS/Request/Request.jasper");
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		
		DatabaseConnector dc = new DatabaseConnector();
		
		dc.connectDatabase();
		OracleResultSet rs = dc.getRequestReportDateById(new BigDecimal("8"));
		
		Vector requestReportList = new Vector();
		List<Date> dateList = new ArrayList<Date>(); 
		
		BigDecimal lastMaterialId = new BigDecimal(0);
		int count = 0;
		BigDecimal sumRequestWeight = new BigDecimal(0);
		BigDecimal totalWeight = new BigDecimal(0);
		
		try
		{
			RequestReport r = new RequestReport();
			r.setId(null);
			r.setLamination(null);
			r.setLength(null);
			r.setThickness(null);
			r.setTreatment(null);
			r.setWidth(null);
			r.setArrivalDate(null);
			r.setWeight(null);
			
			requestReportList.add(r);
			
			while(rs.next())
			{
				RequestReport rr = new RequestReport();
				
				BigDecimal currentMaterialId = rs.getBigDecimal("ID");
				
				if(lastMaterialId.compareTo(currentMaterialId)==0)
				{
					rr.setWeight(rs.getBigDecimal("WEIGHT"));
					rr.setArrivalDate(rs.getDate("ARRIVAL_DATE"));
					
					// copy REQUEST_DATE into dateList
					dateList.add(rs.getDate("ARRIVAL_DATE"));
					
					System.out.println(rs.getBigDecimal("WEIGHT"));
					totalWeight = totalWeight.add(rs.getBigDecimal("WEIGHT"));
					sumRequestWeight = sumRequestWeight.add(rs.getBigDecimal("WEIGHT"));
					count++;
				}
				else
				{
					rr.setId(rs.getBigDecimal("ID"));
					rr.setLamination(rs.getString("LAMINATION"));
					rr.setTreatment(rs.getString("TREATMENT"));
					rr.setThickness(rs.getBigDecimal("THICKNESS"));
					rr.setWidth(rs.getBigDecimal("WIDTH"));
					rr.setLength(rs.getBigDecimal("LENGTH"));
					rr.setWeight(rs.getBigDecimal("WEIGHT"));
					rr.setArrivalDate(rs.getDate("ARRIVAL_DATE"));
					
					// copy REQUEST_DATE into dateList
					dateList.add(rs.getDate("ARRIVAL_DATE"));
					
					totalWeight = totalWeight.add(rs.getBigDecimal("WEIGHT"));
					lastMaterialId = currentMaterialId;
					
					if(count!=0)
					{
						RequestReport addition = new RequestReport();
						addition.setWeight(sumRequestWeight);
						
						requestReportList.add(addition);
					}
					
					sumRequestWeight = rs.getBigDecimal("WEIGHT");
					count=0;
				}
				
				requestReportList.add(rr);
			}
			
			if(count>0)
			{
				RequestReport addition = new RequestReport();
				addition.setWeight(sumRequestWeight);
				
				requestReportList.add(addition);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		Collections.sort(dateList);
		
		parameters.put("REQUEST_START_DATE", dateList.get(0));
		parameters.put("REQUEST_END_DATE", dateList.get(dateList.size()-1));		
		parameters.put("REQUEST_ID", new BigDecimal("8"));
		parameters.put("TOTAL_WEIGHT", totalWeight);
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
		
		JRDataSource dataSource = new JRBeanCollectionDataSource(requestReportList, false);
		
		JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, parameters, dataSource);
		
		JasperExportManager.exportReportToPdfFile(jasperPrint, "D:/REQUEST sample/request-1.pdf");
	}
}
