package com.purchaseorder.webservice;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Test 
{
	public static void main(String ar[])
	{
		try 
		{
			String str = "10.258";
			
			BigDecimal matWidth = new BigDecimal(str);
			System.out.println(matWidth);
			matWidth = matWidth.setScale(2, RoundingMode.CEILING);
			System.out.println(matWidth);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}
