package com;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.Vector;

import dao.DatabaseConnector;

import oracle.jdbc.OracleResultSet;


public class RequestReport 
{
	private BigDecimal id;
	
	private String lamination;
	
	private String treatment;
	
	private BigDecimal thickness;
	
	private BigDecimal width;
	
	private BigDecimal length;
	
	private BigDecimal weight;
	
	private Date arrivalDate;

	public BigDecimal getId() {
		return id;
	}

	public void setId(BigDecimal id) {
		this.id = id;
	}

	public String getLamination() {
		return lamination;
	}

	public void setLamination(String lamination) {
		this.lamination = lamination;
	}

	public String getTreatment() {
		return treatment;
	}

	public void setTreatment(String treatment) {
		this.treatment = treatment;
	}

	public BigDecimal getThickness() {
		return thickness;
	}

	public void setThickness(BigDecimal thickness) {
		this.thickness = thickness;
	}

	public BigDecimal getWidth() {
		return width;
	}

	public void setWidth(BigDecimal width) {
		this.width = width;
	}

	public BigDecimal getLength() {
		return length;
	}

	public void setLength(BigDecimal length) {
		this.length = length;
	}

	public BigDecimal getWeight() {
		return weight;
	}

	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}

	public Date getArrivalDate() {
		return arrivalDate;
	}

	public void setArrivalDate(Date arrivalDate) {
		this.arrivalDate = arrivalDate;
	}

	public static Collection getReportData()
	{
		DatabaseConnector dc = new DatabaseConnector();
		
		dc.connectDatabase();
		OracleResultSet rs = dc.getRequestReportDateById(new BigDecimal("43"));
		
		Vector requestReportList = new Vector();
		
		BigDecimal lastMaterialId = new BigDecimal("");
		int count = 0;
		BigDecimal sumRequestWeight = new BigDecimal(0);
		
		try
		{
			while(rs.next())
			{
				RequestReport rr = new RequestReport();
				
				BigDecimal currentMaterialId = rs.getBigDecimal("ID");
				
				if(lastMaterialId.compareTo(currentMaterialId)==0)
				{
					rr.setWeight(rs.getBigDecimal("WEIGHT"));
					rr.setArrivalDate(rs.getDate("ARRIVAL_DATE"));
					
					System.out.println(rs.getBigDecimal("WEIGHT"));
					sumRequestWeight = sumRequestWeight.add(rs.getBigDecimal("WEIGHT"));
					count++;
				}
				else
				{
					rr.setLamination(rs.getString("LAMINATION"));
					rr.setTreatment(rs.getString("TREATMENT"));
					rr.setThickness(rs.getBigDecimal("THICKNESS"));
					rr.setWidth(rs.getBigDecimal("WIDTH"));
					rr.setLength(rs.getBigDecimal("LENGTH"));
					rr.setWeight(rs.getBigDecimal("WEIGHT"));
					rr.setArrivalDate(rs.getDate("ARRIVAL_DATE"));
					
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
		}
		catch(Exception e)
		{
			// do nothing
		}
		
		return requestReportList;
	}
	
	public static void main(String ar[])
	{
		getReportData();
	}
	
}
