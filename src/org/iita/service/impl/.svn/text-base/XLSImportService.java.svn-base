/*
 * 
 */
package org.iita.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.iita.service.DataImportService;
import org.iita.service.XLSDataImportService;
import org.iita.util.StringUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

/**
 * XLS import service allows users to manage their data in XLS files and then update application database with updated information. This service will generate
 * required objects.
 * 
 * @author mobreza
 */
public class XLSImportService implements DataImportService, XLSDataImportService {

	/**
	 * 
	 */
	private static final Class<?>[] NOARGS = new Class<?>[] {};

	/** The LOG. */
	private static Log LOG = LogFactory.getLog(XLSImportService.class);

	/** The entity manager. */
	private EntityManager entityManager;

	/**
	 * Sets the entity manager.
	 * 
	 * @param entityManager the new entity manager
	 */
	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/**
	 * @see org.iita.service.DataImportService#importFromStream(java.io.InputStream, java.lang.Class, java.lang.String[])
	 */
	@Transactional()
	public List<?> importFromStream(InputStream inputStream, Class<?> clazz, final String[] expressions) throws IOException, XLSImportException {
		return fromStream(inputStream, clazz, expressions);
	}

	/**
	 * Test from stream.
	 * 
	 * @param inputStream the input stream
	 * @param clazz the clazz
	 * @param expressions the expressions
	 * 
	 * @return the list<?>
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws XLSImportException the XLS import exception
	 */
	@Transactional()
	public List<?> testFromStream(InputStream inputStream, Class<?> clazz, final String[] expressions) throws IOException, XLSImportException {
		return fromStream(inputStream, clazz, expressions);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.iita.projecttask.service.impl.DataImportService#importFromStream(java.io.InputStream, java.lang.Class, java.lang.String[])
	 */
	/**
	 * From stream.
	 * 
	 * @param inputStream the input stream
	 * @param clazz the clazz
	 * @param expressions the expressions
	 * 
	 * @return the list<?>
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws XLSImportException the XLS import exception
	 */
	private List<?> fromStream(InputStream inputStream, Class<?> clazz, final String[] expressions) throws IOException, XLSImportException {
		// XLS stuff
		HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
		HSSFSheet sheet = workbook.getSheetAt(0);
		return fromXLS(sheet, clazz, expressions);
	}

	private List<?> fromXLS(HSSFSheet sheet, Class<?> clazz, final String[] expressions) throws XLSImportException {
		Object[] ognlExpressions = getExpressions(expressions);
		if (ognlExpressions.length == 0) {
			LOG.warn("No OGNL expressions given to import data");
			return null;
		}

		// figure out object identifier type
		Class<?> identifierType = findIdentifierType(clazz);
		if (identifierType == null) {
			LOG.warn("Could not find identifier type for class " + clazz.getName());
			throw new XLSImportException("Could not find identifier type for class " + clazz.getName());
		}

		Integer identifierColumn = findIdentifierColumn(clazz, expressions);

		// OGNL
		OgnlContext ognlContext = new OgnlContext();
		Ognl.setTypeConverter(ognlContext, new ognl.DefaultTypeConverter() {
			private java.util.Hashtable<Class<?>, Class<?>> identifierTypes = new java.util.Hashtable<Class<?>, Class<?>>();

			@SuppressWarnings("unchecked")
			@Override
			public Object convertValue(@SuppressWarnings("rawtypes") Map context, Object value, @SuppressWarnings("rawtypes") Class toType) {
				LOG.debug("Converting: " + value + " to " + toType);
				HSSFCell cell = (HSSFCell) value;
				if (toType == java.util.Date.class) {
					return cell.getDateCellValue();
				}
				if (toType.getAnnotation(Entity.class) != null) {
					LOG.debug("Looking up identifier type for " + toType);
					Class<?> identifierType = identifierTypes.get(toType);
					if (identifierType == null) {
						identifierType = findIdentifierType(toType);
						if (identifierType==null)
							// default to Long
							identifierType=Long.class;
						identifierTypes.put(toType, identifierType);
					}
					try {
						LOG.debug("Convertnig cell to " + identifierType);
						Object convertedId = convertToType((HSSFCell) value, identifierType);
						LOG.debug("Loading " + toType + " ID=" + convertedId + " from DB.");
						Object r = entityManager.find(toType, convertedId);
						LOG.debug("Got " + r);
						return r;
					} catch (Exception e) {
						LOG.error("Error loading @Entity from DB. " + e);
					}
				}
				return super.convertValue(context, getCellValue(cell), toType);
			}
		});

		// resulting array of updated objects
		ArrayList<Object> updatedObjects = new ArrayList<Object>();

		// skip header row, start import from second row
		int lastRowNum = sheet.getLastRowNum();
		for (int rowIndex = 1; rowIndex <= lastRowNum; rowIndex++) {
			HSSFRow row = sheet.getRow(rowIndex);

			// skip empty rows
			if (row == null) {
				// LOG.debug("Skipping empty row " + (rowIndex + 1));
				continue;
			}

			// find object
			Object existingObject = null;

			if (identifierColumn != null) {
				// get ID
				Object identifier = null;
				HSSFCell identifierCell = row.getCell(identifierColumn);
				try {
					identifier = convertToType(identifierCell, identifierType);
				} catch (Exception e) {
					throw new XLSImportException("Cannot get ID from column " + identifierCell.getColumnIndex() + " in row " + (rowIndex + 1) + ". "
							+ e.getMessage());
				}

				// load if identifier exists
				if (identifier != null)
					existingObject = loadObject(clazz, identifier);
				if (existingObject == null) {
					LOG.warn(clazz.getName() + " with ID " + identifier + " not found in database, creating instance");
				}
			}

			if (existingObject == null) {
				// LOG.debug("Creating instance of " + clazz);
				existingObject = createObject(clazz);
			}

			if (existingObject == null) {
				LOG.error("Could not get object instance, skipping row");
				continue;
			}

			try {
				fillObject(existingObject, row, ognlExpressions, expressions, ognlContext);
				LOG.warn("Merging " + existingObject);
				// this.entityManager.merge(existingObject);
				updatedObjects.add(existingObject);
			} catch (OgnlException e) {
				LOG.error("Error filling object in row " + (rowIndex + 1) + ". " + e.getMessage());
				LOG.error(e);
			}
		}

		return updatedObjects;
	}

	/**
	 * The method looks for @Id annotation, gets OGNL expressions and returns the XLS column index
	 * 
	 * @param clazz
	 * @param expressions
	 * @return
	 */
	private Integer findIdentifierColumn(Class<?> clazz, String[] expressions) {
		Method[] methods = clazz.getMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			// LOG.trace("\t" + method.getReturnType().toString() + " " + method.getName() + "(...)");
			Id annotation = method.getAnnotation(Id.class);
			if (annotation != null) {
				LOG.debug("Found @Id annotation " + annotation);
				String ognlName = StringUtil.getOgnlName(method.getName());
				for (int j = 0; j < expressions.length; j++)
					if (expressions[j] != null && expressions[j].equals(ognlName))
						return j;
			}
		}
		// No ID mapping
		return null;
	}

