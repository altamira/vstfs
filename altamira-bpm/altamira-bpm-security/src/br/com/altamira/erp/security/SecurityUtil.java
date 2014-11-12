/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.erp.security;

import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.security.auth.login.LoginException;

/**
 *
 * @author PARTH
 */
public class SecurityUtil {
    
    private static Logger logger = Logger.getLogger(SecurityUtil.class.getName());
    
    public static final String AUTHENTICATION_INTERFACE = "java:global/altamira-bpm/UserAuthenticationService";
    
    private static IUserAuthenticationService lookupUserValidationService(String jndiClassname) throws Exception
    {
        Properties properties = new Properties();
 
        properties.put("java.naming.factory.initial" , "com.sun.enterprise.naming.SerialInitContextFactory");
        properties.put("java.naming.factory.url.pkgs", "com.sun.enterprise.naming");
        properties.put("java.naming.factory.state"   , "com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl");
 
        InitialContext remoteContext = new InitialContext(properties);
 
        Object object = remoteContext.lookup(jndiClassname);
 
        return (IUserAuthenticationService) object;
    }
    
    public static void authenticateUser(String username, char[] password) throws LoginException
    {
        logger.log(Level.INFO, AUTHENTICATION_INTERFACE + " trying to authenticate user " + username);
 
        try
        {
            IUserAuthenticationService validationService = lookupUserValidationService(AUTHENTICATION_INTERFACE);
            validationService.validatePassword(username, new String(password));
        }
        catch (LoginException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            //TODO El missatge d'error hauria de dependre de l'idioma de l'usuari
            throw new LoginException((new StringBuilder()).append("Error in user validation: ").append(username).toString() + ". " + e.getMessage());
        }
    }
    
    public static List<String> getGroups(String usid)
    {
        List<String> result = new Vector<String>();
 
        logger.log(Level.INFO, AUTHENTICATION_INTERFACE + " trying to get groups of user  " + usid);
 
        try
        {
            IUserAuthenticationService validationService = SecurityUtil.lookupUserValidationService(AUTHENTICATION_INTERFACE);
            result = validationService.getGroups(usid);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return result;
    }
}
