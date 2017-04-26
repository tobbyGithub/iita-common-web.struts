/*
 * 
 */
package org.iita.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.iita.service.ExportService;
import org.iita.util.DeleteFileAfterCloseInputStream;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

/**
 * XLS exporter will use Apache POI to generate Excel files with provided data. 
 */
public class XLSExportService implements ExportService {

	/** The Constant EMTPY_ARRAY. */
	private static final Object[] EMTPY_ARRAY = new Object[] {};

	/** The Constant LOG. */
	private static final Log LOG = LogFactory.getLog(XLSExportService.class);

	/** The Constant FONT_BOLD. */
	private static final int FONT_BOLD = 0;

	/** The Constant VERTICAL_BOTTOM. */
	private static final int VERTICAL_BOTTOM = 0;

	/** The Constant TITLE_STYLE. */
	private static final int TITLE_STYLE = 1;

	/** The Constant MONEY_STYLE. */
	private static final int MONEY_STYLE = 2;

	/** The Constant DATE_STYLE. */
	private static final int DATE_STYLE = 3;

	/**
	 * Export to stream.
	 * 
	 * @param template the template
	 * @param headings the headings
	 * @param properties the properties
	 * @param data the data
	 * 
	 * @return the input stream
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 * 
	 * @see org.iita.service.ExportService#exportToStream(java.io.File, java.lang.String[], java.lang.String[], java.util.List)
	 */
	@Override
	public InputStream exportToStream(File template, String[] headings, String[] properties, List<? extends Object> data) throws IOException {
		return exportToStream(new FileInputStream(template), headings, properties, data);
	}

	/**
	 * Export to stream.
	 * 
	 * @param inputStream the input stream
	 * @param headings the headings
	 * @param properties the properties
	 * @param data the data
	 * 
	 * @return the input stream
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public InputStream exportToStream(InputStream inputStream, String[] headings, String[] properties, List<? extends Object> data) throws IOException {
		HSSFWorkbook wb = new HSSFWorkbook(inputStream);
		HSSFSheet sheet = wb.getSheetAt(0);

		return internalExport(wb, sheet, headings, properties, data);
	}

	/**
	 * Export to stream.
	 * 
	 * @param headings the headings
	 * @param properties the properties
	 * @param data the data
	 * 
	 * @return the input stream
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 * 
	 * @see org.iita.service.ExportService#exportToStream(java.lang.String[], java.lang.String[], java.util.List)
	 */
	@Override
	public InputStream exportToStream(String[] headings, String[] properties, List<? extends Object> data) throws IOException {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("Data export");

		return internalExport(wb, sheet, headings, properties, data);
	}

