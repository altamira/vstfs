package com.purchaseorder.service.steelpurchase;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;

import com.purchaseorder.dao.PurchaseOrderDao;
import com.purchaseorder.entity.PurchaseOrder;
import com.purchaseorder.entity.PurchaseOrderItem;

/**
 * 
 * @author PARTH
 *
 */
public class GenerateOrdersService implements JavaDelegate 
{
	@Autowired
	private PurchaseOrderDao purchaseOrderDao;
	
	public PurchaseOrderDao getPurchaseOrderDao() 
	{
		return purchaseOrderDao;
	}

	public void setPurchaseOrderDao(PurchaseOrderDao purchaseOrderDao) 
	{
		this.purchaseOrderDao = purchaseOrderDao;
	}

	/**
	 * Java method implementation for BPMN service task 'GenerateOrdersServiceTask'.
	 */
	@Override
	public void execute(DelegateExecution execution) throws Exception 
	{
		System.out.println("Generation Order service task execution started...");
		
		List<String> planningIdList = (List<String>) execution.getVariable("planningId");
		
		BigDecimal planningId = new BigDecimal(planningIdList.get(0));
		
		List<BigDecimal> supplierList = purchaseOrderDao.selectDistinctSuppliersFromPurchasePlan(planningId);
		
		List<String> purchaseOrderIdList = new ArrayList<String>();
		
		try
		{
			for(BigDecimal supplierId : supplierList)
			{
				PurchaseOrder purchaseOrder = new PurchaseOrder();
				purchaseOrder.setPurchasePlanningId(planningId);
				purchaseOrder.setSupplierId(supplierId);
				purchaseOrder.setPurchaseOrderDate(new Date());
				purchaseOrder.setComments(null);
				
				List<Object[]> list = purchaseOrderDao.selectOrderItemDetailsBySupplierId(planningId, supplierId);
				
				BigDecimal purchaseOrderId = purchaseOrderDao.insertPurchaseOrder(purchaseOrder);
				purchaseOrderIdList.add(purchaseOrderId.toString());
				List<BigDecimal> purchaseOrderItemList = new ArrayList<BigDecimal>();
				
				for(Object[] rs : list)
				{
					BigDecimal planningItemId = (BigDecimal) rs[0];
					Date purchaseOrderItemDate = (Date) rs[1];
					BigDecimal purchaseOrderItemWeight = ((BigDecimal) rs[2]);
					BigDecimal purchaseOrderItemUnitPrice = (BigDecimal) rs[3];
					BigDecimal purchaseOrderItemTax = (BigDecimal)rs[4];
					
					PurchaseOrderItem purchaseOrderItem = new PurchaseOrderItem();
					purchaseOrderItem.setPurchaseOrderId(purchaseOrderId);
					purchaseOrderItem.setPlanningItemId(planningItemId);
					purchaseOrderItem.setPurchaseOrderItemDate(purchaseOrderItemDate);
					purchaseOrderItem.setPurchaseOrderItemWeight(purchaseOrderItemWeight);
					purchaseOrderItem.setPurchaseOrderItemUnitPrice(purchaseOrderItemUnitPrice);
					purchaseOrderItem.setPurchaseOrderItemTax(purchaseOrderItemTax);
					
					BigDecimal purchaseOrderItemId = purchaseOrderDao.insertPurchaseOrderItem(purchaseOrderItem);
					
					purchaseOrderItemList.add(purchaseOrderItemId);
				}				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		execution.setVariable("purchaseOrderId", purchaseOrderIdList);
	}

}
