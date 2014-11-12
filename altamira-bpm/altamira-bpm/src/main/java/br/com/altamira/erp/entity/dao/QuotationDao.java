/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.erp.entity.dao;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import br.com.altamira.erp.entity.model.Quotation;
import br.com.altamira.erp.entity.model.QuotationReportLog;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import net.sf.jasperreports.engine.JasperPrint;
import org.hibernate.Session;
import org.joda.time.DateTime;

/**
 *
 * @author pci109
 */
public class QuotationDao {

    @PersistenceContext(name = "persistence/altamira-bpm", unitName = "altamira-bpm-PU")
    private EntityManager entityManager;
    
    public List<Quotation> list() {
        return (List<Quotation>)entityManager
                .createNamedQuery("Quotation.list", Quotation.class)
                .getResultList(); 
    }
    
    public Quotation getCurrent() {
        
        // ToDo: Call stored procedure CREATE_QUOTATION to create/update a current quotation
        
        long quotation_id = 0L;
        
        entityManager.createNativeQuery("BEGIN CREATE_QUOTATION(?); END;")
                     .setParameter(1, quotation_id)
                     .executeUpdate();
        
//        StoredProcedureQuery storedProcedure = entityManager.createStoredProcedureQuery("TEST_CREATE_QUOTATION");
//        storedProcedure.registerStoredProcedureParameter("quotation_id", Number.class, ParameterMode.OUT);
//        
//        storedProcedure.execute();
//        quotation_id = (Long) storedProcedure.getOutputParameterValue("quotation_id");
        
//        Quotation quotation = getQuotationDetailsById(quotation_id);
        
//      return quotation;
        
        List<br.com.altamira.erp.entity.model.Quotation> quotations;

        quotations = (List<br.com.altamira.erp.entity.model.Quotation>) entityManager.createQuery("SELECT q FROM Quotation q WHERE q.id = (SELECT MAX(qq.id) FROM Quotation qq WHERE qq.closed_date IS NULL)", Quotation.class)
                .getResultList();

        if (quotations.isEmpty()) {

            Quotation quotation = new Quotation();
            quotation.setCreatedDate(DateTime.now().toDate());
            quotation.setCreatorName("system");

            entityManager.persist(quotation);

            return quotation;
        }

        return quotations.get(0);
    }

    public Quotation find(long id) {
        return entityManager.find(Quotation.class, id);
    }

    public Quotation update(Quotation quotation) {
        entityManager.merge(quotation);
        
        return quotation;
    }

    public Quotation remove(Quotation quotation) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public byte[] getQuotationReportJasperFile() throws SQLException {
        Blob tempBlob = (Blob) entityManager.createNativeQuery("SELECT JASPER_FILE FROM QUOTATION_REPORT WHERE REPORT = (SELECT MAX(REPORT) FROM QUOTATION_REPORT)")
                .getSingleResult();

        return tempBlob.getBytes(1, (int) tempBlob.length());
    }

    public byte[] getQuotationReportAltamiraImage() throws SQLException {
        Blob tempBlob = (Blob) entityManager.createNativeQuery("SELECT ALTAMIRA_LOGO FROM QUOTATION_REPORT WHERE REPORT = (SELECT MAX(REPORT) FROM QUOTATION_REPORT)")
                .getSingleResult();

        return tempBlob.getBytes(1, (int) tempBlob.length());
    }

    public List selectQuotationReportDataById(long quotationId) {
        StringBuffer selectSql = new StringBuffer().append(" SELECT QI.LAMINATION, ")
                                                   .append("        QI.TREATMENT, ")
                                                   .append("        QI.THICKNESS ")
                                                   .append(" FROM QUOTATION_ITEM QI ")
                                                   .append(" WHERE QI.QUOTATION = :quotation_id ")
                                                   .append(" GROUP BY QI.LAMINATION, QI.TREATMENT, QI.THICKNESS ")
                                                   .append(" ORDER BY QI.LAMINATION, QI.TREATMENT, QI.THICKNESS ");

        List<Object[]> list = entityManager.createNativeQuery(selectSql.toString())
                                           .setParameter("quotation_id", quotationId)
                                           .getResultList();

        return list;
    }

    public Quotation getQuotationDetailsById(long quotationId) {
        return entityManager.find(Quotation.class, quotationId);
    }

    public boolean insertGeneratedQuotationReport(JasperPrint print) {
        byte[] bArray = null;

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);

            oos.writeObject(print);
            oos.close();
            baos.close();

            bArray = baos.toByteArray();
        } catch (Exception e) {
            System.out.println("Error converting JasperPrint object to byte[] array");
            e.printStackTrace();
            return false;
        }

        try {
            QuotationReportLog log = new QuotationReportLog();
            log.setReportInstance(bArray);

            entityManager.persist(log);
        } catch (Exception e) {
            System.out.println("Error while inserting generated report in database.");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public Session unwrap() {
        return entityManager.unwrap(Session.class);
    }
}