	/**
	 * Export to stream.
	 * 
	 * @param data the data
	 * @param headings the headings
	 * 
	 * @return the input stream
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public InputStream exportToStream(String[] headings, List<Object[]> data) throws IOException {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("Data export");

		return internalExport(wb, sheet, headings, data);
	}

	/**
	 * Export to stream.
	 * 
	 * @param templateStream the template stream
	 * @param data the data
	 * @param headings the headings
	 * 
	 * @return the input stream
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public InputStream exportToStream(InputStream templateStream, String[] headings, List<Object[]> data) throws IOException {
		HSSFWorkbook wb = new HSSFWorkbook(templateStream);
		HSSFSheet sheet = wb.getSheetAt(0);

		return internalExport(wb, sheet, headings, data);
	}

	/**
	 * Internal export.
	 * 
	 * @param wb the wb
	 * @param sheet the sheet
	 * @param headings the headings
	 * @param properties the properties
	 * @param data the data
	 * 
	 * @return the input stream
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private InputStream internalExport(HSSFWorkbook wb, HSSFSheet sheet, String[] headings, String[] properties, List<? extends Object> data)
			throws IOException {
		HSSFCellStyle[] styles = createDefaultStyles(wb);

		int rowIndex = 0;
		HSSFRow row = sheet.createRow(rowIndex++);
		fillRow(row, headings, styles[VERTICAL_BOTTOM], styles);

		if (data.size() > 0) {
			Object[] ognlExpressions = getExpressions(properties);

			OgnlContext ognlContext = new OgnlContext();

			for (Object d : data) {
				fillRow(sheet.createRow(rowIndex++), getData(ognlExpressions, ognlContext, d), null, styles);
			}
		}

		File file = File.createTempFile("export", "xls");
		FileOutputStream fs = new FileOutputStream(file);
		wb.write(fs);
		fs.flush();
		fs.close();
		return new DeleteFileAfterCloseInputStream(file);
	}

	/**
	 * Internal export.
	 * 
	 * @param wb the wb
	 * @param sheet the sheet
	 * @param headings the headings
	 * @param data the data
	 * 
	 * @return the input stream
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private InputStream internalExport(HSSFWorkbook wb, HSSFSheet sheet, String[] headings, List<Object[]> data) throws IOException {
		HSSFCellStyle[] styles = createDefaultStyles(wb);

		int rowIndex = 0;
		HSSFRow row = null;
		
		LOG.debug("Last row number in sheet: " + sheet.getLastRowNum() + "  and the first one: " + sheet.getFirstRowNum());
		if (sheet.getLastRowNum() == 0)
			row = sheet.getRow(rowIndex++);
		else
			row = sheet.createRow(rowIndex++);
		
		fillRow(row, headings, styles[VERTICAL_BOTTOM], styles);

		if (data.size() > 0) {
			for (Object[] d : data) {
				fillRow(sheet.createRow(rowIndex++), d, null, styles);
			}
		}

		File file = File.createTempFile("export", "xls");
		FileOutputStream fs = new FileOutputStream(file);
		wb.write(fs);
		fs.flush();
		fs.close();
		return new DeleteFileAfterCloseInputStream(file);
	}

	/**
	 * Gets the expressions.
	 * 
	 * @param properties the properties
	 * 
	 * @return the expressions
	 */
	private static Object[] getExpressions(String[] properties) {
		Object[] expressions = new Object[properties.length];
		for (int i = 0; i < properties.length; i++) {
			try {
				if (properties[i] != null)
					expressions[i] = Ognl.parseExpression(properties[i]);
			} catch (OgnlException e) {
				LOG.error(e);
			}
		}
		return expressions;
	}

	/**
	 * Gets the data.
	 * 
	 * @param ognlExpressions the ognl expressions
	 * @param context the context
	 * @param d2 the d2
	 * 
	 * @return the data
	 */
	private static Object[] getData(Object[] ognlExpressions, OgnlContext context, Object d2) {
		if (d2 == null)
			return EMTPY_ARRAY;
		Object[] results = new Object[ognlExpressions.length];
		for (int i = 0; i < results.length; i++) {
			results[i] = null;
			try {
				if (ognlExpressions[i] != null)
					results[i] = Ognl.getValue(ognlExpressions[i], context, d2);
			} catch (OgnlException e) {
				// LOG.error(e);
			}
		}

		return results;
	}

	/**
	 * Fill row.
	 * 
	 * @param row the row
	 * @param objects the objects
	 * @param cellStyle the cell style
	 * @param allStyles the all styles
	 */
	private static void fillRow(HSSFRow row, Object[] objects, HSSFCellStyle cellStyle, HSSFCellStyle[] allStyles) {
		if (objects == null)
			return;

		for (int i = 0; i < objects.length; i++) {
			Object o = objects[i];
			HSSFCell cell = row.createCell(i);
			if (cellStyle != null)
				cell.setCellStyle(cellStyle);

			fillCell(cell, o, allStyles);
		}
	}

