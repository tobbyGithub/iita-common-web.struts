/**
 * iita-common-web.struts Sep 14, 2009
 */
package org.iita.servlet.html.filter.etag;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author mobreza
 * 
 */
public class BufferingResponse implements HttpServletResponse {
	private final static Log LOG = LogFactory.getLog(BufferingResponse.class);
	private PrintWriter writer;
	private ByteArrayOutputStream cache;
	private Hashtable<String, String> headers = new Hashtable<String, String>();
	private int status;
	private CachedServletOutputStream outputStream;
	private HttpServletResponse response;
	private boolean cookieAdded = false;

	/**
	 * @param response
	 * @param cache
	 */
	public BufferingResponse(HttpServletResponse response, ByteArrayOutputStream cache) {
		this.response = response;
		this.cache = cache;
	}

	/**
	 * @see javax.servlet.http.HttpServletResponseWrapper#addHeader(java.lang.String, java.lang.String)
	 */
	@Override
	public void addHeader(String name, String value) {
		LOG.debug("addHeader " + name + " = " + value);
		this.headers.put(name, value);
		this.response.addHeader(name, value);
	}

	public boolean hasHeader(String name) {
		return this.headers.containsKey(name);
	}

	/**
	 * @see javax.servlet.http.HttpServletResponseWrapper#sendRedirect(java.lang.String)
	 */
	@Override
	public void sendRedirect(String location) throws IOException {
		LOG.debug("sendRedirect " + location);
		this.status = 302;
		this.response.sendRedirect(location);
	}

	/**
	 * @see javax.servlet.http.HttpServletResponseWrapper#sendError(int)
	 */
	@Override
	public void sendError(int sc) throws IOException {
		LOG.debug("sendError " + sc);
		this.status = sc;
		this.response.sendError(sc);
	}

	/**
	 * @see javax.servlet.http.HttpServletResponseWrapper#sendError(int, java.lang.String)
	 */
	@Override
	public void sendError(int sc, String msg) throws IOException {
		LOG.debug("sendError " + sc + " " + msg);
		this.status = sc;
		this.response.sendError(sc, msg);
	}

	/**
	 * @see javax.servlet.http.HttpServletResponseWrapper#addIntHeader(java.lang.String, int)
	 */
	@Override
	public void addIntHeader(String name, int value) {
		LOG.debug("addIntHeader " + name + " = " + value);
		this.response.addIntHeader(name, value);
	}

	/**
	 * @see javax.servlet.ServletResponseWrapper#flushBuffer()
	 */
	@Override
	public void flushBuffer() throws IOException {
		this.response.flushBuffer();
	}
	
	public void flush() throws IOException {
		if (this.outputStream != null)
			this.outputStream.flush();
		if (this.writer != null)
			this.writer.flush();
	}

