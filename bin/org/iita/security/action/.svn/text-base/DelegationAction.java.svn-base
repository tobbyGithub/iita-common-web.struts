/**
 * 
 */
package org.iita.security.action;

import java.util.Calendar;
import java.util.List;

import org.iita.security.model.User;
import org.iita.security.model.UserDelegation;
import org.iita.security.service.UserService;
import org.iita.security.service.UserServiceException;
import org.iita.struts.BaseAction;
import org.jfree.util.Log;

import com.opensymphony.xwork2.Action;

/**
 * @author mobreza
 * 
 */
@SuppressWarnings("serial")
public class DelegationAction extends BaseAction {
	private UserService userService;

	private List<User> delegatedTo = null, delegatedFrom = null;
	private List<UserDelegation> userDelegatedTo = null, userDelegatedFrom = null;
	private String email;
	private Calendar fromDate = null;
	private Calendar toDate = null;

	/**
	 * 
	 */
	public DelegationAction(UserService userService) {
		this.userService = userService;
	}

	/**
	 * @return the delegatedTo
	 */
	public List<User> getDelegatedTo() {
		if (this.delegatedTo == null) {
			this.delegatedTo = userService.getDelegatedTo(getUser());
		}
		return this.delegatedTo;
	}

	/**
	 * @return the delegatedFrom
	 */
	public List<User> getDelegatedFrom() {
		if (this.delegatedFrom == null) {
			this.delegatedFrom = userService.getDelegatedFrom(getUser());
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
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	
	/**
	 * @return the fromDate
	 */
	public Calendar getFromDate() {
		return this.fromDate;
	}

	/**
	 * @param fromDate the fromDate to set
	 */
	public void setFromDate(Calendar fromDate) {
		this.fromDate = fromDate;
	}
	
	/**
	 * @return the toDate
	 */
	public Calendar getToDate() {
		return this.toDate;
	}

	/**
	 * @param toDate the toDate to set
	 */
	public void setToDate(Calendar toDate) {
		this.toDate = toDate;
	}
	
	/**
	 * @return the userDelegatedTo
	 */
	public List<UserDelegation> getUserDelegatedTo() {
		if (this.userDelegatedTo == null) {
			this.userDelegatedTo = userService.getUserDelegatedTo(getUser());
		}
		return this.userDelegatedTo;
	}

	/**
	 * @return the userDelegatedFrom
	 */
	public List<UserDelegation> getUserDelegatedFrom() {
		if (this.userDelegatedFrom == null) {
			this.userDelegatedFrom = userService.getUserDelegatedFrom(getUser());
		}
		
		for(UserDelegation usrFrm : this.userDelegatedFrom){
			System.out.println("Owner: " + usrFrm.getOwner().getFullName());
			System.out.println("Delegated To: " + usrFrm.getDelegatedTo().getFullName());
		}
		
		return this.userDelegatedFrom;
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
			addFieldError("email", "Please provide the email of the delegatee you wish to remove from the list.");
			return Action.INPUT;
		}

		User delegate = userService.lookup(this.email);
		if (delegate == null) {
			addFieldError("email", "Could not find user with email: " + this.email);
			return Action.INPUT;
		}

		try {
			userService.addDelegation(getUser(), delegate);
			return "reload";
		} catch (UserServiceException e) {
			addActionError(e.getMessage());
			return "input";
		}
	}
	
	/**
	 * Method executed to grant access to somebody
	 * 
	 * @return
	 */
	public String toTa() {
		if (this.email == null || this.email.trim().length() == 0) {
			addFieldError("email", "Please provide the email of the delegatee you wish to remove from the list.");
			return Action.INPUT;
		}

		User delegate = userService.lookup(this.email);
		if (delegate == null) {
			addFieldError("email", "Could not find user with email: " + this.email);
			return Action.INPUT;
		}

		try {
			Log.info("FromDate VALUE: " + fromDate);
			Log.info("ToDate VALUE: " + toDate);
			userService.addDelegation(getUser(), delegate, fromDate, toDate);//
			return "reload";
		} catch (UserServiceException e) {
			addActionError(e.getMessage());
			return "input";
		}
	}

	public String delete() {
		try {
			userService.deleteDelegation(getUser(), this.email);
		} catch (UserServiceException e) {
			addActionError(e.getMessage());
			return "input";
		}
		return "reload";
	}

	public String switchuser() {
		try {
			UserDelegation delegation = userService.findDelegation(getPrincipal(), this.email);
			if (delegation == null) {
				addActionError("You are not delegated to manage data for " + this.email + "");
				return Action.INPUT;
			} else {
				userService.switchUser(getPrincipal(), delegation);
				return "switch";
			}
		} catch (UserServiceException e) {
			addActionError(e.getMessage());
			return Action.INPUT;
		}
	}

	public String unswitch() {
		userService.unswitchUser();
		return "switch";
	}
}