	private static void fillCell(HSSFCell cell, Object o, HSSFCellStyle[] allStyles) {
		if (o == null) {
			// null value, remain blank
		} else if (o instanceof String) {
			String val = (String) o;
			if(val.startsWith("http://localhost:8082") || val.startsWith("http://cdo.iita.org/training/announcement/document-download.jspx?id=")){	
				try {
					File file = new File( "htmlTags.html" );
	
				    // if file doesnt exists, then create it 
				    if ( ! file.exists( ) )
				    {
				        file.createNewFile( );
				    }
	
				    FileWriter fw = new FileWriter( file.getAbsoluteFile( ) );
				    BufferedWriter bw = new BufferedWriter( fw );
				    bw.write( val );
				    bw.close( );
				    
				    Tika tika = new Tika();
				    String filecontent;
					try {
						filecontent = tika.parseToString(file);
						cell.setCellValue(new HSSFRichTextString(filecontent.toString()));
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (TikaException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} //the true will append the new data
			}else{
				cell.setCellValue(new HSSFRichTextString(val));
			}
		} else if (o instanceof Long) {
			cell.setCellValue((Long) o);
		} else if (o instanceof Integer) {
			cell.setCellValue((Integer) o);
		} else if (o instanceof Double) {
			cell.setCellValue((Double) o);
		} else if (o instanceof Float) {
			cell.setCellValue((Float) o);
		} else if (o instanceof Date) {
			cell.setCellValue((Date) o);
			if (allStyles!=null) cell.setCellStyle(allStyles[DATE_STYLE]);
		} else if (o instanceof Timestamp) {
			cell.setCellValue((Date) o);
			if (allStyles!=null) cell.setCellStyle(allStyles[DATE_STYLE]);
		} else if (o instanceof Calendar) {
			cell.setCellValue((Calendar) o);
			if (allStyles!=null) cell.setCellStyle(allStyles[DATE_STYLE]);
		} else if (o instanceof Boolean) {
			cell.setCellValue((Boolean) o);
		} else if (o instanceof Enum<?>) {
			cell.setCellValue(new HSSFRichTextString(((Enum<?>) o).name()));
		} else if (o instanceof BigInteger) {
			cell.setCellValue(((BigInteger) o).longValue());
		} else {
			cell.setCellValue(o.toString());
			//LOG.warn("Unmanaged: " + o.getClass());
		}
	}

	/**
	 * Fill sheet.
	 * 
	 * @param sheet the sheet
	 * @param headings the headings
	 * @param data the data
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void fillSheet(HSSFSheet sheet, String[] headings, List<Object[]> data) throws IOException {
		HSSFWorkbook wb = sheet.getWorkbook();
		HSSFCellStyle[] styles = createDefaultStyles(wb);

		int rowIndex = 0;
		HSSFRow row = sheet.createRow(rowIndex++);
		fillRow(row, headings, styles[VERTICAL_BOTTOM], styles);

		if (data!=null && data.size() > 0) {
			for (Object[] d : data) {
				fillRow(sheet.createRow(rowIndex++), d, null, styles);
				if (rowIndex % 100 == 0) {
					LOG.debug("Inserted " + rowIndex + " rows to sheet");
				}
			}
		}
	}

	
	/**
	 * Creates the default styles.
	 * 
	 * @param wb the workbook
	 * 
	 * @return the hSSF cell style[]
	 */
	private static HSSFCellStyle[] createDefaultStyles(HSSFWorkbook wb) {
		HSSFDataFormat format = wb.createDataFormat();

		HSSFFont[] fonts = new HSSFFont[1];
		fonts[FONT_BOLD] = wb.createFont();
		fonts[FONT_BOLD].setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		fonts[FONT_BOLD].setFontHeightInPoints((short) 12);

		HSSFCellStyle[] styles = new HSSFCellStyle[4];
		styles[VERTICAL_BOTTOM] = wb.createCellStyle();
		styles[VERTICAL_BOTTOM].setWrapText(true);
		styles[VERTICAL_BOTTOM].setVerticalAlignment(HSSFCellStyle.VERTICAL_BOTTOM);
		styles[VERTICAL_BOTTOM].setBorderBottom(HSSFCellStyle.BORDER_THIN);
		styles[VERTICAL_BOTTOM].setFont(fonts[FONT_BOLD]);

		styles[TITLE_STYLE] = wb.createCellStyle();
		styles[TITLE_STYLE].setFont(fonts[FONT_BOLD]);

		styles[MONEY_STYLE] = wb.createCellStyle();
		styles[MONEY_STYLE].setDataFormat(format.getFormat("#,###.00 $"));

		styles[DATE_STYLE] = wb.createCellStyle();
		styles[DATE_STYLE].setDataFormat(format.getFormat("d/M/yyyy"));
		return styles;
	}

	/**
	 * Append to sheet.
	 * 
	 * @param sheet the sheet
	 * @param data the data
	 */
	public static void appendToSheet(HSSFSheet sheet, List<Object[]> data) {
		HSSFWorkbook wb = sheet.getWorkbook();
		HSSFCellStyle[] styles = createDefaultStyles(wb);
		
		int rowIndex=sheet.getLastRowNum();
		if (rowIndex==0)
			rowIndex=sheet.getPhysicalNumberOfRows()-1;
		if (data!=null && data.size() > 0) {
			for (Object[] d : data) {
				fillRow(sheet.createRow(++rowIndex), d, null, styles);
				if (rowIndex % 100 == 0) {
					LOG.debug("Inserted " + rowIndex + " rows to sheet");
				}
			}
		}
	}

	/**
	 * @param seedSheet
	 * @param traitHeadings
	 * @param string
	 * @param traitLastValuesWide
	 */
	public static void expandSheet(HSSFSheet sheet, String[] traitHeadings, String lookupColumn, Hashtable<Long, Object[]> traitLastValuesWide) {
		// find lookup column
		HSSFRow headerRow = sheet.getRow(0);
		int lookupCellIndex=-1, expandPosition=headerRow.getLastCellNum();
		for (int i=0; i<headerRow.getLastCellNum(); i++) {
			//LOG.debug("Header: " + headerRow.getCell(i).getStringCellValue());
			if (headerRow.getCell(i).getStringCellValue().equalsIgnoreCase(lookupColumn)) {
				lookupCellIndex=i;
			}
		}
		
		HSSFCellStyle[] styles = createDefaultStyles(sheet.getWorkbook());
		
		// add columns to header
		if (traitHeadings!=null)
			for (int i=0; i<traitHeadings.length; i++) {
				LOG.debug("Adding header " + traitHeadings[i] + " at " + (expandPosition + i));
				HSSFCell cell = headerRow.createCell(expandPosition+i);
				cell.setCellValue(traitHeadings[i]);
				cell.setCellStyle(styles[VERTICAL_BOTTOM]);
			}
				
		if (traitLastValuesWide==null || traitLastValuesWide.size()==0) {
			LOG.info("No data sent in to expand. Exiting");
			return;
		}
		
		if (traitHeadings==null)
			expandPosition=headerRow.getLastCellNum() - traitLastValuesWide.values().iterator().next().length;
		
		for (int i=1; i<=sheet.getLastRowNum(); i++) {
			// fetch ID
			HSSFRow row = sheet.getRow(i);
			long identifier=(long) row.getCell(lookupCellIndex).getNumericCellValue();
			//LOG.debug("Row " + i + " is for " + lookupColumn + "=" + identifier);
			Object[] extras = traitLastValuesWide.get(identifier);
			// extend row
			if (extras!=null && extras.length>0) {
				LOG.debug("Found extended information for " + identifier);
				for (int j=0; j<extras.length; j++) {
					HSSFCell cell = row.createCell(expandPosition+j);
					fillCell(cell, extras[j], styles);
				}
			}
		}
	}

	public static List<Object[]> convertData(List<?> data, String[] properties) {
		List<Object[]> converted=new ArrayList<Object[]>();
		if (data.size() > 0) {
			Object[] ognlExpressions = getExpressions(properties);

			OgnlContext ognlContext = new OgnlContext();

			for (Object d : data) {
				Object[] convertedObject = getData(ognlExpressions, ognlContext, d);
				if (convertedObject!=null)
					converted.add(convertedObject);
			}
		}
		return converted;
	}

}
