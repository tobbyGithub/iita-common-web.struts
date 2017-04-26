package org.iita.struts;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.util.StrutsTypeConverter;
import org.iita.util.DateUtil;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.TypeConversionException;

public class CalendarConverter extends StrutsTypeConverter {
	private static final Log LOG = LogFactory.getLog(CalendarConverter.class);

	@SuppressWarnings("unchecked")
	public Object convertFromString(Map ctx, String[] value, Class arg2) {
		if (value[0] == null || value[0].trim().equals("")) {
			return null;
		}

		TimeZone timezone=(TimeZone) ActionContext.getContext().get("WW_TRANS_I18N_TIMEZONE");
		if (timezone==null) timezone=TimeZone.getDefault();

		try {
			LOG.debug("Parsing calendar: " + value[0] + " in timezone " + timezone.getDisplayName());
			Calendar c = DateUtil.convertStringToCalendar(value[0], timezone);
			SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy HH:mm:ss z");			
			LOG.debug("Parsed to : " + sdf.format(c.getTime()) + " in timezone " + c.getTimeZone().getDisplayName());
			return c;
		} catch (ParseException pe) {
			pe.printStackTrace();
			throw new TypeConversionException(pe.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public String convertToString(Map ctx, Object data) {
		Calendar c = (Calendar) data;
		return DateUtil.convertDateToString(c.getTime());
	}
}