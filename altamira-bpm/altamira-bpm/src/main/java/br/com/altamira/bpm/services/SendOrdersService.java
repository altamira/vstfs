/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.bpm.services;

import br.com.altamira.erp.entity.dao.OrderDao;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.ejb.Stateless;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.lang.StringUtils;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.hibernate.Session;
import org.hibernate.jdbc.ReturningWork;

/**
 *
 * @author PARTH
 */

@Stateless
//@TransactionManagement(TransactionManagementType.BEAN)
public class SendOrdersService implements JavaDelegate 
{
    
    @PersistenceContext(name = "persistence/altamira-bpm", unitName = "altamira-bpm-PU")
    private EntityManager entityManager;
    
    @Inject
    private OrderDao orderDao;
    
    @Inject
    IdentityService identityService;
    
    @Inject
    MailService mailService;
    
    public void execute(DelegateExecution de) throws Exception {
        
        System.out.println("Send Orders To Suppliers service task execution started...");
        
        List<String> purchaseOrderIdList = (List<String>) de.getVariable("purchaseOrderId");
        
        for (String str : purchaseOrderIdList) {
            
            BigDecimal purchaseOrderId = new BigDecimal(str);
            
            JasperPrint print = generatePurchaseOrderReport(purchaseOrderId);
            ByteArrayInputStream bais = new ByteArrayInputStream(JasperExportManager.exportReportToPdf(print));
            
            Map<String, InputStream> emailAttachmentList = new HashMap<String, InputStream>();
            emailAttachmentList.put("PurchaseOrderReport", bais);
            
            Map supplierInfo = orderDao.getSupplierMailAddressForPurchaseOrder(purchaseOrderId);
            
            String message = "Please find the attachment for the Purchase Order";
            
            String subject = "Purchase Order ID :" +
                             new DecimalFormat("#00000").format((BigDecimal)supplierInfo.get("PURCHASE_ORDER_ID")) +
                             " Supplier: " + 
                             (String)supplierInfo.get("SUPPLIER_NAME");
            
            mailService.sendMail((String)supplierInfo.get("MAIL_ADDRESS"), 
                                 null, 
                                 null, 
                                 subject, 
                                 message, 
                                 emailAttachmentList);
        }
        
    }

    private JasperPrint generatePurchaseOrderReport(BigDecimal orderId) {

        // generate report
        JasperPrint jasperPrint = null;

         try {
            byte[] purchaseOrderReportJasper = orderDao.getPurchaseOrderReportJasperFile();
            byte[] purchaseOrderReportAltamiraimage = orderDao.getPurchaseOrderReportAltamiraImage();
            byte[] pdf = null;

            final ByteArrayInputStream reportStream = new ByteArrayInputStream(purchaseOrderReportJasper);
            final Map<String, Object> parameters = new HashMap<String, Object>();

            parameters.put("PURCHASE_ORDER_ID", orderId);

            Date purchaseOrderDate = orderDao.getPurchaseOrderCreatedDateById(orderId.longValue());

            parameters.put("PURCHASE_ORDER_DATE", purchaseOrderDate);
            parameters.put("USERNAME", StringUtils.defaultIfEmpty(
                                                       identityService.getCurrentAuthentication()==null 
                                                       ? "" : identityService.getCurrentAuthentication().getUserId(),""));

            Locale locale = new Locale.Builder().setLanguage("pt").setRegion("BR").build();
            parameters.put("REPORT_LOCALE", locale);

            BufferedImage imfg = null;
            try {
                InputStream in = new ByteArrayInputStream(purchaseOrderReportAltamiraimage);
                imfg = ImageIO.read(in);
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            parameters.put("altamira_logo", imfg);

            Session session = entityManager.unwrap(Session.class);

            jasperPrint = session.doReturningWork(new ReturningWork<JasperPrint>() {
                @Override
                public JasperPrint execute(Connection connection) {
                    JasperPrint jasperPrint = null;

                    try {
                        jasperPrint = JasperFillManager.fillReport(reportStream, parameters, connection);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return jasperPrint;
                }
            });

            pdf = JasperExportManager.exportReportToPdf(jasperPrint);

            ByteArrayInputStream pdfStream = new ByteArrayInputStream(pdf);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (jasperPrint != null) {
                    // store generated report in database
                    orderDao.insertGeneratedPurchaseOrderReport(jasperPrint);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Could not insert generated report in database.");
            }
        }

        return jasperPrint;
    }
    
    
}
