/**
 * iita-common-web.struts Dec 15, 2009
 */
package org.iita.service;

import java.util.Hashtable;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.iita.service.impl.XLSImportException;

/**
 * @author mobreza
 * 
 */
public interface XLSDataImportService {

	/**
	 * @param targetEntity
	 * @param mappings
	 * @param sheetAt
	 * @return
	 * @throws XLSImportException
	 */
	List<?> getObjectsFromXLS(Class<?> targetEntity, Hashtable<String, String> mappings, HSSFSheet sheetAt) throws XLSImportException;

	/**
	 * @param targetEntity
	 * @param mappings
	 * @param xlsSheet
	 * @return
	 * @throws XLSImportException
	 */
	List<?> persistObjectsFromXLS(Class<?> targetEntity, Hashtable<String, String> mappings, HSSFSheet xlsSheet) throws XLSImportException;

	/**
	 * Generate List of <code>Object[]</code>s containing the data from XLS
	 * 
	 * @param mappings
	 * @param sheetAt
	 * @return
	 */
	List<Object[]> getObjectsFromXLS(HSSFSheet sheetAt);

	List<Object[]> getObjectsFromXLS(HSSFSheet xlsSheet, int firstRow);
}
