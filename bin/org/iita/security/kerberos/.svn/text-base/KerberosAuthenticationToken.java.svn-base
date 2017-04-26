/**
 * iita-common-web.struts Feb 12, 2010
 */
package org.iita.security.kerberos;

import org.springframework.security.providers.UsernamePasswordAuthenticationToken;

/**
 * @author mobreza
 *
 */
public class KerberosAuthenticationToken extends UsernamePasswordAuthenticationToken {

	private static final long serialVersionUID = 2894897335366820031L;

	/**
	 * @param principal
	 * @param credentials
	 */
	public KerberosAuthenticationToken(Object principal, Object credentials) {
		super(principal, credentials);
	}

	/**
	 * @param userName
	 */
	public KerberosAuthenticationToken(String principal) {
		super(principal, "kerberos-authenticated");
	}

}
