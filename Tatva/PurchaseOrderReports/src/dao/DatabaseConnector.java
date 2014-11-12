package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleResultSet;


public class DatabaseConnector 
{
	static Connection conn = null;
	
	public void connectDatabase()
	{
		if(conn==null)
		{
			// load jdbc driver for Oracle Database
			try 
			{
				Class.forName("oracle.jdbc.driver.OracleDriver");
			} 
			catch (ClassNotFoundException e) 
			{
				System.out.println("Could not load JDBC driver for Oracle");
			}
			
			// Make Connection to Oracle Database
			try 
			{
				conn = DriverManager.getConnection("jdbc:oracle:thin:@altamira-head-db.ci3m0ww55h4q.sa-east-1.rds.amazonaws.com:1521:orcl", "head_user_db", "brkm39$sxz");
			} 
			catch (SQLException e) 
			{
				System.out.println("Connection failed to Oracle database");
			}
		}
		
		if(conn!=null)
		{
			System.out.println("Database Connection established successfully");
		}
		else
		{
			System.out.println("Failed to connect database");
		}
	}
	
	public Connection getConnection()
	{
		return conn;
	}
	
	public OracleResultSet getRequestReportDateById(BigDecimal requestId)
	{
		OraclePreparedStatement sqlStatement = null;
		OracleResultSet rs = null;
		
		try 
		{
			StringBuffer selectSql = new StringBuffer().append(" SELECT M.ID, ")
													   .append("        M.LAMINATION, ")
													   .append("        M.TREATMENT, ")
													   .append("        M.THICKNESS, ")
													   .append("        M.WIDTH, ")
													   .append("        M.LENGTH, ")
													   .append("        RT.WEIGHT, ")
													   .append("        RT.ARRIVAL_DATE ")
													   .append(" FROM REQUEST R, REQUEST_ITEM RT, MATERIAL M ")
													   .append(" WHERE R.ID = RT.REQUEST_ID ")
													   .append(" AND RT.MATERIAL_ID = M.ID ")
													   .append(" AND R.ID = ? ");
			
			sqlStatement = (OraclePreparedStatement) conn.prepareStatement(selectSql.toString());
			sqlStatement.setBigDecimal(1, requestId);
			
			rs = (OracleResultSet) sqlStatement.executeQuery();
		}
		catch (SQLException e) 
		{
			e.printStackTrace();
			System.out.println("ERROR");
		}
		
		return rs;
	}
}
