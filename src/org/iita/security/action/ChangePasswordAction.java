/**
 * 
 */
package org.iita.security.action;

import org.iita.security.model.User;
import org.iita.security.service.UserService;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;

/**
 * "Change password" action.
 * 
 * @author mobreza
 * 
 */
@SuppressWarnings("serial")
public class ChangePasswordAction extends ActionSupport implements Preparable {
	private UserService userService;
	private Long id;
	private User user;
	/**
	 * The two passwords
	 */
	private String passwd1, passwd2;

	/**
	 * 
	 */
	public ChangePasswordAction(UserService userService) {
		this.userService = userService;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return this.user;
	}

	/**
	 * @return the passwd1
	 */
	public String getPasswd1() {
		return this.passwd1;
	}

	/**
	 * @return the passwd2
	 */
	public String getPasswd2() {
		return this.passwd2;
	}

	/**
	 * @param passwd1 the passwd1 to set
	 */
	public void setPasswd1(String passwd1) {
		this.passwd1 = passwd1;
	}

	/**
	 * @param passwd2 the passwd2 to set
	 */
	public void setPasswd2(String passwd2) {
		this.passwd2 = passwd2;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.iita.par.action.BaseActionPar#prepare()
	 */
	@Override
	public void prepare() {
		if (this.id != null)
			this.user = userService.find(this.id);
	}

	public String execute() {
		return Action.INPUT;
	}

	/**
	 * Set new password
	 * 
	 * @return
	 */
	public String set() {
		if (this.user == null)
			return Action.INPUT;
		if (this.passwd1 == null || this.passwd1.length() == 0) {
			addActionError("Please provide the new password.");
			return Action.INPUT;
		}
		if (!this.passwd1.equals(this.passwd2)) {
			addActionError("Passwords do not match.");
			return Action.INPUT;
		}

		userService.setPassword(user, passwd1);
		return Action.SUCCESS;
	}

	/**
	 * Set new password
	 * 
	 * @return
	 */
	public String toldap() {
		if (this.user == null)
			return Action.INPUT;
		userService.clearPassword(user);
		return Action.SUCCESS;
	}
}
