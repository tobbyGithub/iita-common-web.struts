/**
 * iita-common-web.struts Sep 2, 2010
 */
package org.iita.service;

import java.util.List;

import org.iita.query.model.Template;

/**
 * @author mobreza
 *
 */
public interface TemplateService {

	/**
	 * @param template
	 */
	void update(Template template);

	/**
	 * @param template
	 */
	void delete(Template template);

	/**
	 * @param id
	 * @return
	 */
	Template find(Long id);

	/**
	 * @param shortName
	 * @return
	 */
	Template find(String shortName);

	/**
	 * Get list of available templates
	 * 
	 * @return
	 */
	List<Template> listTemplates();

	/**
	 * @param shortName
	 * @return
	 */
	Template findInDatabase(String shortName);

}
