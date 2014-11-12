/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.erp.security;

import java.util.List;

/**
 *
 * @author PARTH
 */
public interface IUserAuthenticationService {
    
    public void validatePassword(String userId, String password) throws Exception;
    
    public List<String> getGroups(String usid);
}