	/**
	 * @see javax.servlet.ServletResponseWrapper#getOutputStream()
	 */
	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		LOG.debug("getOutputStream()");
		if (this.outputStream == null) {
			this.outputStream = new CachedServletOutputStream(this.cache);
		}
		return outputStream;
	}

	/**
	 * @see javax.servlet.ServletResponseWrapper#reset()
	 */
	@Override
	public void reset() {
		LOG.debug("reset");
		this.headers.clear();
		this.response.reset();
	}

	/**
	 * @see javax.servlet.ServletResponseWrapper#getWriter()
	 */
	@Override
	public PrintWriter getWriter() throws IOException {
		LOG.debug("getWriter");
		if (this.writer == null) {
			this.writer = new PrintWriter(this.cache);
		}
		return this.writer;
	}

	/**
	 * @param string
	 * @return
	 */
	public String getHeader(String name) {
		return this.headers.get(name);
	}

	public int getStatus() {
		return this.status;
	}

	/**
	 * @return the cookieAdded
	 */
	public boolean isCookieAdded() {
		if (this.cookieAdded) return true;
		if (this.containsHeader("Set-Cookie")) 
			return true;
		return false;
	}

	/**
	 * @see javax.servlet.http.HttpServletResponse#addCookie(javax.servlet.http.Cookie)
	 */
	@Override
	public void addCookie(Cookie cookie) {
		this.cookieAdded = true;
		LOG.debug("addCookie " + cookie.getName() + " = " + cookie.toString());
		this.response.addCookie(cookie);
	}

	/**
	 * @see javax.servlet.http.HttpServletResponse#addDateHeader(java.lang.String, long)
	 */
	@Override
	public void addDateHeader(String name, long date) {
		this.response.addDateHeader(name, date);		
	}

	/**
	 * @see javax.servlet.http.HttpServletResponse#containsHeader(java.lang.String)
	 */
	@Override
	public boolean containsHeader(String name) {
		return this.response.containsHeader(name);
	}

	/**
	 * @see javax.servlet.http.HttpServletResponse#encodeRedirectURL(java.lang.String)
	 */
	@Override
	public String encodeRedirectURL(String url) {
		return this.response.encodeRedirectURL(url);
	}

	/**
	 * @see javax.servlet.http.HttpServletResponse#encodeRedirectUrl(java.lang.String)
	 */
	@Override
	@Deprecated
	public String encodeRedirectUrl(String url) {
		return this.response.encodeRedirectUrl(url);
	}

	/**
	 * @see javax.servlet.http.HttpServletResponse#encodeURL(java.lang.String)
	 */
	@Override
	public String encodeURL(String url) {
		return this.response.encodeURL(url);
	}

	/**
	 * @see javax.servlet.http.HttpServletResponse#encodeUrl(java.lang.String)
	 */
	@Override
	@Deprecated
	public String encodeUrl(String url) {
		return this.response.encodeUrl(url);
	}

	/**
	 * @see javax.servlet.http.HttpServletResponse#setDateHeader(java.lang.String, long)
	 */
	@Override
	public void setDateHeader(String name, long date) {
		this.response.setDateHeader(name, date);		
	}

	/**
	 * @see javax.servlet.http.HttpServletResponse#setHeader(java.lang.String, java.lang.String)
	 */
	@Override
	public void setHeader(String name, String value) {
		this.response.setHeader(name, value);
	}

	/**
	 * @see javax.servlet.http.HttpServletResponse#setIntHeader(java.lang.String, int)
	 */
	@Override
	public void setIntHeader(String name, int value) {
		this.response.setIntHeader(name, value);
	}

	/**
	 * @see javax.servlet.http.HttpServletResponse#setStatus(int)
	 */
	@Override
	public void setStatus(int sc) {
		this.status=sc;
		this.response.setStatus(sc);
	}

	/**
	 * @see javax.servlet.http.HttpServletResponse#setStatus(int, java.lang.String)
	 */
	@Override
	@Deprecated
	public void setStatus(int sc, String msg) {
		this.status=sc;
		this.response.setStatus(sc, msg);
	}

	/**
	 * @see javax.servlet.ServletResponse#getBufferSize()
	 */
	@Override
	public int getBufferSize() {
		return this.response.getBufferSize();
	}

	/**
	 * @see javax.servlet.ServletResponse#getCharacterEncoding()
	 */
	@Override
	public String getCharacterEncoding() {
		return this.response.getCharacterEncoding();
	}

	/**
	 * @see javax.servlet.ServletResponse#getContentType()
	 */
	@Override
	public String getContentType() {
		return this.response.getContentType();
	}

	/**
	 * @see javax.servlet.ServletResponse#getLocale()
	 */
	@Override
	public Locale getLocale() {
		return this.response.getLocale();
	}

	/**
	 * @see javax.servlet.ServletResponse#isCommitted()
	 */
	@Override
	public boolean isCommitted() {
		return this.response.isCommitted();
	}

	/**
	 * @see javax.servlet.ServletResponse#resetBuffer()
	 */
	@Override
	public void resetBuffer() {
		this.response.resetBuffer();
	}

	/**
	 * @see javax.servlet.ServletResponse#setBufferSize(int)
	 */
	@Override
	public void setBufferSize(int size) {
		this.response.setBufferSize(size);
	}

	/**
	 * @see javax.servlet.ServletResponse#setCharacterEncoding(java.lang.String)
	 */
	@Override
	public void setCharacterEncoding(String encoding) {
		this.response.setCharacterEncoding(encoding);
	}

	/**
	 * @see javax.servlet.ServletResponse#setContentLength(int)
	 */
	@Override
	public void setContentLength(int length) {
		this.response.setContentLength(length);
	}

	/**
	 * @see javax.servlet.ServletResponse#setContentType(java.lang.String)
	 */
	@Override
	public void setContentType(String contentType) {
		this.response.setContentType(contentType);
	}

	/**
	 * @see javax.servlet.ServletResponse#setLocale(java.util.Locale)
	 */
	@Override
	public void setLocale(Locale locale) {
		this.response.setLocale(locale);
	}
}
