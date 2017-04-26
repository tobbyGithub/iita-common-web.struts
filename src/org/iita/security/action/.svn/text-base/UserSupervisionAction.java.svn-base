/**
 * 
 */
package org.iita.security.action;

import java.util.List;

import org.iita.security.model.User;
import org.iita.security.model.UserSupervisor;
import org.iita.security.service.UserService;
import org.iita.security.service.UserServiceException;
import org.iita.security.service.UserSupervisionService;
import org.iita.struts.BaseAction;

import com.opensymphony.xwork2.Action;

/**
 * @author mobreza
 * 
 */
@SuppressWarnings("serial")
public class UserSupervisionAction extends BaseAction {
	private UserService userService;
	private UserSupervisionService userSupervisionService;

	private List<User> delegatedTo = null, delegatedFrom = null;
	private String email;

	/**
	 * 
	 */
	public UserSupervisionAction(UserService userService, UserSupervisionService userSupervisionService) {
		this.userService=userService;
		this.userSupervisionService = userSupervisionService;
	}

	/**
	 * @return the delegatedTo
	 */
	public List<User> getDelegatedTo() {
		if (this.delegatedTo == null) {
			this.delegatedTo = userSupervisionService.getSupervisors(getPrincipal());
		}
		return this.delegatedTo;
	}

	/**
	 * @return the delegatedFrom
	 */
	public List<User> getDelegatedFrom() {
		if (this.delegatedFrom == null) {
			this.delegatedFrom = userSupervisionService.getSupervisedUsers(getPrincipal());
		}
		return this.delegatedFrom;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return this.email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	public String execute() {
		return Action.SUCCESS;
	}

	/**
	 * Method executed to grant access to somebody
	 * 
	 * @return
	 */
	public String to() {
		if (this.email == null || this.email.trim().length() == 0) {
			addFieldError("email", "Please provide the email of the supervisor you wish to add to the list.");
			return Action.INPUT;
		}

		User supervisor = userService.lookup(this.email);
		if (supervisor == null) {
			addFieldError("email", "Could not find user with email: " + this.email);
			return Action.INPUT;
		}

		try {
			userSupervisionService.addSupervisor(getPrincipal(), supervisor);
			return "reload";
		} catch (UserServiceException e) {
			addActionError(e.getMessage());
			return "input";
		}
	}

	public String delete() {
		if (this.email == null || this.email.trim().length() == 0) {
			addFieldError("email", "Please provide the email of the delegatee you wish to remove from the list.");
			return Action.INPUT;
		}

		User supervisor = userService.lookup(this.email);
		if (supervisor == null) {
			addFieldError("email", "Could not find user with email: " + this.email);
			return Action.INPUT;
		}

		try {
			userSupervisionService.deleteSupervisor(getPrincipal(), supervisor);
		} catch (UserServiceException e) {
			addActionError(e.getMessage());
			return "input";
		}
		return "reload";
	}

	public String switchuser() {
		try {
			System.out.println("switchuser executed!!!");
			User supervisor = userService.lookup(this.email);
			if (supervisor == null) {
				addFieldError("email", "Could not find user with email: " + this.email);
				return Action.INPUT;
			}
			UserSupervisor delegation = userSupervisionService.findSupervisor(getPrincipal(), supervisor);
			if (delegation == null) {
				addActionError("You are not delegated to manage data for " + this.email + "");
				return Action.INPUT;
			} else {
				System.out.println("switchuser executed!!! and your principal is  " + getPrincipal().getFirstName());
				System.out.println("switchuser executed!!! and your delegation is  " + delegation.getSupervisor().getFirstName());
				userSupervisionService.switchUser(getPrincipal(), delegation);
				return "switch";
			}
		} catch (UserServiceException e) {
			addActionError(e.getMessage());
			return Action.INPUT;
		}
	}

	public String unswitch() {
		userSupervisionService.unswitchUser(getPrincipal());
		return "reload";
	}
}
