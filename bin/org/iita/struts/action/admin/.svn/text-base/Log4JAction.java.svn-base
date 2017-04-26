/**
 * projecttask.Struts Oct 7, 2010
 */
package org.iita.struts.action.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.iita.struts.BaseAction;
import org.iita.util.StringUtil;

import com.opensymphony.xwork2.Action;

/**
 * @author mobreza
 */
@SuppressWarnings("serial")
public class Log4JAction extends BaseAction {
	private ArrayList<Logger> loggers;
	private String name;
	private String level;
	private Enumeration<Object> appenders;

	/**
	 * @return the loggers
	 */
	public ArrayList<Logger> getLoggers() {
		return this.loggers;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(String level) {
		this.level = StringUtil.nullOrString(level);
	}

	/**
	 * @return the appenders
	 */
	public Enumeration<?> getAppenders() {
		return this.appenders;
	}

	/**
	 * @see org.iita.struts.BaseAction#execute()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String execute() {
		loggers = new ArrayList<Logger>();
		Enumeration<Logger> en = LogManager.getCurrentLoggers();
		this.appenders = LogManager.getRootLogger().getAllAppenders();

		while (en.hasMoreElements())
			loggers.add(en.nextElement());
		loggers.add(LogManager.getRootLogger());

		Collections.sort(loggers, new Comparator<Logger>() {
			@Override
			public int compare(Logger o1, Logger o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		return Action.SUCCESS;
	}

	public String level() {
		Logger logger;
		if (this.name != null && "root".equalsIgnoreCase(this.name))
			logger = LogManager.getRootLogger();
		else
			logger = LogManager.getLogger(this.name);
		if (logger != null) {
			LOG.debug("Got logger: " + logger.getName());
			logger.setLevel(this.level == null ? null : Level.toLevel(this.level));
		}
		return "refresh";
	}
}
