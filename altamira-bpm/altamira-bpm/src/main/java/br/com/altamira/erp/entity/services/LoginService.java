/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.erp.entity.services;

import br.com.altamira.erp.entity.dao.UserDao;
import br.com.altamira.erp.entity.model.AuthenticationRequest;
import br.com.altamira.erp.entity.model.UserDetails;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.User;

/**
 *
 * @author PARTH
 */

@Stateless
//@TransactionManagement(TransactionManagementType.BEAN)
@Path("/")
public class LoginService {

    @PersistenceContext(name = "persistence/altamira-bpm", unitName = "altamira-bpm-PU")
    private EntityManager entityManager;
    
    @Context
    private HttpServletRequest request;
    
    @Inject
    private IdentityService identityService;
    
    @Inject
    private UserDao userDao;

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ReturnMessage doLogin(AuthenticationRequest loginRequest) {
        
        ReturnMessage message = new ReturnMessage();
        String userId = loginRequest.getUserId();
        String password = loginRequest.getPassword();
        String firstName = null;
        String lastName = null;
        List<String> groupNames = null;
        String preferences = null;
        
        try 
        {
            
            Principal principal = request.getUserPrincipal();
            
            if(principal == null)
            {
                request.login(userId, password);
                
                // TO-DO - Get user details and set it in ReturnMessage.
                groupNames = new ArrayList<String>();
                List<Group> groups = identityService.createGroupQuery().groupMember(userId).list();
                
                for(Group group: groups)
                {
                    groupNames.add(group.getName());
                }
                
                User user = identityService.createUserQuery().userId(userId).singleResult();
                firstName = user.getFirstName();
                lastName = user.getLastName();
                
                br.com.altamira.erp.entity.model.User altamiraUser = userDao.find(userId);
                preferences = altamiraUser.getPreference();
                
                UserDetails response = new UserDetails();
                response.setLogin(userId);
                response.setName(firstName+" "+lastName);
                response.setGroups(groupNames);
                response.setPreferences(preferences);
                
                message.setError(0);
                message.setMessage("Login successful");
                message.setData(response);
            }
            else
            {
                message.setError(9999);
                message.setMessage("Login failed : " + " Another User is already logged in");
            }
            
        }
        catch (Exception e) 
        {
            message.setError(9999);
            message.setMessage("Login failed : " + e.getMessage());
        }
        
        return message;
    }
    
    @POST
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    public ReturnMessage doLogout()
    {
        ReturnMessage message = new ReturnMessage();
        
        try
        {
            request.logout();
            
            message.setError(0);
            message.setMessage("Logout successful");
        }
        catch(Exception e)
        {
            message.setError(9999);
            message.setMessage("Logout failed");
        }
        
        return message;
    }
}
