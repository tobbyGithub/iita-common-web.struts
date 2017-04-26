/**
 * iita-common-web.struts Apr 16, 2010
 */
package org.iita.struts.action;

import java.util.List;

import org.iita.hibernate.UndeleteData;
import org.iita.service.UndeleteService;
import org.iita.struts.BaseAction;

import com.opensymphony.xwork2.Action;

/**
 * @author mobreza
 * 
 */
@SuppressWarnings("serial")
public class UndeleteAction extends BaseAction {
	private List<UndeleteData> deletions;
	private String hash;
	private UndeleteService undeleteService;

	/**
	 * @param undeleteService 
	 * 
	 */
	public UndeleteAction(UndeleteService undeleteService) {
		this.undeleteService=undeleteService;
	}
	
	/**
	 * @param hash the hash to set
	 */
	public void setHash(String hash) {
		this.hash = hash;
	}

	/**
	 * @see org.iita.struts.BaseAction#prepare()
	 */
	@Override
	public void prepare() {
		this.deletions = this.undeleteService.listDeletions();
	}

	/**
	 * @see org.iita.struts.BaseAction#execute()
	 */
	@Override
	public String execute() {
		return Action.SUCCESS;
	}

	public String undelete() {
		LOG.info("Undeleting " + this.hash);
		for (UndeleteData deleted : this.deletions) {
			String objectHash=String.format("%1$s@%2$d",deleted.getClass().getName(), deleted.hashCode());
			if (objectHash.equals(this.hash)) {
				try {
					this.undeleteService.undelete(deleted);
					this.deletions = this.undeleteService.listDeletions();
					addActionMessage("Object undeleted");
					return Action.SUCCESS;
				} catch (Exception e) {
					this.undeleteService.cannotUndelete(deleted);
					this.deletions = this.undeleteService.listDeletions();
					addActionError("Could not restore entity. " + e.getMessage() + " (Removed from list)");
					return Action.ERROR;
				}
			}
		}
		addActionError("No such object.");
		return Action.ERROR;
	}

	/**
	 * @return the deletedEntities
	 */
	public List<UndeleteData> getDeletedEntities() {
		return this.deletions;
	}
}
