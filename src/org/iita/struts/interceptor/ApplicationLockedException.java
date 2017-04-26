/**
 * iita-common-web.struts Aug 5, 2009
 */
package org.iita.struts.interceptor;

/**
 * @author mobreza
 *
 */
public class ApplicationLockedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 213795823477713323L;

	/**
	 * 
	 */
	public ApplicationLockedException() {

	}

	/**
	 * @param message
	 */
	public ApplicationLockedException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ApplicationLockedException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ApplicationLockedException(String message, Throwable cause) {
		super(message, cause);
	}

}
