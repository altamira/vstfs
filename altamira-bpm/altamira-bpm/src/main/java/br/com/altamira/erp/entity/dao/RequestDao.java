package br.com.altamira.erp.entity.dao;

import br.com.altamira.erp.entity.model.Request;
import br.com.altamira.erp.entity.model.RequestReportLog;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import net.sf.jasperreports.engine.JasperPrint;
import org.joda.time.DateTime;

public class RequestDao {

    @PersistenceContext(name = "persistence/altamira-bpm", unitName = "altamira-bpm-PU")
    private EntityManager entityManager;
      
    public List<Request> list() {
        return (List<Request>)entityManager
                .createNamedQuery("Request.list", Request.class)
                .getResultList(); 
    }

    public Request find(long id) {
        return entityManager.find(Request.class, id);
    }

    public Request create(Request request) {
        entityManager.persist(request);

        entityManager.flush();
        
        return request;
    }
    
    public Request update(Request request) {
        entityManager.merge(request);
        
        return request;
    }

    public Request remove(Request request) {
        entityManager.remove(request);
        
        return request;
    }

    public Request remove(long id) {
        Request request = find(id);
        
        entityManager.remove(request);
        
        return request;
    }
    
    public Request getCurrent() {
        List<Request> requests;
        
        requests = (List<Request>)entityManager
                .createNamedQuery("Request.getCurrent", Request.class)
                .getResultList();
        
        if (requests.isEmpty()) {
            
            return create(new Request(DateTime.now().toDate(), "system", null));
            
        }
        
        return requests.get(0);
    }
    
    public byte[] getRequestReportJasperFile() throws SQLException {
        Blob tempBlob = (Blob) entityManager.createNativeQuery("SELECT JASPER_FILE FROM REQUEST_REPORT WHERE REPORT = (SELECT MAX(REPORT) FROM REQUEST_REPORT)")
                                            .getSingleResult();

        return tempBlob.getBytes(1, (int) tempBlob.length());
    }
    
    public byte[] getRequestReportAltamiraImage() throws SQLException {
        Blob tempBlob = (Blob) entityManager.createNativeQuery("SELECT ALTAMIRA_LOGO FROM REQUEST_REPORT WHERE REPORT = (SELECT MAX(REPORT) FROM REQUEST_REPORT)")
                                            .getSingleResult();

        return tempBlob.getBytes(1, (int) tempBlob.length());
    }
    
    public List selectRequestReportDataById(long requestId) {
        StringBuffer selectSql = new StringBuffer().append(" SELECT M.ID, ")
                                                   .append("        M.LAMINATION, ")
                                                   .append("        M.TREATMENT, ")
                                                   .append("        M.THICKNESS, ")
                                                   .append("        M.WIDTH, ")
                                                   .append("        M.LENGTH, ")
                                                   .append("        RT.WEIGHT, ")
                                                   .append("        RT.ARRIVAL_DATE ")
                                                   .append(" FROM REQUEST R, REQUEST_ITEM RT, MATERIAL M ")
                                                   .append(" WHERE R.ID = RT.REQUEST ")
                                                   .append(" AND RT.MATERIAL = M.ID ")
                                                   .append(" AND R.ID = :request_id ");

        List<Object[]> list = entityManager.createNativeQuery(selectSql.toString())
                                           .setParameter("request_id", requestId)
                                           .getResultList();

        return list;
    }

    public boolean insertGeneratedRequestReport(JasperPrint print) {
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
            RequestReportLog log = new RequestReportLog();
            log.setReportInstance(bArray);

            entityManager.persist(log);
        } catch (Exception e) {
            System.out.println("Error while inserting generated report in database.");
            e.printStackTrace();
            return false;
        }

        return true;
    }

}
