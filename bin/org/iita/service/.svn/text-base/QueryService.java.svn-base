/**
 * iita-common Apr 22, 2010
 */
package org.iita.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.hibernate.hql.ast.QuerySyntaxException;
import org.iita.query.model.Query;
import org.iita.security.model.User;
import org.iita.util.PagedResult;

/**
 * @author mobreza
 */
public interface QueryService {

	/**
	 * Load query from DB
	 * 
	 * @param id
	 * @return
	 */
	Query loadQuery(Long id);

	/**
	 * @param query
	 * @return
	 */
	PagedResult<?> executeQuery(Query query, int startAt, int maxResults);
	
	/**
	 * @param query
	 * @return
	 */
	PagedResult<?> executeAlumniSearchQuery(String query, int startAt, int maxResults);

	/**
	 * Execute query and return results in XLS stream
	 * 
	 * @param query
	 * @return
	 * @throws IOException
	 */
	InputStream executeXLS(Query query) throws IOException;

	/**
	 * List all queries available to a user
	 * 
	 * @param user
	 * @return
	 */
	List<Query> list(User user);

	/**
	 * @param query
	 */
	void update(Query query);

	/**
	 * @param query
	 */
	void remove(Query query);

	/**
	 * List queries accessible by given user role
	 * 
	 * @param role
	 */
	List<Query> listForRole(String role);

	/**
	 * @param query
	 * @return
	 * @throws IOException
	 */
	InputStream executeHtml2XLS(Query query, BatchTemplater batchTemplater) throws IOException;

	Query updateSearch(Query query) throws QuerySyntaxException;
}
