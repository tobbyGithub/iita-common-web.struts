/**
 * 
 */
package org.iita.security.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.iita.security.model.User;
import org.iita.security.service.UserService;
import org.iita.service.EmailException;
import org.iita.service.EmailService;
import org.iita.service.TemplatingException;
import org.iita.service.TemplatingService;
import org.iita.struts.BaseAction;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;

/**
 * @author mobreza
 */
@SuppressWarnings("serial")
public class UserProfileAction extends BaseAction {
	private UserService userService;
	private TemplatingService templatingService;
	private EmailService emailService;

	private String email;
	private String key;

	/**
	 * 
	 */
	public UserProfileAction(UserService userService) {
		this.userService = userService;
	}

	/**
	 * @param templatingService the templatingService to set
	 */
	public void setTemplatingService(TemplatingService templatingService) {
		this.templatingService = templatingService;
	}

	/**
	 * @param emailService the emailService to set
	 */
	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
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
	 * @return the key
	 */
	public String getKey() {
		return this.key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	public String execute() {
		return Action.SUCCESS;
	}

	public String requestPassword() {
		User user = userService.lookup(this.email);
		if (user == null)
			return Action.INPUT;

		// get the request key
		String key = userService.requestPassword(user);
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("key", key);
		data.put("user", user);
		this.key = key;
		String message = null;
		try {
			message = templatingService.fillTemplate("passwordrequest", data);
		} catch (TemplatingException e) {
			addActionError(e.getMessage());
			LOG.error(e);
			LOG.error(e.getCause());
			return Action.INPUT;
		}
		try {
			emailService.sendEmail(null, user.getMail(), getText("password.request.subject"), message);
		} catch (EmailException e) {
			addActionError("Could not send email. " + e.getMessage());
			return Action.INPUT;
		}
		return Action.SUCCESS;
	}

	/**
	 * Generate a new password and send it to user's email address
	 * 
	 * @return
	 */
	public String generatePassword() {
		User user = userService.lookup(this.email);
		if (user == null)
			return Action.INPUT;

		String newPasword = null;
		if (null != (newPasword = userService.generatePassword(user, this.key))) {
		//if (null != userService.generatePassword(user, this.key)) {
			
			 Map<String, Object> data = new HashMap<String, Object>(); data.put("password", newPasword); data.put("user", user); String message = null; try {
			 message = templatingService.fillTemplate("passwordgenerated", data); } catch (TemplatingException e) { addActionError(e.getMessage());
			 LOG.error(e); LOG.error(e.getCause()); return Action.INPUT; }
			 
			try {
				//userService.
				emailService.sendEmail(null, user.getMail(), getText("password.generated.subject"), message);
				//System.out.println("TESTING USER: " + user.getFullName());
				UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(user, user, user.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(authRequest);
				System.out.println("CURRENT USER: " + getUser().getFullName());
				ActionContext ac = ActionContext.getContext().getActionInvocation().getInvocationContext();
				HttpServletRequest request = (HttpServletRequest) ac.get(ServletActionContext.HTTP_REQUEST);
				request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
			} catch (Exception e) {
				addActionError("Could not send email. " + e.getMessage());
				return Action.INPUT;
			}
			return Action.SUCCESS;
		} else {
			return Action.INPUT;
		}
	}
}
