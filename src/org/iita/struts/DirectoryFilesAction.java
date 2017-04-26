/**
 * iita-common-web.struts Oct 22, 2009
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

import com.opensymphony.xwork2.Action;

/**
 * Action allows for browsing, managing and downloading files from a specified server-side directory
 * 
 * @author mobreza
 */
@SuppressWarnings("serial")
public class DirectoryFilesAction extends FilesAction {
	private static final Log LOG = LogFactory.getLog(DirectoryFilesAction.class);
	private File rootDirectory;
	private String newDirectory = null;
	private List<String> selectedFiles = new ArrayList<String>();

	/**
	 * @param newDirectory the newDirectory to set
	 */
	public void setNewDirectory(String newDirectory) {
		this.newDirectory = newDirectory;
	}

	/**
	 * @return the selectedFiles
	 */
	public List<String> getSelectedFiles() {
		return this.selectedFiles;
	}
	
	/**
	 * @param selectedFiles the selectedFiles to set
	 */
	public void setSelectedFiles(List<String> selectedFiles) {
		this.selectedFiles = selectedFiles;
	}

	/**
	 * Construct a DirectoryFilesAction using a base directory
	 */
	public DirectoryFilesAction(String directory) {
		this.rootDirectory = new File(directory);
		if (!this.rootDirectory.exists())
			LOG.warn("Directory '" + directory + "' does not exist");
		if (!this.rootDirectory.isDirectory())
			LOG.warn("'" + directory + "' is not a directory");
		if (!this.rootDirectory.canRead())
			LOG.warn("Directory '" + directory + "' cannot be read");
	}

	/**
	 * @see org.iita.struts.FilesAction#getBrowserTitle()
	 */
	@Override
	public String getBrowserTitle() {
		return "application files";
	}

	/**
	 * @see org.iita.struts.FilesAction#getFiles(java.lang.String)
	 */
	@Override
	public List<ServerFile> getFiles(String subDirectory) {
		try {
			return ServerFile.getServerFiles(this.rootDirectory, subDirectory);
		} catch (IOException e) {
			addActionError(e.getMessage());
		}
		return null;
	}

	/**
	 * @see org.iita.struts.FilesAction#getId()
	 */
	@Override
	public Long getId() {
		return null;
	}

	/**
	 * @see org.iita.struts.FilesAction#getRootDirectory()
	 */
	@Override
	protected File getRootDirectory() throws FileNotFoundException {
		return this.rootDirectory;
	}

	/**
	 * @see org.iita.struts.FilesAction#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {

	}

	/**
	 * @see org.iita.struts.FileDownloadAction#getServerFile(java.lang.String, java.lang.String)
	 */
	@Override
	public ServerFile getServerFile(String subDirectory, String fileName) throws IOException {
		return ServerFile.getServerFile(this.rootDirectory, subDirectory, fileName);
	}

	/**
	 * @see org.iita.struts.FileDownloadAction#remove()
	 */
	@Override
	public String remove() {
		try {
			if (getFile() != null) {
				// delete file
				File selectedFile = ServerFile.getServerFile(this.rootDirectory, this.getDirectory(), this.getFile()).getFile();
				if (selectedFile.exists())
					selectedFile.delete();
				return Action.SUCCESS;
			} else if (this.selectedFiles.size() > 0) {
				for (String file : this.selectedFiles) {
					File selectedFile = ServerFile.getServerFile(this.rootDirectory, this.getDirectory(), file).getFile();
					if (selectedFile.exists())
						selectedFile.delete();
				}
			} else {
				addNotificationMessage("No files to delete");
			}
			return Action.SUCCESS;
		} catch (IOException e) {
			addActionError(e.getMessage());
			return Action.ERROR;
		}
	}

	/**
	 * Make directory on server
	 * 
	 * @return
	 */
	public String mkdir() {
		try {
			File directory = ServerFile.getServerFile(this.rootDirectory, this.getDirectory()).getFile();
			if (directory.isDirectory()) {
				File newDirectory = new File(directory, this.newDirectory);
				newDirectory.mkdir();
			}
			return Action.SUCCESS;
		} catch (IOException e) {
			addActionError(e.getMessage());
			return Action.ERROR;
		}
	}
}
