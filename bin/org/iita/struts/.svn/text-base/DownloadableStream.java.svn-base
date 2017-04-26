package org.iita.struts;

import java.io.InputStream;

/**
 * <p>Commonly used with struts managed downloads. The implementing action needs to provide the name of file being downloaded and the stream from which the data is
 * read and transferred to client. Commonly a global action result <b>download</b> is configured that will use the action configuration as described here:</p>
 * 
 * <pre>&lt;global-results>
	...
	&lt;result name="download" type="stream">
		&lt;param name="contentType">application/x-binary</param>
		&lt;param name="inputName">downloadStream&lt;/param>
		&lt;param name="contentDisposition">filename="${downloadFileName}"&lt;/param>
		&lt;param name="bufferSize">2048&lt;/param>
	&lt;/result>
	...
&lt;global-results>
 * </pre>
 * 
 * <p>You can always override the behaviour in your particular action:</p>
 * 
 * <pre>&lt;action name="training/trainees/export" class="traineeListAction" method="export">
	&lt;result name="download" ...>
		...
	&lt;/result>
&lt;/action>
	</pre>
 * 
 * @since Revision 1669
 * @author mobreza
 * 
 */
public interface DownloadableStream {
	/**
	 * Get name of file being downloaded
	 * 
	 * @return
	 */
	public abstract String getDownloadFileName();

	/**
	 * Get the stream that contains the file contents.
	 * 
	 * @return
	 */
	public abstract InputStream getDownloadStream();

}