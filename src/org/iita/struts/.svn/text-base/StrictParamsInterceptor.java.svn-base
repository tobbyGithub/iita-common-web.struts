/**
 * 
 */
package org.iita.struts;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.ParametersInterceptor;

/**
 * @author mobreza
 */
@SuppressWarnings("serial")
public class StrictParamsInterceptor extends ParametersInterceptor {
	private static final Object STRICTPARAMS_ALLOWED = "__STRICTPARAMS_ALLOWED";
	private static final Object STRICTPARAMS_DENIED = "__STRICTPARAMS_DENIED";
	private static Log log = LogFactory.getLog(StrictParamsInterceptor.class);

	public StrictParamsInterceptor() {
		log.debug("Creating instance of StrictParamsInterceptor");
	}

	public void setAllowSimpleNames(boolean v) {
		// Does nothing
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean acceptableName(String name) {
		ActionInvocation actionInvocation = ActionContext.getContext().getActionInvocation();
		if (actionInvocation == null) {
			log.debug("ActionInvocation is null, allowing " + name);
			return true;
		}

		ActionContext invocationContext = actionInvocation.getInvocationContext();
		ArrayList<Pattern> allowedRegexps = null, deniedRegexps = null;

		allowedRegexps = (ArrayList<Pattern>) invocationContext.get(STRICTPARAMS_ALLOWED);
		deniedRegexps = (ArrayList<Pattern>) invocationContext.get(STRICTPARAMS_DENIED);

		if (deniedRegexps != null && deniedRegexps.size() > 0) {
			log.debug("Denied regexps: " + deniedRegexps.size());
			for (Pattern pattern : deniedRegexps) {
				log.debug("Matching parameter \"" + name + "\" against denied \"" + pattern.toString() + "\"");
				if (pattern.matcher(name).matches()) {
					log.debug("Parameter \"" + name + "\" matches denied \"" + pattern.toString() + "\"");
					return false;
				}
			}
		}

		if (allowedRegexps == null) {
			log.debug("Default allow parameter \"" + name + "\". does not match any patterns");
			return true;
		} else {
			log.debug("Allowed regexps: " + allowedRegexps.size());
			for (Pattern pattern : allowedRegexps) {
				log.debug("Matching parameter \"" + name + "\" against \"" + pattern.toString() + "\"");
				if (pattern.matcher(name).matches()) {
					log.debug("Parameter \"" + name + "\" matches \"" + pattern.toString() + "\"");
					return true;
				}
			}
			log.debug("Ignored parameter \"" + name + "\" does not match any patterns");
			return false;
		}

	}

	/**
	 * @see com.opensymphony.xwork2.interceptor.MethodFilterInterceptor#intercept(com.opensymphony.xwork2.ActionInvocation)
	 */
	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		getParameterRegexps(invocation);
		String result = super.intercept(invocation);
		log.debug("Removing Regexps from context");
		invocation.getInvocationContext().put(STRICTPARAMS_ALLOWED, null);
		invocation.getInvocationContext().put(STRICTPARAMS_DENIED, null);
		return result;
	}

	/**
	 * @param invocation
	 */
	@SuppressWarnings("unchecked")
	private void getParameterRegexps(ActionInvocation invocation) {
		ArrayList<Pattern> allowedRegexps = null, deniedRegexps = null;
		allowedRegexps = (ArrayList<Pattern>) invocation.getInvocationContext().get(STRICTPARAMS_ALLOWED);
		deniedRegexps = (ArrayList<Pattern>) invocation.getInvocationContext().get(STRICTPARAMS_DENIED);
		if (allowedRegexps != null) {
			log.debug("Found existing allowed regexps in invocationcontext");
			return;
		}
		if (deniedRegexps != null) {
			log.debug("Found existing denied regexps in invocationcontext");
			return;
		}

		String methodName = invocation.getProxy().getMethod();
		Class<? extends Object> actionobject = invocation.getAction().getClass();
		log.debug("Action class: " + (actionobject == null ? "NULL" : actionobject.getName()));
		Method method = null;
		try {
			method = actionobject.getMethod(methodName);
			log.debug("Method: " + (method == null ? "NULL" : method.getName()));
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
		}

		if (method != null) {
			AllowedParameters annotation = method.getAnnotation(AllowedParameters.class);
			log.debug("Annotation: " + (annotation == null ? "NULL" : "Got it"));

			if (annotation != null) {
				allowedRegexps = new ArrayList<Pattern>();
				for (String allowed : annotation.value()) {
					try {
						// force start and end!
						allowed = "^" + allowed + "$";
						log.debug("Compiling parameter name regexp: \"" + allowed + "\"");
						allowedRegexps.add(Pattern.compile(allowed));
					} catch (PatternSyntaxException e) {
						log.error("Could not compile expression: \"" + allowed + "\" of method " + actionobject.getName() + "." + method.getName());
					}
				}
			}

			DeniedParameters annotationDenied = method.getAnnotation(DeniedParameters.class);
			log.debug("Annotation: " + (annotation == null ? "NULL" : "Got it"));

			if (annotationDenied != null) {
				deniedRegexps = new ArrayList<Pattern>();
				for (String denied : annotationDenied.value()) {
					try {
						// force start and end!
						denied = "^" + denied + "$";
						log.debug("Compiling parameter name regexp: \"" + denied + "\"");
						deniedRegexps.add(Pattern.compile(denied));
					} catch (PatternSyntaxException e) {
						log.error("Could not compile expression: \"" + denied + "\" of method " + actionobject.getName() + "." + method.getName());
					}
				}
			}

			if ((allowedRegexps != null && allowedRegexps.size() > 0) || (deniedRegexps != null && deniedRegexps.size() > 0)) {
				log.debug("Putting lists to Context");
				if (allowedRegexps != null)
					invocation.getInvocationContext().put(STRICTPARAMS_ALLOWED, allowedRegexps);
				if (deniedRegexps != null)
					invocation.getInvocationContext().put(STRICTPARAMS_DENIED, deniedRegexps);
			}
		}
	}
}