	/**
	 * The method looks for @Id annotation, gets OGNL expressions and returns the XLS column index
	 * 
	 * @param clazz
	 * @param expressions
	 * @return
	 */
	private Integer findIdentifierColumn(Class<?> clazz, List<String> expressions) {
		Method[] methods = clazz.getMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			// LOG.trace("\t" + method.getReturnType().toString() + " " + method.getName() + "(...)");
			Id annotation = method.getAnnotation(Id.class);
			if (annotation != null) {
				LOG.debug("Found @Id annotation " + annotation);
				String ognlName = StringUtil.getOgnlName(method.getName());
				for (int j = 0; j < expressions.size(); j++)
					if (expressions.get(j) != null && expressions.get(j).equals(ognlName))
						return j;
			}
		}
		// No ID mapping
		return null;
	}

	/**
	 * Fill object.
	 * 
	 * @param existingObject the existing object
	 * @param row the row
	 * @param ognlExpressions the ognl expressions
	 * @param ognlContext the ognl context
	 * 
	 * @throws OgnlException the ognl exception
	 */
	private void fillObject(Object existingObject, HSSFRow row, Object[] ognlExpressions, String[] expressions, OgnlContext ognlContext) throws OgnlException {
		for (int i = 0; i < ognlExpressions.length; i++) {
			Object ognlExpression = ognlExpressions[i];

			// skip empty assignments
			if (ognlExpression == null)
				continue;

			if (ognlExpression.toString().equals("id"))
				continue;

			// TODO This is where we need to check if access to OGNL expression is valid
			// if not, a related object must be created before. This should work like this:
			// - if there are dots in OGNL expression, need to check if parent OGNL exists
			// - if parent is there, execute
			// - if parent is missing, create parent by looking it up using all available information
			// - otherwise create new instance and set it

			String expression = expressions[i];
			if (expression.indexOf('.') >= 0) {
				findOrCreateEntity(existingObject, row, ognlContext, row.getCell(i), ognlExpression, expression);
			} else {
				LOG.trace("Setting " + ognlExpression + " to " + row.getCell(i));
				Ognl.setValue(ognlExpression, ognlContext, existingObject, row.getCell(i));
			}
		}
	}

	/**
	 * This method is used when a related entity is set in an object -- for example <code>item.name</code> expression needs first to see if <code>item</code>
	 * returns an existing value, if not, this method is used to either load or create such an object.
	 * 
	 * @param existingObject
	 * @param row
	 * @param ognlContext
	 * @param i
	 * @param ognlExpression
	 * @param expression
	 * @throws OgnlException
	 */
	private void findOrCreateEntity(Object existingObject, HSSFRow row, OgnlContext ognlContext, HSSFCell cell, Object ognlExpression, String expression)
			throws OgnlException {
		String parentOgnl = expression.substring(0, expression.lastIndexOf('.'));
		// have dot in expression, check for parent
		LOG.debug("Checking parent OGNL " + parentOgnl);
		Object value = Ognl.getValue(parentOgnl, ognlContext, existingObject);
		if (value == null) {
			LOG.debug("Parent evaluates to null, need to get parent!");
		} else {
			LOG.trace("Setting " + ognlExpression + " to " + cell);
			Ognl.setValue(ognlExpression, ognlContext, existingObject, cell);
		}
	}

	/**
	 * Gets the cell value.
	 * 
	 * @param cell the cell
	 * 
	 * @return the cell value
	 */
	public static Object getCellValue(HSSFCell cell) {
		// return null value for empty cells
		if (cell == null)
			return null;

		int cellType = cell.getCellType();
		Object value = null;
		if (cellType == HSSFCell.CELL_TYPE_BLANK)
			return null;
		if (cellType == HSSFCell.CELL_TYPE_ERROR) {
			LOG.error("Cell " + cell + " at " + cell.getRowIndex() + ", " + cell.getColumnIndex() + " has an error");
			return null;
		}
		if (cellType == HSSFCell.CELL_TYPE_BOOLEAN)
			return cell.getBooleanCellValue();
		if (cellType == HSSFCell.CELL_TYPE_STRING) {
			String strvalue = cell.getRichStringCellValue().getString().trim();
			if (strvalue.length() == 0)
				return null;
			return strvalue;
		}
		if (cellType == HSSFCell.CELL_TYPE_FORMULA) {
			LOG.debug("type is formula. what now? " + cell.getCellFormula());

			return null;
		}
		String dataFormatString = cell.getCellStyle().getDataFormatString();
		if (dataFormatString.contains("YY") || dataFormatString.contains("yy")) {
			// LOG.debug("Format string: " + dataFormatString);
			return cell.getDateCellValue();
		}
		if (cellType == HSSFCell.CELL_TYPE_NUMERIC) {
			return cell.getNumericCellValue();
		}
		LOG.warn("Unknown HSSF cell type: " + cellType);
		return value;
	}

	/**
	 * Gets the expressions.
	 * 
	 * @param properties the properties
	 * 
	 * @return the expressions
	 */
	private Object[] getExpressions(String[] properties) {
		Object[] expressions = new Object[properties.length];
		for (int i = 0; i < properties.length; i++) {
			try {
				// parse existing expressions
				if (properties[i] != null && properties[i].length() > 0)
					expressions[i] = Ognl.parseExpression(properties[i]);
				LOG.debug("Parsed expression: " + properties[i]);
			} catch (OgnlException e) {
				LOG.error("Error parsing: " + properties[i]);
				LOG.error(e);
			}
		}
		return expressions;
	}

	/**
	 * Creates the object.
	 * 
	 * @param clazz the clazz
	 * 
	 * @return the object
	 */
	private Object createObject(Class<?> clazz) {
		// create a new instance if object was not found
		try {
			return clazz.newInstance();
		} catch (InstantiationException e) {
			LOG.error(e);
		} catch (IllegalAccessException e) {
			LOG.error(e);
		}
		return null;
	}

	/**
	 * Load object.
	 * 
	 * @param clazz the clazz
	 * @param identifier the identifier
	 * 
	 * @return the object
	 */
	private Object loadObject(Class<?> clazz, Object identifier) {
		Object loadedObject = null;
		LOG.trace("Loading object " + clazz + " ID=" + identifier);
		loadedObject = this.entityManager.find(clazz, identifier);
		return loadedObject;
	}

	/**
	 * Convert to type.
	 * 
	 * @param cell the cell
	 * @param identifierType the identifier type
	 * 
	 * @return the object
	 * 
	 * @throws Exception the exception
	 */
	public static Object convertToType(HSSFCell cell, Class<?> identifierType) throws Exception {
		// deal with null values
		if (cell == null) {
			// primitives cannot be null
			if (identifierType.isPrimitive())
				throw new Exception("Cell cannot be empty. Type " + identifierType.getName() + " is primitive");

			// object can be null
			return null;
		}

		if (identifierType == Long.class) {
			return new Long((long) cell.getNumericCellValue());
		}
		if (identifierType == Integer.class) {
			return new Integer((int) cell.getNumericCellValue());
		}
		if (identifierType == Short.class) {
			return new Short((short) cell.getNumericCellValue());
		}
		if (identifierType == String.class) {
			return cell.getRichStringCellValue().getString();
		}

		return null;
	}

	/**
	 * Searches for {@link Id} annotation on getters.
	 * 
	 * @param clazz the clazz
	 * 
	 * @return identifier type if found, <code>null</code> if not found
	 */
	private Class<?> findIdentifierType(Class<?> clazz) {
		Method[] methods = clazz.getMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			LOG.trace("\t" + method.getReturnType().toString() + " " + method.getName() + "(...)");
			Id annotation = method.getAnnotation(Id.class);
			if (annotation != null) {
				LOG.debug("Found @Id annotation " + annotation);
				return method.getReturnType();
			}
		}
		return null;
	}

	@Transactional
	public List<?> getObjectsFromXLS(Class<?> targetEntity, Hashtable<String, String> mappings, HSSFSheet xlsSheet) throws XLSImportException {
		List<?> result = persistObjectsFromXLS(targetEntity, mappings, xlsSheet);
		// set rollback!
		TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		return result;
	}

	/**
	 * @param targetEntity
	 * @param mappings
	 * @param xls
	 * @return
	 * @throws XLSImportException
	 */
	@Override
	@Transactional
	public List<?> persistObjectsFromXLS(Class<?> targetEntity, Hashtable<String, String> mappings, HSSFSheet xlsSheet) throws XLSImportException {
		// create query cache
		Hashtable<String, Query> queryCache = new Hashtable<String, Query>();

		// ok, sort mappings
		List<String> sortedExpressions = new ArrayList<String>();
		// only add OGNL expressions that have a column mapping assigned
		for (String expression : mappings.keySet()) {
			LOG.debug("Expression: " + expression + " = " + mappings.get(expression));
			String mapping = mappings.get(expression);
			if (mapping != null && mapping.length() > 0) {
				LOG.debug("Adding: " + expression);
				addExpression(sortedExpressions, expression);
			}
		}

		// sort by number of dots and then string
		Collections.sort(sortedExpressions, new Comparator<String>() {
			@Override
			public int compare(String arg0, String arg1) {
				int dots0 = countDots(arg0), dots1 = countDots(arg1);
				if (dots0 == dots1)
					return arg0.compareTo(arg1);
				return dots0 > dots1 ? -1 : 1;
			}
		});

		// print the sorted list
		for (String sortedExpression : sortedExpressions) {
			LOG.info("Sorted " + sortedExpression);
		}

		// get data types
		Hashtable<String, Class<?>> dataTypes = new Hashtable<String, Class<?>>();
		fillDataTypes(null, targetEntity, sortedExpressions, dataTypes);
		for (String sortedExpression : sortedExpressions) {
			LOG.info("Data type '" + sortedExpression + "' is " + dataTypes.get(sortedExpression));
		}

		// find column indexes
		HSSFRow topRow = xlsSheet.getRow(0);
		Hashtable<String, Integer> columnMappings = new Hashtable<String, Integer>();
		for (String sortedExpression : sortedExpressions) {
			String expressionColumn = mappings.get(sortedExpression);
			for (int column = topRow.getFirstCellNum(); column < topRow.getLastCellNum(); column++) {
				String columnName = topRow.getCell(column).getRichStringCellValue().getString();
				if (columnName.equalsIgnoreCase(expressionColumn)) {
					LOG.debug("Column " + column + ": " + columnName + " matches expression " + sortedExpression);
					columnMappings.put(sortedExpression, column);
				}
			}
		}

		// now we go into rows
		// figure out object identifier type
		Class<?> identifierType = findIdentifierType(targetEntity);
		if (identifierType == null) {
			LOG.warn("Could not find identifier type for class " + targetEntity.getName());
			throw new XLSImportException("Could not find identifier type for class " + targetEntity.getName());
		}

		Integer identifierColumn = findIdentifierColumn(targetEntity, sortedExpressions);

		// OGNL
		OgnlContext ognlContext = new OgnlContext();

		// resulting array of updated objects
		ArrayList<Object> updatedObjects = new ArrayList<Object>();
		List<String> topObjects = new ArrayList<String>();

		for (String sortedExpression : sortedExpressions) {
			int dotPos = sortedExpression.lastIndexOf('.');
			if (dotPos >= 0) {
				String parentExpression = sortedExpression.substring(0, dotPos);
				addTopObject(parentExpression, topObjects);
			}
		}

		for (int i = 0; i < topObjects.size(); i++) {
			String expression = topObjects.get(i);
			int dotPos = expression.lastIndexOf('.');
			if (dotPos >= 0) {
				String parentExpression = expression.substring(0, dotPos);
				addTopObject(parentExpression, topObjects);
			}
		}
		// add null prefix
		topObjects.add(null);

		for (String topObject : topObjects) {
			LOG.info("TOP: " + topObject);
		}

		// skip header row, start import from second row
		int lastRowNum = xlsSheet.getLastRowNum();
		for (int rowIndex = 1; rowIndex <= lastRowNum; rowIndex++) {
			HSSFRow row = xlsSheet.getRow(rowIndex);

			// skip empty rows
			if (row == null) {
				// LOG.debug("Skipping empty row " + (rowIndex + 1));
				continue;
			}

			// find object
			Object existingObject = null;

			if (identifierColumn != null) {
				// get ID
				Object identifier = null;
				HSSFCell identifierCell = row.getCell(identifierColumn);
				try {
					identifier = convertToType(identifierCell, identifierType);
				} catch (Exception e) {
					throw new XLSImportException("Cannot get ID from column " + identifierCell.getColumnIndex() + " in row " + (rowIndex + 1) + ". "
							+ e.getMessage());
				}

				// load if identifier exists
				if (identifier != null)
					existingObject = loadObject(targetEntity, identifier);
				if (existingObject == null) {
					LOG.warn(targetEntity.getName() + " with ID " + identifier + " not found in database, creating instance");
				}
			}

			if (existingObject == null) {
				// LOG.debug("Creating instance of " + targetEntity);
				existingObject = createObject(targetEntity);
			}

			if (existingObject == null) {
				LOG.error("Could not get object instance, skipping row");
				continue;
			}

			LOG.info("**** ROW " + rowIndex);

			Hashtable<String, Object> existingValues = new Hashtable<String, Object>();
			// get all values from existing object
			for (String expression : sortedExpressions) {
				try {
					Object existingValue = Ognl.getValue(expression, ognlContext, existingObject);
					if (existingValue != null) {
						existingValues.put(expression, existingValue);
						// LOG.info("Got existing value of '" + expression + "' = " + existingValue);
					} else {
						// LOG.info("No existing value of '" + expression + "'");
					}
				} catch (OgnlException e) {
					// Usually "source is null" message
					// LOG.debug(e.getMessage());
				}
			}

			Hashtable<String, Object> newValues = new Hashtable<String, Object>();
			// get all values from existing object
			for (String expression : sortedExpressions) {
				if (!columnMappings.containsKey(expression))
					continue;

				Object newValue = getCellValue(row.getCell(columnMappings.get(expression)), dataTypes.get(expression));
				if (newValue != null) {
					newValues.put(expression, newValue);
					// LOG.info("Got XLS value of '" + expression + "' = " + newValue);
				} else {
					// LOG.info("No XLS value of '" + expression + "'");
				}
			}

			try {
				for (String prefix : topObjects) {
					doMagic4(prefix, targetEntity, existingObject, sortedExpressions, existingValues, newValues, dataTypes, ognlContext, queryCache);
				}
				LOG.warn("Got " + existingObject);
			} catch (OgnlException e) {
				LOG.error("Error filling object in row " + (rowIndex + 1) + ". " + e.getMessage());
				LOG.error(e);
			}

			try {
				this.entityManager.persist(existingObject);
				updatedObjects.add(existingObject);
			} catch (PersistenceException e) {
				// could not persist object
				// possibly: not-null property references a null or transient value
				LOG.error("Could not persist object because: " + e.getMessage());
			}
		}

		return updatedObjects;
	}

	/**
	 * @param parentExpression
	 * @param topObjects
	 */
	private void addTopObject(String expression, List<String> topObjects) {
		if (!topObjects.contains(expression)) {
			LOG.info("Adding TOP object '" + expression + "'");
			topObjects.add(expression);
		}
	}

	/**
	 * @param cell
	 * @param class1
	 * @return
	 */
	public static Object getCellValue(HSSFCell cell, Class<?> wantedType) {
		// return null value for empty cells
		if (cell == null)
			return null;

		int cellType = cell.getCellType();
		Object value = null;
		if (cellType == HSSFCell.CELL_TYPE_BLANK)
			return null;
		if (cellType == HSSFCell.CELL_TYPE_ERROR) {
			LOG.error("Cell " + cell + " at " + cell.getRowIndex() + ", " + cell.getColumnIndex() + " has an error");
			return null;
		}
		if (cellType == HSSFCell.CELL_TYPE_BOOLEAN)
			return cell.getBooleanCellValue();
		if (cellType == HSSFCell.CELL_TYPE_STRING) {
			String strvalue = cell.getRichStringCellValue().getString().trim();
			if (strvalue.length() == 0)
				return null;
			else
				return strvalue;
		}
		if (cellType == HSSFCell.CELL_TYPE_FORMULA) {
			LOG.error("type is formula. what now? " + cell.getCellFormula());
			return null;
		}
		if (wantedType == Date.class) {
			return cell.getDateCellValue();
		}
		if (cellType == HSSFCell.CELL_TYPE_NUMERIC) {
			if (wantedType == Long.class)
				return (long) cell.getNumericCellValue();
			if (wantedType == Integer.class)
				return (int) cell.getNumericCellValue();
			if (wantedType == Double.class)
				return cell.getNumericCellValue();
			if (wantedType == Short.class)
				return (short) cell.getNumericCellValue();
			if (wantedType == int.class)
				return (int) cell.getNumericCellValue();
			if (wantedType == short.class)
				return (short) cell.getNumericCellValue();
			if (wantedType == long.class)
				return (long) cell.getNumericCellValue();
			if (wantedType == double.class)
				return (double) cell.getNumericCellValue();
			if (wantedType == String.class)
				return "" + cell.getNumericCellValue();
			if (wantedType == Boolean.class)
				return cell.getNumericCellValue() == 1;
			if (wantedType == boolean.class)
				return cell.getNumericCellValue() == 1;
		}
		LOG.warn("Unknown HSSF cell type: " + cellType + " wanted type " + wantedType.getName());
		return value;

	}

	/**
	 * @param targetEntity
	 * @param sortedExpressions
	 * @return
	 */
	private void fillDataTypes(String prefix, Class<?> targetEntity, List<String> sortedExpressions, Hashtable<String, Class<?>> dataTypes) {
		for (String sortedExpression : sortedExpressions) {
			// skip expressions that don't start with prefix if given
			if (prefix != null && (prefix.length() >= sortedExpression.length() || !sortedExpression.startsWith(prefix)))
				continue;

			String expression = sortedExpression;

			if (prefix != null)
				expression = expression.substring(prefix.length() + 1);

			LOG.info("Getting type for " + expression + " on " + targetEntity.getName());

			if (expression.indexOf('.') >= 0) {
				LOG.debug(expression + " has dot");
				// ookay, need to go into the object
				String[] objectTree = expression.split("\\.", 2);
				LOG.debug(expression + " parent '" + objectTree[0] + "'");
				Class<?> relatedEntityType = getGetterType(targetEntity, objectTree[0]);
				dataTypes.put((prefix == null ? "" : prefix + ".") + objectTree[0], relatedEntityType);
				fillDataTypes((prefix == null ? "" : prefix + ".") + objectTree[0], relatedEntityType, sortedExpressions, dataTypes);
			} else {
				Class<?> relatedEntityType = getGetterType(targetEntity, expression);
				dataTypes.put(sortedExpression, relatedEntityType);
			}
		}
	}

	/**
	 * @param targetEntity
	 * @param string
	 * @return
	 */
	private Class<?> getGetterType(Class<?> targetEntity, String expression) {
		try {
			Method getter = targetEntity.getMethod("get" + expression.substring(0, 1).toUpperCase() + expression.substring(1), NOARGS);
			return getter.getReturnType();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param sortedExpressions
	 * @param expression
	 */
	private void addExpression(List<String> sortedExpressions, String expression) {
		if (sortedExpressions.contains(expression))
			return;
		LOG.info("Adding expression '" + expression + "'");
		sortedExpressions.add(expression);
		int lastDot = expression.lastIndexOf('.');
		if (lastDot >= 0) {
			String parentExpression = expression.substring(0, lastDot);
			addExpression(sortedExpressions, parentExpression);
		}
	}

	private void doMagic4(String prefix, Class<?> objectType, Object existingObject, List<String> sortedExpressions, Hashtable<String, Object> existingValues,
			Hashtable<String, Object> newValues, Hashtable<String, Class<?>> dataTypes, OgnlContext ognlContext, Hashtable<String, Query> queryCache) throws OgnlException {

		if (prefix == null) {
			LOG.info("Filling root");
			fillObject(prefix, sortedExpressions, newValues, ognlContext, existingObject);
		} else {
			// fill related object
			objectType = dataTypes.get(prefix);
			// LOG.debug("\nManaging " + prefix + " on type " + objectType.getName());
			// LOG.debug("Need to handle '" + prefix + "' now");
			Object existingValue = existingValues.get(prefix);
			if (existingValue == null) {
				// LOG.info("Finding for " + prefix);
				existingValue = findOrCreate(prefix, objectType, sortedExpressions, existingValues, newValues, ognlContext, queryCache);
			} else {
				// check properties, all of them
				boolean allMatch = true;
				while (allMatch) {
					allMatch = false;
				}
				if (!allMatch) {
					// LOG.info("Need to create new " + prefix);
					existingValue = findOrCreate(prefix, objectType, sortedExpressions, existingValues, newValues, ognlContext, queryCache);
				}
			}
			// LOG.info("Handled '" + prefix + "' = " + existingValue);
			if (existingValue != null) {
				existingValues.put(prefix, existingValue);
				newValues.put(prefix, existingValue);
			}
		}
	}

	/**
	 * @param prefix
	 * @param objectType
	 * @param sortedExpressions
	 * @param existingValues
	 * @param newValues
	 * @param ognlContext
	 * @param queryCache  Query cache
	 * @param updatedObjects List where updated or instantiated objects are stored.
	 * @return
	 */
	private Object findOrCreate(String prefix, Class<?> objectType, List<String> sortedExpressions, Hashtable<String, Object> existingValues,
			Hashtable<String, Object> newValues, @SuppressWarnings("rawtypes") Map ognlContext, Hashtable<String, Query> queryCache) {
		// need to get the matching object from DB, if all mappings exist
		Object object = null;

		// LOG.debug("findOrCreate " + prefix + " of type " + objectType.getName());

		// build query
		String queryKey = "QC" + prefix + objectType.getName();
		Query query = queryCache.get(queryKey);
		if (query == null) {
			LOG.info("Creating query for " + queryKey);
			StringBuilder sb = new StringBuilder();

			for (String sortedExpression : sortedExpressions) {
				// check that we only have primitives left!
				if (prefix != null && sortedExpression.length() > prefix.length() && sortedExpression.startsWith(prefix)) {
					String expression = sortedExpression;
					if (prefix != null)
						expression = sortedExpression.substring(prefix.length() + 1);

					if (sb.length() > 0)
						sb.append(" and ");
					if (expression.indexOf('.', prefix.length()) < 0) {
						// primitive
						sb.append(expression).append("=?");
					} else {
						// related entity
						// sb.append(expression).append("=?");
						// parameters.add(newValues.get(sortedExpression));
					}
				}
			}

			sb.insert(0, "from " + objectType.getName() + " where ");
			LOG.info("Query: " + sb.toString());
			query = this.entityManager.createQuery(sb.toString());
			queryCache.put(queryKey, query);
		}

		List<Object> parameters = new ArrayList<Object>();
		boolean allParamsNull = true;

		for (String sortedExpression : sortedExpressions) {
			// check that we only have primitives left!
			if (prefix != null && sortedExpression.length() > prefix.length() && sortedExpression.startsWith(prefix)) {
				String expression = sortedExpression;
				if (prefix != null)
					expression = sortedExpression.substring(prefix.length() + 1);

				if (expression.indexOf('.', prefix.length()) < 0) {
					// primitive
					Object paramValue = newValues.get(sortedExpression);
					parameters.add(paramValue);
					if (allParamsNull && paramValue != null)
						allParamsNull = false;
				}
			}
		}

		if (allParamsNull) {
			LOG.info("All params null, can't do squat, returning null");
			return null;
		}

		for (int i = 0; i < parameters.size(); i++) {
			query.setParameter(i + 1, parameters.get(i));
		}

		// load single
		try {
			object = query.getSingleResult();
		} catch (NoResultException e) {

		} catch (org.hibernate.AssertionFailure e) {
			// if we have something in session that should not be there
		}

		if (object == null)
			try {
				// LOG.info("Need to create new instance of " + objectType.getName() + " for '" + prefix + "'");
				object = objectType.newInstance();
				// and fill it!
				fillObject(prefix, sortedExpressions, newValues, ognlContext, object);
				// and persist it
				try {
					this.entityManager.persist(object);
				} catch (RuntimeException e) {
					LOG.error("Cannot persist object: " + e.getMessage());
					object = null;
				} catch (Exception e) {
					LOG.error("Cannot persist object: " + e.getMessage());
					object = null;
				}
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		else {
			// LOG.info("Got object from DB " + object);
			// don't need to report that!
		}

		return object;
	}

	private void fillObject(String prefix, List<String> sortedExpressions, Hashtable<String, Object> newValues, @SuppressWarnings("rawtypes") Map ognlContext, Object object) {
		String prefixDot = (prefix == null ? "" : prefix + ".");
		int prefixLen = (prefix == null ? 0 : prefix.length() + 1);

		for (String sortedExpression : sortedExpressions) {
			// check that we only have primitives left!
			String expression = sortedExpression;
			if (prefix != null && !sortedExpression.startsWith(prefixDot))
				continue;
			if (prefix != null)
				expression = sortedExpression.substring(prefixLen);

			if (expression.indexOf('.') >= 0)
				continue;

			// LOG.debug("Setting '" + expression + "' on " + object + " of type " + object.getClass().getName());
			try {
				Ognl.setValue(expression, ognlContext, object, newValues.get(sortedExpression));
			} catch (OgnlException e) {
				e.printStackTrace();
			}
		}
	}

	private int countDots(String arg0) {
		if (arg0 == null)
			return 0;
		int dots = 0;
		for (int i = arg0.length() - 1; i >= 0; i--) {
			if (arg0.charAt(i) == '.')
				dots++;
		}
		// LOG.debug("'" + arg0 + "' has " + dots + " dots");
		return dots;
	}

	@Override
	public List<Object[]> getObjectsFromXLS(HSSFSheet xlsSheet) {
		return getObjectsFromXLS(xlsSheet, 0);
	}

	/**
	 * @param firstRow row index of header row, starting at 0
	 * @see org.iita.service.XLSDataImportService#getObjectsFromXLS(java.util.Hashtable, org.apache.poi.hssf.usermodel.HSSFSheet)
	 */
	@Override
	public List<Object[]> getObjectsFromXLS(HSSFSheet xlsSheet, int firstRow) {
		// find column indexes
		HSSFRow topRow = xlsSheet.getRow(firstRow);
		int columns = topRow.getLastCellNum();
		LOG.debug("Sheet has " + columns + " columns in header row");

		List<Object[]> objects = new ArrayList<Object[]>();

		// skip header row, start import from second row
		int lastRowNum = xlsSheet.getLastRowNum();
		for (int rowIndex = firstRow + 1; rowIndex <= lastRowNum; rowIndex++) {
			HSSFRow row = xlsSheet.getRow(rowIndex);

			// skip empty rows
			if (row == null) {
				// LOG.debug("Skipping empty row " + (rowIndex + 1));
				continue;
			}

			Object[] rowData = readRow(row, columns);
			objects.add(rowData);
		}

		return objects;
	}

	/**
	 * @param row
	 * @param columnMappings
	 * @param cells
	 * @return
	 */
	private Object[] readRow(HSSFRow row, int columns) {
		Object[] rowData = new Object[columns];
		int lastCell = row.getLastCellNum();
		for (int cellIndex = 0; cellIndex < lastCell && cellIndex < columns; cellIndex++) {
			HSSFCell cell = row.getCell(cellIndex);
			Object value = getCellValue(cell);
			rowData[cellIndex] = value;
		}
		return rowData;
	}
}
