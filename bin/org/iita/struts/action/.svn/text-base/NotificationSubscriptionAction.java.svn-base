/**
 * projecttask.Struts Feb 1, 2010
 */
package org.iita.struts.action;

import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.List;

import org.iita.annotation.Notification;
import org.iita.security.model.UserNotification;
import org.iita.service.NotificationSubscriptionService;
import org.iita.struts.BaseAction;

import com.opensymphony.xwork2.Action;

/**
 * @author mobreza
 * 
 */
@SuppressWarnings("serial")
public class NotificationSubscriptionAction extends BaseAction {
	private NotificationSubscriptionService subscriptionService;
	private List<Method> availableNotifications = null;
	private Hashtable<String, UserNotification> subscriptions=new Hashtable<String, UserNotification>();

	/**
	 * @param subscriptionService
	 * 
	 */
	public NotificationSubscriptionAction(NotificationSubscriptionService subscriptionService) {
		this.subscriptionService = subscriptionService;
	}

	/**
	 * @return the availableNotifications
	 */
	public List<Method> getAvailableNotifications() {
		return this.availableNotifications;
	}

	/**
	 * @return the subscriptions
	 */
	public Hashtable<String, UserNotification> getSubscriptions() {
		return this.subscriptions;
	}
	
	/**
	 * @see org.iita.struts.BaseAction#prepare()
	 */
	@Override
	public void prepare() {
		this.availableNotifications = this.subscriptionService.listAvailableNotifications(getUser());
		this.subscriptions = this.subscriptionService.getSubscriptions(getUser());
		for (Method method : availableNotifications) {
			if (this.subscriptions.get(method.getName())==null) {
				this.subscriptions.put(method.getName(), new UserNotification());
			}
		}
	}
	
	/**
	 * @see org.iita.struts.BaseAction#execute()
	 */
	@Override
	public String execute() {
		if (getUser()==null)
		{
			addActionError("You need to be logged in in order to configure notification settings.");
			return Action.ERROR;
		}
		return Action.SUCCESS;
	}

	public Notification notification(Method method) {
		Notification notification = method.getAnnotation(Notification.class);
		return notification;
	}

	public String update() {
		this.subscriptionService.updateSubscriptions(getUser(), this.subscriptions);
		return "reload";
	}
}
