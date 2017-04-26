/**
 * iita-common-web.struts Sep 16, 2009
 */
package org.iita.struts.webfile;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.iita.util.NaturalOrderComparator;
import org.iita.util.PagedResult;

/**
 * A ServerFile contains information on a particular downloadable file. Most commonly you will use fileName, contentType and so on to display file information
 * in the browser.
 * 
 * @author mobreza
 */
public class ServerFile {
	private static final Log LOG = LogFactory.getLog(ServerFile.class);

	/**
	 * 
	 */
	private static final ArrayList<ServerFile> EMPTY_LIST = new ArrayList<ServerFile>();

	/** Serial ID. */
	private static final long serialVersionUID = -6274640563719507565L;

	/** The file name. */
	private String fileName;

	/** The title. */
	private String title;

	/** The download link. */
	private String downloadLink;

	/** Actual file on server */
	private File file;

	/**
	 * @param file2
	 */
	public ServerFile(File file) {
		this.file = file;
		this.fileName = this.file.getName();
	}

	/**
	 * Gets the file name.
	 * 
	 * @return the fileName
	 */
	public String getFileName() {
		return this.fileName;
	}

	/**
	 * Sets the file name.
	 * 
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Gets the title.
	 * 
	 * @return the title
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * Sets the title.
	 * 
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Gets the download link.
	 * 
	 * @return the downloadLink
	 */
	public String getDownloadLink() {
		return this.downloadLink;
	}

	/**
	 * Sets the download link.
	 * 
	 * @param downloadLink the downloadLink to set
	 */
	public void setDownloadLink(String downloadLink) {
		this.downloadLink = downloadLink;
	}

	/**
	 * @return the file
	 */
	public File getFile() {
		return this.file;
	}

	/**
	 * @param file the file to set
	 */
	public void setFile(File file) {
		this.file = file;
	}

	public static List<ServerFile> getServerFiles(File rootDirectory) {
		return getServerFiles(rootDirectory, (FileFilter) null);
	}

	public static List<ServerFile> getServerFiles(File rootDirectory, String subDirectory) throws IOException {
		return getServerFiles(rootDirectory, subDirectory, (FileFilter) null);
	}

	public static List<ServerFile> getServerFiles(File rootDirectory, String subDirectory, FileFilter fileFilter) throws IOException {
		File directory = rootDirectory;
		if (subDirectory != null)
			directory = new File(rootDirectory, subDirectory);
		if (!directory.getCanonicalPath().startsWith(rootDirectory.getCanonicalPath()))
			throw new IOException("Requested directory " + subDirectory + " is out of root directory");
		return getServerFiles(directory, fileFilter);
	}

	/**
	 * @param rootDirectory
	 * @param fileFilter Can be null to list all files
	 * @return
	 */
	public static List<ServerFile> getServerFiles(File rootDirectory, FileFilter fileFilter) {
		if (!rootDirectory.exists()) {
			LOG.warn("Root directory " + rootDirectory + " not found!");
			return EMPTY_LIST;
		}
		if (!rootDirectory.canRead()) {
			LOG.warn("Requesting server files from non readable directory: " + rootDirectory);
			return EMPTY_LIST;
		}
		if (!rootDirectory.isDirectory()) {
			LOG.warn("Requesting server files from non-directory: " + rootDirectory);
			return EMPTY_LIST;
		}

		List<ServerFile> serverFiles = EMPTY_LIST;

		for (File file : rootDirectory.listFiles(fileFilter)) {
			LOG.debug("Found: " + file);
			if (serverFiles == EMPTY_LIST)
				serverFiles = new ArrayList<ServerFile>();

			serverFiles.add(new ServerFile(file));
		}

		if (serverFiles != null) {
			Collections.sort(serverFiles, new Comparator<ServerFile>() {
				@Override
				public int compare(ServerFile arg0, ServerFile arg1) {
					if (arg0.file.isDirectory() && arg1.file.isDirectory())
						return NaturalOrderComparator.naturalCompare(arg0.fileName.toLowerCase(), arg1.fileName.toLowerCase());
					if (arg0.file.isDirectory())
						return -1;
					if (arg1.file.isDirectory())
						return 1;
					return NaturalOrderComparator.naturalCompare(arg0.fileName.toLowerCase(), arg1.fileName.toLowerCase());
				}
			});
		}
		return serverFiles;
	}

	public static ServerFile getServerFile(File rootDirectory, String subDirectory, String pathToFile) throws IOException {
		File directory = rootDirectory;
		if (subDirectory != null)
			directory = new File(rootDirectory, subDirectory);
		if (!directory.getCanonicalPath().startsWith(rootDirectory.getCanonicalPath()))
			throw new IOException("Requested directory " + subDirectory + " is out of root directory");
		return getServerFile(directory, pathToFile);
	}

