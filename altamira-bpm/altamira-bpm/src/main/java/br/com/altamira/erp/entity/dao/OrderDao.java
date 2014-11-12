/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.erp.entity.dao;

import br.com.altamira.erp.entity.model.PurchaseOrder;
import br.com.altamira.erp.entity.model.PurchaseOrderItem;
import br.com.altamira.erp.entity.model.PurchaseOrderReportLog;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import net.sf.jasperreports.engine.JasperPrint;

/**
 *
 * @author PARTH
 */
public class OrderDao {
    
    @PersistenceContext(name = "persistence/altamira-bpm", unitName = "altamira-bpm-PU")
    private EntityManager entityManager;
    
    public byte[] getPurchaseOrderReportJasperFile() throws SQLException {
        Blob tempBlob = (Blob) entityManager.createNativeQuery("SELECT JASPER_FILE FROM PURCHASE_ORDER_REPORT WHERE REPORT = (SELECT MAX(REPORT) FROM PURCHASE_ORDER_REPORT)")
                                            .getSingleResult();

        return tempBlob.getBytes(1, (int) tempBlob.length());
    }
    
    public byte[] getPurchaseOrderReportAltamiraImage() throws SQLException {
        Blob tempBlob = (Blob) entityManager.createNativeQuery("SELECT ALTAMIRA_LOGO FROM PURCHASE_ORDER_REPORT WHERE REPORT = (SELECT MAX(REPORT) FROM PURCHASE_ORDER_REPORT)")
                                            .getSingleResult();

        return tempBlob.getBytes(1, (int) tempBlob.length());
    }
    
    public Date getPurchaseOrderCreatedDateById(long purchaseOrderId) {
        
        Date created_date = (Date) entityManager.createNativeQuery("SELECT CREATED_DATE FROM PURCHASE_ORDER WHERE ID = :purchase_order_id ")
                                                .setParameter("purchase_order_id", purchaseOrderId)
                                                .getSingleResult();
        
        return created_date;
    }
    
    public boolean insertGeneratedPurchaseOrderReport(JasperPrint print) {
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
            PurchaseOrderReportLog log = new PurchaseOrderReportLog();
            log.setReportInstance(bArray);

            entityManager.persist(log);
        } catch (Exception e) {
            System.out.println("Error while inserting generated report in database.");
            e.printStackTrace();
            return false;
        }

        return true;
    }
    
    public List selectDistinctSuppliersFromPurchasePlan(long planningId) {
        
        StringBuffer selectSql = new StringBuffer().append(" SELECT DISTINCT PPI.SUPPLIER ")
                                                   .append(" FROM PURCHASE_PLANNING_ITEM PPI ")
                                                   .append(" WHERE PPI.PLANNING = :planning_id ")
                                                   .append(" ORDER BY PPI.SUPPLIER ");

        return entityManager.createNativeQuery(selectSql.toString())
                            .setParameter("planning_id", planningId)
                            .getResultList();
    }
    
    public List selectOrderItemDetailsBySupplierId(long planningId,
                                                   long supplierId) {
        StringBuffer selectSql = new StringBuffer().append(" SELECT PPI.ID AS PLANNING_ITEM, ")
                                                   .append("        RI.ARRIVAL_DATE AS \"DATE\", ")
                                                   .append("        PPI.WEIGHT AS WEIGHT, ")
                                                   .append("        QIQ.PRICE AS PRICE, ")
                                                   .append("        M.TAX AS TAX ")
                                                   .append(" FROM PURCHASE_PLANNING_ITEM PPI, ")
                                                   .append("      REQUEST_ITEM RI, ")
                                                   .append("      REQUEST R, ")
                                                   .append("      MATERIAL M, ")
                                                   .append("      QUOTATION_ITEM QI, ")
                                                   .append("      QUOTATION_REQUEST QR, ")
                                                   .append("      QUOTATION_ITEM_QUOTE QIQ ")
                                                   .append(" WHERE PPI.PLANNING = :planning_id ")
                                                   .append(" AND PPI.SUPPLIER = :supplier_id ")
                                                   .append(" AND PPI.REQUEST_ITEM = RI.ID ")
                                                   .append(" AND RI.MATERIAL = M.ID")
                                                   .append(" AND RI.REQUEST = R.ID")
                                                   .append(" AND R.ID = QR.REQUEST")
                                                   .append(" AND QR.QUOTATION = QI.QUOTATION ")
                                                   .append(" AND QIQ.SUPPLIER = PPI.SUPPLIER ")
                                                   .append(" AND QI.LAMINATION = M.LAMINATION ")
                                                   .append(" AND QI.TREATMENT = M.TREATMENT ")
                                                   .append(" AND QI.THICKNESS = M.THICKNESS ")
                                                   .append(" AND QI.ID = QIQ.QUOTATION_ITEM ");

        return entityManager.createNativeQuery(selectSql.toString())
                .setParameter("planning_id", planningId)
                .setParameter("supplier_id", supplierId)
                .getResultList();
    }
    
    public long insertPurchaseOrder(PurchaseOrder purchaseOrder) {
        
        entityManager.persist(purchaseOrder);
        return purchaseOrder.getId();
    }
    
    public long insertPurchaseOrderItem(PurchaseOrderItem purchaseOrderItem) {
        
        entityManager.persist(purchaseOrderItem);
        return purchaseOrderItem.getId();
    }
    
    public Map getSupplierMailAddressForPurchaseOrder(BigDecimal purchaseOrderId) {
        
        StringBuffer selectSql = new StringBuffer().append(" SELECT PO.ID, ")
                                                   .append("        S.NAME, ")
                                                   .append("        SC.MAIL_ADDRESS ")
                                                   .append(" FROM PURCHASE_ORDER PO, ")
                                                   .append("      SUPPLIER S, ")
                                                   .append("      SUPPLIER_CONTACT SC ")
                                                   .append(" WHERE PO.ID = :purchase_order_id ")
                                                   .append(" AND PO.SUPPLIER = S.ID ")
                                                   .append(" AND S.ID = SC.SUPPLIER ");

        Object[] result = (Object[]) entityManager.createNativeQuery(selectSql.toString())
                                                  .setParameter("purchase_order_id", purchaseOrderId)
                                                  .getSingleResult();

        Map resultMap = new HashMap();
        resultMap.put("PURCHASE_ORDER_ID", result[0]);
        resultMap.put("SUPPLIER_NAME", result[1]);
        resultMap.put("MAIL_ADDRESS", result[2]);

        return resultMap;
    }
}
