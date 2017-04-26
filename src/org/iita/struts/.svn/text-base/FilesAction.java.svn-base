/**
 * projecttask.Struts Sep 17, 2009
 */
package org.iita.struts;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.iita.struts.webfile.ServerFile;
import org.iita.struts.webfile.ServerFileDownload;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;

/**
 * Generic file management action. Your action extending this abstract class will have to implement a few methods in order to allow for file management
 * 
 * @author mobreza
 */
@SuppressWarnings("serial")
public abstract class FilesAction extends BaseAction implements FileDownloadAction, FileUploadAction {
	private static Log LOG = LogFactory.getLog(FilesAction.class);

	private String downloadFileName = null;
	private String downloadDirectory = null;
	private List<File> uploads = new ArrayList<File>();
	private List<String> uploadFileNames = new ArrayList<String>();

	/**
	 * Set the name of file to download
	 * 
	 * @see org.iita.struts.FileDownloadAction#setFile(java.lang.String)
	 */
	public void setFile(String fileName) {
		this.downloadFileName = fileName;
	}

	/**
	 * Get file
	 * 
	 * @return the downloadFileName
	 */
	public String getFile() {
		return this.downloadFileName;
	}
	
	/**
	 * Set the sub-directory name
	 * 
	 * @see org.iita.struts.FileDownloadAction#setDirectory(java.lang.String)
	 */
	public void setDirectory(String directory) {
		this.downloadDirectory = directory;
	}

	/**
	 * Get directory
	 * 
	 * @return the downloadDirectory
	 */
	public String getDirectory() {
		return this.downloadDirectory;
	}

	public String getParentDirectory() {
		try {
			ServerFile currDir = ServerFile.getServerFile(getRootDirectory(), this.downloadDirectory);
			return currDir.getFile().getParentFile().getCanonicalPath().substring(getRootDirectory().getCanonicalPath().length());
		} catch (FileNotFoundException e) {
			LOG.error(e.getMessage());
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
		return null;
	}

	/**
	 * Uploads
	 * 
	 * @see org.iita.struts.FileUploadAction#getUploads()
	 */
	@Override
	public void setUploads(List<File> uploads) {
		this.uploads = uploads;
	}

	/**
	 * @see org.iita.struts.FileUploadAction#setUploadContentTypes(java.util.List)
	 */
	@Override
	public void setUploadsContentType(List<String> uploadContentTypes) {
		LOG.debug("Not setting content types");
	}

	/**
	 * @see org.iita.struts.FileUploadAction#setUploadFileNames(java.util.List)
	 */
	@Override
	public void setUploadsFileName(List<String> uploadFileNames) {
		this.uploadFileNames = uploadFileNames;
	}

	/**
	 * Call abstract method to provide file list
	 * 
	 * @return
	 */
	public List<ServerFile> getFiles() {
		return getFiles(this.downloadDirectory);
	}

	/**
	 * Override this method in your own action class
	 * 
	 * @param subDirectory sub directory to list, <code>null</code> lists root folder
	 * @return
	 */
	public abstract List<ServerFile> getFiles(String subDirectory);

	/**
	 * @see org.iita.struts.BaseAction#execute()
	 */
	@Override
	public String execute() {
		return Action.SUCCESS;
	}

	/**
	 * @see org.iita.struts.FileDownloadAction#download()
	 */
	public String download() {
		if (this.downloadFileName == null) {
			addActionError("Did not specify file to download");
			return Action.ERROR;
		}
		LOG.debug("Request to download " + this.downloadDirectory + "/" + this.downloadFileName + " by " + getUser());
		ServerFile serverFile;
		try {
			serverFile = getServerFile(this.downloadDirectory, this.downloadFileName);
		} catch (IOException e) {
			addActionError(e.getMessage());
			return Action.ERROR;
		}
		if (serverFile == null) {
			addActionError("Requested file '" + this.downloadFileName + "' not found on server");
			return Action.ERROR;
		}
		// Push downloader to value stack
		ValueStack valueStack = ActionContext.getContext().getValueStack();
		valueStack.push(new ServerFileDownload(serverFile));
		return "download";
	}

	public String upload() {
		if (this.uploads.size() == 0) {
			addActionError("No files to upload");
			return Action.ERROR;

		}
		LOG.info("Uploading " + this.uploads.size() + " files");

		int errors = 0;
		for (int i = 0; i < this.uploads.size(); i++) {
			File uploadedFile = this.uploads.get(i);
			try {
				updateServerFile(this.downloadDirectory, uploadedFile, this.uploadFileNames.get(i));
			} catch (IOException e) {
				addActionError(e.getMessage());
				errors++;
			}
		}

		return errors > 0 ? Action.ERROR : "reload";
	}

	/**
	 * @param downloadDirectory2
	 * @param uploadedFile
	 * @param fileName
	 * @throws IOException
	 */
	protected void updateServerFile(String directory, File uploadedFile, String fileName) throws IOException {
		File rootDirectory = getRootDirectory();
		ServerFile.updateFile(rootDirectory, directory, fileName, uploadedFile);
	}

	/**
	 * Get root directory where files should be stored
	 * 
	 * @return root directory where files should be stored
	 * @throws FileNotFoundException
	 */
	protected abstract File getRootDirectory() throws FileNotFoundException;

	/**
	 * Get browser title
	 * 
	 * @return browser title
	 */
	public abstract String getBrowserTitle();

	/**
	 * @return
	 */
	public abstract Long getId();

	public abstract void setId(Long id);

	/**
	 * Get action name, but remove method!
	 */
	public String getAction() {
		String action=ActionContext.getContext().getName();
		if (action.contains("!"))
			return action.substring(0, action.indexOf("!"));
		return action;
	}
}