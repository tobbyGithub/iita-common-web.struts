/**
 * iita-common-web.struts Oct 11, 2010
 */
package org.iita.struts.action.admin;

import org.iita.struts.BaseAction;
import org.iita.util.StringUtil;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import com.opensymphony.xwork2.Action;

/**
 * Admin action to display scheduled jobs, pause/resume scheduler and trigger individual jobs.
 * 
 * @author mobreza
 */
@SuppressWarnings("serial")
public class SchedulerAction extends BaseAction {
	private Scheduler scheduler;
	private String jobName;
	private String groupName;

	/**
	 * @param schedulerFactoryBean the schedulerFactoryBean to set
	 */
	public void setSchedulerFactoryBean(SchedulerFactoryBean schedulerFactoryBean) {
		this.scheduler = (Scheduler) schedulerFactoryBean.getObject();
	}

	/**
	 * @return the scheduler
	 */
	public Scheduler getScheduler() {
		return this.scheduler;
	}

	/**
	 * @param jobName the jobName to set
	 */
	public void setJobName(String jobName) {
		this.jobName = StringUtil.nullOrString(jobName);
	}

	/**
	 * @param groupName the groupName to set
	 */
	public void setGroupName(String groupName) {
		this.groupName = StringUtil.nullOrString(groupName);
	}

	/**
	 * @param scheduler the scheduler to set
	 */
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	@Override
	public String execute() {
		return Action.SUCCESS;
	}

	/**
	 * Action method to trigger a job
	 * 
	 * @return
	 */
	public String trigger() {
		try {
			this.scheduler.triggerJob(this.jobName, this.groupName);
			LOG.info("Triggered job: " + this.jobName);
		} catch (SchedulerException e) {
			LOG.error(e, e);
			addActionError("Error triggering job " + this.jobName + ". " + e.getMessage());
		}
		return "reload";
	}

	public String standby() {
		try {
			this.scheduler.standby();
			LOG.warn("Scheduler was put in standy mode.");
		} catch (SchedulerException e) {
			LOG.error(e, e);
			addActionError("Error pausing scheduler. " + e.getMessage());
		}
		return "reload";
	}

	public String resume() {
		try {
			this.scheduler.start();
			LOG.warn("Scheduler was resumed.");
		} catch (SchedulerException e) {
			LOG.error(e, e);
			addActionError("Error resuming scheduler. " + e.getMessage());
		}
		return "reload";
	}
}
