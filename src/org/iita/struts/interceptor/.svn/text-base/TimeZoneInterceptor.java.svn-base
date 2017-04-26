/**
 * iita-common-web.struts Feb , 2010
 */
package org.iita.struts.interceptor;

import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

/**
 * @author mobreza
 * 
 */
public class TimeZoneInterceptor extends AbstractInterceptor {
	private static final long serialVersionUID = -4360632829312120584L;
	private static final Log LOG = LogFactory.getLog(TimeZoneInterceptor.class);
	public static final String DEFAULT_SESSION_ATTRIBUTE = "WW_TRANS_I18N_TIMEZONE";
	public static final String DEFAULT_PARAMETER = "request_timezone";
	public static final String DEFAULT_REQUESTONLY_PARAMETER = "request_only_timezone";

	protected String parameterName = DEFAULT_PARAMETER;
	protected String requestOnlyParameterName = DEFAULT_REQUESTONLY_PARAMETER;
	protected String attributeName = DEFAULT_SESSION_ATTRIBUTE;

	/**
	 * @see com.opensymphony.xwork2.interceptor.AbstractInterceptor#intercept(com.opensymphony.xwork2.ActionInvocation)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		LOG.debug("Intercepting for TimeZone");
		Map<String, Object> params = invocation.getInvocationContext().getParameters();

		boolean storeInSession = true;
		TimeZone requested_timezone = findTimeZoneParameter(params, parameterName);
		if (requested_timezone == null) {
			requested_timezone = findTimeZoneParameter(params, requestOnlyParameterName);
			if (requested_timezone != null) {
				storeInSession = false;
			}
		}

		// save it in session
		Map<String, Object> session = invocation.getInvocationContext().getSession();
		TimeZone timezone = null;
		if (requested_timezone != null) {
			timezone = requested_timezone;
			if (timezone != null && LOG.isDebugEnabled()) {
				LOG.debug("applied request timezone =" + timezone);
			}
		}
		if (session != null) {
			synchronized (session) {
				if (timezone == null) {
					// check session for saved timezone
					Object sessionTimeZone = session.get(attributeName);
					if (sessionTimeZone != null && sessionTimeZone instanceof TimeZone) {
						timezone = (TimeZone) sessionTimeZone;
						if (LOG.isDebugEnabled()) {
							LOG.debug("applied session timezone=" + timezone);
						}
					} else {
						// no overriding timezone definition found, stay with current invokation (=system) timezone
						timezone = TimeZone.getDefault();
						if (timezone != null && LOG.isDebugEnabled()) {
							LOG.debug("applied invocation context timezone=" + timezone);
						}
					}
				}
				if (storeInSession) {
					session.put(attributeName, timezone);
				}
			}
		}
		saveTimeZone(invocation, timezone);

		final String result = invocation.invoke();

		return result;
	}

	private TimeZone findTimeZoneParameter(Map<String, Object> params, String parameterName) {
		Object requested_timezone = params.remove(parameterName);
		if (requested_timezone != null && requested_timezone.getClass().isArray() && ((Object[]) requested_timezone).length == 1) {
			requested_timezone = ((Object[]) requested_timezone)[0];

			if (LOG.isDebugEnabled()) {
				LOG.debug("requested_timezone=" + requested_timezone);
			}
			return TimeZone.getTimeZone((String) requested_timezone);
		}
		return null;
	}

	/**
	 * Save the given timezone to the ActionInvocation.
	 * 
	 * @param invocation The ActionInvocation.
	 * @param timezone The timezone to save.
	 */
	protected void saveTimeZone(ActionInvocation invocation, TimeZone timezone) {
		invocation.getInvocationContext().put(attributeName, timezone);
	}
}
