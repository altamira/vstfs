/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.bpm.services;

import java.math.BigDecimal;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

/**
 *
 * @author PARTH
 */

@Stateless
public class SendNotificationService implements JavaDelegate
{
    private static final String notificationFromUserName = "parth.patel@server1.com";
    
    private static final String notificationFromPassword = "tatva123";
    
    private static final String notificationTo = "tarkik.shah@server1.com";
    
    @PersistenceContext(name = "persistence/altamira-bpm", unitName = "altamira-bpm-PU")
    private EntityManager entityManager;
    
    @Inject
    MailService mailService;

    public void execute(DelegateExecution execution) throws Exception {
        
        System.out.println("Send Notification Task execution started...");
        
        List<String> planningIdList = (List<String>)execution.getVariable("planningId");
        
        BigDecimal planningId = new BigDecimal(planningIdList.get(0));
        
        String subject = "Confirm Receipt Order Timeout Planning Id: " + planningId;
        
        String message = "Purchase Order Timeout. This This is to notify you that Purchase Order Confirmation Task has reached timeout. The process will be ended.";
        
        mailService.sendMailNotification(notificationTo, 
                                         notificationFromUserName, 
                                         notificationFromPassword, 
                                         null, 
                                         null, 
                                         subject, 
                                         message, 
                                         null);
        
    }
    
}
