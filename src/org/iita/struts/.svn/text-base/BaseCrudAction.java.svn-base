/**
 * travelauth.Struts May 12, 2009
 */
package org.iita.struts;

import org.iita.service.SimpleDaoService;
import org.iita.util.PagedResult;

import com.opensymphony.xwork2.Action;

/**
 * Base CRUD action implements the basic CRUD action methods:
 * 
 * <ul>
 * <li><code>execute()</code> loads object list from storage and is used to display lists of objects. Only accepts <code>startAt</code> parameter.</li>
 * <li><code>add()</code> will clear the object and return Action.INPUT, used for adding objects</li>
 * <li><code>edit()</code> accepts object identifier passed as <code>id</code> parameter</li>
 * <li><code>update()</code> accepts object identifier passed as <code>id</code> parameter and all <code>obj.*</code> parameters used to set additional
 * properties of the edited object</li>
 * <li><code>remove()</code> accepts object identifer passed as <code>id</code> parameter and will remove object from storage</li>
 * </ul>
 * 
 * @author mobreza
 * 
 */
@SuppressWarnings("serial")
public class BaseCrudAction<T> extends BaseAction {
	protected PagedResult<T> paged;
	protected SimpleDaoService<T> daoService;
	protected int startAt = 0;
	protected int maxResults = 50;
	protected T obj = null;
	protected Object id;

	/**
	 * @param startAt the startAt to set
	 */
	public void setStartAt(int startAt) {
		this.startAt = startAt;
	}

	/**
	 * @param maxResults the maxResults to set
	 */
	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

	/**
	 * @return the paged
	 */
	public PagedResult<T> getPaged() {
		return this.paged;
	}

	/**
	 * @return the obj
	 */
	public T getObj() {
		return this.obj;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	public BaseCrudAction(SimpleDaoService<T> service) {
		this.daoService = service;
	}

	/**
	 * Prepare method will load the referenced object if object's ID is provided.
	 * 
	 * @see org.iita.struts.BaseAction#prepare()
	 */
	@Override
	public void prepare() {
		if (this.id != null)
			this.obj = this.daoService.find(this.id);
	}

	/**
	 * @see com.opensymphony.xwork2.ActionSupport#execute()
	 */
	@Override
	@AllowedParameters( { "startAt" })
	public String execute() {
		this.paged = this.daoService.list(startAt, maxResults);
		return Action.SUCCESS;
	}

	@AllowedParameters( { "id" })
	public String edit() {
		this.obj = this.daoService.find(id);
		if (this.obj == null) {
			addActionError("No object with id " + id);
			return Action.ERROR;
		}
		return Action.INPUT;
	}

	@AllowedParameters( {})
	public String add() {
		return Action.INPUT;
	}

	@AllowedParameters( { "id", "obj\\..+" })
	public String update() {
		try {
			this.daoService.merge(this.obj);
		} catch (Exception e) {
			addActionError(e.getMessage());
			return Action.ERROR;
		}
		return Action.SUCCESS;
	}

	@AllowedParameters( { "id" })
	public String remove() {
		try {
			this.daoService.remove(this.obj);
		} catch (Exception e) {
			addActionError(e.getMessage());
			return Action.ERROR;
		}
		return Action.SUCCESS;
	}
}
