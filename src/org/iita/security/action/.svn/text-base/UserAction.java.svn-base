/**
 * 
 */
package org.iita.security.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.iita.security.model.User;
import org.iita.security.model.UserLookup;
import org.iita.security.model.UserRole;
import org.iita.security.service.UserLookupService;
import org.iita.security.service.UserRoleService;
import org.iita.security.service.UserService;
import org.iita.struts.BaseAction;
import org.iita.util.PagedResult;

import com.opensymphony.xwork2.Action;

/**
 * @author aafolayan
 * 
 */
@SuppressWarnings("serial")
public class UserAction extends BaseAction {
	/** Service object to be injected by DI container */
	private UserService userService;
	private UserLookupService lookupService;
	private UserRoleService roleService;
	/** Model object to be accessed on top of the Value Stack */
	private PagedResult<User> users;
	/** Id of user to work with */
	private Long id;
	/** Model object to be accessed on top of the Value Stack */
	private User user;
	/** Model object to be accessed on top of the Value Stack */
	private List<String> roles = new ArrayList<String>();
	/** Model object to be accessed on top of the Value Stack */
	private List<String> lookups = new ArrayList<String>();
	/** List of all available roles */
	// private List<String> allRoles = new ArrayList<String>();
	private Map<String, Boolean> allRoles = new HashMap<String, Boolean>();

	private String application;
	private int startAt = 0, maxResults = 50;
	/** Filter to be used when searching for users */
	private String filter = null;
	private boolean includeImportService = false;

	/**
	 * @param userLookupService
	 * @param userRoleService
	 * @param userService
	 */
	public UserAction(UserService userService) {
		this.userService = userService;
	}

	/**
	 * Set search filter
	 * 
	 * @param filter
	 */
	public void setFilter(String filter) {
		this.filter = filter;
	}

	/**
	 * @param includeImportService the includeImportService to set
	 */
	public void setExtensiveSearch(boolean includeImportService) {
		this.includeImportService = includeImportService;
	}

	public boolean getExtensiveSearch() {
		return this.includeImportService;
	}

	/**
	 * Get search filter
	 * 
	 * @return
	 */
	public String getFilter() {
		return filter;
	}

	/**
	 * @param startAt the startAt to set
	 */
	public void setStartAt(int startAt) {
		this.startAt = startAt;
	}

	public String execute() {
		users = userService.findAll(startAt, maxResults);
		return Action.SUCCESS;
	}

	public String search() {
		users = userService.findAll(startAt, maxResults, filter, this.includeImportService);
		if (users.getTotalHits() == 1) {
			user = users.getResults().get(0);
			if (user.getId() == null) {
				addActionMessage("Found one user using import service.");
				return Action.INPUT;
			} else
				return "found-one";
		} else if (users.getTotalHits() == 0) {
			addActionMessage("No matching users found.");
			return Action.SUCCESS;
		} else
			return Action.SUCCESS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.opensymphony.xwork2.Preparable#prepare()
	 */
	@Override
	public void prepare() {
		if (id != null) {
			user = userService.find(id);
		} else {

		}
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	/**
	 * @return the users
	 */
	public PagedResult<User> getPaged() {
		return users;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param role the role to set
	 */
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	/**
	 * @param lookup the lookup to set
	 */
	public void setLookups(List<String> lookups) {
		this.lookups = lookups;
	}

	public String update() {
		save();
		// addBlanks();
		return "reload";
	}

	public String save() {
		checkRoles();// this ensures that 'false' value is removed from roles should no role is checked in the view checkboxes
		addLookups();
		addRoles();
		fixAccessTags();
		userService.save(user);
		return Action.SUCCESS;
	}

	private void checkRoles() {
		if (roles.size() == 1) {
			if (roles.get(0).equals("false")) {
				roles.remove(0);
			}
		}
	}
	
	private void fixAccessTags() {
		List<String> accessTags=user.getAccessTags();
		for (int i=accessTags.size()-1; i>=0; i--) {
			String tag=accessTags.get(i);
			if (tag==null || tag.trim().length()==0)
				accessTags.remove(i);
		}
	}

	private void addLookups() {
		// remove initial lookup values for this user
		List<UserLookup> userLookup = user.getLookups();
		if (userLookup.size() > 0) {
			for (UserLookup lookup : userLookup) {
				lookupService.remove(lookup);
			}
		}
		// create new lookup values for user based on the submitted form
		userLookup = new ArrayList<UserLookup>();
		for (String newLookup : lookups) {
			LOG.debug("New lookup: " + newLookup);
			UserLookup lookup = new UserLookup();
			lookup.setIdentifier(newLookup);
			lookup.setUser(user);
			userLookup.add(lookup);
		}
		user.setLookups(userLookup);
	}

	private void addRoles() {
		// remove initial role values for this user
		List<UserRole> userRoles = user.getRoles();
		if (userRoles.size() > 0) {
			for (UserRole role : userRoles) {
				roleService.remove(role);
			}
		}
		// create new role values for user based on the submitted form
		userRoles = new ArrayList<UserRole>();
		for (String newRole : roles) {
			LOG.debug("New role: " + newRole);
			UserRole role = new UserRole();
			role.setApplication(getApplication());
			role.setRole(newRole);
			role.setUser(user);
			userRoles.add(role);
		}
		user.setRoles(userRoles);
	}

	/**
	 * old roles are retrieved and their values updated in the all roles list collected in order to reflect their checked state in the view checkboxes
	 */
	public String input() {
		List<String> roleList = userService.getUserRoles();
		for (String role : roleList) {
			allRoles.put(role, new Boolean(false));
		}
		LOG.debug("all roles value before modification : " + allRoles.values());
		
		if (user!=null) {
			LOG.debug("ROLE SIZE: " + user.getRoles().size());
			for (UserRole role : user.getRoles()) {
				if (allRoles.containsKey(role.getRole())) {
					allRoles.put(role.getRole(), new Boolean(true));
				}
			}
		}
		LOG.debug("all roles value after modification : " + allRoles.values());
		return Action.INPUT;
	}

	public String delete() {
		user.setLookups(null);
		user.setRoles(null);
		return userService.remove(id);
	}

	public String switchto() {
		userService.switchUser(this.user);
		return "switch";
	}

	public String unswitch() {
		userService.unswitchUser();
		return "switch";
	}

	/**
	 * @return the allRoles
	 */
	public Map<String, Boolean> getAllRoles() {
		return allRoles;
	}

	/**
	 * @param lookupService the lookupService to set
	 */
	public void setLookupService(UserLookupService lookupService) {
		this.lookupService = lookupService;
	}

	/**
	 * @return the lookupService
	 */
	public UserLookupService getLookupService() {
		return lookupService;
	}

	/**
	 * @param roleService the roleService to set
	 */
	public void setRoleService(UserRoleService roleService) {
		this.roleService = roleService;
	}

	/**
	 * @return the roleService
	 */
	public UserRoleService getRoleService() {
		return roleService;
	}
}
