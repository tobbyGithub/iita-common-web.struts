/**
 * iita-common-web.struts Apr 16, 2010
 */
package org.iita.service.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.iita.hibernate.UndeleteData;
import org.iita.service.UndeleteService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author mobreza
 * 
 */
public class UndeleteServiceImpl implements UndeleteService {
	private EntityManager entityManager;
	private static final Log LOG = LogFactory.getLog(UndeleteServiceImpl.class);

	/**
	 * @param entityManager the entityManager to set
	 */
	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public List<UndeleteData> listDeletions() {
		return org.iita.hibernate.UndeleteInterceptor.getDeletions();
	}

	/**
	 * @see org.iita.service.UndeleteService#undelete(java.lang.Object)
	 */
	@Override
	@Transactional
	public void undelete(UndeleteData deleted) {
		for (Object entity : deleted.getEntities()) {
			try {
				if (!this.entityManager.contains(entity))
					this.entityManager.merge(entity);
			} catch (Exception e) {
				LOG.error(e.getMessage());
			}
		}
		org.iita.hibernate.UndeleteInterceptor.undelete(deleted);
	}

	/**
	 * @see org.iita.service.UndeleteService#cannotUndelete(java.lang.Object)
	 */
	@Override
	public void cannotUndelete(UndeleteData deleted) {
		org.iita.hibernate.UndeleteInterceptor.undelete(deleted);
	}
}
