/**
 * promisCRM.Struts Aug 19, 2010
 */
package org.iita.cron;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * @author mobreza
 * 
 */
public class OpenSessionInView {
	private static final Log LOG = LogFactory.getLog(OpenSessionInView.class);
	private EntityManagerFactory entityManagerFactory;
	
	/**
	 * @param entityManagerFactory the entityManagerFactory to set
	 */
	public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	public void open() {
		if (TransactionSynchronizationManager.hasResource(this.entityManagerFactory)) {
			LOG.info("Alread has thread-bound emf.");
		} else {
			LOG.info("Opening JPA EntityManager in OpenEntityManagerInViewFilter");
			try {
				EntityManager em = createEntityManager(entityManagerFactory);
				TransactionSynchronizationManager.bindResource(entityManagerFactory, new EntityManagerHolder(em));
			} catch (PersistenceException ex) {
				throw new DataAccessResourceFailureException("Could not create JPA EntityManager", ex);
			}
		}
	}

	public void close() {
		EntityManagerHolder emHolder = (EntityManagerHolder) TransactionSynchronizationManager.unbindResource(this.entityManagerFactory);

		LOG.info("Closing JPA EntityManager in OpenEntityManagerInViewInterceptor");
		EntityManagerFactoryUtils.closeEntityManager(emHolder.getEntityManager());
	}

	/**
	 * @param entityManagerFactory2
	 * @return
	 */
	protected EntityManager createEntityManager(EntityManagerFactory emf) {
		return emf.createEntityManager();
	}
}
