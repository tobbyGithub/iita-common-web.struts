/**
 * 
 */
package org.iita.struts;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.util.StrutsTypeConverter;
import org.springframework.transaction.annotation.Transactional;

/**
 * Generic converter converts objects to their ID values by invoking <b>getId</b> method of the object if it exists. The reverse process will check the return
 * type of <b>getId</b> method, convert the string representation of the ID to that type using parseLong, parseInt, etc. and load the object using
 * EntityManager's <b>load(id, type)</b> method.
 * 
 * @author mobreza
 */
public class GenericConverter extends StrutsTypeConverter {
	private static Log LOG = LogFactory.getLog(GenericConverter.class);
	private static Map<Class<?>, Method> identifierMethodsCache = new Hashtable<Class<?>, Method>();
	private EntityManager entityManager;

	/**
	 * @param entityManager the entityManager to set
	 */
	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.struts2.util.StrutsTypeConverter#convertFromString(java.util .Map, java.lang.String[], java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public Object convertFromString(Map context, String[] values, Class arg2) {
		// no value passed in, must be null
		if (values.length == 0 || values[0].length() == 0)
			return null;

		// trademark
		if (LOG.isDebugEnabled())
			LOG.debug("Generic for Struts: " + arg2.getName() + " with " + values.length + " values: " + (values.length > 0 ? values[0] : ""));

		Object idValue = null;
		Method getIdMethod;
		try {
			getIdMethod = findIdentifierMethod(arg2);
			// exit immediately
			if (getIdMethod == null) {
				LOG.warn("No @Id annotation on class " + arg2);
				return null;
			}

			// System.err.println("getId: " +
			// getIdMethod.getReturnType().getName());

			if (getIdMethod.getReturnType() == Short.class)
				idValue = Short.parseShort(values[0]);
			else if (getIdMethod.getReturnType() == Long.class)
				idValue = Long.parseLong(values[0]);
			else if (getIdMethod.getReturnType() == Integer.class)
				idValue = Integer.parseInt(values[0]);

		} catch (SecurityException e) {
			LOG.warn(e.getMessage());
		} catch (NoSuchMethodException e) {
			LOG.warn(e.getMessage());
		}

		if (idValue == null)
			return null;
		else
			return this.entityManager.find(arg2, idValue);
	}

	/**
	 * Find the method that returns object identifier.
	 * 
	 * @param clazz
	 * @return
	 * @throws NoSuchMethodException
	 */
	private Method findIdentifierMethod(Class<? extends Object> clazz) throws NoSuchMethodException {
		Method getIdMethod = null;
		// check local cache
		synchronized (GenericConverter.identifierMethodsCache) {
			getIdMethod = GenericConverter.identifierMethodsCache.get(clazz);
		}

		if (getIdMethod == null) {
			// find method annotated with @Id
			Method[] methods = clazz.getMethods();
			for (int i = 0; i < methods.length; i++) {
				Method method = methods[i];
				Id annotation = method.getAnnotation(Id.class);
				if (annotation != null) {
					LOG.debug("Found @Id annotation " + annotation);
					synchronized (GenericConverter.identifierMethodsCache) {
						GenericConverter.identifierMethodsCache.put(clazz, method);
					}
					return method;
				}
			}
		}

		return getIdMethod;
	}

	/**
	 * Converts an object to string representations. All
	 * 
	 * @see org.apache.struts2.util.StrutsTypeConverter#convertToString(java.util .Map, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String convertToString(Map arg0, Object arg1) {
		// return null if object is null!
		if (arg1 == null)
			return null;

		try {
			// find "getId" method
			Method getId = findIdentifierMethod(arg1.getClass());

			if (getId == null)
				return null;

			// invoke method and return string representation of id
			return getId.invoke(arg1, (Object[]) null).toString();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

}
