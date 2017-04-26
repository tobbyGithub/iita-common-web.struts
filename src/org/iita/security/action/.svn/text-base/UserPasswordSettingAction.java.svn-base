/**
 * 
 */
package org.iita.security.action;

import javax.naming.CommunicationException;

import org.iita.security.service.UserService;
import org.iita.security.service.impl.LDAPUserService;
import org.iita.struts.BaseAction;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;

/**
 * @author koraegbunam
 *
 */
@SuppressWarnings("serial")
public class UserPasswordSettingAction extends BaseAction {
	private UserService userService;
	private LDAPUserService ldapUserService;
	//private User user;
	/**
	 * The two passwords
	 */
	private String passwd1, passwd2, ldapPassword;
	
	/**
	 * 
	 */
	public UserPasswordSettingAction(UserService userService, LDAPUserService ldapUserService) {
		this.userService = userService;
		this.ldapUserService = ldapUserService;
	}
	
	/**
	 * @return the user
	 */
	//public User getUser() {
	//	return this.user;
	//}

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
	 * @param ldapPassword the ldapPassword to set
	 */
	public void setLdapPassword(String ldapPassword) {
		this.ldapPassword = ldapPassword;
	}

	/**
	 * @return the ldapPassword
	 */
	public String getLdapPassword() {
		return ldapPassword;
	}
	
	public String execute() {
		return Action.INPUT;
	}
	
	/**
	 * Set new password to use LDAP
	 * 
	 * @return
	 */
	@Validations(requiredStrings = { @RequiredStringValidator(fieldName = "ldapPassword", trim = true, message = "Specify your IITA Network password") })
	public String useLDAP(){
		//System.out.print(getUser() + " TEST: " + this.ldapPassword);
		//Validate current user object
		if (getUser() == null){
			addActionError("Invalid current User session!");
			return Action.ERROR;
		}
		
		//Check if entered password is valid before activating LDAP password
		if(this.ldapPassword==null){
			addActionError("Enter your IITA Network password to proceed!");
			return Action.ERROR;
		}
		
		try {
			//Validate password against LDAP
			if(ldapUserService.authenticate(getUser().getUsername(), this.ldapPassword, getUser())){
				userService.clearPassword(getUser());
				addActionMessage("LDAP Password setting successful!");
			}else{
				addActionError("Invalid LDAP user password encountered!");
				return Action.ERROR;
			}
		} catch (CommunicationException e) {
			//e.printStackTrace();
			addActionError("Communcation could not be established with the Active Directory.");
			return Action.ERROR;
		}
		return Action.SUCCESS;
	} 
	
	/**
	 * Set new password to user password choice
	 * 
	 * @return
	 */
	@Validations(requiredStrings = { @RequiredStringValidator(fieldName = "passwd1", trim = true, message = "Specify your custom password") })
	public String usePassword(){
		//Validate current user object
		if (getUser() == null){
			addActionError("Invalid current User session!");
			return Action.ERROR;
		}
		
		//Validate entered passwords
		if (this.passwd1 == null || this.passwd1.length() == 0) {
			addActionError("Please provide the new password.");
			return Action.INPUT;
		}
		if (!this.passwd1.equals(this.passwd2)) {
			addActionError("Passwords do not match.");
			return Action.INPUT;
		}
	
		addActionMessage("Custom Password setting successful!");
	
		userService.setPassword(getUser(), passwd1);
		return Action.SUCCESS;
	}

	@Override
	public void prepare() {
		// TODO Auto-generated method stub
		
	}
}
