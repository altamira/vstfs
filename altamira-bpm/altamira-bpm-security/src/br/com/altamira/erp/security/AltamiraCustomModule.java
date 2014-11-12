/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.erp.security;

import com.sun.appserv.security.AppservPasswordLoginModule;
import java.util.List;
import javax.security.auth.login.LoginException;

/**
 *
 * @author PARTH
 */
public class AltamiraCustomModule extends AppservPasswordLoginModule {

    @Override
    protected void authenticateUser() throws LoginException {
        
        SecurityUtil.authenticateUser(_username, _passwd);
        
        List<String> userGroups = SecurityUtil.getGroups(_username);
        
        String[] groups = userGroups.toArray(new String[userGroups.size()]);
        commitUserAuthentication(groups);
        
    }
    
}
