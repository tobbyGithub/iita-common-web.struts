/**
 * 
 */
package org.iita.struts;

import java.util.List;

import org.iita.security.model.User;
import org.iita.security.service.UserSupervisionService;

import com.opensymphony.xwork2.Preparable;

/**
 * Dashboard action class.
 * 
 * 
 * 
 * @author mobreza
 * 
 */
@SuppressWarnings("serial")
public class Dashboard extends BaseAction implements Preparable {
	private UserSupervisionService userSupervisionService=null;

	
	/**
	 * @param userSupervisionService the userSupervisionService to set
	 */
	public void setUserSupervisionService(UserSupervisionService userSupervisionService) {
		this.userSupervisionService = userSupervisionService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.iita.par.action.BaseActionPar#prepare()
	 */
	@Override
	public void prepare() {
		super.prepare();
	}

	public List<User> getSupervisors() {
		return this.userSupervisionService.getSupervisors(getPrincipal());
	}
}
