/**
 * iita-common-web.struts Aug 5, 2009
 */
package org.iita.struts.interceptor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.iita.security.Authorize;
import org.iita.security.service.ApplicationLockService;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

/**
 * @author mobreza
 * 
 */
public class ApplicationLockInterceptor extends AbstractInterceptor {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6684357328075127946L;
	private static final Log LOG = LogFactory.getLog(ApplicationLockInterceptor.class);
	private ApplicationLockService applicationLockService = null;

	/**
	 * @param applicationLockService
	 * 
	 */
	public ApplicationLockInterceptor(ApplicationLockService applicationLockService) {
		LOG.debug("Creating interceptor.");
		this.applicationLockService = applicationLockService;
	}

	/**
	 * @see com.opensymphony.xwork2.interceptor.AbstractInterceptor#intercept(com.opensymphony.xwork2.ActionInvocation)
	 */
	@Override
	public String intercept(ActionInvocation invocationContext) throws Exception {
		LOG.trace("Checking if application is locked");
		if (this.applicationLockService.isLocked()) {
			LOG.trace("Application is locked, need to check user roles!");
			// check if user has ROLE_ADMIN
			if (Authorize.hasAuthority("ROLE_ADMIN")) {
				// is admin
				LOG.debug("User is admin, allowing access.");
				return invocationContext.invoke();
			} else {
				// not admin, redirect if not on notification page
				// check if we're on notification page or login page
				String actionName = invocationContext.getProxy().getActionName();
				if (actionName.equalsIgnoreCase("application-locked")) {
					// we're on notification page, need invoke action
					LOG.trace("Invoking notification page");
					return invocationContext.invoke();
				} else {
					LOG.warn("Application is locked, blocking access to action " + actionName);
					throw new ApplicationLockedException(this.applicationLockService.getLockMessage());
				}
			}
		} else {
			// Application not locked, invoking
			return invocationContext.invoke();
		}
	}
}
