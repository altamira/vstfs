/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.erp.entity.services;

import br.com.altamira.erp.entity.dao.PurchasePlanningDao;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
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
@Path("/planning")
public class PurchasePlanningService {
    
    @PersistenceContext(name = "persistence/altamira-bpm", unitName = "altamira-bpm-PU")
    private EntityManager entityManager;
    
    @Inject
    private PurchasePlanningDao purchasPlanningDao;
    
    @GET
    @Path("{id}/report")
    @Produces("application/pdf")
    public Response getPlanningReportInPdf(@PathParam("id") long planningId) {

        // generate report
        JasperPrint jasperPrint = null;

        try {
            byte[] planningReportJasper = purchasPlanningDao.getPlanningReportJasperFile();
            byte[] planningReportAltamiraimage = purchasPlanningDao.getPlanningReportAltamiraImage();
            byte[] pdf = null;

            final ByteArrayInputStream reportStream = new ByteArrayInputStream(planningReportJasper);
            final Map<String, Object> parameters = new HashMap<String, Object>();

            parameters.put("PLANNING_ID", new BigDecimal(planningId));

            Locale locale = new Locale.Builder().setLanguage("pt").setRegion("BR").build();
            parameters.put("REPORT_LOCALE", locale);

            BufferedImage imfg = null;
            try {
                InputStream in = new ByteArrayInputStream(planningReportAltamiraimage);
                imfg = ImageIO.read(in);
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            parameters.put("altamira_logo", imfg);
            parameters.put("USERNAME", "Parth");

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
                    purchasPlanningDao.insertGeneratedPlanningReport(jasperPrint);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Could not insert generated report in database.");
            }
        }

    }
    
}
