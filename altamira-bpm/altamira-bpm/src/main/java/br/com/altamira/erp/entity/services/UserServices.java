/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.altamira.erp.entity.services;

import br.com.altamira.erp.entity.dao.UserDao;
import br.com.altamira.erp.entity.model.UserDetails;
import br.com.altamira.erp.entity.model.User;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.identity.Group;

/**
 *
 * @author Alessandro
 */
@Stateless
@Path("/user")
public class UserServices {
    
    @Inject
    private UserDao userDao;
    
    @Inject
    private IdentityService identityService;
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ReturnMessage list() throws Exception {

        ReturnMessage message = new ReturnMessage();

        try {

            List<User> users = userDao.list();
            List<UserDetails> response = new ArrayList<UserDetails>();
            
            for(User user : users)
            {
                UserDetails tempModel = createUserDetailsModel(user);
                response.add(tempModel);
            }

            message.setError(!users.isEmpty() ? 0 : 9998);
            message.setMessage(!users.isEmpty() ? "Um ou mais usuários forão encontrados." : "Nenhum Usuário foi encontrado !");

            message.setData(response);

        } catch (Exception e) {
            
            message.setError(9999);
            message.setMessage("Erro ao consultar o Usuário. " + e != null ? e.getMessage() : "");
            
        }

        return message;
    }
    
    @GET
    @Path("{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public ReturnMessage getByName(@PathParam("name") String name) {

        ReturnMessage message = new ReturnMessage();
        String firstName = null;
        String lastName = null;
        List<String> groupNames = null;
        String preferences = null;
        
        try {

            User user = userDao.find(name);

            if (user != null) {
                
                UserDetails response = createUserDetailsModel(user);
                
                message.setError(0);
                message.setMessage("O Usuário foi encontrado.");
                message.setData(response);

            } else {

                message.setError(9999);
                message.setMessage("O Usuário não foi encontrado !");
            }

        } catch (Exception e) {

            message.setError(9999);
            message.setMessage(e.getMessage());

        }

        return message;
    }

//    @POST
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public ReturnMessage create(User user) throws Exception {
//
//        ReturnMessage message = new ReturnMessage();
//
//        try {
//
//            userDao.create(user);
//
//            message.setError(0);
//            message.setMessage("O Usuário " + user.getName() + " foi criado com sucesso.");
//
//        } catch (Exception e) {
//
//            message.setError(9999);
//            message.setMessage("Erro ao criar o Usuário !" + e != null ? e.getMessage() : "");
//
//        }
//
//        message.setData(user);
//
//        return message;
//    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ReturnMessage update(UserDetails userDetails) {

        ReturnMessage message = new ReturnMessage();

        String login = userDetails.getLogin();
        
        User user = userDao.find(login);
        
        if(user != null)
        {
             try {    
                user.setPreference(userDetails.getPreferences());
                userDao.update(user);
            
                String name = userDetails.getName();
                String firstName = name.substring(0, name.indexOf(" "));
                String lastName = name.substring(name.indexOf(" ")+1);
                List<String> newGroups = userDetails.getGroups();
                
                // update camunda user properties
                // save user properties
                org.camunda.bpm.engine.identity.User camundaUser = identityService.createUserQuery().userId(login).singleResult();
                camundaUser.setFirstName(firstName);
                camundaUser.setLastName(lastName);
                identityService.saveUser(camundaUser);
                
                // save user group properties
                List<Group> existingGroups = identityService.createGroupQuery().groupMember(login).list();
                
                // copy new user groups
                List<String> copyOfNewGroups = new ArrayList<String>(newGroups);
                
                // delete if existing group is not there in new list
                for(Group existingGroup : existingGroups)
                {
                    String existingGroupName = existingGroup.getName();
                    if(!copyOfNewGroups.contains(existingGroupName))
                    {
                        identityService.deleteMembership(login, existingGroup.getId());
                    }
                    else
                    {
                        copyOfNewGroups.remove(existingGroupName);
                    }
                }
                
                // add new groups
                for(String tempGroupName : copyOfNewGroups)
                {
                    Group newGroup = identityService.createGroupQuery().groupNameLike(tempGroupName).singleResult();
                    if(newGroup != null)
                    {
                        identityService.createMembership(login, newGroup.getId());
                    }
                }
                
                message.setError(0);
                message.setMessage("O Usuário " + user.getName() + " foi alterado com sucesso.");
                message.setData(userDetails);
                
             } catch(Exception e) {
                 
                 message.setError(9999);
                 message.setMessage("Erro ao alterar o Usuário !" + e != null ? e.getMessage() : "");
             }
          
        }
        else
        {
            
        }

        return message;
    }

//    @DELETE
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public ReturnMessage remove(User user) {
//
//        ReturnMessage message = new ReturnMessage();
//
//        try {
//            userDao.remove(user);
//
//            message.setError(0);
//            message.setMessage("O Usuário " + user.getName() + " foi excluido com sucesso.");
//
//        } catch (Exception e) {
//            message.setError(9999);
//            message.setMessage("Erro ao excluir o Usuário !" + e != null ? e.getMessage() : "");
//        }
//
//        message.setData(user);
//
//        return message;
//    }    
    
    private UserDetails createUserDetailsModel(User user)
    {
        String login = user.getName();
        String firstName = null;
        String lastName = null;
        List<String> groupNames = null;
        String preferences = null;
        
        // fetch camunda user details
        org.camunda.bpm.engine.identity.User camundaUser = identityService.createUserQuery().userId(login).singleResult();
        List<Group> groups = identityService.createGroupQuery().groupMember(login).list();
        
        // getting properties
        preferences = user.getPreference();
        firstName = camundaUser.getFirstName();
        lastName = camundaUser.getLastName();
                
        groupNames = new ArrayList<String>();
        for(Group group: groups)
        {
            groupNames.add(group.getName());
        }
        
        // set properties in model
        UserDetails response = new UserDetails();
        response.setLogin(login);
        response.setName(firstName+" "+lastName);
        response.setGroups(groupNames);
        response.setPreferences(preferences);
        
        return response;
    }
}
