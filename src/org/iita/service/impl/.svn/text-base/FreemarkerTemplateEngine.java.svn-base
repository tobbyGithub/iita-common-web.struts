/**
 * 
 */
package org.iita.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.iita.query.model.Template;
import org.iita.service.BatchTemplater;
import org.iita.service.TemplateService;
import org.iita.service.TemplatingException;
import org.iita.service.TemplatingService;
import org.iita.util.PagedResult;
import org.iita.util.StringUtil;
import org.springframework.transaction.annotation.Transactional;

import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;

/**
 * @author mobreza
 */
public class FreemarkerTemplateEngine implements TemplatingService {
	private static final Log LOG = LogFactory.getLog(FreemarkerTemplateEngine.class);

	private String templateDir = "./WEB-INF/template";

	private Configuration config;
	private Properties templateProperties;

	private TemplateService templateService;

	/** Extensions beans to templates */
	private Map<String, Object> extensions = new Hashtable<String, Object>();

	/**
	 * @return the templateDir
	 */
	public String getTemplateDir() {
		return this.templateDir;
	}

	/**
	 * @param templateProperties the templateProperties to set
	 */
	public void setTemplateProperties(Properties templateProperties) {
		this.templateProperties = templateProperties;
	}

	/**
	 * @param templateService the templateService to set
	 */
	public void setTemplateService(TemplateService templateService) {
		this.templateService = templateService;
	}

	/**
	 * @param extensions the extensions to set
	 */
	public void setExtensions(Map<String, Object> extensions) {
		this.extensions = extensions;
	}

	/**
	 * Return a copy of template properties
	 * 
	 * @return the templateProperties
	 */
	@Override
	public Properties getTemplateProperties() {
		Properties prop = new Properties();
		prop.putAll(this.templateProperties);
		return prop;
	}

	/**
	 * @param templateDir the templateDir to set
	 */
	public void setTemplateDir(String templateDir) {
		this.templateDir = templateDir;
	}

	/*
	 * (non-Javadoc)
	 * @see org.iita.par.service.TemplatingService#fillTemplate(java.lang.String, java.util.Dictionary)
	 */
	@Override
	public String fillTemplate(String templateName, Map<String, Object> data) throws TemplatingException {
		if (config == null) {
			configureFreeMarker();
		}

		try {
			freemarker.template.Template template = config.getTemplate(templateName + ".ftl");

			// add StringUtil
			BeansWrapper wrapper = BeansWrapper.getDefaultInstance();
			TemplateHashModel staticModels = wrapper.getStaticModels();

			TemplateHashModel stringUtilStatics = (TemplateHashModel) staticModels.get("org.iita.util.StringUtil");
			data.put("StringUtil", stringUtilStatics);
			data.put("config", templateProperties);
			data.putAll(this.extensions);

			StringWriter sw = new StringWriter();
			template.process(data, sw);
			sw.flush();
			if (LOG.isTraceEnabled())
				LOG.trace(sw.toString());
			return sw.toString();
		} catch (IOException e) {
			LOG.error("Failed to fill template " + templateName);
			throw new TemplatingException("Failed to fill template " + templateName, e);
		} catch (TemplateException e) {
			LOG.error("Failed to fill template " + templateName);
			throw new TemplatingException("Failed to fill template " + templateName, e);
		}
	}

	/**
	 * 
	 */
	private synchronized void configureFreeMarker() {
		config = new Configuration();
		List<TemplateLoader> loaders = new ArrayList<TemplateLoader>();

		if (this.templateService != null) {
			LOG.debug("Creating TemplateService template loader");
			TemplateServiceTemplateLoader tstLoader = new TemplateServiceTemplateLoader(templateService);
			loaders.add(tstLoader);
		}

		try {
			File path = null;
			if (this.templateDir.startsWith("/"))
				path = new File(this.templateDir);
			else {
				// find /WEB-INF/templates
				java.net.URL x = Thread.currentThread().getContextClassLoader().getResource("");
				path = new File(new File(x.getFile()).getParentFile().getParentFile(), this.templateDir);
			}
			LOG.debug("Template directory: " + path.getAbsolutePath());

			FileTemplateLoader fileLoader = new FileTemplateLoader(path);
			loaders.add(fileLoader);
		} catch (IOException e1) {
			LOG.error(e1);
		}

		MultiTemplateLoader mtl = new MultiTemplateLoader(loaders.toArray(new TemplateLoader[] {}));
		config.setTemplateLoader(mtl);

		config.setObjectWrapper(new DefaultObjectWrapper());
		config.setDateFormat("dd/MM/yyyy");
	}

