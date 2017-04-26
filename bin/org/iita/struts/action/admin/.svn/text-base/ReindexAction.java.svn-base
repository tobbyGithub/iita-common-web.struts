/**
 * 
 */
package org.iita.struts.action.admin;

import java.util.List;

import org.iita.service.impl.Indexer;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Lucene reindexing action
 * 
 * @author mobreza
 * 
 */
@SuppressWarnings("serial")
public class ReindexAction extends ActionSupport {
	private String tableName = null;
	private List<String> tables=null;

	/** Indexer */
	private Indexer indexer = null;

	/**
	 * Indexer must be passed as constructor argument. There will be only one instance of Indexer per application.
	 * 
	 * @param indexer
	 */
	public ReindexAction(Indexer indexer) {
		this.indexer = indexer;
	}

	/**
	 * @return the tableName
	 */
	public String getTableName() {
		return this.tableName;
	}
	
	/**
	 * @param tables the tables to set
	 */
	public void setTables(List<String> tables) {
		this.tables = tables;
	}
	
	/**
	 * @return the tables
	 */
	public List<String> getTables() {
		return this.tables;
	}

	/**
	 * @param tableName the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	/**
	 * @return the indexer
	 */
	public Indexer getIndexer() {
		return this.indexer;
	}

	public String execute() {
		return Action.SUCCESS;
	}

	public String reindex() {
		if (this.indexer == null) {
			this.addActionError("No indexer in this application");
			return Action.ERROR;
		}

		if (!this.indexer.getRunning()) {
			if (this.tables!=null && this.tables.size()>0) {
					this.indexer.start(this.tables);
			}else if (this.tableName!=null)
				this.indexer.start(this.tableName, org.iita.struts.PersistenceUtil.getEntityManager());
		}
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			
		}

		return "redirect";
	}

	public String stop() {
		if (this.indexer == null) {
			this.addActionError("No indexer in this application");
			return Action.ERROR;
		}

		if (this.indexer.getRunning()) {
			this.indexer.stop();
		}

		return "redirect";
	}
}
