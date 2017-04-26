package org.iita.struts;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.iita.entity.Notification;
import org.iita.security.model.User;
import org.iita.security.service.UserService;
import org.iita.service.UserNotificationReader;
import org.iita.util.PagedResult;

import com.opensymphony.xwork2.Action;

public class NotificationAction extends BaseAction {
	private static final Log log = LogFactory.getLog(NotificationAction.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = -4462924003640701300L;

	private static final int PAGE_SIZE = 20;

	private Long id;

	private Notification notification;

	private UserNotificationReader notificationService;

	@SuppressWarnings("unused")
	private UserService userService;

	private PagedResult<Notification> paged = null;

	private int startAt;
	private long unreadCount;

	/**
	 * @param startAt the startAt to set
	 */
	public void setStartAt(int startAt) {
		this.startAt = startAt;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public Notification getNotification() {
		return notification;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setNotificationService(UserNotificationReader notificationService) {
		this.notificationService = notificationService;
	}

	public PagedResult<Notification> getPaged() {
		return paged;
	}

	@Override
	public String execute() {
		log.debug("execute");

		User user = getUser();
		if (getUser() == null) {
			addActionError("You need to be logged in in order view notifications.");
			return Action.ERROR;
		}

		paged = notificationService.listMessages(user, startAt, PAGE_SIZE, false);

		return SUCCESS;
	}

	/**
	 * Quick action to display number of unread messages only
	 * 
	 * @return
	 */
	public String unread() {
		log.debug("Unread");
		if (getUser() != null) {
			this.unreadCount = notificationService.countUnread(getUser());
		}

		return SUCCESS;
	}

	/**
	 * @return the unreadCount
	 */
	public long getUnreadCount() {
		return this.unreadCount;
	}

	public String read() {
		log.debug("read");
		notification = notificationService.findNotification(id);
		notificationService.markAsRead(notification);

		return "detail";
	}

	public String toggle() {
		log.debug("toggle");
		notification = notificationService.findNotification(id);

		if (notification.isRead()) {
			notificationService.markAsUnread(notification);
		} else if (!notification.isRead()) {
			notificationService.markAsRead(notification);
		}

		return "refresh";
	}

	public String del() {
		log.debug("del");
		notificationService.deleteMessage(id);

		return "refresh";
	}

	public String delAll() {
		log.debug("delAll");
		notificationService.deleteAllMessages(getUser());

		return "refresh";
	}
}
