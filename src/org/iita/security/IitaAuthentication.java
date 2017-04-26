/**
 * 
 */
package org.iita.security;

import java.util.Date;

import javax.naming.CommunicationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.iita.security.kerberos.KerberosAuthenticationToken;
import org.iita.security.model.User;
import org.iita.security.model.UserStatus;
import org.iita.security.service.AuthenticationService;
import org.iita.security.service.UserService;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.BadCredentialsException;
import org.springframework.security.providers.AuthenticationProvider;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.userdetails.UsernameNotFoundException;

/**
 * IITA Authentication module authenticates users against ActiveDirectory or other LDAP. After a successful "bind" to the directory, user's data is loaded from
 * table User. If there's no record of that user in the database, but user authenticated successfully, a record is created for that user.
 * 
 * @author mobreza
 * 
 */
public class IitaAuthentication implements AuthenticationProvider {
	private static final Log log = LogFactory.getLog(IitaAuthentication.class);
	private UserService userService;
	/** Allow automatic import from LDAP */
	private boolean automaticImportAllowed = true;
	/** Authentication service */
	private AuthenticationService ldapAuthenticationService = null;

	/**
	 * This setter has been deprecated, application identifier is no longer required.
	 * 
	 * @param applicationIdentifier the applicationIdentifier to set
	 */
	@Deprecated
	public void setApplication(String application) {
	}

	public IitaAuthentication(UserService userService) {
		this.userService = userService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.providers.AuthenticationProvider#authenticate (org.springframework.security.Authentication)
	 */
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		Object principal = authentication.getPrincipal();
		Object credentials = authentication.getCredentials();
		if (principal instanceof String && credentials instanceof String) {
			String username = (String) principal;
			String sAMAccountName = username;
			String domainName = null;
			if (username != null && username.split("\\\\").length == 2) {
				String[] loginSplit = username.split("\\\\");
				domainName = loginSplit[0];
				sAMAccountName = loginSplit[1];
				log.info("Authenticating domain: " + domainName + " Username: " + sAMAccountName);
			}
			String password = (String) credentials;

			log.info("Authenticating username '" + username + "' with password length " + password.length());
			if (username.trim().length() == 0 || password == null || password.length() == 0) {
				log.info("Not allowing blank username");
				throw new BadCredentialsException("Provide username and password!");
			}

			if (username.contains("@")) {
				throw new BadCredentialsException("Please do not use your email address as username!");
			}

			// load user by username
			User userdetails = userService.loadUserByUsername(sAMAccountName);

			if (userdetails == null && isAutomaticImportAllowed()) {
				// we don't have that user in the system just yet
				// let's try authenticating the user against LDAP
				log.info("User " + sAMAccountName + " not registered with applicationIdentifier, trying to import with import service.");
				synchronized (this) {
					userdetails = userService.importUser(sAMAccountName);
				}
			} else if (!isAutomaticImportAllowed()) {
				log.warn("User " + sAMAccountName + " not registered with application, not trying LDAP because 'automaticImportAllowed=false'");
			}

			if (userdetails == null) {
				throw new UsernameNotFoundException("User not registered with application. Missing user: " + username);
			}

			if (userdetails != null) {
				// user registered with local applicationIdentifier, authenticate
				log.info("User " + sAMAccountName + " registered with applicationIdentifier, authenticating.");

				if (userdetails.getStatus() != UserStatus.ENABLED) {
					log.error("The account has been disabled. Status is: " + userdetails.getStatus() + ", needs to be " + UserStatus.ENABLED);
					throw new BadCredentialsException("The account has been disabled. Username: " + username);
				}

				if (authentication instanceof KerberosAuthenticationToken) {
					doKerberosLogin(authentication, userdetails);
				} else {
					switch (userdetails.getAuthenticationType()) {
					case PASSWORD:
						log.info("User " + sAMAccountName + " set to authenticate with PASSWORD.");
						if (!userService.isPasswordValid(userdetails, password)) {
							log.warn("Invalid password for user " + sAMAccountName);
							userdetails.setLastLoginFailed(new Date());
							userService.updateLoginData(userdetails);
							userdetails = null;
							throw new BadCredentialsException("Login failed for user: " + username);
						} else {
							log.info("User " + username + " authenticated with PASSWORD method.");
						}
						break;
					case LDAP:
					default:
						log.info("User " + sAMAccountName + " set to authenticate with LDAP.");
						try {
							try {
								log.debug("Authenticating " + username + " with password len=" + password.length());
								if (ldapAuthenticationService==null) {
									log.error("LDAP Auth service not configured!");
									throw new BadCredentialsException("LDAP Auth service not configured.");
								}
								if (ldapAuthenticationService.authenticate(username, password, userdetails)) {
									log.info("User " + username + " authenticated with LDAP method.");
									// cache credentials
									log.info("Caching credentials for " + username);
									// set password, but don't change authentication type!
									this.userService.setPassword(userdetails, password, false);
								} else
									throw new BadCredentialsException("LDAP authentication failed for user " + username);
							} catch (CommunicationException e) {
								log.info("Communication exception thrown. Trying cached credentials.");
								if (userService.isPasswordValid(userdetails, password)) {
									log.info("Cached credentials valid");
								} else
									throw new BadCredentialsException("Cached credentials invalid for user: " + username);
							} catch (Exception e) {
								log.error(e.getMessage(), e);
								throw new BadCredentialsException("LDAP authentication failed for user: " + username, e);
							}
						} catch (BadCredentialsException e) {
							userdetails.setLastLoginFailed(new Date());
							userService.updateLoginData(userdetails);
							userdetails = null;
							throw e;
						}
						break;
					}
				}
			}

			if (userdetails != null) {
				if (log.isInfoEnabled())
					log.info("User " + username + " fully authenticated.");
				userdetails.setLastLogin(new Date());
				userService.updateLoginData(userdetails);

				// force load roles
				userdetails.setRoles(userService.getUserRoles(userdetails));
				userdetails.getAuthorities();

				if (log.isInfoEnabled())
					log.info("User " + username + " successfully logged in.");
				Authentication usernamePasswordAuthToken = new UsernamePasswordAuthenticationToken(userdetails, userdetails, userdetails.getAuthorities());
				return usernamePasswordAuthToken;
			} else {
				log.error("Login failed for username: '" + username + "'.");
				throw new org.springframework.security.AuthenticationServiceException("Login failed for username: " + username);
			}
		}
		return authentication;

	}

	/**
 	 * Kerberos authentication already happens by this point, no need to check passwords.
	 *
	 * @param authentication
	 * @param userdetails
	 */
	private void doKerberosLogin(Authentication authentication, User userdetails) {
		log.debug("Kerberos authentication accepted between " + authentication + " and " + userdetails);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.providers.AuthenticationProvider#supports (java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean supports(Class arg0) {
		return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(arg0));
	}

	/**
	 * @return the automaticImportAllowed
	 */
	public boolean isAutomaticImportAllowed() {
		return this.automaticImportAllowed;
	}

	/**
	 * @param automaticImportAllowed the automaticImportAllowed to set
	 */
	public void setAutomaticImportAllowed(boolean automaticImportAllowed) {
		this.automaticImportAllowed = automaticImportAllowed;
	}

	/**
	 * @param ldapAuthenticationService the ldapAuthenticationService to set
	 */
	public void setLdapAuthenticationService(AuthenticationService ldapAuthenticationService) {
		this.ldapAuthenticationService = ldapAuthenticationService;
	}
}
