/**
 * projecttask.Struts Jan 30, 2010
 */
package org.iita.struts;

import org.iita.security.model.User;

/**
 * @author mobreza
 *
 */
public interface ApplicationNotifications {

	/**
	 * @param principal
	 */
	void userLoggedIn(User principal);

	/**
	 * @param message
	 */
	void authenticationFailed(String message);

	/**
	 * @param principal
	 */
	void userLoggingOut(User principal);

	/**
	 * @param principal
	 * @param currentAuthentication
	 */
	void userSwitched(User principal, User delegatedUser);

	/**
	 * @param principal
	 * @param currentAuthentication
	 */
	void userUnswitched(User principal, User delegatedUser);

	/**
	 * @param ex
	 */
	void applicationExceptionThrown(Throwable ex);

	/**
	 * @param sender
	 * @param recipients
	 * @param cc
	 * @param subject
	 */
	void emailsSent(String sender, String[] recipients, String[] cc, String subject);
}