	/**
	 * Get reference to server hosted file
	 * 
	 * @param rootDirectory Root directory where files are stored
	 * @param pathToFile suppath to file under root directory
	 * @return
	 * @throws IOException
	 */
	public static ServerFile getServerFile(File rootDirectory, String pathToFile) throws IOException {
		if (rootDirectory == null)
			throw new IOException("Root directory argument cannot be null");
		if (!rootDirectory.exists())
			throw new FileNotFoundException("Root directory does not exist");

		File file = new File(rootDirectory.getAbsolutePath() + File.separatorChar + pathToFile);
		if (!file.getCanonicalPath().startsWith(rootDirectory.getCanonicalPath())) {
			LOG.warn("Requested file " + pathToFile + " is out of root directory");
			throw new IOException("Requested file " + pathToFile + " is out of root directory");
		}
		if (!file.exists()) {
			LOG.info("File '" + file.getAbsolutePath() + "' does not exist");
			throw new FileNotFoundException("File '" + file.getAbsolutePath() + "' does not exist");
		}
		if (!file.canRead()) {
			LOG.info("Cannot read '" + file.getAbsolutePath() + "'");
			throw new IOException("Cannot read '" + file.getAbsolutePath() + "'");
		}

		return new ServerFile(file);
	}

	public static void removeServerFile(File rootDirectory, String subDirectory, String pathToFile) throws IOException {
		File directory = rootDirectory;
		if (subDirectory != null)
			directory = new File(rootDirectory, subDirectory);
		if (!directory.getCanonicalPath().startsWith(rootDirectory.getCanonicalPath()))
			throw new IOException("Requested directory " + subDirectory + " is out of root directory");
		removeServerFile(directory, pathToFile);
	}

	/**
	 * Get reference to server hosted file
	 * 
	 * @param rootDirectory Root directory where files are stored
	 * @param pathToFile suppath to file under root directory
	 * @return
	 * @throws IOException
	 */
	public static void removeServerFile(File rootDirectory, String pathToFile) throws IOException {
		if (rootDirectory == null)
			throw new IOException("Root directory argument cannot be null");
		if (!rootDirectory.exists())
			throw new FileNotFoundException("Root directory does not exist");

		File file = new File(rootDirectory.getAbsolutePath() + File.separatorChar + pathToFile);
		if (!file.getCanonicalPath().startsWith(rootDirectory.getCanonicalPath())) {
			LOG.warn("Requested file " + pathToFile + " is out of root directory");
			throw new IOException("Requested file " + pathToFile + " is out of root directory");
		}
		if (!file.exists()) {
			LOG.info("File '" + file.getAbsolutePath() + "' does not exist");
			throw new FileNotFoundException("File '" + file.getAbsolutePath() + "' does not exist");
		}
		if (file.delete()) {
			LOG.info("Server file deleted '" + file.getAbsolutePath() + "'");
		} else {
			LOG.info("Cannot read '" + file.getAbsolutePath() + "'");
			throw new IOException("Cannot read '" + file.getAbsolutePath() + "'");
		}
	}

	/**
	 * Update a file in root directory
	 * 
	 * @param rootDirectory Root directory
	 * @param directory Sub directory under root
	 * @param fileName Name of file to update (destination file name)
	 * @param uploadedFile Source file
	 * @throws IOException
	 */
	public static void updateFile(File rootDirectory, String subDirectory, String fileName, File uploadedFile) throws IOException {
		File directory = new File(rootDirectory.getAbsolutePath() + File.separatorChar + (subDirectory == null ? "" : subDirectory));
		if (!directory.getCanonicalPath().startsWith(rootDirectory.getCanonicalPath()))
			throw new IOException("Requested directory " + subDirectory + " is out of root directory");
		updateFile(directory, fileName, uploadedFile);
	}

	/**
	 * @param directory
	 * @param fileName2
	 * @param uploadedFile
	 * @throws IOException
	 */
	private static void updateFile(File directory, String fileName, File uploadedFile) throws IOException {
		LOG.info("Uploading " + fileName + " to " + directory.getCanonicalPath());
		File destination = new File(directory, fileName);
		LOG.debug("Destination " + destination.getCanonicalPath());
		if (!destination.getCanonicalPath().startsWith(directory.getCanonicalPath()))
			throw new IOException("Requested file " + fileName + " is out of jailed directory");

		if (!directory.exists()) {
			LOG.info("Root directory " + directory.getCanonicalPath() + " does not exist. Creating now.");
			directory.mkdirs();
		}

		if (destination.exists())
			LOG.info("Overwriting " + destination.getName());
		FileUtils.copyFile(uploadedFile, destination);
	}

	/**
	 * Paged listing of files in particular folder
	 * 
	 * @param accessionImageDirectory
	 * @param shortName
	 * @param startAt
	 * @param maxRecords
	 * @return
	 * @throws IOException 
	 */
	public static PagedResult<ServerFile> getServerFiles(File rootDirectory, String subDirectory, int startAt, int maxRecords) throws IOException {
		List<ServerFile> serverFiles = getServerFiles(rootDirectory, subDirectory);
		PagedResult<ServerFile> paged=new PagedResult<ServerFile>(startAt, maxRecords);
		paged.setTotalHits(serverFiles.size());
		
		// remove entries before startAt
		for (int i=0; i<startAt; i++)
			serverFiles.remove(0);
		
		// keep only maxrecords
		for (int i=serverFiles.size()-1; i>=maxRecords; i--)
			serverFiles.remove(maxRecords);
		
		paged.setResults(serverFiles);
		return paged;
	}
}
