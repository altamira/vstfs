/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.erp.security;

import com.sun.appserv.security.AppservRealm;
import com.sun.enterprise.security.auth.realm.BadRealmException;
import com.sun.enterprise.security.auth.realm.InvalidOperationException;
import com.sun.enterprise.security.auth.realm.NoSuchRealmException;
import com.sun.enterprise.security.auth.realm.NoSuchUserException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author PARTH
 */
public class AltamiraCustomRealm extends AppservRealm {
 
    /** JAAS Context parameter name */ public static final String PARAM_JAAS_CONTEXT = "jaas-context";
    
    /** Authentication type description */
    
    public static final String AUTH_TPYE = "Authentication done by checking user at Camunda Engine";

    @Override
    protected void init(Properties properties) throws BadRealmException, NoSuchRealmException {
        
        String propJaasContext = properties.getProperty(PARAM_JAAS_CONTEXT);
        
        if (propJaasContext != null) setProperty(PARAM_JAAS_CONTEXT, propJaasContext);
        
        Logger logger = Logger.getLogger(this.getClass().getName());
        
        String realmName = this.getClass().getSimpleName();
        
        logger.log(Level.INFO, realmName + " started. ");
        logger.log(Level.INFO, realmName + ": " + getAuthType());
        
        for (Entry<Object, Object> property:properties.entrySet()) logger.log(Level.INFO, property.getKey() + ": " + property.getValue());
        
    }

    @Override
    public String getAuthType() {
        
        return AUTH_TPYE;
    }

    @Override
    public Enumeration getGroupNames(String userId) throws InvalidOperationException, NoSuchUserException {
        
        return Collections.enumeration(SecurityUtil.getGroups(userId));
        
    }
    
    
}
