/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.erp.entity.services;

import br.com.altamira.erp.entity.model.AuthenticationRequest;
import java.util.Properties;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.InitialContext;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author PARTH
 */
public class LoginServiceTest {
    
    public LoginServiceTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of doLogin method, of class LoginService.
     */
    @Test
    @Ignore
    public void testDoLogin() throws Exception {
        System.out.println("doLogin");
        AuthenticationRequest loginRequest = null;
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        LoginService instance = (LoginService)container.getContext().lookup("java:global/classes/LoginService");
        ReturnMessage expResult = null;
        ReturnMessage result = instance.doLogin(loginRequest);
        assertEquals(expResult, result);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of doLogout method, of class LoginService.
     */
    @Test
    public void testDoLogout() throws Exception {
        System.out.println("doLogout");
        Properties properties = new Properties();
 
        properties.put("java.naming.factory.initial" , "com.sun.enterprise.naming.SerialInitContextFactory");
        properties.put("java.naming.factory.url.pkgs", "com.sun.enterprise.naming");
        properties.put("java.naming.factory.state"   , "com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl");
 
        InitialContext remoteContext = new InitialContext(properties);
        //EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        //LoginService instance = (LoginService)remoteContext.lookup("java:global/altamira-bpm/LoginService");
        Object instance = remoteContext.lookup("java:global/altamira-bpm/LoginService");
        ReturnMessage expResult = null;
        //ReturnMessage result = instance.doLogout();
        //assertEquals(expResult, result);
        //container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}