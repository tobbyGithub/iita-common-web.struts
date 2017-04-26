/**
 * promisCRM.Struts Oct 6, 2010
 */
package org.iita.struts.interceptor;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

/**
 * This interceptor allows you to put objects on value stack before action invocation. Setting the map of stackers, allows you to put helper classes on value
 * stack and access them in your JSP files. This reduces the amount of code in individual actions where these helper methods would need to be added.
 * 
 * @author mobreza
 */
public class ValueStackInterceptor extends AbstractInterceptor {
	private static final long serialVersionUID = 8857792056058694989L;
	private static final Log LOG = LogFactory.getLog(ValueStackInterceptor.class);
	private Map<String, Object> stackers = new HashMap<String, Object>();

	/**
	 * @param stackers the stackers to set
	 */
	public void setStackers(Map<String, Object> stackers) {
		this.stackers = stackers;
	}

	/**
	 * @see com.opensymphony.xwork2.interceptor.AbstractInterceptor#intercept(com.opensymphony.xwork2.ActionInvocation)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		if (stackers != null)
			for (String key : stackers.keySet()) {
				LOG.debug("Setting: #" + key + " to " + stackers.get(key));
				invocation.getStack().getContext().put(key, stackers.get(key));
			}

		String result = invocation.invoke();

		if (stackers != null)
			for (String key : stackers.keySet()) {
				LOG.debug("Removing: #" + key + " to " + stackers.get(key));
				invocation.getStack().getContext().remove(key);
			}

		return result;
	}

}
