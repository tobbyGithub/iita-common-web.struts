/**
 * iita-common-web.struts Sep 14, 2009
 */
package org.iita.servlet.html.filter.etag;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;

/**
 * @author mobreza
 * 
 */
public class ETagFilter implements Filter {
	private final static Log LOG = LogFactory.getLog(ETagFilter.class);
	private static final String FILTERAPPLIED = "org.iita.servlet.html.filter.etag.APPLIED_ONCE";

	/**
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
		LOG.info("Destroying filter ETagFilter");
	}

	/**
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest rq, ServletResponse rs, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) rq;
		HttpServletResponse response = (HttpServletResponse) rs;
		// ServletContext servletContext = filterConfig.getServletContext();

		if (isFilterRegistered(request)) {
			chain.doFilter(rq, rs);
			return;
		}

		LOG.debug("" + request.getRequestURI());
		// get auth
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		// need to call next in chain
		ByteArrayOutputStream cache = new ByteArrayOutputStream();
		BufferingResponse bufferingResponse = new BufferingResponse(response, cache);
		chain.doFilter(request, bufferingResponse);

		bufferingResponse.flush();
		byte[] byteArray = cache.toByteArray();

		// try to fix ISA
		if (authentication == null)
			// check if we're authenticated now, as we weren't before
			authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null && authentication.isAuthenticated()) {
			LOG.info("User " + authentication.getName() + " is/was authenticated, set response cache headers to private.");
			// user is authenticated, all is private
			response.addHeader("Cache-Control", "private");
			response.addHeader("Cache-Control", "no-cache");
			response.addHeader("Expires", "-1");
		} else {
			if (request.getMethod().equalsIgnoreCase("get")) {
				if (bufferingResponse.isCookieAdded()) {
					LOG.info("GET method, but cookie added. Cache-Control to: private");
					response.addHeader("Cache-Control", "private");
					response.addHeader("Cache-Control", "no-cache");
					response.addHeader("Expires", "-1");
				}
			} else {
				response.addHeader("Cache-Control", "private");
				response.addHeader("Cache-Control", "no-cache");
				response.addHeader("Expires", "-1");
			}
		}

		String eTag = bufferingResponse.getHeader("ETag");
		if (eTag == null)
			eTag = Integer.toHexString(Arrays.hashCode(byteArray));

		String noneMatch = request.getHeader("If-None-Match");

		String pragmaNoCache = request.getHeader("Pragma");
		boolean noCache = (pragmaNoCache != null && pragmaNoCache.equalsIgnoreCase("no-cache"));
		pragmaNoCache = request.getHeader("Cache-Control");
		noCache = noCache || (pragmaNoCache != null && pragmaNoCache.equalsIgnoreCase("no-cache"));

		if (!noCache && eTag != null && eTag.equals(noneMatch)) {
			LOG.debug("Not modified " + request.getRequestURI());
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			response.setContentLength(0);
		} else {
			LOG.debug("Sending response");
			if (bufferingResponse.getHeader("ETag") == null) {
				// ony add ETag header if cookies are not added
				if (!bufferingResponse.isCookieAdded()) {
					response.addHeader("ETag", eTag);
				} else {
					LOG.info("Not adding ETag, cookie added " + bufferingResponse.isCookieAdded());
				}
			}
			response.getOutputStream().write(byteArray);
		}
	}

	/**
	 * @param request
	 * @return
	 */
	private boolean isFilterRegistered(ServletRequest request) {
		if (request.getAttribute(FILTERAPPLIED) == Boolean.TRUE) {
			return true;
		} else {
			request.setAttribute(FILTERAPPLIED, Boolean.TRUE);
			return false;
		}
	}

	/**
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// this.filterConfig = filterConfig;
	}

}
