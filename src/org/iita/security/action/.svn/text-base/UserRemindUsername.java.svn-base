/**
 * 
 */
package org.iita.security.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.iita.security.IitaAuthentication;
import org.iita.security.service.UserService;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

/**
 * @author mobreza
 * 
 */
@SuppressWarnings("serial")
public class UserRemindUsername extends ActionSupport {
	private static final Log log = LogFactory.getLog(IitaAuthentication.class);

	private UserService userService;
	private String email = null;
	private String username;

	/**
	 * @return the email
	 */
	public String getEmail() {
		return this.email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return this.username;
	}

	public UserRemindUsername(UserService userService) {
		this.userService = userService;
	}

	public String execute() {
		return Action.SUCCESS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.iita.par.action.BaseActionPar#prepare()
	 */
	public String tell() {
		if (this.email != null) {
			try {
				log.info("Telling usernames for " + this.email + " to " + ServletActionContext.getRequest().getRemoteAddr());
				this.username = userService.lookup(this.email).getUsername();
			} catch (Exception e) {

			}
		}
		return Action.SUCCESS;
	}
}
