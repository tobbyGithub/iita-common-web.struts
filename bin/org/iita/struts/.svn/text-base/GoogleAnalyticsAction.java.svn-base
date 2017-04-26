/**
 * iita-common-web.struts Oct 27, 2009
 */
package org.iita.struts;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork2.Action;

/**
 * @author mobreza
 * 
 */
public class GoogleAnalyticsAction {
	private static final Log LOG=LogFactory.getLog(GoogleAnalyticsAction.class);
	private ArrayList<String> trackers;

	/**
	 * Can use a comma separated list of ids
	 * 
	 * @param googleIds
	 */
	public void setSiteId(String googleIds) {
		this.trackers=new ArrayList<String>();
		if (googleIds==null || googleIds.trim().length()==0) {
			LOG.warn("Google site ID not specified. Not enabling.");
			return;
		}
		LOG.info("Google site IDs: " + googleIds);
		String[] split = googleIds.split(",");
		for (String trackerId : split) {
			if (trackerId==null || trackerId.trim().length()==0) {
				continue;
			}
			LOG.info("Registering " + trackerId);
			this.trackers.add(trackerId);
		}
	}
	
	/**
	 * @return the trackers
	 */
	public ArrayList<String> getTrackers() {
		return this.trackers;
	}
	
	public String execute() {
		return Action.SUCCESS;
	}
}
