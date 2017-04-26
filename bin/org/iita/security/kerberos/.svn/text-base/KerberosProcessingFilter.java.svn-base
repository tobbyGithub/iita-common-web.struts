/**
 * iita-common-web.struts Feb 12, 2010
 */
package org.iita.security.kerberos;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.security.sasl.RealmCallback;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.alfresco.jlan.server.auth.kerberos.KerberosDetails;
import org.alfresco.jlan.server.auth.kerberos.SessionSetupPrivilegedAction;
import org.alfresco.jlan.server.auth.ntlm.NTLM;
import org.alfresco.jlan.server.auth.spnego.NegTokenInit;
import org.alfresco.jlan.server.auth.spnego.NegTokenTarg;
import org.alfresco.jlan.server.auth.spnego.OID;
import org.alfresco.jlan.server.auth.spnego.SPNEGO;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ietf.jgss.Oid;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationCredentialsNotFoundException;
import org.springframework.security.AuthenticationException;
import org.springframework.security.AuthenticationManager;
import org.springframework.security.InsufficientAuthenticationException;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.anonymous.AnonymousAuthenticationToken;
import org.springframework.security.ui.FilterChainOrder;
import org.springframework.security.ui.SpringSecurityFilter;
import org.springframework.security.ui.webapp.AuthenticationProcessingFilter;
import org.springframework.util.Assert;

/**
 * Struts2 KerberosProcessingFilter
 * 
 * @author Alfresco, Inc.
 * @author mobreza
 */
public class KerberosProcessingFilter extends SpringSecurityFilter implements InitializingBean, CallbackHandler {
	private static final Log LOG = LogFactory.getLog(KerberosProcessingFilter.class);
	private static final String STATE_ATTR = "SpringSecurityKerberos";
	private static final Integer BEGIN = new Integer(0);
	private static final Integer COMPLETE = new Integer(2);
	private static final Integer DELAYED = new Integer(3);
	public static final Integer NOT_SUPPORTED = new Integer(4);
	/** Should the filter initiate Kerberos negotiations, default <code>true</code> */
	private boolean forceIdentification = true;
	private AuthenticationManager authenticationManager;
	private String m_accountName = "Principal";
	private String m_password = "Password";
	private LoginContext m_loginContext;
	private String m_loginEntryName = "KerberosAuth";
	private String m_krbRealm;
	private boolean enabled = true;
	private boolean retryOnAuthFailure = false;
	private List<String> excludedIPlist = new ArrayList<String>();

	/**
	 * @param authenticationManager the authenticationManager to set
	 */
	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	/**
	 * Set principal name
	 * 
	 * @param principal
	 */
	public void setPrincipal(String principal) {
		this.m_accountName = principal;
	}

	/**
	 * Set password to use if authenticating service with password
	 * 
	 * @param password
	 */
	public void setPassword(String password) {
		this.m_password = password;
	}

	/**
	 * Set realm or Domain name
	 * 
	 * @param realm
	 */
	public void setRealm(String realm) {
		this.m_krbRealm = realm;
	}

	/**
	 * Set name of JAAS configuration section
	 * 
	 * @param section
	 */
	public void setJaasConfigSection(String section) {
		this.m_loginEntryName = section;
	}

	/**
	 * @param excludedIPlist the excludedIPlist to set
	 */
	public void setExcludedIP(String excludedIP) {
		LOG.debug("Excluded IP: " + excludedIP);
		if (excludedIP == null)
			return;
		String[] excludedIPs = excludedIP.trim().split("[^\\d\\.]+");
		for (String xIp : excludedIPs) {
			xIp = xIp.trim();
			if (xIp.length() > 0) {
				LOG.info("Adding IP to Kerberos excluded list: " + xIp);
				this.excludedIPlist.add(xIp.trim());
			}
		}
	}

	public KerberosProcessingFilter() {
	}

