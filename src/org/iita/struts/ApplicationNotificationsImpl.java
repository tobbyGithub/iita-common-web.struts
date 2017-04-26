/**
 * projecttask.Struts Jan 30, 2010
 */
package org.iita.struts;

import java.util.IllegalFormatException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.iita.annotation.Notification;
import org.iita.notifications.ApplicationEventNotification;
import org.iita.security.Authorize;
import org.iita.security.model.User;
import org.iita.security.model.UserNotification;
import org.iita.security.model.UserStatus;
import org.iita.service.DaemonService;
import org.iita.service.NotificationSubscriptionService;
import org.iita.service.UserNotificationSender;
import org.iita.util.StringUtil;

/**
 * 
 * @author mobreza
 */
public class ApplicationNotificationsImpl extends DaemonService implements ApplicationNotifications {
	private static final String EXCEPTION_THROWN = "Exception thrown %1$s\nStack trace: %2$s";
	private static final String AUTHENTICATION_FAILED = "Authentication failed with error: %1$s";
	private static final String USER_LOGGED_IN = "User %1$s logged in.";
	private static final String USER_LOGGING_OUT = "User %1$s logging out.";
	private static final String USER_SWITCHED_TO = "User %1$s switched to %2$s.";
	private static final String USER_SWITCHED_BACK = "User %1$s switched back from %2$s.";
	private static final Log LOG = LogFactory.getLog(ApplicationNotificationsImpl.class);
	private static final String EMAILS_SENT = "Emails sent to %2$s, cc %3$s: %4$s";
	private NotificationSubscriptionService subscriptionService;
	private Queue<ApplicationEventNotification> incomingQueue = new LinkedList<ApplicationEventNotification>();
	protected UserNotificationSender notificationSender = null;

	/**
	 * @param notificationSender
	 * 
	 */
	public ApplicationNotificationsImpl(UserNotificationSender notificationSender, NotificationSubscriptionService subscriptionService) {
		this.notificationSender = notificationSender;
		this.subscriptionService = subscriptionService;
		start("Application notifications");
	}

	/**
	 * @param eventName
	 * @param subject
	 * @param defaultFormat
	 * @param params
	 */
	protected void sendNotification(User sender, String eventName, String subject, String defaultFormat, Object... params) {
		LOG.debug("Sending : " + eventName + " as BROADCAST");
		ApplicationEventNotification eventNotification = new ApplicationEventNotification(sender, eventName, subject, defaultFormat, params);
		enqueue(eventNotification);
	}

	/**
	 * @param eventName
	 * @param allowedSubscribers
	 * @param subject
	 * @param defaultFormat
	 * @param params
	 */
	protected void sendNotifications(User sender, String eventName, User singleSubscriber, String subject, String defaultFormat, Object... params) {
		LOG.debug("Sending : " + eventName + " as SINGLE to " + singleSubscriber);
		ApplicationEventNotification eventNotification = new ApplicationEventNotification(sender, eventName, singleSubscriber, subject, defaultFormat, params);
		enqueue(eventNotification);
	}

	/**
	 * @param eventName
	 * @param allowedSubscribers
	 * @param subject
	 * @param defaultFormat
	 * @param params
	 */
	protected void sendNotifications(User sender, String eventName, List<User> allowedSubscribers, String subject, String defaultFormat, Object... params) {
		LOG.debug("Sending : " + eventName + " as MULTIPLE to " + allowedSubscribers.size());
		// don't deliver to user who triggered the event
		allowedSubscribers.remove(Authorize.getUser());
		ApplicationEventNotification eventNotification = new ApplicationEventNotification(sender, eventName, allowedSubscribers, subject, defaultFormat, params);
		enqueue(eventNotification);
	}

	private synchronized void enqueue(ApplicationEventNotification eventNotification) {
		LOG.debug("Entered enqueue");
		synchronized (this.incomingQueue) {
			LOG.debug("Got incomingqueue");
			incomingQueue.add(eventNotification);
		}
		LOG.debug("Notfiying all");
		notifyAll();
	}

	/**
	 * @see org.iita.service.DaemonService#businessMethod()
	 */
	@Override
	protected void businessMethod() {
		ApplicationEventNotification eventNotification = null;
		do {
			synchronized (this.incomingQueue) {
				eventNotification = this.incomingQueue.poll();
			}
			if (eventNotification != null)
				processNotification(eventNotification);

		} while (eventNotification != null);
	}

