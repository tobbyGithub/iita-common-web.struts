/**
 * iita-common-web.struts Oct 15, 2009
 */
package org.iita.security.action;

import java.util.List;

import org.iita.security.model.User;
import org.iita.security.service.UserService;
import org.iita.struts.BaseAction;
import org.iita.util.PagedResult;

import com.opensymphony.xwork2.Action;

/**
 * Action class to manage application roles and users in those roles.
 * 
 * @author mobreza
 */
@SuppressWarnings("serial")
public class UserRolesAction extends BaseAction {
	private UserService userService;
	private List<String> allRoles;
	private String role=null;
	private int startAt = 0, maxResults = 50;
	private PagedResult<User> paged;
	
	/**
	 *  
	 * @param userService 
	 */
	public UserRolesAction(UserService userService) {
		this.userService=userService;
	}
	
	/**
	 * @param startAt the startAt to set
	 */
	public void setStartAt(int startAt) {
		this.startAt = startAt;
	}
	
	/**
	 * @param role the role to set
	 */
	public void setRole(String role) {
		this.role = role;
	}
	
	/**
	 * @return the allRoles
	 */
	public List<String> getAllRoles() {
		return this.allRoles;
	}
	
	/**
	 * @return the paged
	 */
	public PagedResult<User> getPaged() {
		return this.paged;
	}
	
	/**
	 * Default action method lists all roles available in the application
	 * 
	 * @see org.iita.struts.BaseAction#execute()
	 */
	@Override
	public String execute() {
		this.allRoles=this.userService.getUserRoles();
		if (this.role!=null) {
			this.paged=this.userService.findByRole(this.role, this.startAt, this.maxResults);
		}
		return Action.SUCCESS;
	}
}
