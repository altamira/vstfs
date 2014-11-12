package com.purchaseorder.filter;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.IdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class CustomAuthenticationProvider implements AuthenticationProvider 
{
	@Autowired
	IdentityService identityService;
	
	public IdentityService getIdentityService() 
	{
		return identityService;
	}

	public void setIdentityService(IdentityService identityService) 
	{
		this.identityService = identityService;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException 
	{
		String userName = String.valueOf(authentication.getPrincipal());
		String passWord = String.valueOf(authentication.getCredentials());
		
		boolean validUser = identityService.checkPassword(userName, passWord);
		
		if(validUser)
		{
			List<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();
			grantedAuths.add(new SimpleGrantedAuthority("ROLE_USER"));
			
			return new UsernamePasswordAuthenticationToken(userName, passWord, grantedAuths);
		}
		else
		{
			throw new BadCredentialsException("User not authorized");
		}
		
	}

	@Override
	public boolean supports(Class<?> authentication) 
	{
		return true;
	}
}
