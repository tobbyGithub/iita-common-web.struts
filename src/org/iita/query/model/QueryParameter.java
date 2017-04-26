/**
 * iita-common Apr 22, 2010
 */
package org.iita.query.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.iita.entity.SimpleEntity;

/**
 * @author mobreza
 * 
 */
@Entity
public class QueryParameter extends SimpleEntity {
	private static final long serialVersionUID = -6247480379057410728L;
	private Query query;
	private String name;

	/**
	 * @return the query
	 */
	@ManyToOne(cascade = {})
	public Query getQuery() {
		return this.query;
	}

	/**
	 * @param query the query to set
	 */
	public void setQuery(Query query) {
		this.query = query;
	}

	/**
	 * @return the name
	 */
	@Column(length = 100)
	public String getName() {
		return this.name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * This method figures out the type of parameter and returns a proper object
	 * 
	 * @return
	 */
	@Transient
	public Object getValue() {
		return null;
	}
}
