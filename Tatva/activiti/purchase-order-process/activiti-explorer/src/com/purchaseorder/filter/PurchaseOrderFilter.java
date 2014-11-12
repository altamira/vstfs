package com.purchaseorder.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.activiti.explorer.filter.ExplorerFilter;

public class PurchaseOrderFilter extends ExplorerFilter implements Filter
{
	public PurchaseOrderFilter()
	{
		super();
	}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException 
	{
		super.init(filterConfig);
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException 
	{
		HttpServletRequest req = (HttpServletRequest) request;
		String path = req.getRequestURI().substring(req.getContextPath().length());
		int indexSlash = path.indexOf("/", 1);
		String firstPart = null;
		
		if (indexSlash > 0)
		{
			firstPart = path.substring(0, indexSlash);
		}
		else 
		{
			firstPart = path;
		}
		
		if(firstPart.equalsIgnoreCase("/webservice"))
		{
			chain.doFilter(request, response);
		}
		else if(firstPart.equalsIgnoreCase("/pages"))
		{
			chain.doFilter(request, response);
		}
		else if(firstPart.equalsIgnoreCase("/ws"))
		{
			chain.doFilter(request, response);
		}
		else if(firstPart.equalsIgnoreCase("/index.jsp"))
		{
			chain.doFilter(request, response);
		}
		else if(firstPart.startsWith("/j_spring_security_check"))
		{
			chain.doFilter(request, response);
		}
		else if(firstPart.equalsIgnoreCase("/j_spring_security_logout"))
		{
			chain.doFilter(request, response);
		}
		else
		{
			super.doFilter(request, response, chain);			
		}
		
	}
	
	@Override
	public void destroy() 
	{
		super.destroy();
	}
	
}
