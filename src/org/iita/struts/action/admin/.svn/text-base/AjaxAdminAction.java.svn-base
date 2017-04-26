/**
 * iita-common-web.struts Oct 12, 2010
 */
package org.iita.struts.action.admin;

import java.util.List;

import org.iita.security.model.User;
import org.iita.security.service.UserService;

import com.googlecode.jsonplugin.annotations.SMDMethod;
import com.opensymphony.xwork2.Action;

/**
 * JSON Action to expose methods to administrator screen
 * 
 * @author mobreza
 */
public class AjaxAdminAction {
	private UserService userService;

	public AjaxAdminAction(UserService userService) {
		this.userService = userService;
	}

	public String execute() {
		return Action.SUCCESS;
	}

	@SMDMethod
	public List<User> autocompleteUser(String text) {
		return this.userService.autocompleteUser(text, 10);
	}
}