	/**
	 * @see org.iita.service.QueryService#fillReport(java.lang.String, java.lang.String[], org.iita.util.PagedResult)
	 */
	@Override
	@Transactional(readOnly = true)
	public String fillReport(String templateName, String[] headings, PagedResult<?> pagedData, Map<String, Object> extraBans) {
		StringBuffer sb = new StringBuffer();
		Template template = this.templateService.find(templateName);
		if (template == null)
			throw new RuntimeException("Template '" + templateName + "' could not be loaded");

		// append header
		if (template.getHeader() != null)
			sb.append(template.getHeader());

		// make template
		freemarker.template.Configuration configuration = new Configuration();
		configuration.setObjectWrapper(new DefaultObjectWrapper());
		configuration.setDateFormat("dd/MM/yyyy");
		freemarker.template.Template ftl;
		try {
			ftl = new freemarker.template.Template(template.getShortName(), new StringReader(template.getTemplate()), configuration);
		} catch (IOException e1) {
			LOG.error("Failed to construct template: " + e1.getMessage());
			return null;
		}

		for (Object row : pagedData.getResults()) {
			Map<String, Object> data = new Hashtable<String, Object>();
			data.put("config", templateProperties);
			// add extension services
			data.putAll(this.extensions);
			if (extraBans != null)
				data.putAll(extraBans);

			data.put("raw", row);

			if (row instanceof List<?>) {
				List<?> rowList = (List<?>) row;
				for (int i = 0; i < headings.length; i++) {
					LOG.debug("Putting list data '" + StringUtil.getCamel(headings[i]) + "' = " + rowList.get(i));
					data.put(StringUtil.getCamel(headings[i]), rowList.get(i));
				}
			} else {
				if (headings.length > 0) {
					LOG.debug("Putting data '" + StringUtil.getCamel(headings[0]) + "' = " + row);
					data.put(StringUtil.getCamel(headings[0]), row);
				} else {
					LOG.debug("Type " + row.getClass() + " not supported fully. Crosscheck");
				}
			}

			try {
				StringWriter sw = new StringWriter();
				ftl.process(data, sw);
				sw.flush();

				if (LOG.isDebugEnabled())
					LOG.debug(sw.toString());

				// append results
				sb.append(sw.toString());

			} catch (TemplateException e) {
				LOG.error("Error processing template: " + e.getMessage());
				throw new RuntimeException("Error processing template: " + e.getMessage());
			} catch (IOException e) {
				LOG.error("IO exception: " + e.getMessage());
			}
		}

		// append footer
		if (template.getFooter() != null)
			sb.append(template.getFooter());

		return sb.toString();
	}

	@Override
	public BatchTemplater createBatchTemplater(String templateName) {
		Template template = this.templateService.find(templateName);
		if (template == null)
			throw new RuntimeException("Template '" + templateName + "' could not be loaded");

		// make template
		freemarker.template.Configuration configuration = new Configuration();
		configuration.setObjectWrapper(new DefaultObjectWrapper());
		configuration.setDateFormat("dd/MM/yyyy");
		freemarker.template.Template ftl;
		try {
			ftl = new freemarker.template.Template(template.getShortName(), new StringReader(template.getTemplate()), configuration);
		} catch (IOException e1) {
			LOG.error("Failed to construct template: " + e1.getMessage());
			return null;
		}

		BatchTemplater batchTemplater = new BatchTemplater(template.getHeader(), template.getFooter(), ftl, this.templateProperties, this.extensions);

		return batchTemplater;
	}
}
