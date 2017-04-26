/**
 * iita-common-web.struts Sep 17, 2010
 */
package org.iita.struts;

import java.util.Map;

import org.apache.struts2.util.StrutsTypeConverter;

/**
 * OGNL incorrectly interpretes "" value (null) as false for Boolean types.
 * 
 * @author mobreza
 */
public class BooleanConverter extends StrutsTypeConverter {

	/**
	 * @see org.apache.struts2.util.StrutsTypeConverter#convertFromString(java.util.Map, java.lang.String[], java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object convertFromString(Map ctx, String[] value, Class arg2) {
		if (value[0] == null || value[0].trim().equals("")) {
			return null;
		}
		return Boolean.parseBoolean(value[0].trim());
	}

	/**
	 * @see org.apache.struts2.util.StrutsTypeConverter#convertToString(java.util.Map, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String convertToString(Map ctx, Object data) {
		if (data == null)
			return null;
		return data.toString();
	}

}
