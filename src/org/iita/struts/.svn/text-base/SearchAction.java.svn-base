package org.iita.struts;

import org.iita.service.SearchException;
import org.iita.service.SearchService;
import org.iita.util.PagedResult;

import com.opensymphony.xwork2.Action;

/**
 * Generic search action to be overriden in sub-projects.
 * 
 * @author mobreza
 * 
 * @param <T> Type of stuff we're searching for
 */
@SuppressWarnings("serial")
public abstract class SearchAction<T> extends BaseAction {
	private SearchService<T> searchService;
	private int startAt = 0, maxResults = 50;
	private String searchString;
	private PagedResult<T> paged;

	@SuppressWarnings("unchecked")
	public SearchAction(SearchService<Object> searchService) {
		this.searchService = (SearchService<T>) searchService;
	}

	public void setStartAt(int startAt) {
		this.startAt = startAt;
	}

	public void setQ(String q) {
		this.searchString = q;
	}

	/**
	 * @return the searchString
	 */
	public String getSearchString() {
		return this.searchString;
	}

	public PagedResult<T> getPaged() {
		return paged;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String execute() {
		try {
			paged = (PagedResult<T>) this.searchService.search(this.searchString, getSearchedClass(), new String[] { "" }, startAt, maxResults);
		} catch (SearchException e) {
			LOG.error(e);
			addActionError(e.getMessage());
			return Action.ERROR;
		}
		return super.execute();
	}

	/**
	 * @return
	 */
	protected abstract Class<?> getSearchedClass();
}
