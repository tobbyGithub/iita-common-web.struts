/**
 * iita-common-web.struts Sep 1, 2010
 */
package org.iita.query.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.iita.entity.MySqlBaseEntity;
import org.iita.util.StringUtil;

/**
 * <p>
 * Query templates allow for custom formatting of query results. Queries executed will return rows of Objects. For every row, we can use {@link QueryTemplate}
 * and use Freemarker to generate the resulting "report".
 * </p>
 * <p>
 * The template consists of three sections: header, template and footer. Header and footer are to be rendered once, while template body is rendered for every
 * row. This way, header and footer allow us to set up &lt;table&gt; and other elements of the report.
 * </p>
 * <p>
 * This same entity can be used to "override" system provided template files (/WEB-INF/template/) by using the same short-name.
 * </p>
 * 
 * @author mobreza
 */
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "shortName" }) })
public class Template extends MySqlBaseEntity {
	private static final long serialVersionUID = 933818905717863147L;
	private String shortName;
	private String title;
	private String header;
	private String template;
	private String footer;

	/**
	 * @return the title
	 */
	@Column(nullable = false, length = 250)
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
	 * @return the template
	 */
	@Lob
	public String getTemplate() {
		return this.template;
	}

	/**
	 * @param template the template to set
	 */
	public void setTemplate(String template) {
		this.template = template;
	}

	/**
	 * @return the shortName
	 */
	@Column(nullable = false, length = 100)
	public String getShortName() {
		return this.shortName;
	}

	/**
	 * @param shortName the shortName to set
	 */
	public void setShortName(String shortName) {
		this.shortName = StringUtil.nullOrString(shortName);
	}

	/**
	 * @return the header
	 */
	@Lob
	public String getHeader() {
		return this.header;
	}

	/**
	 * @param header the header to set
	 */
	public void setHeader(String header) {
		this.header = header;
	}

	/**
	 * @return the footer
	 */
	@Lob
	public String getFooter() {
		return this.footer;
	}

	/**
	 * @param footer the footer to set
	 */
	public void setFooter(String footer) {
		this.footer = footer;
	}

}
