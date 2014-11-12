/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.erp.entity.services;

import br.com.altamira.erp.entity.dao.OrderDao;
import br.com.altamira.erp.entity.model.PurchaseOrder;
import br.com.altamira.erp.entity.model.PurchaseOrderItem;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

/**
 *
 * @author PARTH
 */

@Stateless
public class GeneratePurchaseOrderService implements JavaDelegate
{
    
    @Inject
    private OrderDao orderDao;
    
    public void execute(DelegateExecution de) throws Exception {
        
        System.out.println("Generate Order service task execution started...");
        
        List<String> planningIdList = (List<String>) de.getVariable("planningId");
        
        BigDecimal planningId = new BigDecimal(planningIdList.get(0));
        
        List<BigDecimal> supplierList = orderDao.selectDistinctSuppliersFromPurchasePlan(planningId.longValue());
        
        List<String> purchaseOrderIdList = new ArrayList<String>();
            
        try {
            
            for (BigDecimal supplierId : supplierList) {
                
                PurchaseOrder purchaseOrder =  new PurchaseOrder();
                purchaseOrder.setPurchasePlanningId(planningId.longValue());
                purchaseOrder.setSupplierId(supplierId.longValue());
		purchaseOrder.setCreatedDate(new Date());
		purchaseOrder.setComments(null);
                
                List<Object[]> list = orderDao.selectOrderItemDetailsBySupplierId(planningId.longValue(), supplierId.longValue());
                
                Long purchaseOrderId = orderDao.insertPurchaseOrder(purchaseOrder);
                purchaseOrderIdList.add(purchaseOrderId.toString());
                List<Long> purchaseOrderItemList = new ArrayList<Long>();
                
                for (Object[] rs : list) {
                    
                    BigDecimal planningItemId = (BigDecimal) rs[0];
                    Date date = (Date) rs[1];
                    BigDecimal weight = ((BigDecimal) rs[2]);
                    BigDecimal price = (BigDecimal) rs[3];
                    BigDecimal tax = (BigDecimal) rs[4];

                    PurchaseOrderItem purchaseOrderItem = new PurchaseOrderItem();
                    purchaseOrderItem.setPurchaseOrderId(purchaseOrderId);
                    purchaseOrderItem.setPlanningItemId(planningItemId.longValue());
                    purchaseOrderItem.setDate(date);
                    purchaseOrderItem.setWeight(weight.longValue());
                    purchaseOrderItem.setPrice(price.longValue());
                    purchaseOrderItem.setTax(tax.longValue());

                    Long purchaseOrderItemId = orderDao.insertPurchaseOrderItem(purchaseOrderItem);

                    purchaseOrderItemList.add(purchaseOrderItemId);
                }
            }
            
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        de.setVariable("purchaseOrderId", purchaseOrderIdList);
        
    }
    
}
