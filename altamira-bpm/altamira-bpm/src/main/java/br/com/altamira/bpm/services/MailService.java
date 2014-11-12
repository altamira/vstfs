/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.bpm.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import javax.activation.DataSource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.ByteArrayDataSource;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.camunda.bpm.engine.cdi.CdiStandaloneProcessEngineConfiguration;

/**
 *
 * @author PARTH
 */
@Stateless
public class MailService {

    @Inject
    private CdiStandaloneProcessEngineConfiguration processEngineConfiguration;

    public void sendMail(String to,
                         String cc,
                         String bcc,
                         String subject,
                         String text,
                         Map<String, InputStream> emailAttachmentList) throws EmailException, IOException {
        
       // ProcessEngine pe = ProcessEngines.getProcessEngine("my-engine");
        //ProcessEngineConfigurationImpl impl = 
        //String dbschema = processEngineConfiguration.getDatabaseSchema();
        
        /*String hostName = processEngineConfiguration.getMailServerHost();
        int smtpPort = processEngineConfiguration.getMailServerPort();
        String userName = processEngineConfiguration.getMailServerUsername();
        String password = processEngineConfiguration.getMailServerPassword();
        boolean tls = processEngineConfiguration.getMailServerUseTLS();*/
        
        InputStream input = this.getClass().getClassLoader().getResourceAsStream("mail.properties");
        
        if (input == null) {
            throw new EmailException("Mail Server not Configured properly.");
        }
        
        Properties prop = new Properties();
        prop.load(input);
        
        String hostName = prop.getProperty("mailServerHost");
        int smtpPort = Integer.parseInt(prop.getProperty("mailServerPort"));
        String userName = prop.getProperty("mailServerUserName");
        String password = prop.getProperty("mailServerPassword");
        boolean tls = prop.getProperty("mailServerUseTLS").equalsIgnoreCase("true") ? true : false;
        
        if (StringUtils.isEmpty(hostName)
                || StringUtils.isEmpty(userName)
                || StringUtils.isEmpty(password)) {
            throw new EmailException("Mail Server not Configured properly.");
        }
        
        MultiPartEmail email = new MultiPartEmail();
        
        // Set body text of E-Mail
        email.setMsg(StringUtils.defaultIfEmpty(text, ""));
        
        // Set From address
	email.setFrom(userName);
        
        // Add To address
        String[] toAddress = splitAndTrim(to);
        if (toAddress != null) {
            for (String tempTo : toAddress) {
                if (!StringUtils.isBlank(tempTo)) {
                    email.addTo(tempTo);
                }
            }
        }
        
        // Add Cc address
        String[] ccAddress = splitAndTrim(cc);
        if (ccAddress != null) {
            for (String tempCc : ccAddress) {
                if (!StringUtils.isBlank(tempCc)) {
                    email.addCc(tempCc);
                }
            }
        }
        
        // Add Bcc address
        String[] bccAddress = splitAndTrim(bcc);
        if (bccAddress != null) {
            for (String tempBcc : bccAddress) {
                if (!StringUtils.isBlank(tempBcc)) {
                    email.addBcc(tempBcc);
                }
            }
        }
        
        // Add subject
	email.setSubject(StringUtils.defaultIfEmpty(subject, ""));
        
        // Add attachments
        for (String key : emailAttachmentList.keySet()) {
            InputStream is = emailAttachmentList.get(key);
            DataSource source = null;

            try {
                source = new ByteArrayDataSource(is, "application/pdf");
            } catch (IOException e) {
                e.printStackTrace();
            }

            email.attach(source, key, "");
        }
        
        // Set mail server properties
        email.setHostName(hostName);
        email.setSmtpPort(smtpPort);
        email.setAuthentication(userName, password);
        email.setTLS(tls);

        // Send mail
        email.send();
    }
    
    public void sendMailNotification(String to,
                                     String userName,
                                     String password,
                                     String cc,
                                     String bcc,
                                     String subject,
                                     String text,
                                     Map<String, InputStream> emailAttachmentList) throws EmailException, IOException {
        
        InputStream input = this.getClass().getClassLoader().getResourceAsStream("mail.properties");
        
        if (input == null) {
            throw new EmailException("Mail Server not Configured properly.");
        }
        
        Properties prop = new Properties();
        prop.load(input);
        
        String hostName = prop.getProperty("mailServerHost");
        int smtpPort = Integer.parseInt(prop.getProperty("mailServerPort"));
        //String userName = prop.getProperty("mailServerUserName");
        //String password = prop.getProperty("mailServerPassword");
        boolean tls = prop.getProperty("mailServerUseTLS").equalsIgnoreCase("true") ? true : false;
        
        if (StringUtils.isEmpty(hostName)
                || StringUtils.isEmpty(userName)
                || StringUtils.isEmpty(password)) {
            throw new EmailException("Mail Server not Configured properly.");
        }
        
        MultiPartEmail email = new MultiPartEmail();
        
        // Set body text of E-Mail
        email.setMsg(StringUtils.defaultIfEmpty(text, ""));
        
        // Set From address
	email.setFrom(userName);
        
        // Add To address
        String[] toAddress = splitAndTrim(to);
        if (toAddress != null) {
            for (String tempTo : toAddress) {
                if (!StringUtils.isBlank(tempTo)) {
                    email.addTo(tempTo);
                }
            }
        }
        
        // Add Cc address
        String[] ccAddress = splitAndTrim(cc);
        if (ccAddress != null) {
            for (String tempCc : ccAddress) {
                if (!StringUtils.isBlank(tempCc)) {
                    email.addCc(tempCc);
                }
            }
        }
        
        // Add Bcc address
        String[] bccAddress = splitAndTrim(bcc);
        if (bccAddress != null) {
            for (String tempBcc : bccAddress) {
                if (!StringUtils.isBlank(tempBcc)) {
                    email.addBcc(tempBcc);
                }
            }
        }
        
        // Add subject
	email.setSubject(StringUtils.defaultIfEmpty(subject, ""));
        
        // Add attachments
        if (emailAttachmentList != null) {
            for (String key : emailAttachmentList.keySet()) {
                InputStream is = emailAttachmentList.get(key);
                DataSource source = null;

                try {
                    source = new ByteArrayDataSource(is, "application/pdf");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                email.attach(source, key, "");
            }
        }
        
        
        // Set mail server properties
        email.setHostName(hostName);
        email.setSmtpPort(smtpPort);
        email.setAuthentication(userName, password);
        email.setTLS(tls);

        // Send mail
        email.send();
    }
    
    private String[] splitAndTrim(String str) {
        if (str != null) {
            String[] splittedStrings = str.split(",");

            for (int i = 0; i < splittedStrings.length; i++) {
                splittedStrings[i] = splittedStrings[i].trim();
            }

            return splittedStrings;
        }

        return null;
    }
}
