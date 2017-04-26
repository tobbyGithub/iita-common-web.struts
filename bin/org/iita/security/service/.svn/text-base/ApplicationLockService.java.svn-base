/**
 * iita-common-web.struts Aug 5, 2009
 */
package org.iita.security.service;

/**
 * Application lock service allows administrators to temporarily block access to application for maintenance reasons. Only ROLE_ADMINs are allowed to access the
 * application, while other users are redirected to a particular page displaying the notification message.
 * 
 * @author mobreza
 */
public interface ApplicationLockService {

	/**
	 * Is application currently locked?
	 * @return
	 */
	boolean isLocked();

	/**
	 * Get notification message
	 * @return
	 */
	String getLockMessage();

	/**
	 * Lock application using a particular notification message
	 * @param message
	 */
	void lock(String message);

	/**
	 * Unlock application
	 */
	void unlock();
}
