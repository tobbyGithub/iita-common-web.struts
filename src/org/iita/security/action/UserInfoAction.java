/**
 * iita-common-web.struts Jun 8, 2009
 */
package org.iita.security.action;

import org.iita.security.service.UserService;
import org.iita.struts.BaseAction;

import com.opensymphony.xwork2.Action;

/**
 * User information action exposes the currently logged in user (or switched-to user)
 * 
 * @author mobreza
 * 
 */
@SuppressWarnings("serial")
public class UserInfoAction extends BaseAction {
	private UserService userService = null;

	/**
	 * @param userService
	 * 
	 */
	public UserInfoAction(UserService userService) {
		this.userService = userService;
	}

	/**
	 * @see com.opensymphony.xwork2.ActionSupport#execute()
	 */
	@Override
	public String execute() {
		return Action.SUCCESS;
	}
	
	public boolean isSwitched() {
		return this.userService.isUserSwitched();
	}
}
