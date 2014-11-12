package com.purchaseorder.service;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.CronTriggerBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;

/**
 * 
 * @author PARTH
 *
 */
public class PurchaseOrderMailboxService implements JavaDelegate
{
	@Autowired
	PurchaseOrderMailboxProcessor mailboxProcessor;
	
	@Autowired
	Scheduler scheduler;
	
	public PurchaseOrderMailboxProcessor getMailboxProcessor() 
	{
		return mailboxProcessor;
	}
	
	public void setMailboxProcessor(PurchaseOrderMailboxProcessor mailboxProcessor) 
	{
		this.mailboxProcessor = mailboxProcessor;
	}

	public Scheduler getScheduler() 
	{
		return scheduler;
	}

	public void setScheduler(Scheduler scheduler) 
	{
		this.scheduler = scheduler;
	}

	/**
	 * Java method implementation for BPMN task 'Purchase Order Mail Downloader'.
	 */
	@Override
	public void execute(DelegateExecution execution) throws Exception 
	{
		try
		{
			MethodInvokingJobDetailFactoryBean jobDetail = new MethodInvokingJobDetailFactoryBean();
			jobDetail.setTargetObject(mailboxProcessor);
			jobDetail.setTargetMethod("execute");
			jobDetail.setName("purchaseOrderJobDetail");
			jobDetail.setConcurrent(false);
			jobDetail.afterPropertiesSet();
			
			CronTriggerBean cronTrigger = new CronTriggerBean();
			cronTrigger.setBeanName("PurchaseOrderTrigger");
			cronTrigger.setCronExpression("0 0/1 * * * ?");
			cronTrigger.afterPropertiesSet();
			
			scheduler.scheduleJob((JobDetail)jobDetail.getObject(), cronTrigger);
			
			scheduler.start();
			
		}
		catch(Exception e)
		{
			System.out.println("Error in Quartz scheduling...");
			e.printStackTrace();
		}
	}
}