	/**
	 * @param eventNotification
	 */
	private void processNotification(ApplicationEventNotification eventNotification) {
		LOG.debug("Processing Notification of type " + eventNotification.getNotiticationType());
		List<UserNotification> userNotifications = this.subscriptionService.getUserNotifications(eventNotification.getEventName(), eventNotification
				.getNotiticationType(), eventNotification.getAllowedSubscribers());
		if (userNotifications == null || userNotifications.size() == 0) {
			LOG.debug("No subscriptions for " + eventNotification.getEventName());
			return;
		}

		for (UserNotification userNotification : userNotifications) {
			if (eventNotification.getSender() != null && userNotification.getUser().getId().equals(eventNotification.getSender().getId())) {
				LOG.debug("Skipping sender");
				continue;
			}
			
			if (userNotification.getUser().getStatus()!=UserStatus.ENABLED) {
				LOG.info("Recipient is not enabled in this application (" + userNotification.getUser().getStatus() + "). Will not send notification.");
				continue;
			}

			LOG.debug("Sending message to " + userNotification.getUser());

			String userFormatString = userNotification.getFormatString();
			String notificationMsg = null;

			if (userFormatString != null) {
				try {
					notificationMsg = String.format(userFormatString, eventNotification.getParams());
				} catch (IllegalFormatException e) {
					LOG.error("User " + userNotification.getUser() + " is using a faulty format string: " + userFormatString);
					notificationMsg = String.format(eventNotification.getDefaultFormat(), eventNotification.getParams());
				}
			} else {
				try {
					notificationMsg = String.format(eventNotification.getDefaultFormat(), eventNotification.getParams());
				} catch (IllegalFormatException e) {
					LOG.error("Application using faulty format string: " + eventNotification.getDefaultFormat());
					return;
				}
			}
			
			// include sender name
			if (eventNotification.getSender()!=null)
				notificationMsg=String.format("%1$s (%2$s)", notificationMsg, eventNotification.getSender().getFullName());

			notificationSender.sendBroadcast(null, userNotification.getUser(), eventNotification.getSubject(), notificationMsg,
					userNotification.getFrequency(), userNotification.getPriority());
		}
	}

	/**
	 * @see org.iita.struts.ApplicationNotifications#authenticationFailed(java.lang.String)
	 */
	@Override
	@Notification(defaultFormat = AUTHENTICATION_FAILED, requiredRoles = { "ROLE_ADMIN" })
	public void authenticationFailed(String message) {
		sendNotification(Authorize.getUser(), "authenticationFailed", null, AUTHENTICATION_FAILED, message);
	}

	/**
	 * @see org.iita.struts.ApplicationNotifications#userLoggedIn(org.iita.security.model.User)
	 */
	@Override
	@Notification(defaultFormat = USER_LOGGED_IN, requiredRoles = { "ROLE_ADMIN" })
	public void userLoggedIn(User principal) {
		sendNotification(Authorize.getUser(), "userLoggedIn", null, USER_LOGGED_IN, principal.toString());
	}

	/**
	 * @see org.iita.struts.ApplicationNotifications#userLoggingOut(org.iita.security.model.User)
	 */
	@Override
	@Notification(defaultFormat = USER_LOGGING_OUT, requiredRoles = { "ROLE_ADMIN" })
	public void userLoggingOut(User principal) {
		sendNotification(Authorize.getUser(), "userLoggingOut", null, USER_LOGGING_OUT, principal.toString());
	}

	/**
	 * @see org.iita.struts.ApplicationNotifications#userSwitched(org.iita.security.model.User, org.iita.security.model.User)
	 */
	@Override
	@Notification(defaultFormat = USER_SWITCHED_TO, requiredRoles = { "ROLE_ADMIN" })
	public void userSwitched(User principal, User delegatedUser) {
		sendNotification(Authorize.getUser(), "userSwitched", null, USER_SWITCHED_TO, principal.toString(), delegatedUser.toString());
	}

	/**
	 * @see org.iita.struts.ApplicationNotifications#userUnswitched(org.iita.security.model.User, org.iita.security.model.User)
	 */
	@Override
	@Notification(defaultFormat = USER_SWITCHED_BACK, requiredRoles = { "ROLE_ADMIN" })
	public void userUnswitched(User principal, User delegatedUser) {
		sendNotification(Authorize.getUser(), "userUnswitched", null, USER_SWITCHED_BACK, principal.toString(), delegatedUser.toString());
	}

	/**
	 * @see org.iita.struts.ApplicationNotifications#applicationExceptionThrown(java.lang.Throwable)
	 */
	@Override
	@Notification(defaultFormat = EXCEPTION_THROWN, requiredRoles = { "ROLE_ADMIN" })
	public void applicationExceptionThrown(Throwable ex) {
		StackTraceElement frame = ex.getStackTrace()[0];
		String oneFrame=String.format("at %4$s.%3$s(%1$s:%2$d)", frame.getFileName(), frame.getLineNumber(), frame.getMethodName(), frame.getClassName()); 
//		StringWriter sw=new StringWriter();
//		ex.printStackTrace(new PrintWriter(sw));
//		sw.flush();
		sendNotification(Authorize.getPrincipal(), "applicationExceptionThrown", null, EXCEPTION_THROWN, ex.getMessage(), oneFrame);
	}

	/**
	 * @see org.iita.struts.ApplicationNotifications#emailSent(java.lang.String, java.lang.String[], java.lang.String[], java.lang.String)
	 */
	@Override
	@Notification(defaultFormat = EMAILS_SENT, requiredRoles = { "ROLE_ADMIN" })
	public void emailsSent(String sender, String[] recipients, String[] cc, String subject) {
		sendNotification(Authorize.getUser(), "emailsSent", null, EMAILS_SENT, sender, StringUtil.arrayToString(recipients), StringUtil.arrayToString(cc), subject);
	}
}