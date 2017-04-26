/**
 * iita-common-web.struts Sep 22, 2009
 */
package org.iita.struts.jasper;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author mobreza
 * 
 */
public class ReportCompiler {
	private static final Log LOG = LogFactory.getLog(ReportCompiler.class);

	public static synchronized void checkAndCompileReports(File sourceDir) throws JRException, FileNotFoundException, ReportingException {
		// Normally we would provide a pre-compiled .jrxml file
		// or check to make sure we don't compile on every request.
		if (!sourceDir.exists()) {
			LOG.error("Jasper templates directory " + sourceDir.getAbsolutePath() + " does not exist!");
			throw new FileNotFoundException("Jasper templates directory " + sourceDir.getAbsolutePath() + " does not exist!");
		}

		File[] jrxmlFiles = sourceDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File arg0) {
				return arg0.getName().endsWith(".jrxml");
			}
		});
		for (File jrxmlFile : jrxmlFiles) {
			String fullPath = jrxmlFile.getAbsolutePath();
			String compiledName = fullPath.substring(0, fullPath.length() - 6) + ".jasper";

			boolean recompile = true;

			File jasperFile = new File(compiledName);
			if (jasperFile.exists()) {
				if (jrxmlFile.lastModified() > jasperFile.lastModified()) {
					LOG.info("Need to recompile: " + jrxmlFile.getName());
				} else {
					recompile = false;
				}
			}
			if (recompile) {
				LOG.info("Building: " + fullPath + " ->> " + compiledName);
				try {
					JasperCompileManager.compileReportToFile(fullPath, compiledName);
				} catch (Exception e) {
					throw new ReportingException("Error compiling " + fullPath + ". " + e.getMessage(), e);
				}
				jasperFile.setLastModified(jrxmlFile.lastModified());
				LOG.info("Jasper: " + jasperFile.getName() + "  " + jasperFile.lastModified() + " " + jrxmlFile.lastModified());
			}
		}
	}
}
