/**
 * iita-common Apr 22, 2010
 */
package org.iita.query.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.iita.entity.VersionedEntity;
import org.iita.util.StringUtil;

/**
 * @author mobreza
 */
@Entity
public class Query extends VersionedEntity {
	private static final long serialVersionUID = 8883594594981508390L;

	private String title;
	private String shortName;
	private String description;
	private String query;
	private List<QueryParameter> parameters;
	private String heads;
	private String allowedRoles;

	private String[] headings;
	private String templateName;

	/**
	 * @return the title
	 */
	@Column(length = 200)
	public String getTitle() {
		return this.title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the shortName
	 */
	@Column(length = 100)
	public String getShortName() {
		return this.shortName;
	}

	/**
	 * @param shortName the shortName to set
	 */
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	/**
	 * @return the query
	 */
	@Lob
	public String getQuery() {
		return this.query;
	}

	/**
	 * @param query the query to set
	 */
	public void setQuery(String query) {
		this.query = query;
	}

	/**
	 * @return the parameters
	 */
	@OneToMany(cascade = { CascadeType.ALL }, mappedBy = "query")
	public List<QueryParameter> getParameters() {
		return this.parameters;
	}

	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(List<QueryParameter> parameters) {
		this.parameters = parameters;
	}

	/**
	 * @return the heads
	 */
	@Lob
	public String getHeads() {
		return this.heads;
	}

	/**
	 * @param heads the heads to set
	 */
	public void setHeads(String heads) {
		this.heads = heads;
		this.headings = StringUtil.splitString(this.heads, ",");
	}

	/**
	 * @return
	 */
	@Transient
	public String[] getHeadings() {
		if (this.heads == null)
			return null;
		else {
			return this.headings;
		}
	}

	/**
	 * @return the description
	 */
	@Lob
	public String getDescription() {
		return this.description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return
	 */
	public Query copy() {
		Query q = new Query();
		q.setQuery(this.query);
		q.setTitle(this.title);
		q.setShortName(this.shortName);
		q.setHeads(this.heads);
		q.setDescription(this.description);
		return q;
	}

	/**
	 * @return the needRoles
	 */
	@Column(length = 300)
	public String getAllowedRoles() {
		return this.allowedRoles;
	}

	/**
	 * @param needRoles the needRoles to set
	 */
	public void setAllowedRoles(String allowedRoles) {
		this.allowedRoles = StringUtil.nullOrString(allowedRoles);
	}

	/**
	 * Get name of template that should be used to generate resulting report
	 * 
	 * @return the templateName
	 */
	@Column(length = 100)
	public String getTemplateName() {
		return this.templateName;
	}

	/**
	 * @param templateName the templateName to set
	 */
	public void setTemplateName(String templateName) {
		this.templateName = StringUtil.nullOrString(templateName);
	}
}