	/**
	 * All Kerberos related stuff happens in this method.
	 * 
	 * @see org.springframework.security.ui.SpringSecurityFilter#doFilterHttp(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse,
	 *      javax.servlet.FilterChain)
	 */
	@Override
	protected void doFilterHttp(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws IOException,
			ServletException {

		if (!this.enabled || isExcludedIP(request.getRemoteAddr())) {
			chain.doFilter(request, response);
			return;
		}

		if (request.getRequestURI().substring(request.getContextPath().length()).startsWith("/j_")) {
			LOG.info("Ignoring " + request.getRequestURI().substring(request.getContextPath().length()));
			chain.doFilter(request, response);
			return;
		}

		final HttpSession session = request.getSession();
		Integer kerberosState = (Integer) session.getAttribute(STATE_ATTR);

		final String authMessage = request.getHeader("Authorization");
		LOG.debug("Request: " + request.getRequestURI() + " AUTH: " + authMessage);

		// Start Kerberos negotiations the first time through the filter
		if (kerberosState == null) {
			if (forceIdentification) {
				logger.info("Starting Kerberos handshake");
				session.setAttribute(STATE_ATTR, BEGIN);
				restartKerberosAuthentication(request, response, session);
				return;
			} else {
				logger.info("Kerberos handshake not yet started");
				session.setAttribute(STATE_ATTR, DELAYED);
			}
		} else {
			LOG.debug("Kerberos state: " + kerberosState);
		}

		// IE will send a Type 1 message to reauthenticate the user during an HTTP POST
		if (kerberosState == COMPLETE && this.reAuthOnIEPost(request))
			kerberosState = BEGIN;

		if (kerberosState != COMPLETE && authMessage != null && authMessage.startsWith("Negotiate ")) {
			if (kerberosState == BEGIN && authMessage.startsWith("NTLM")) {
				LOG.info("Received NTLM logon from client, restaring Kerberos");
				restartKerberosAuthentication(request, response, session);
				return;
			} else if (kerberosState == BEGIN) {
				final byte[] spnegoByts = Base64.decodeBase64(authMessage.substring(10).getBytes());
				logger.debug("Received SPNEGO response");

				LOG.info("Checking for NTLMSSP");
				if (isNTLMSSPBlob(spnegoByts, 0)) {
					LOG.info("Client sent an NTLMSSP security blob, re-requesting");
					restartKerberosAuthentication(request, response, session);
					return;
				}

				LOG.debug("Checking SPNEGO token type");
				int tokType = -1;

				try {
					tokType = SPNEGO.checkTokenType(spnegoByts, 0, spnegoByts.length);
					LOG.debug("Token checked.");
				} catch (IOException ex) {
					LOG.warn(ex.getMessage());
				}

				// Check for a NegTokenInit blob
				if (tokType == SPNEGO.NegTokenInit) {
					LOG.debug("Got SPNEGO NegTokenInit");

					// Parse the SPNEGO security blob to get the Kerberos ticket
					NegTokenInit negToken = new NegTokenInit();

					try {
						// Decode the security blob
						negToken.decode(spnegoByts, 0, spnegoByts.length);
						// Determine the authentication mechanism the client is using and logon
						String oidStr = null;
						if (negToken.numberOfOids() > 0)
							oidStr = negToken.getOidAt(0).toString();

						if (oidStr != null && (oidStr.equals(OID.ID_MSKERBEROS5) || oidStr.equals(OID.ID_KERBEROS5))) {
							// Kerberos logon
							LOG.debug("Starting Kerberos Login");

							KerberosAuthenticationToken token = doKerberosLogon(negToken, request, response, session);
							if (token != null) {
								LOG.info("Got user token: " + token.getName());
								session.setAttribute(STATE_ATTR, COMPLETE);

								// Do not reauthenticate the user in Spring Security during an IE POST
								final Authentication myCurrentAuth = SecurityContextHolder.getContext().getAuthentication();
								if (myCurrentAuth == null || myCurrentAuth instanceof AnonymousAuthenticationToken) {
									logger.info("Authenticating user credentials");
									try {
										this.authenticate(request, response, session, token);
									} catch (AuthenticationException authException) {
										LOG.warn("Authentication failed: " + authException.getMessage());
									} catch (Throwable exception) {
										LOG.warn("Other exception: " + exception.getMessage());
									}
								}

							} else {
								LOG.warn("Kerberos login failed, chaining filter!");
								session.setAttribute(STATE_ATTR, NOT_SUPPORTED);
							}
						}
					} catch (IOException ex) {
						LOG.debug(ex);
					}
				} else {
					// Unknown SPNEGO token type
					LOG.warn("Unknown SPNEGO token type: " + tokType);
				}
			}
		}

		chain.doFilter(request, response);
	}

	/**
	 * @param remoteAddr
	 * @return
	 */
	private boolean isExcludedIP(String remoteAddr) {
		LOG.debug("Checking if " + remoteAddr + " is on exclude list");
		if (this.excludedIPlist.contains(remoteAddr))  {
			LOG.info("IP " + remoteAddr + " is on exclude list. Not doing Kerberos.");
			return true;
		} else {
			LOG.debug("Not on excluded list.");
		}
		return false;
	}

	/**
	 * Authenticates the user credentials acquired from NTLM against the Spring Security <code>AuthenticationManager</code>.
	 * 
	 * @param request the <code>HttpServletRequest</code> object.
	 * @param response the <code>HttpServletResponse</code> object.
	 * @param session the <code>HttpSession</code> object.
	 * @param auth the <code>NtlmPasswordAuthentication</code> object.
	 * @throws IOException
	 */
	private void authenticate(final HttpServletRequest request, final HttpServletResponse response, final HttpSession session,
			final KerberosAuthenticationToken authRequest) throws IOException {
		final Authentication authResult;
		final Authentication backupAuth;

		// Place the last username attempted into HttpSession for views
		session.setAttribute(AuthenticationProcessingFilter.SPRING_SECURITY_LAST_USERNAME_KEY, authRequest.getName());

		// Backup the current authentication in case of an AuthenticationException
		backupAuth = SecurityContextHolder.getContext().getAuthentication();

		try {
			// Authenitcate the user with the authentication manager
			authResult = authenticationManager.authenticate(authRequest);
		} catch (AuthenticationException failed) {
			if (logger.isInfoEnabled()) {
				logger.info("Authentication request for user: " + authRequest.getName() + " failed: " + failed.toString());
			}

			// Reset the backup Authentication object and rethrow the AuthenticationException
			SecurityContextHolder.getContext().setAuthentication(backupAuth);

			if (retryOnAuthFailure && (failed instanceof AuthenticationCredentialsNotFoundException || failed instanceof InsufficientAuthenticationException)) {
				logger.debug("Restart NTLM authentication handshake due to AuthenticationException");
				session.removeAttribute(STATE_ATTR);
			}

			throw failed;
		}

		// Set the Authentication object with the valid authentication result
		SecurityContextHolder.getContext().setAuthentication(authResult);
	}

	/**
	 * Returns <code>true</code> if reauthentication is needed on an IE POST.
	 */
	private boolean reAuthOnIEPost(final HttpServletRequest request) {
		String ua = request.getHeader("User-Agent");
		return (request.getMethod().equalsIgnoreCase("POST") && ua != null && ua.indexOf("MSIE") != -1);
	}

	// @SuppressWarnings("unchecked")
	// private void printHeaders(final HttpServletRequest request) {
	// Enumeration<String> headerNames = request.getHeaderNames();
	// while (headerNames.hasMoreElements()) {
	// String headerName = (String) headerNames.nextElement();
	// LOG.warn(">> " + headerName + ": " + request.getHeader(headerName));
	// }
	// }

	private void restartKerberosAuthentication(final HttpServletRequest req, final HttpServletResponse resp, final HttpSession session) throws IOException {
		// Force the logon to start again
		LOG.debug("Restarting Login challenge");
		resp.setHeader("WWW-Authenticate", "Negotiate");
		resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

		// Need to render some sort of information if client does not respond to WWW-Authenticate request
		// (when disabled on client, etc)
		ServletOutputStream os = resp.getOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(os);
		osw.write("<html><head><title>Authentication required</title><meta http-equiv=\"refresh\" content=\"1\" /></head>"
				+ "<body>Checking if Kerberos authentication works. If not, go to <a href='.'>login form</a>.</body></html>");
		osw.flush();
	}

	/**
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(this.authenticationManager, "An AuthenticationManager is required");
		Assert.notNull(this.m_accountName, "Principal required");
		this.init();
	}

	/**
	 * Check if a security blob starts with the NTLMSSP signature
	 * 
	 * @param byts byte[]
	 * @param offset int
	 * @return boolean
	 */
	protected final boolean isNTLMSSPBlob(byte[] byts, int offset) {
		// Check if the blob has the NTLMSSP signature
		boolean isNTLMSSP = false;

		if ((byts.length - offset) >= NTLM.Signature.length) {
			// Check for the NTLMSSP signature
			int idx = 0;
			while (idx < NTLM.Signature.length && byts[offset + idx] == NTLM.Signature[idx])
				idx++;

			if (idx == NTLM.Signature.length)
				isNTLMSSP = true;
		}

		return isNTLMSSP;
	}

	/**
	 * Perform a Kerberos login and return an SPNEGO response
	 * 
	 * @param negToken NegTokenInit
	 * @param req HttpServletRequest
	 * @param resp HttpServletResponse
	 * @param httpSess HttpSession
	 * @return NegTokenTarg
	 */
	@SuppressWarnings("unchecked")
	private final KerberosAuthenticationToken doKerberosLogon(NegTokenInit negToken, HttpServletRequest req, HttpServletResponse resp, HttpSession httpSess) {
		// Authenticate the user

		KerberosDetails krbDetails = null;
		NegTokenTarg negTokenTarg = null;
		LOG.debug("Doing Kerberos Login");

		try {
			// Run the session setup as a privileged action
			SessionSetupPrivilegedAction sessSetupAction = new SessionSetupPrivilegedAction(m_accountName, negToken.getMechtoken());
			Object result = Subject.doAs(m_loginContext.getSubject(), sessSetupAction);

			if (result != null) {
				// Access the Kerberos response
				krbDetails = (KerberosDetails) result;

				// Create the NegTokenTarg response blob
				negTokenTarg = new NegTokenTarg(SPNEGO.AcceptCompleted, OID.KERBEROS5, krbDetails.getResponseToken());

				// Check if the user has been authenticated, if so then setup the user environment
				if (negTokenTarg != null) {
					// Create and store the user authentication context

					// User user = createUserEnvironment(httpSess, krbDetails.getUserName());
					KerberosAuthenticationToken userToken = new KerberosAuthenticationToken(krbDetails.getUserName());
					LOG.info("Got username from Kerberos: " + krbDetails.getUserName());

					// Debug
					return userToken;
				}
			} else {
				LOG.info("No SPNEGO response, Kerberos logon failed");
			}
		} catch (Exception ex) {
			LOG.warn("Kerberos logon error", ex);
		}

		return null;
	}

	public void init() throws Exception {
		// Get the local host name
		String localName = null;

		try {
			localName = InetAddress.getLocalHost().getCanonicalHostName().toLowerCase();
			LOG.debug("Got localName: " + localName);
		} catch (UnknownHostException ex) {
			throw new ServletException("Failed to get local host name");
		}

		// Create a login context for the HTTP server service

		try {
			// Login the HTTP server service
			LOG.debug("Creating login context: " + m_loginEntryName);
			m_loginContext = new LoginContext(m_loginEntryName, this);
			m_loginContext.login();

			// DEBUG
			LOG.debug("HTTP Kerberos login successful");
		} catch (SecurityException e) {
			LOG.warn("Error establishing JAAS context, Kerberos disabled. " + e.getMessage());
			this.enabled = false;
			return;
		} catch (LoginException e) {
			LOG.warn("HTTP Kerberos web filter error, Kerberos disabled. " + e.getMessage());
			this.enabled = false;
			return;
		}

		// Get the HTTP service account name from the subject

		Subject subj = m_loginContext.getSubject();
		Principal princ = subj.getPrincipals().iterator().next();

		m_accountName = princ.getName();

		// DEBUG
		LOG.debug("Logged on using principal " + m_accountName);

		// Create the Oid list for the SPNEGO NegTokenInit, include NTLMSSP for fallback

		Vector<Oid> mechTypes = new Vector<Oid>();

		mechTypes.add(OID.KERBEROS5);
		mechTypes.add(OID.MSKERBEROS5);

		// Build the SPNEGO NegTokenInit blob

		try {
			// Build the mechListMIC principle
			//
			// Note: This field is not as specified

			String mecListMIC = null;

			StringBuilder mic = new StringBuilder();
			mic.append(localName);
			mic.append("$@");
			mic.append(m_krbRealm);

			mecListMIC = mic.toString();

			// Build the SPNEGO NegTokenInit that contains the authentication types that the HTTP server accepts

			NegTokenInit negTokenInit = new NegTokenInit(mechTypes, mecListMIC);

			// Encode the NegTokenInit blob
			negTokenInit.encode();
		} catch (IOException ex) {
			// Debug
			LOG.error("Error creating SPNEGO NegTokenInit blob", ex);
			this.enabled = false;
		}
	}

	/**
	 * @see javax.security.auth.callback.CallbackHandler#handle(javax.security.auth.callback.Callback[])
	 */
	@Override
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
		// Process the callback list

		for (int i = 0; i < callbacks.length; i++) {
			// Request for user name

			if (callbacks[i] instanceof NameCallback) {
				NameCallback cb = (NameCallback) callbacks[i];
				LOG.debug("Sending name: " + m_accountName);
				cb.setName(m_accountName);
			}

			// Request for password
			else if (callbacks[i] instanceof PasswordCallback) {
				PasswordCallback cb = (PasswordCallback) callbacks[i];
				LOG.debug("Sending password: " + m_password);
				cb.setPassword(m_password.toCharArray());
			}

			// Request for realm
			else if (callbacks[i] instanceof RealmCallback) {
				RealmCallback cb = (RealmCallback) callbacks[i];
				LOG.debug("Sending realm: " + m_krbRealm);
				cb.setText(m_krbRealm);
			} else {
				throw new UnsupportedCallbackException(callbacks[i]);
			}
		}
	}

	/**
	 * @see org.springframework.core.Ordered#getOrder()
	 */
	@Override
	public int getOrder() {
		return FilterChainOrder.PRE_AUTH_FILTER;
	}
}
