/**
 * projecttask.Struts Oct 7, 2010
 */
package org.iita.struts.action.admin;

import org.apache.log4j.Appender;
import org.apache.log4j.LogManager;
import org.iita.log4j.MemoryAppender;
import org.iita.struts.BaseAction;

import com.opensymphony.xwork2.Action;

/**
 * @author mobreza
 */
@SuppressWarnings("serial")
public class LogViewAction extends BaseAction {
	private MemoryAppender memoryAppender;
	private Integer maxEvents;

	/**
	 * @return the memoryAppender
	 */
	public MemoryAppender getMemoryAppender() {
		return this.memoryAppender;
	}
	
	public void prepare() {
		Appender appender = LogManager.getRootLogger().getAppender("memory");
		if (appender != null && appender instanceof MemoryAppender) {
			this.memoryAppender = (MemoryAppender) appender;
		}
	}
	
	public void setMaxEvents(int maxEvents) {
		this.maxEvents=maxEvents;
	}

	/**
	 * @see org.iita.struts.BaseAction#execute()
	 */
	@Override
	public String execute() {
		return Action.SUCCESS;
	}
	
	public String reconfigure() {
		if (this.maxEvents!=null) {
			if (this.memoryAppender!=null)
				this.memoryAppender.setMaxEvents(this.maxEvents);
		}
		return "refresh";
	}
	
	public String clear() {
		if (this.memoryAppender!=null)
			this.memoryAppender.clearBuffer();
		return "refresh";
	}
}
