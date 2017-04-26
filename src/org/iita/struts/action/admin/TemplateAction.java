/**
 * iita-common-web.struts Sep 2, 2010
 */
package org.iita.struts.action.admin;

import java.util.List;

import org.iita.query.model.Template;
import org.iita.service.TemplateService;
import org.iita.struts.BaseAction;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.validator.annotations.RegexFieldValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;

/**
 * @author mobreza
 */
@SuppressWarnings("serial")
public class TemplateAction extends BaseAction {
	private TemplateService templateService;
	private List<Template> templates;
	private Template template = new Template();
	private String shortName = null;

	/**
	 * @param templateService
	 */
	public TemplateAction(TemplateService templateService) {
		this.templateService = templateService;
	}

	/**
	 * @return the templates
	 */
	public List<Template> getTemplates() {
		return this.templates;
	}

	/**
	 * @return the template
	 */
	public Template getTemplate() {
		return this.template;
	}

	/**
	 * @param shortName
	 * @param template the template to set
	 */
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	/**
	 * @return the shortName
	 */
	public String getShortName() {
		return this.shortName;
	}

	/**
	 * @see org.iita.struts.BaseAction#prepare()
	 */
	@Override
	public void prepare() {
		if (this.shortName != null && this.shortName.length() > 0)
			this.template = this.templateService.find(this.shortName);

		if (this.template == null)
			this.template = new Template();
	}

	/**
	 * @see org.iita.struts.BaseAction#execute()
	 */
	@Override
	public String execute() {
		this.templates = this.templateService.listTemplates();
		return Action.SUCCESS;
	}

	public String profile() {
		return Action.SUCCESS;
	}

	public String duplicate() {
		this.template.setId(null);
		this.template.setShortName(null);
		this.shortName = null;
		return Action.SUCCESS;
	}

	@Validations(requiredStrings = { @RequiredStringValidator(fieldName = "template.shortName", message = "Template short name must be provided. No spaces!") }, regexFields = { @RegexFieldValidator(fieldName = "template.shortName", expression = "^[\\w\\-]+$", message = "Short name can only contain a-Z and digits!") })
	public String update() {
		try {
			this.templateService.update(this.template);
			addNotificationMessage("Template updated");
			return "reload";
		} catch (javax.persistence.PersistenceException e) {
			addActionError(e.getMessage());
			return Action.INPUT;
		}
	}

	public String remove() {
		this.templateService.delete(this.template);
		return "to-list";
	}
}
