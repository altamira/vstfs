/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.erp.security;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.identity.Group;

/**
 *
 * @author PARTH
 */

@Stateless
public class UserAuthenticationService implements IUserAuthenticationService {
    
    @Inject
    private IdentityService identityService;

    public void validatePassword(String userId, String password) throws Exception {
        
        boolean isUserValid = identityService.checkPassword(userId, password);
        
        if(!isUserValid)
        {
            throw new Exception("Username or password not valid");
        }
    }

    public List<String> getGroups(String userId) {
        
        // TO-DO - inject identity service of camunda engine & get the user associated group names.
//        if(userId.equalsIgnoreCase("parth"))
//        {
//            List<String> groups = new ArrayList<String>();
//            groups.add("ADMIN");
//            groups.add("SALES");
//            groups.add("MARKETING");
//            groups.add("ENGINERRING");
//            
//            return groups;
//        }
//        else
//        {
//            return null;
//        }
        
        List<Group> groups = identityService.createGroupQuery().groupMember(userId).list();
        
        List<String> groupNames = new ArrayList<String>();
        for(Group group: groups)
        {
            groupNames.add(group.getName());
        }
        
        return groupNames;
    }
    
}
