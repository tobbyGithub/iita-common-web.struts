/**
 * iita-common-web.struts Jul 6, 2010
 */
package org.iita.servlet.html.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author mobreza
 */
public class KeepNotificationMessagesFilter implements Filter {
	private final static Log LOG = LogFactory.getLog(KeepNotificationMessagesFilter.class);
	private static final String SESSION_NOTIFICATIONMESSAGES = "SESSION_NOTIFICATIONMESSAGES";
	private static final String SESSION_NEXTNOTIFICATIONMESSAGES = "SESSION_NEXTNOTIFICATIONMESSAGES";
	public static final String FILTERAPPLIED = "org.iita.servlet.html.filter.KeepNotificationMessagesFilter.APPLIED_ONCE";

	/**
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
		LOG.info("Destroying NotificationMessages filter");
	}

	/**
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void doFilter(ServletRequest rq, ServletResponse rs, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) rq;
		HttpServletResponse response = (HttpServletResponse) rs;
		request.setAttribute(FILTERAPPLIED, Boolean.TRUE);

		HttpSession session = request.getSession();

		Collection<String> nextNotificationMessages = (Collection<String>) session.getAttribute(SESSION_NEXTNOTIFICATIONMESSAGES);
		Collection<String> notificationMessages = (Collection<String>) session.getAttribute(SESSION_NOTIFICATIONMESSAGES);

		if (nextNotificationMessages != null && nextNotificationMessages.size() > 0) {
			if (notificationMessages == null) {
				notificationMessages = new ArrayList<String>();
				session.setAttribute(SESSION_NOTIFICATIONMESSAGES, notificationMessages);
			}
			LOG.debug("Adding " + nextNotificationMessages.size() + " to current notifications.");
			notificationMessages.addAll(nextNotificationMessages);
		}

		LOG.trace("Executing next filter in chain on URL:" + request.getRequestURL());
		net.sf.ehcache.constructs.web.GenericResponseWrapper wrappedResponse = new net.sf.ehcache.constructs.web.GenericResponseWrapper(response, response
				.getOutputStream());

		// execute next filter
		chain.doFilter(rq, wrappedResponse);
		wrappedResponse.flush();

		int status = wrappedResponse.getStatus();

		LOG.debug("HTTP Response status: " + status);

		try {
			// check if there's stuff in our session
			notificationMessages = (Collection<String>) session.getAttribute(SESSION_NOTIFICATIONMESSAGES);
			// got something
			if (notificationMessages != null) {
				LOG.debug("Clearing notification messages response");
				notificationMessages.clear();
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		String gearsHeader = request.getHeader("gears-filename");

		if ((status >= 200 && status < 300) && gearsHeader == null) {
			LOG.debug("Okayed response: " + status);
			try {
				// check if there's stuff in our session
				nextNotificationMessages = (Collection<String>) session.getAttribute(SESSION_NEXTNOTIFICATIONMESSAGES);
				// got something
				if (nextNotificationMessages != null) {
					LOG.debug("Clearing NEXT notification messages due to OKAY response");
					nextNotificationMessages.clear();
				}
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	/**
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		LOG.info("Initializing Notification messages filter");
	}

}
