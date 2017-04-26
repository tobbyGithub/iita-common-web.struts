/**
 * iita-common-web.struts Sep 2, 2010
 */
package org.iita.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.iita.query.model.Template;
import org.iita.service.TemplateService;
import org.iita.util.Collections;
import org.iita.util.collections.Match;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author mobreza
 */
public class TemplateServiceImpl implements TemplateService {
	private static final Log LOG = LogFactory.getLog(TemplateServiceImpl.class);

	private EntityManager entityManager;
	private String templateDir = "./WEB-INF/template";

	/**
	 * @param entityManager the entityManager to set
	 */
	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/**
	 * @param templateDir the templateDir to set
	 */
	public void setTemplateDir(String templateDir) {
		this.templateDir = templateDir;
	}

	private List<Template> listTemplatesOnDisk() throws IOException {
		File path = null;
		if (this.templateDir.startsWith("/"))
			path = new File(this.templateDir);
		else {
			// find /WEB-INF/templates
			java.net.URL x = Thread.currentThread().getContextClassLoader().getResource("");
			path = new File(new File(x.getFile()).getParentFile().getParentFile(), this.templateDir);
		}
		LOG.info("Template directory: " + path.getAbsolutePath());

		List<Template> fileTemplates = new ArrayList<Template>();

		if (!path.exists()) {
			LOG.warn("Template directory does not exist: " + path.getAbsolutePath());
			return fileTemplates;
		}
		for (File file : path.listFiles()) {
			Template template = readFileTemplate(file);
			fileTemplates.add(template);
		}

		return fileTemplates;
	}

	private Template readFileTemplate(File file) throws FileNotFoundException, IOException {
		Reader sr = new BufferedReader(new FileReader(file));
		CharBuffer sb = CharBuffer.allocate(1048576); // 1M max
		sr.read(sb);
		sr.close();
		sb.limit(sb.position());
		sb.rewind();
		LOG.debug(sb.toString());
		Template template = new Template();
		template.setTemplate(sb.toString());
		String templateName = file.getName();
		if (templateName.endsWith(".ftl"))
			templateName = templateName.substring(0, templateName.indexOf(".ftl"));
		LOG.debug("Template name: " + templateName);
		template.setShortName(templateName);
		template.setTitle(templateName);
		return template;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public List<Template> listTemplates() {
		List<Template> fileList = null;
		try {
			fileList = listTemplatesOnDisk();
		} catch (IOException e) {
			LOG.warn(e);
		}
		List<Template> dbList = this.entityManager.createQuery("from Template t order by t.title").getResultList();
		if (fileList != null) {
			for (final Template template : fileList) {
				Template foundOne = new Collections().findFirst(dbList, new Match<Template>() {
					@Override
					public boolean isMatch(Template element) {
						return element.getShortName() != null && element.getShortName().equalsIgnoreCase(template.getShortName());
					}
				});
				if (foundOne == null) {
					LOG.debug("Template '" + template.getShortName() + "' not found in DB, adding");
					dbList.add(template);
				}
			}
		}

		return dbList;
	}

	/**
	 * @see org.iita.service.TemplateService#find(java.lang.Long)
	 */
	@Override
	@Transactional(readOnly = true)
	public Template find(Long id) {
		return this.entityManager.find(Template.class, id);
	}

	/**
	 * @see org.iita.service.TemplateService#find(java.lang.Long)
	 */
	@Override
	@Transactional(readOnly = true)
	public Template find(String shortName) {
		Template template = null;
		try {
			template = (Template) this.entityManager.createQuery("from Template t where t.shortName=:shortName").setParameter("shortName", shortName)
					.getSingleResult();
		} catch (javax.persistence.NoResultException e) {
			LOG.debug("Template '" + shortName + "' not in DB!");
		}
		if (template != null)
			return template;
		try {
			return findFileTemplate(shortName);
		} catch (IOException e) {
			LOG.error(e, e);
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Template findInDatabase(String shortName) {
		Template template = null;
		try {
			template = (Template) this.entityManager.createQuery("from Template t where t.shortName=:shortName").setParameter("shortName", shortName)
					.getSingleResult();
		} catch (javax.persistence.NoResultException e) {
			LOG.debug("Template '" + shortName + "' not in DB!");
		}
		return template;
	}

	/**
	 * @param shortName
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private Template findFileTemplate(String shortName) throws FileNotFoundException, IOException {
		File path = null;
		if (this.templateDir.startsWith("/"))
			path = new File(this.templateDir);
		else {
			// find /WEB-INF/templates
			java.net.URL x = Thread.currentThread().getContextClassLoader().getResource("");
			path = new File(new File(x.getFile()).getParentFile().getParentFile(), this.templateDir);
		}
		LOG.info("Template directory: " + path.getAbsolutePath());

		if (!path.exists()) {
			LOG.warn("Template directory does not exist: " + path.getAbsolutePath());
			return null;
		}

		File templateFile = new File(path, shortName + ".ftl");
		if (templateFile.exists()) {
			return readFileTemplate(templateFile);
		} else {
			LOG.error("Template file does not exist: " + templateFile.getAbsolutePath());
			throw new FileNotFoundException("Template file does not exist: " + templateFile.getAbsolutePath());
		}
	}

	@Override
	@Transactional
	public void update(Template template) {
		if (template.getId() == null)
			this.entityManager.persist(template);
		else
			this.entityManager.merge(template);
	}

	@Override
	@Transactional
	public void delete(Template template) {
		this.entityManager.remove(template);
	}

}
