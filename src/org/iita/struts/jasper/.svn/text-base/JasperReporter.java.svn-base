package org.iita.struts.jasper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Locale;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRCsvExporterParameter;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXmlExporter;
import net.sf.jasperreports.engine.util.JRLoader;

import org.apache.struts2.views.jasperreports.JasperReportConstants;
import org.apache.struts2.views.jasperreports.ValueStackDataSource;
import org.apache.struts2.views.jasperreports.ValueStackShadowMap;

import com.opensymphony.xwork2.util.OgnlValueStack;
import com.opensymphony.xwork2.util.ValueStack;

public class JasperReporter implements JasperReportConstants {

	protected String format;
	protected String delimiter;
	protected String timeZone;

	/**
	 * Names a report parameters map stack value, allowing additional report parameters from the action.
	 */
	protected String reportParameters;

	/**
	 * Names an exporter parameters map stack value, allowing the use of custom export parameters.
	 */
	protected String exportParameters;
	private JasperReport jasperReport;
	private String systemId;

	/**
	 * Default ctor.
	 */
	public JasperReporter() {

	}

	public void setFormat(String format) {
		this.format = format;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	/**
	 * set time zone id
	 * 
	 * @param timeZone
	 */
	public void setTimeZone(final String timeZone) {
		this.timeZone = timeZone;
	}

	public String getReportParameters() {
		return reportParameters;
	}

	public void setReportParameters(String reportParameters) {
		this.reportParameters = reportParameters;
	}

	public String getExportParameters() {
		return exportParameters;
	}

	public void setExportParameters(String exportParameters) {
		this.exportParameters = exportParameters;
	}

	public void prepare(String fullPathToJasperFile) throws ReportingException {
		this.systemId = fullPathToJasperFile;

		// put timezone in jasper report parameter
		/*
		 * if (timeZone != null) { timeZone = conditionalParse(timeZone, invocation); final TimeZone tz = TimeZone.getTimeZone(timeZone); if (tz != null) { //
		 * put the report time zone parameters.put(JRParameter.REPORT_TIME_ZONE, tz); } }
		 */

		// Fill the report and produce a print object
		try {
			jasperReport = (JasperReport) JRLoader.loadObject(this.systemId);
		} catch (JRException e) {
			throw new ReportingException(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public byte[] execute(Object data, Locale locale) throws ReportingException {
		// Construct the data source for the report.
		ValueStack stack = new OgnlValueStack();
		stack.set("JASPERDATA", data);
		ValueStackDataSource stackDataSource = new ValueStackDataSource(stack, "JASPERDATA");

		Map parameters = new ValueStackShadowMap(stack);
		File directory = new File(systemId.substring(0, systemId.lastIndexOf(File.separator)));
		parameters.put("reportDirectory", directory.getAbsolutePath() + File.separator);
		if (locale!=null)
			parameters.put(JRParameter.REPORT_LOCALE, locale);

		// Add any report parameters from action to param map.
		Map reportParams = (Map) stack.findValue(reportParameters);
		if (reportParams != null) {
			parameters.putAll(reportParams);
		}

		byte[] output;
		JasperPrint jasperPrint;

		// Fill the report and produce a print object
		try {
			jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, stackDataSource);
		} catch (JRException e) {
			throw new ReportingException(e.getMessage(), e);
		}
		// Export the print object to the desired output format
		try {

			JRExporter exporter;

			if (format.equals(FORMAT_PDF)) {
				exporter = new JRPdfExporter();
			} else if (format.equals(FORMAT_CSV)) {
				exporter = new JRCsvExporter();
			} else if (format.equals(FORMAT_HTML)) {
				// IMAGES_MAPS seems to be only supported as
				// "backward compatible" from JasperReports 1.1.0

				exporter = new JRHtmlExporter();

				// Needed to support chart images:
				exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			} else if (format.equals(FORMAT_XLS)) {
				exporter = new JRXlsExporter();
			} else if (format.equals(FORMAT_XML)) {
				exporter = new JRXmlExporter();
			} else if (format.equals(FORMAT_RTF)) {
				exporter = new JRRtfExporter();
			} else {
				throw new ReportingException("Unknown report format: " + format);
			}

			Map exportParams = (Map) stack.findValue(exportParameters);
			if (exportParams != null) {
				exporter.getParameters().putAll(exportParams);
			}

			output = exportReportToBytes(jasperPrint, exporter);
			return output;
		} catch (JRException e) {
			throw new ReportingException("Error producing " + format + " report for uri " + systemId, e);
		}
	}

	/**
	 * Run a Jasper report to CSV format and put the results in a byte array
	 * 
	 * @param jasperPrint The Print object to render as CSV
	 * @param exporter The exporter to use to export the report
	 * @return A CSV formatted report
	 * @throws net.sf.jasperreports.engine.JRException If there is a problem running the report
	 */
	private byte[] exportReportToBytes(JasperPrint jasperPrint, JRExporter exporter) throws JRException {
		byte[] output;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
		if (delimiter != null) {
			exporter.setParameter(JRCsvExporterParameter.FIELD_DELIMITER, delimiter);
		}

		exporter.exportReport();

		output = baos.toByteArray();

		return output;
	}

}
