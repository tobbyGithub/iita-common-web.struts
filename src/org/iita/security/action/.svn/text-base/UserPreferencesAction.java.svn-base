/**
 * iita-common-web.struts May 7, 2009
 */
package org.iita.security.action;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.iita.security.model.Preference;
import org.iita.security.model.User;
import org.iita.security.service.UserService;
import org.iita.struts.BaseAction;

import com.opensymphony.xwork2.Action;

/**
 * @author mobreza
 * 
 */
@SuppressWarnings("serial")
public class UserPreferencesAction extends BaseAction {
	private static final Log log = LogFactory.getLog(UserPreferencesAction.class);
			
	private UserService userService;
	
	private List<Preference> settings;

	/**
	 * @param userService
	 */
	public UserPreferencesAction(UserService userService) {
		this.userService = userService;
	}

	public List<Preference> getSettings() {
		return settings;
	}

	@Override
	public void prepare() {
		log.info("prepare");
		
		User user = getUser();
		
		settings = user.getPreferences();
		
		for (Preference pref : settings) {
			LOG.debug("Prepare: " + pref.getClass().getName() + ": " + pref.getPreferenceKey() + " = " + pref.getPreferenceValue());
		}
	}
	
	/**
	 * @see com.opensymphony.xwork2.ActionSupport#execute()
	 */
	@Override
	public String execute() {		
		return Action.SUCCESS;
	}

	public String store() {
		for (Preference pref : settings) {
			LOG.debug("Store: " + pref.getClass().getName() + ": " + pref.getPreferenceKey() + " = " + pref.getPreferenceValue());
		}

		User u = this.userService.find(getUser().getId());
		if (u != null) {
			u.setPreferences(settings);
			this.userService.save(u);
			return "reload";
		}

		addActionError("Could not load user!");
		return Action.ERROR;
	}	
}
