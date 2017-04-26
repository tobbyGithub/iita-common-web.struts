/**
 * iita-common-web.struts Apr 22, 2010
 */
package org.iita.struts.action;

import java.util.List;

import org.iita.query.model.Query;
import org.iita.query.model.Template;
import org.iita.service.QueryService;
import org.iita.service.TemplateService;
import org.iita.service.TemplatingService;
import org.iita.struts.BaseAction;
import org.iita.util.PagedResult;

import com.opensymphony.xwork2.Action;

/**
 * Custom Query management action
 * 
 * @author mobreza
 */
@SuppressWarnings("serial")
public class QueryEditAction extends BaseAction {
	private Long id;
	private Query query = new Query();
	private List<Query> queries;
	protected QueryService queryService;
	private TemplateService templateService;
	private TemplatingService templatingService;
	
	private PagedResult<?> testData;
	private List<Template> templates;
	// Generated Report
	private String report = null;

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @param queryService
	 */
	public QueryEditAction(QueryService queryService) {
		this.queryService = queryService;
	}

	/**
	 * @param templateService the templateService to set
	 */
	public void setTemplateService(TemplateService templateService) {
		this.templateService = templateService;
	}
	
	/**
	 * @param templatingService the templatingService to set
	 */
	public void setTemplatingService(TemplatingService templatingService) {
		this.templatingService = templatingService;
	}

	/**
	 * @return the templates
	 */
	public List<Template> getTemplates() {
		if (this.templates == null && this.templateService != null) {
			this.templates = this.templateService.listTemplates();
		}
		return this.templates;
	}

	/**
	 * @return the report
	 */
	public String getReport() {
		return this.report;
	}

	/**
	 * @see org.iita.struts.BaseAction#prepare()
	 */
	@Override
	public void prepare() {
		if (this.id != null)
			this.query = this.queryService.loadQuery(this.id);
	}

	public String list() {
		this.queries = this.queryService.list(getUser());
		return "list";
	}

	public String profile() {
		if (this.query == null) {
			addActionError("No such query");
			return Action.ERROR;
		}
		return "profile";
	}

	public String update() {
		try {
			this.queryService.update(this.query);
			return "redirect-list";
		} catch (Exception e) {
			addActionError("Could not store query: " + e.getMessage());
			return "profile";
		}
	}

	public String newQuery() {
		this.query = new Query();
		return "profile";
	}

	public String remove() {
		this.queryService.remove(this.query);
		return "redirect-list";
	}

	public String test() {
		try {
			this.testData = this.queryService.executeQuery(this.query, 0, 40);
			if (this.query.getTemplateName() != null) {
				this.report = fillReport(this.query.getTemplateName(), this.query.getHeadings(), this.testData);
			}
		} catch (Exception e) {
			addActionError("Could run query: " + e.getMessage());
			return "profile";
		}
		return "profile";
	}

	/**
	 * This method should be overriden in other implementations to give extra beans required to render report
	 * 
	 * @param pagedResult
	 * @param headings
	 * @param templateName
	 */
	protected String fillReport(String templateName, String[] headings, PagedResult<?> pagedResult) {
		return this.templatingService.fillReport(templateName, headings, pagedResult, null);
	}

	public String copy() {
		Query copy = this.query.copy();
		this.query = copy;
		return "profile";
	}

	/**
	 * @return the queries
	 */
	public List<Query> getQueries() {
		return this.queries;
	}

	/**
	 * @return the query
	 */
	public Query getQuery() {
		return this.query;
	}

	/**
	 * @return the testData
	 */
	public PagedResult<?> getTestData() {
		return this.testData;
	}
}
