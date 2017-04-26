/**
 * iita-common-web.struts Oct 11, 2010
 */
package org.iita.struts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.util.StrutsTypeConverter;

/**
 * Converts human readable time to a double/float value with minutes as the fraction part of double.
 * 
 * @author mobreza
 */
public class TimeConverter extends StrutsTypeConverter {
	private static Log LOG = LogFactory.getLog(TimeConverter.class);
	private static final Pattern TIME_PATTERN = Pattern.compile("(\\d+)?:(\\d+)");

	/**
	 * @see org.apache.struts2.util.StrutsTypeConverter#convertFromString(java.util.Map, java.lang.String[], java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object convertFromString(Map paramMap, String[] paramArrayOfString, Class paramClass) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("TimeConverter for Struts: " + paramClass + " with " + paramArrayOfString.length + " values: "
					+ (paramArrayOfString.length > 0 ? paramArrayOfString[0] : ""));
			//LOG.debug("Stack trace", new Exception("I'm here now"));
		}

		if (paramClass == List.class) {
			// assume List<Double>!
			List<Double> times = new ArrayList<Double>();
			for (String value : paramArrayOfString) {
				times.add(convertTime(value));
			}
			return times;
		}

		Double duration = convertTime(paramArrayOfString[0]);
		if (duration == null)
			return null;

		if (paramClass == Double.class) {
			return duration;
		} else if (paramClass == Float.class) {
			return new Float(duration);
		} else if (paramClass == Long.class) {
			return new Long(duration.longValue());
		} else if (paramClass == Integer.class) {
			return new Integer(duration.intValue());
		} else if (paramClass == double.class) {
			return duration.doubleValue();
		} else if (paramClass == float.class) {
			return duration.floatValue();
		} else if (paramClass == int.class) {
			return duration.intValue();
		}

		LOG.warn("Unhandled TimeConverter class " + paramClass);
		throw new RuntimeException("Unhandled TimeConverter class " + paramClass);
	}

	/**
	 * @param value
	 * @return
	 */
	private Double convertTime(String value) {
		if (value == null || value.trim().length() == 0)
			return null;

		if (value.contains(":")) {
			double duration = 0;
			Matcher matcher = TIME_PATTERN.matcher(value);
			matcher.find();
			String tt = matcher.group(1);
			if (tt != null && tt.length() > 0)
				duration += Double.parseDouble(tt);
			tt = matcher.group(2);
			if (tt != null && tt.length() > 0)
				duration += Double.parseDouble(tt) / 60.0;
			return duration;
		} else {
			return Double.parseDouble(value);
		}
	}

	/**
	 * Convert time to String representation
	 * 
	 * @see org.apache.struts2.util.StrutsTypeConverter#convertToString(java.util.Map, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String convertToString(Map paramMap, Object paramObject) {
		// return null if object is null!
		if (paramObject == null)
			return null;

		double timeValue = 0;
		if (paramObject instanceof Double) {
			timeValue = ((Double) paramObject).doubleValue();
		} else if (paramObject instanceof Float) {
			timeValue = ((Double) paramObject).doubleValue();
		} else if (paramObject instanceof Long) {
			timeValue = ((Long) paramObject).doubleValue();
		} else if (paramObject instanceof Integer) {
			timeValue = ((Integer) paramObject).doubleValue();
		} else {
			LOG.warn("Unhandled TimeConverter class " + paramObject);
		}

		if (timeValue == 0)
			return "-";

		LOG.debug("Time value=" + timeValue);
		double hours = Math.floor(timeValue);
		double minutes = Math.round(60.0d * (Math.abs(timeValue) - Math.abs(hours)));

		if (hours == 0)
			return String.format("%1$02d'", (long) minutes);
		else if (minutes == 0)
			return String.format("%1$01dh", (long) hours);
		else
			return String.format("%1$01dh %2$02d'", (long) hours, (long) minutes);
	}

}
