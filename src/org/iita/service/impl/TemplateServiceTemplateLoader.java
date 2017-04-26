/**
 * iita-common-web.struts Oct 29, 2010
 */
package org.iita.service.impl;

import java.io.IOException;
import java.io.Reader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.iita.query.model.Template;
import org.iita.service.TemplateService;

import freemarker.cache.TemplateLoader;

/**
 * @author mobreza
 */
public class TemplateServiceTemplateLoader implements TemplateLoader {
	private static final Log LOG = LogFactory.getLog(FreemarkerTemplateEngine.class);
	private TemplateService templateService;

	/**
	 * @param templateService
	 */
	public TemplateServiceTemplateLoader(TemplateService templateService) {
		this.templateService = templateService;
	}

	/**
	 * @see freemarker.cache.TemplateLoader#closeTemplateSource(java.lang.Object)
	 */
	@Override
	public void closeTemplateSource(Object templateSource) throws IOException {
		LOG.debug("Closing template:" + templateSource);
	}

	/**
	 * @see freemarker.cache.TemplateLoader#findTemplateSource(java.lang.String)
	 */
	@Override
	public Object findTemplateSource(String templateName) throws IOException {
		if (templateName == null)
			return null;
		if (templateName.endsWith(".ftl"))
			templateName = templateName.substring(0, templateName.length() - 4);
		
		LOG.debug("Looking for template named: " + templateName);
		Template template = this.templateService.findInDatabase(templateName);
		LOG.debug("Got template " + templateName + ": " + template);
		return template;
	}

	/**
	 * @see freemarker.cache.TemplateLoader#getLastModified(java.lang.Object)
	 */
	@Override
	public long getLastModified(Object templateSource) {
		Template template = (Template) templateSource;
		return template.getLastUpdated().getTime();
	}

	/**
	 * @see freemarker.cache.TemplateLoader#getReader(java.lang.Object, java.lang.String)
	 */
	@Override
	public Reader getReader(Object templateSource, String encoding) throws IOException {
		Template template = (Template) templateSource;
		StringBuilder sb = new StringBuilder();
		if (template.getHeader() != null)
			sb.append(template.getHeader());
		if (template.getTemplate() != null)
			sb.append(template.getTemplate());
		if (template.getFooter() != null)
			sb.append(template.getFooter());

		return new java.io.StringReader(sb.toString());
	}

}
