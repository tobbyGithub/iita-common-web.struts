/**
 * 
 */
package org.iita.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;

import com.opensymphony.xwork2.ActionContext;

/**
 * Lucene reindexer
 * 
 * @author mobreza
 */
public class Indexer implements Runnable {
	private static final Log LOG = LogFactory.getLog(Indexer.class);

	public enum Status {
		Stopped, Running
	}

	private boolean doIndex = false;
	private Thread thread = null;
	private AtomicLong currentLot = new AtomicLong(-1);
	private long totalRecordCount = -1;
	private int batchSize = 1000;
	private String indexingTable;
	private List<String> tables;
	private Map<?, ?> context;

	public boolean getRunning() {
		return this.getStatus() == Status.Running;
	}

	public synchronized Status getStatus() {
		if (this.thread != null && this.thread.isAlive())
			return Status.Running;
		else {
			this.stop();
			return Status.Stopped;
		}
	}

	/**
	 * Call {@link #start(List, EntityManager)} with a single element in the list
	 * 
	 * @param tableName
	 * @param entityManager
	 */
	public synchronized void start(String tableName, EntityManager entityManager) {
		List<String> tables = new ArrayList<String>();
		tables.add(tableName);
		start(tables);
	}

	/**
	 * @param tables
	 * @param entityManager
	 */
	public synchronized void start(List<String> tables) {
		if (this.getStatus() == Status.Running)
			throw new java.lang.IllegalStateException("Indexer already running on " + this.indexingTable);
		if (tables == null || tables.size() == 0)
			throw new NullPointerException("Table names not passed to indexer");

		if (this.thread != null && this.thread.isAlive()) {
			throw new java.lang.IllegalStateException("Indexer already running on " + this.indexingTable);
		} else {
			synchronized (this) {
				if (this.thread != null)
					this.stop();

				this.context = ActionContext.getContext().getContextMap();
				this.tables = tables;
				this.thread = new Thread(this);
				this.thread.setName("Lucene reindexer");
				this.thread.setDaemon(false);
				this.thread.start();
			}
		}
	}

	public void stop() {
		this.doIndex = false;
		if (this.thread == null)
			return;
		else {
			synchronized (this) {
				try {
					this.thread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				this.thread = null;
				this.doIndex = false;
			}
		}
	}

	/**
	 * Runner!
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@SuppressWarnings("unchecked")
	public void run() {
		List<String> myTables = new ArrayList<String>(this.tables);
		for (String tableName : myTables) {
			EntityManager em = org.iita.struts.PersistenceUtil.getEntityManager(this.context);
			this.indexingTable = tableName;
			LOG.info("Reindexing " + this.indexingTable);
			Thread.currentThread().setName("Lucene reindex: " + tableName);

			// get number of lots
			if (this.indexingTable.contains(" ") || !this.indexingTable.matches("^[a-zA-Z0-9\\.]+$"))
				throw new RuntimeException("Indexing table name contains invalid characters.");

			doIndex = true;
			this.totalRecordCount = 0;

			// may have more than one result (in case of mapped superclass)
			List<Long> entityCounts = (List<Long>) em.createQuery("select count(*) from " + this.indexingTable + " t").getResultList();
			if (entityCounts.size() > 1)
				LOG.error("Entity " + this.indexingTable + " is a @MappedSuperclass. You need to index all subclasses individually.");

			for (Long entityCount : entityCounts)
				this.totalRecordCount += entityCount;

			LOG.info("Indexer: " + this.totalRecordCount);
			FullTextEntityManager ftEm = Search.createFullTextEntityManager(em);
			try {
				ftEm.getTransaction().begin();
				ftEm.purgeAll(Class.forName(this.indexingTable));
				LOG.info("Purged all " + this.indexingTable);
				ftEm.getTransaction().commit();
			} catch (ClassNotFoundException e) {
				LOG.info("Cannot clear class " + this.indexingTable + ": " + e.getMessage());
				ftEm.getTransaction().rollback();
				return;
			}

			this.currentLot.set(0);
			// reindex lots in batches
			for (int i = 0; doIndex && i < this.totalRecordCount; i += this.batchSize) {
				LOG.info("Starting new fulltext transaction");
				ftEm.getTransaction().begin();
				LOG.info("Loading next batch...");
				@SuppressWarnings("rawtypes")
				List items = ftEm.createQuery("from " + this.indexingTable + " t").setMaxResults(this.batchSize).setFirstResult(i).getResultList();

				LOG.info("Indexing records: " + i + " - " + (i + batchSize) + " of total " + this.totalRecordCount);

				for (Object lot : items) {
					if (!doIndex)
						break;
					ftEm.index(lot);
					this.currentLot.addAndGet(1);
				}
				LOG.info("Committing fulltext transaction...");
				ftEm.getTransaction().commit();
				ftEm.clear();
			}
			ftEm.close();
			LOG.info("DONE reindexing " + this.indexingTable);
			this.currentLot.set(-1);
		}
	}

	/**
	 * @return the totalLotCount
	 */
	public long getTotalLotCount() {
		return this.totalRecordCount;
	}

	/**
	 * @return the currentLot
	 */
	public long getCurrentLot() {
		return this.currentLot.get();
	}

	/**
	 * @return the indexingTable
	 */
	public String getIndexingTable() {
		return this.indexingTable;
	}

}
