/**
 * iita-common-web.struts Sep 17, 2009
 */
package org.iita.struts.webfile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author mobreza
 *
 */
public class ServerFileDownload {
	private static final Log LOG = LogFactory.getLog(ServerFileDownload.class);
	private ServerFile serverFile=null;
	
	/**
	 * @param serverFile 
	 * 
	 */
	public ServerFileDownload(ServerFile serverFile) {
		this.serverFile=serverFile;
	}
	
	public InputStream getDownloadStream() {
		LOG.info("Streaming file " + this.serverFile.getFile());
		try {
			return new FileInputStream(this.serverFile.getFile());
		} catch (FileNotFoundException e) {
			LOG.error(e);
			return null;
		}
	}
	
	public String getDownloadFileName() {
		return this.serverFile.getFileName();
	}
}
