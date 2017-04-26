/**
 * iita-common-web.struts Aug 5, 2009
 */
package org.iita.security.action;

import org.iita.security.service.ApplicationLockService;
import org.iita.struts.BaseAction;

import com.opensymphony.xwork2.Action;

/**
 * @author mobreza
 * 
 */
@SuppressWarnings("serial")
public class ApplicationLockAction extends BaseAction {
	private ApplicationLockService applicationLockService = null;
	private String message;

	/**
	 * @param applicationLockService
	 * 
	 */
	public ApplicationLockAction(ApplicationLockService applicationLockService) {
		this.applicationLockService = applicationLockService;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return this.message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Is application locked?
	 * 
	 * @return
	 */
	public boolean isLocked() {
		return this.applicationLockService.isLocked();
	}

	/**
	 * Default action
	 * 
	 * @see com.opensymphony.xwork2.ActionSupport#execute()
	 */
	@Override
	public String execute() {
		if (this.applicationLockService.isLocked())
			this.message = this.applicationLockService.getLockMessage();
		else
			this.message = "<h1>Access to application is temporarily blocked</h1>\n<p>Please try again later.</p>";
		return Action.SUCCESS;
	}

	/**
	 * Lock application
	 * 
	 * @return
	 */
	public String lock() {
		LOG.warn("User " + getPrincipal().getUsername() + " is locking the application.");
		this.applicationLockService.lock(this.message);
		return Action.SUCCESS;
	}

	public String unlock() {
		LOG.warn("User " + getPrincipal().getUsername() + " is UN-locking the application.");
		this.applicationLockService.unlock();
		return "unlocked";
	}
}
