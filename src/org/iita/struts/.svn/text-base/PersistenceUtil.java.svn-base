/**
 * 
 */
package org.iita.struts;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletContext;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.opensymphony.xwork2.ActionContext;

/**
 * A Struts2+Spring utility providing the EntityManager from the application context.
 * 
 * @author mobreza
 */
public class PersistenceUtil {
	/**
	 * Get reference to EntityManager bean.
	 * 
	 * @return EntityManager
	 */
	@SuppressWarnings("unchecked")
	public static EntityManager getEntityManager() {
		Map context = ActionContext.getContext().getContextMap();
		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext((ServletContext) context
				.get("com.opensymphony.xwork2.dispatcher.ServletContext"));

		Object x = ctx.getBean("entityManagerFactory");
		// System.out.println("::: " + x.getClass().toString() + " " + x);
		EntityManagerFactory f = (EntityManagerFactory) x;
		EntityManager secondEntityManager = f.createEntityManager(context);
		return secondEntityManager;
	}

	/**
	 * Get second entity manager from application context.
	 * 
	 * @param context
	 * @return
	 */
	public static EntityManager getEntityManager(Map<?, ?> context) {
		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext((ServletContext) context
				.get("com.opensymphony.xwork2.dispatcher.ServletContext"));

		Object x = ctx.getBean("entityManagerFactory");
		// System.out.println("::: " + x.getClass().toString() + " " + x);
		EntityManagerFactory f = (EntityManagerFactory) x;
		EntityManager secondEntityManager = f.createEntityManager(context);
		return secondEntityManager;
	}
}
