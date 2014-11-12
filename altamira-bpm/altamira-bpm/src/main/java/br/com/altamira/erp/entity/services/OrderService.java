/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.erp.entity.services;

import br.com.altamira.erp.entity.dao.OrderDao;
import br.com.altamira.erp.entity.model.PurchaseOrder;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.ejb.Stateless;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.hibernate.Session;
import org.hibernate.jdbc.ReturningWork;

/**
 *
 * @author PARTH
 */

@Stateless
//@TransactionManagement(TransactionManagementType.BEAN)
@Path("/order")
public class OrderService {
    
    @PersistenceContext(name = "persistence/altamira-bpm", unitName = "altamira-bpm-PU")
    private EntityManager entityManager;
    
    @Inject
    private OrderDao orderDao;
    
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public ReturnMessage getPurchaseOrderById(@PathParam("id") long id) {

        ReturnMessage message = new ReturnMessage();

        try {
            PurchaseOrder order = entityManager.find(PurchaseOrder.class, id);

            if (order != null) {

                message.setError(0);
                message.setMessage("Purchase Order found.");
                message.setData(order);

            } else {
                message.setError(9999);
                message.setMessage("Purchase Order not found.");
            }
        } catch (Exception e) {
            message.setError(9999);
            message.setMessage(e.getMessage());
        }

        return message;
    }
    
    @GET
    @Path("{id}/report")
    @Produces("application/pdf")
    public Response getPurchaseOrderReportInPdf(@PathParam("id") long orderId) {
        // generate report
        JasperPrint jasperPrint = null;

        try {
            byte[] purchaseOrderReportJasper = orderDao.getPurchaseOrderReportJasperFile();
            byte[] purchaseOrderReportAltamiraimage = orderDao.getPurchaseOrderReportAltamiraImage();
            byte[] pdf = null;

            final ByteArrayInputStream reportStream = new ByteArrayInputStream(purchaseOrderReportJasper);
            final Map<String, Object> parameters = new HashMap<String, Object>();

            parameters.put("PURCHASE_ORDER_ID", new BigDecimal(orderId));

            Date purchaseOrderDate = orderDao.getPurchaseOrderCreatedDateById(orderId);

            parameters.put("PURCHASE_ORDER_DATE", purchaseOrderDate);
            parameters.put("USERNAME", "Parth");

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

            ResponseBuilder response = Response.ok(pdfStream);
            response.header("Content-Disposition","inline; filename=Planning Report.pdf");
            
            return response.build();

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
    }
    
}
