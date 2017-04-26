/**
 * iita-common-web.struts Apr 22, 2010
 */
package org.iita.struts.action;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.iita.query.model.Query;
import org.iita.service.BatchTemplater;
import org.iita.service.QueryService;
import org.iita.service.TemplatingService;
import org.iita.struts.BaseAction;
import org.iita.struts.DownloadableStream;
import org.iita.util.PagedResult;
import org.iita.util.StringUtil;

import com.opensymphony.xwork2.Action;

/**
 * Action to run queries
 * 
 * @author mobreza
 */
@SuppressWarnings("serial")
public class QueryAction extends BaseAction implements DownloadableStream {
	private static final int maxResults = 20;
	private Query query;
	private Long id;
	protected QueryService queryService;
	private TemplatingService templatingService;
	private PagedResult<?> paged;
	private int startAt = 0;
	private String downloadFileName;
	private InputStream downloadStream;
	private List<Query> queries;
	private String report;

	/**
	 * @param queryService
	 */
	public QueryAction(QueryService queryService) {
		this.queryService = queryService;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @param startAt the startAt to set
	 */
	public void setStartAt(int startAt) {
		this.startAt = startAt;
	}

	/**
	 * @return the startAt
	 */
	public int getStartAt() {
		return this.startAt;
	}

	/**
	 * @return the report
	 */
	public String getReport() {
		return this.report;
	}

	/**
	 * @param templatingService the templatingService to set
	 */
	public void setTemplatingService(TemplatingService templatingService) {
		this.templatingService = templatingService;
	}

	/**
	 * @see org.iita.struts.BaseAction#prepare()
	 */
	@Override
	public void prepare() {
		if (this.id != null)
			this.query = this.queryService.loadQuery(this.id);
	}

	/**
	 * @see org.iita.struts.BaseAction#execute()
	 */
	@Override
	public String execute() {
		if (this.query == null) {
			addActionError("No such query");
			return Action.ERROR;
		}

		this.paged = this.queryService.executeQuery(this.query, this.startAt, QueryAction.maxResults);
		if (this.query.getTemplateName() != null) {
			this.report = fillReport(this.query.getTemplateName(), this.query.getHeadings(), this.paged);
		}
		return Action.SUCCESS;
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

	/**
	 * @return the paged
	 */
	public PagedResult<?> getPaged() {
		return this.paged;
	}

	public String download() {
		if (this.query == null) {
			addActionError("No such query");
			return Action.ERROR;
		}

		try {
			if (this.query.getTemplateName() != null) {
				BatchTemplater batchTemplater = this.templatingService.createBatchTemplater(this.query.getTemplateName());
				batchTemplater.setHeadings(this.query.getHeadings());
				this.downloadStream = this.queryService.executeHtml2XLS(this.query, batchTemplater);
			} else {
				// use old fashioned download
				this.downloadStream = this.queryService.executeXLS(this.query);
			}
			this.downloadFileName = StringUtil.sanitizeFileName(String.format("%2$tF %1$s.xls", this.query.getTitle(), new Date()));
			return "download";
		} catch (Exception e) {
			LOG.warn("Could not generate XLS: " + e.getMessage(), e);
			addActionError("Could not generate XLS file. " + e.getMessage());
			return Action.ERROR;
		}
	}

	/**
	 * @see org.iita.struts.DownloadableStream#getDownloadFileName()
	 */
	@Override
	public String getDownloadFileName() {
		return this.downloadFileName;
	}

	/**
	 * @see org.iita.struts.DownloadableStream#getDownloadStream()
	 */
	@Override
	public InputStream getDownloadStream() {
		return this.downloadStream;
	}

	public String list() {
		this.queries = this.queryService.list(getUser());
		return "list";
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
}
