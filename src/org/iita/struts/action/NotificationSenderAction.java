package org.iita.struts.action;

import org.iita.security.model.User;
import org.iita.security.model.UserNotification.NotificationFrequency;
import org.iita.security.model.UserNotification.NotificationPriority;
import org.iita.security.service.UserService;
import org.iita.service.UserNotificationService;
import org.iita.struts.BaseAction;

import com.opensymphony.xwork2.Action;

@SuppressWarnings("serial")
public class NotificationSenderAction extends BaseAction {
	private UserService userService;
	private UserNotificationService notificationSender;
	private String username;
	private String subject;
	private String msgBody;
	private User user;

	public NotificationSenderAction(UserService userService, UserNotificationService notificationSender) {
		this.userService = userService;
		this.notificationSender = notificationSender;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setMsgBody(String msgBody) {
		this.msgBody = msgBody;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String execute() {
		if (getUser()==null)
		{
			addActionError("You need to be logged in in order to configure notification settings.");
			return Action.ERROR;
		}
		
		return Action.SUCCESS;
	}

	public String mailSender() {
		if (getUser()==null)
		{
			addActionError("You need to be logged in in order to configure notification settings.");
			return Action.ERROR;
		}
		
		this.user = this.userService.loadUserByUsername(this.username);

		if (this.user != null) {
			this.notificationSender.sendBroadcast(this.getUser().getUserName(), this.user, this.subject, this.msgBody, NotificationFrequency.WITHINAPPLICATION,
					NotificationPriority.NORMAL);
			addActionMessage("Message sent successfully!");
			return Action.SUCCESS;
		} else {
			addActionMessage("User by " + this.username + " not found! Mail not sent successfully!");
			return Action.ERROR;
		}
	}
}
