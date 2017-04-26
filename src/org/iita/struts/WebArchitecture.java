/**
 * iita-common-web.struts Jan 30, 2010
 */
package org.iita.struts;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.iita.security.model.User;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;

/**
 * @author mobreza
 * 
 */
public class WebArchitecture {
	private static final Log LOG = LogFactory.getLog(WebArchitecture.class);
	private ApplicationNotifications applicationNotifications;

	/**
	 * @param projectTaskNotifications
	 */
	public WebArchitecture(ApplicationNotifications applicationNotifications) {
		this.applicationNotifications = applicationNotifications;
	}

	@AfterReturning(pointcut = "execution(* org.iita.security.IitaAuthentication.authenticate(..))", returning = "authentication")
	public void userLoggedIn(JoinPoint jp, Authentication authentication) {
		LOG.info("User logged in: " + authentication.getPrincipal());
		this.applicationNotifications.userLoggedIn((User) authentication.getPrincipal());
	}

	@AfterThrowing(pointcut = "execution(* org.iita.security.IitaAuthentication.authenticate(..))", throwing = "ex")
	public void authenticationFailed(JoinPoint jp, Throwable ex) {
		LOG.info("User authentication failed: " + ex.getMessage());
		this.applicationNotifications.authenticationFailed(ex.getMessage());
	}

	@Before("execution(* org.springframework.security.ui.logout.LogoutHandler.logout(..))")
	public void userLoggingOut(JoinPoint jp) {
		// LOG.info("Before " + jp.getKind() + " " + jp.getSignature().getDeclaringTypeName() + "." + jp.getSignature().getName() + "(..)");
		// for (Object arg : jp.getArgs()) {
		// if (arg==null)
		// LOG.info("\targ null");
		// else
		// LOG.info("\targ " + arg.getClass().getName() + " " + arg);
		// }
		Authentication authentication = (Authentication) jp.getArgs()[2];
		if (authentication != null) {
			LOG.info("User logging out: " + authentication.getPrincipal());
			this.applicationNotifications.userLoggingOut((User) authentication.getPrincipal());
		}
	}

	@AfterReturning("execution(* org.iita.security.service.UserService.switchUser(..))")
	public void userSwitched(JoinPoint jp) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User currentAuthentication = (User) authentication.getPrincipal();
		LOG.info("User " + currentAuthentication.getImpersonator().getPrincipal() + " switched to " + currentAuthentication);
		this.applicationNotifications.userSwitched((User) currentAuthentication.getImpersonator().getPrincipal(), currentAuthentication);
	}

	@Before("execution(* org.iita.security.service.UserService.unswitchUser())")
	public void userUnswitched(JoinPoint jp) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User currentAuthentication = (User) authentication.getPrincipal();
		LOG.info("User " + currentAuthentication.getImpersonator() + " unswitched from " + currentAuthentication);
		this.applicationNotifications.userUnswitched((User) currentAuthentication.getImpersonator().getPrincipal(), currentAuthentication);
	}

	@Pointcut("within(org.iita.*)")
	public void withinApplicationService() {
	}

	@AfterThrowing(pointcut = "withinApplicationService()", throwing = "ex")
	public void applicationExceptionThrown(Throwable ex) {
		LOG.error("Application exception thrown: " + ex.getMessage(), ex);
		this.applicationNotifications.applicationExceptionThrown(ex);
	}

	@AfterReturning(pointcut = "execution(* org.iita.service.EmailService.sendEmail(String , String , String , String ))")
	public void emailSent(JoinPoint jp) {
		LOG.debug("Email sent");
		String sender = (String) jp.getArgs()[0];
		String recipient = (String) jp.getArgs()[1];
		String subject = (String) jp.getArgs()[2];
		this.applicationNotifications.emailsSent(sender, new String[] { recipient } , null, subject);
	}

	// sendEmail(String sender, String[] recipients, String[] ccRecipients, String subject, String body)
	@AfterReturning(pointcut = "execution(* org.iita.service.EmailService.sendEmail(String , String[] , String[] , String , String ))")
	public void emailsSent(JoinPoint jp) {
		LOG.debug("Emails sent");
		String sender = (String) jp.getArgs()[0];
		String[] recipients = (String[]) jp.getArgs()[1];
		String[] cc = (String[]) jp.getArgs()[2];
		String subject = (String) jp.getArgs()[3];
		this.applicationNotifications.emailsSent(sender, recipients, cc, subject);
	}
}
