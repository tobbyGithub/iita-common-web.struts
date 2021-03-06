/**
 * iita-common-web.struts Sep 17, 2010
 */
package org.iita.service.impl;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Class to convert HTML tables to Excel
 * 
 * @author mobreza
 */
public class Html2Excel implements Runnable {
	private static final Log LOG = LogFactory.getLog(Html2Excel.class);
	private HSSFSheet sheet;
	private InputStream inputStream;

	/**
	 * @param sheet
	 */
	public Html2Excel(HSSFSheet sheet, InputStream inputStream) {
		this.sheet = sheet;
		this.inputStream = inputStream;//.toString().replaceAll("(&(?!amp;))", "&amp;").trim();
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			parser.getXMLReader().setFeature("http://xml.org/sax/features/validation", false);
			parser.getXMLReader().setFeature("http://xml.org/sax/features/use-entity-resolver2", false);
			
			parser.parse(this.inputStream, new DefaultHandler() {
				private StringBuffer textbuffer = new StringBuffer();
				@SuppressWarnings("unused")
				private boolean inCell = false, inHeader = false, inFooter = false;
				private int rownum = 0, colnum = 0;
				private HSSFRow row;
				private HSSFCell cell;

				@Override
				public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
					LOG.info("Start element: " + localName + " " + qName);
					//qName = qName.toString().replaceAll("(&(?!amp;))", "&amp;").trim();
					
					if ("table".equalsIgnoreCase(qName)) {
						LOG.info("Starting new Excel table!");
					} else if ("col".equalsIgnoreCase(qName)) {
						LOG.info("Cell width definition: " + attributes.getValue("width"));
					} else if ("thead".equals(qName)) {
						LOG.info("Starting table header");
						this.inHeader = true;
					} else if ("tbody".equals(qName)) {
						LOG.info("Starting table body");
					} else if ("tfoot".equals(qName)) {
						LOG.info("Starting table footer");
						this.inFooter = true;
					} else if ("tr".equals(qName)) {
						this.row = sheet.createRow(this.rownum++);
						this.colnum = 0;
						LOG.info("Starting row");
					} else if ("td".equals(qName)) {
						LOG.info("Starting cell");
						textbuffer.replace(0, textbuffer.length(), "");
						this.inCell = true;
						this.cell = this.row.createCell(this.colnum++);
					}
				}

				@Override
				public void endElement(String uri, String localName, String qName) throws SAXException {
					//qName = qName.toString().replaceAll("(&(?!amp;))", "&amp;").trim();
					if ("table".equalsIgnoreCase(qName)) {
						LOG.info("Closing new Excel table!");
					} else if ("thead".equals(qName)) {
						LOG.info("Closing table header");
						this.inHeader = false;
					} else if ("tbody".equals(qName)) {
						LOG.info("Closing table body");
					} else if ("tfoot".equals(qName)) {
						LOG.info("Closing table footer");
						this.inFooter = false;
					} else if ("tr".equals(qName)) {
						LOG.info("Closing row");
					} else if ("td".equals(qName)) {
						LOG.info("Closing cell: " + this.textbuffer.toString());
						this.cell.setCellValue(this.textbuffer.toString());
						this.inCell = false;
					}
				}

				@Override
				public void characters(char[] ch, int start, int length) throws SAXException {
					if (inCell)
						textbuffer.append(ch, start, length);//.toString().replaceAll("&", "&amp;");
				}
			});
			this.inputStream.close();
			LOG.info("Done converting HTML to Excel");
		} catch (ParserConfigurationException e) {
			LOG.error(e, e);
		} catch (SAXException e) {
			LOG.error(e, e);
		} catch (IOException e) {
			LOG.error(e, e);
		}
	}

}
