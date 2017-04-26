/**
 * 
 */
package org.iita.security.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.iita.security.model.User;
import org.iita.security.model.UserSupervisor;
import org.iita.security.service.UserServiceException;
import org.iita.security.service.UserSupervisionService;
import org.springframework.security.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;

import com.opensymphony.xwork2.ActionContext;

/**
 * @author aafolayan
 */
@Transactional
public class UserSupervisionServiceImpl implements UserSupervisionService {
	private static final Log log = LogFactory.getLog(UserSupervisionServiceImpl.class);
	private EntityManager entityManager;
	private String applicationName = null;

	/**
	 * @param applicationName the applicationName to set
	 */
	public void setApplication(String applicationId) {
		log.warn("Application ID configured to: " + applicationId);
		this.applicationName = applicationId;
	}

	/**
	 * @return the entityManager
	 */
	public EntityManager getEntityManager() {
		return this.entityManager;
	}

	/**
	 * @param entityManager the entityManager to set
	 */
	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	@Transactional
	@Secured( { "ROLE_USER" })
	public void addSupervisor(User owner, User supervisor, String application) throws UserServiceException {
		this.addSupervisor(owner, supervisor);
	}

	/*
	 * (non-Javadoc)
	 * @see org.iita.par.service.UserSupervisionService#addSupervisor(org.iita.par .model .User, org.iita.par.model.User, java.lang.String)
	 */
	@Override
	@Transactional
	@Secured( { "ROLE_ADMIN" })
	public void addSupervisor(User owner, User supervisor) throws UserServiceException {
		log.info("Adding supervisor " + supervisor + " to user " + owner + " in " + this.applicationName);
		List<User> supervisedTo = getSupervisors(owner, this.applicationName);
		if (supervisedTo.contains(supervisor)) {
			log.debug("Supervision exsits. Not adding.");
			throw new UserServiceException("Supervison already exists.");
		}
		UserSupervisor userSupervisor = new UserSupervisor();
		userSupervisor.setApplication(this.applicationName);
		userSupervisor.setUser(owner);
		userSupervisor.setSupervisor(supervisor);
		entityManager.persist(userSupervisor);
		log.info("User " + supervisor + " is now supervising " + owner + " in " + this.applicationName);
	}

	@Override
	@Transactional
	@Secured( { "ROLE_USER" })
	public void deleteSupervisor(User user, User supervisor, String application) throws UserServiceException {
		this.deleteSupervisor(user, supervisor);
	}

	/*
	 * (non-Javadoc)
	 * @see org.iita.par.service.UserSupervisionService#deleteSupervisor(org.iita .par.model .User, java.lang.String, java.lang.String)
	 */
	@Override
	@SuppressWarnings("unchecked")
	@Transactional
	@Secured( { "ROLE_USER" })
	public void deleteSupervisor(User user, User delegate) throws UserServiceException {
		log.info("Removing supervisor " + delegate + " from " + user + " in " + this.applicationName);
		// list delegations
		List<UserSupervisor> delegations = entityManager.createQuery(
				"from UserSupervisor ud where ud.application=:application and ud.user=:owner and ud.supervisor=:delegatedTo").setParameter("application",
				this.applicationName).setParameter("owner", user).setParameter("delegatedTo", delegate).getResultList();

		// Remove delegations
		for (UserSupervisor delegation : delegations) {
			log.info("Removing supervisor " + delegate + " from " + user + " in " + this.applicationName);
			entityManager.remove(delegation);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public UserSupervisor findSupervisor(User user, User supervisor, String application) throws UserServiceException {
		return this.findSupervisor(user, supervisor);
	}

	/*
	 * (non-Javadoc)
	 * @see org.iita.par.service.UserSupervisionService#findSupervisor(org.iita.par .model .User, java.lang.String, java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public UserSupervisor findSupervisor(User user, User supervisor) throws UserServiceException {
		log.info("findSupervisor record for supervisor " + supervisor + " of " + user + " in " + this.applicationName);
		try {
			// return delegation
			return (UserSupervisor) entityManager.createQuery(
					"from UserSupervisor ud where ud.application=:application and ud.user=:owner and ud.supervisor=:delegatedTo").setParameter("application",
					this.applicationName).setParameter("owner", supervisor).setParameter("delegatedTo", user).getSingleResult();
		} catch (javax.persistence.NoResultException e) {
			log.error("No supervisor record for " + supervisor + " of " + user);
			return null;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<User> getSupervisedUsers(User user, String application) {
		return this.getSupervisedUsers(user);
	}

	/*
	 * (non-Javadoc)
	 * @see org.iita.par.service.UserSupervisionService#getSupervisedFrom(org.iita .par. model.User, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public List<User> getSupervisedUsers(User user) {
		log.info("listing supervised users of " + user + " in " + this.applicationName);
		return entityManager.createQuery("select ud.user from UserSupervisor ud where ud.supervisor=:user and ud.application=:application").setParameter(
				"user", user).setParameter("application", this.applicationName).getResultList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<User> getSupervisedUsers(String application) {
		return this.getSupervisedUsers();
	}

	/*
	 * (non-Javadoc)
	 * @see org.iita.par.service.UserSupervisionService#getSupervisedUsers(java.lang .String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public List<User> getSupervisedUsers() {
		log.info("listing supervised users in " + this.applicationName);
		List<User> withDuplicates = entityManager.createQuery(
				"select ud.user from UserSupervisor ud where ud.application=:application order by ud.user.lastName").setParameter("application",
				this.applicationName).getResultList();
		log.info("Total of " + withDuplicates.size() + " supervised users in " + this.applicationName);
		List<Long> uniqueKeys = new ArrayList<Long>();
		for (int i = withDuplicates.size() - 1; i >= 0; i--) {
			long id = withDuplicates.get(i).getId();
			if (uniqueKeys.contains(id))
				withDuplicates.remove(i);
			else
				uniqueKeys.add(id);
		}
		log.info("Unique " + withDuplicates.size() + " supervised users in " + this.applicationName);
		return withDuplicates;
	}

	@Override
	@Transactional(readOnly = true)
	public List<User> getSupervisors(User user, String application) {
		return this.getSupervisors(user);
	}

	/*
	 * (non-Javadoc)
	 * @see org.iita.par.service.UserSupervisionService#getSupervisedTo(org.iita. par.model .User, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public List<User> getSupervisors(User user) {
		log.info("Listing supervisors of " + user + " in " + this.applicationName);
		return entityManager.createQuery(
				"select ud.supervisor from UserSupervisor ud where ud.user=:user and ud.application=:application order by ud.supervisor.lastName")
				.setParameter("user", user).setParameter("application", this.applicationName).getResultList();
	}

	/*
	 * (non-Javadoc)
	 * @see org.iita.par.service.UserSupervisionService#switchUser(org.iita.par.model .User, org.iita.par.model.UserSupervisor)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void switchUser(User user, UserSupervisor supervisor) {
		if (supervisor.getSupervisor().getId().equals(user.getId())) {
			log.info("User " + user.getUsername() + " switching TO user " + supervisor.getUser().getUsername());
			@SuppressWarnings("rawtypes")
			Map attributes = ActionContext.getContext().getSession();
			attributes.put("delegatedUser", supervisor.getUser());
		} else {
			log.error("User " + user.getUsername() + " tried switching to user " + supervisor.getUser().getUsername() + ", but the delegation does not match.");
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.iita.par.service.UserSupervisionService#unswitchUser(org.iita.par .model .User)
	 */
	@Override
	public void unswitchUser(User user) {
		log.info("User " + user.getUsername() + " switching back to their account.");
		@SuppressWarnings("rawtypes")
		Map attributes = ActionContext.getContext().getSession();
		attributes.remove("delegatedUser");

	}
}
