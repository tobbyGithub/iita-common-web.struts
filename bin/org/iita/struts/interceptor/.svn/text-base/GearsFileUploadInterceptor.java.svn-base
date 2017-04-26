/*
 * $Id: FileUploadInterceptor.java 495094 2007-01-11 02:51:40Z mrdon $
 * 
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package org.iita.struts.interceptor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.FileUploadInterceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.util.LocalizedTextUtil;

/**
 * <!-- START SNIPPET: description -->
 * 
 * Interceptor that is based off of {@link FileUploadInterceptor}. It adds the following parameters, where [File Name] is the constant "uploads" to the file
 * uploaded in the HTTP request InputStream:
 * 
 * <ul>
 * 
 * <li>[File Name] : File - the actual File</li>
 * 
 * <li>[File Name]ContentType : String - the content type of the file</li>
 * 
 * <li>[File Name]FileName : String - the actual name of the file uploaded (not the HTML name)</li>
 * 
 * </ul>
 * 
 * <p/>
 * You can get access to these files by merely providing setters in your action that correspond to any of the three patterns above, such as setDocument(File
 * document), setDocumentContentType(String contentType), etc. <br/>
 * See the example code section.
 * 
 * <p/>
 * This interceptor will add several field errors, assuming that the action implements {@link ValidationAware}. These error messages are based on several i18n
 * values stored in struts-messages.properties, a default i18n file processed for all i18n requests. You can override the text of these messages by providing
 * text for the following keys:
 * 
 * <ul>
 * 
 * <li>struts.messages.error.uploading - a general error that occurs when the file could not be uploaded</li>
 * 
 * <li>struts.messages.error.file.too.large - occurs when the uploaded file is too large</li>
 * 
 * <li>struts.messages.error.content.type.not.allowed - occurs when the uploaded file does not match the expected content types specified</li>
 * 
 * </ul>
 * 
 * <!-- END SNIPPET: description -->
 * 
 * <p/>
 * <u>Interceptor parameters:</u>
 * 
 * <!-- START SNIPPET: parameters -->
 * 
 * <ul>
 * 
 * <li>maximumSize (optional) - the maximum size (in bytes) that the interceptor will allow a file reference to be set on the action. Note, this is <b>not</b>
 * related to the various properties found in struts.properties. Default to approximately 2MB.</li>
 * 
 * <li>allowedTypes (optional) - a comma separated list of content types (ie: text/html) that the interceptor will allow a file reference to be set on the
 * action. If none is specified allow all types to be uploaded.</li>
 * 
 * </ul>
 * 
 * <!-- END SNIPPET: parameters -->
 * 
 * <p/>
 * <u>Extending the interceptor:</u>
 * 
 * <p/>
 * 
 * <!-- START SNIPPET: extending -->
 * 
 * You can extend this interceptor and override the {@link #acceptFile} method to provide more control over which files are supported and which are not.
 * 
 * <!-- END SNIPPET: extending -->
 * 
 * <p/>
 * <u>Example code:</u>
 * 
 * <pre>
 * &lt;!-- START SNIPPET: example --&gt;
 * &lt;action name=&quot;doUpload&quot; class=&quot;com.examples.UploadAction&quot;&gt;
 *     &lt;interceptor-ref name=&quot;fileUpload&quot;/&gt;
 *     &lt;interceptor-ref name=&quot;basicStack&quot;/&gt;
 *     &lt;result name=&quot;success&quot;&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 * </pre>
 * 
 * And then you need to set encoding <code>multipart/form-data</code> in the form where the user selects the file to upload.
 * 
 * <pre>
 *   &lt;iita:fileupload value=&quot;Upload files&quot; action=&quot;doUpload&quot; namespace=&quot;/&quot; queryParams=&quot;id=${id}&quot; /&gt;
 * </pre>
 * 
 * And then in your action code you'll have access to the File object if you provide setters according to the naming convention documented in the start.
 * 
 * <pre>
 *    public com.examples.UploadAction implemements Action {
 *       private File file;
 *       private String contentType;
 *       private String filename;
 *       private Long id;
 *       
 *       public void setId(Long id) {
 *          this.id = id;
 *       }
 *       public void setUpload(File file) {
 *          this.file = file;
 *       }
 *       public void setUploadContentType(String contentType) {
 *          this.contentType = contentType;
 *       }
 *       public void setUploadFileName(String filename) {
 *          this.filename = filename;
 *       }
 *       ...
 *  }
 * </pre>
 * 
 * <!-- END SNIPPET: example -->
 * 
 */
public class GearsFileUploadInterceptor extends AbstractInterceptor {

	private static final long serialVersionUID = 2031393178090027707L;
	protected static final Log log = LogFactory.getLog(GearsFileUploadInterceptor.class);
	private static final String DEFAULT_DELIMITER = ",";
	private static final String DEFAULT_MESSAGE = "no.message.found";

	protected Long maximumSize;
	protected String allowedTypes;
	@SuppressWarnings("unchecked")
	protected Set allowedTypesSet = Collections.EMPTY_SET;

	/**
	 * Sets the allowed mimetypes
	 * 
	 * @param allowedTypes A comma-delimited list of types
	 */
	public void setAllowedTypes(String allowedTypes) {
		this.allowedTypes = allowedTypes;

		// set the allowedTypes as a collection for easier access later
		allowedTypesSet = getDelimitedValues(allowedTypes);
	}

	/**
	 * Sets the maximum size of an uploaded file
	 * 
	 * @param maximumSize The maximum size in bytes
	 */
	public void setMaximumSize(Long maximumSize) {
		this.maximumSize = maximumSize;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.opensymphony.xwork2.interceptor.Interceptor#intercept(com.opensymphony.xwork2.ActionInvocation)
	 */
	public String intercept(ActionInvocation invocation) throws Exception {
		ActionContext ac = invocation.getInvocationContext();
		HttpServletRequest request = (HttpServletRequest) ac.get(ServletActionContext.HTTP_REQUEST);

		String fileName = request.getHeader("gears-filename");
		if (fileName != null) {

			if (log.isDebugEnabled()) {
				ActionProxy proxy = invocation.getProxy();
				log.debug(getTextMessage("iita.messages.upload.isGears", new Object[] { proxy.getNamespace(), proxy.getActionName() }, ActionContext
						.getContext().getLocale()));
			}
		} else {
			if (log.isDebugEnabled()) {
				ActionProxy proxy = invocation.getProxy();
				log.debug(getTextMessage("struts.messages.bypass.request", new Object[] { proxy.getNamespace(), proxy.getActionName() }, ActionContext
						.getContext().getLocale()));
			}

			return invocation.invoke();
		}

		final Object action = invocation.getAction();
		ValidationAware validation = null;

		if (action instanceof ValidationAware) {
			validation = (ValidationAware) action;
		}

		File uploadedFile = doGearsUpload(request, validation, ac);

		// invoke action
		String result = invocation.invoke();

		log.info(getTextMessage("struts.messages.removing.file", new Object[] { "uploads", uploadedFile }, ActionContext.getContext().getLocale()));

		if ((uploadedFile != null) && uploadedFile.isFile() && uploadedFile.exists()) {
			uploadedFile.delete();
		}

		return result;
	}

	/**
	 * @param request
	 * @param validation
	 * @param ac
	 * 
	 */
	@SuppressWarnings("unchecked")
	private File doGearsUpload(HttpServletRequest request, ValidationAware validation, ActionContext ac) {
		// this is our extension for Gears uploads
		String fileName = request.getHeader("gears-filename");
		if (fileName != null) {
			String contentType = request.getHeader("gears-contentType");
			if (contentType == null)
				contentType = "application/x-binary";
			String inputName = request.getHeader("gears-inputName");
			if (inputName == null)
				inputName = "uploads";

			log.info("Gears filename: " + fileName);
			log.info("Gears contentType: " + contentType);
			log.info("Content-length: " + request.getContentLength());
			try {
				ServletInputStream uploadStream = request.getInputStream();
				File tempFile = File.createTempFile("gears.", ".upload");
				tempFile.deleteOnExit();
				FileOutputStream tempStream = new FileOutputStream(tempFile);
				byte[] b = new byte[2048];
				int len = 0, total = 0;
				do {
					len = uploadStream.read(b);
					if (len > 0) {
						tempStream.write(b, 0, len);
						total += len;
					}
				} while (len > 0);
				uploadStream.close();
				tempStream.flush();
				tempStream.close();
				log.debug("File uploaded from stream.");
				
				if (request.getContentLength() != total) {
					log.warn("Upload not complete? " + total + " received of " + request.getContentLength());
					tempFile.delete();
					tempFile = null;
					return null;
				}
				if (acceptFile(tempFile, contentType, inputName, validation, ac.getLocale())) {
					Map parameters = ac.getParameters();

					parameters.put(inputName, new File[] { tempFile });
					parameters.put(inputName + "ContentType", contentType);
					parameters.put(inputName + "FileName", fileName);
					return tempFile;

				}
			} catch (IOException e) {
				log.error(e);
			}
		}
		return null;
	}

	/**
	 * Override for added functionality. Checks if the proposed file is acceptable based on contentType and size.
	 * 
	 * @param file - proposed upload file.
	 * @param contentType - contentType of the file.
	 * @param inputName - inputName of the file.
	 * @param validation - Non-null ValidationAware if the action implements ValidationAware, allowing for better logging.
	 * @param locale
	 * @return true if the proposed file is acceptable by contentType and size.
	 */
	protected boolean acceptFile(File file, String contentType, String inputName, ValidationAware validation, Locale locale) {
		boolean fileIsAcceptable = false;

		// If it's null the upload failed
		if (file == null) {
			String errMsg = getTextMessage("struts.messages.error.uploading", new Object[] { inputName }, locale);
			if (validation != null) {
				validation.addFieldError(inputName, errMsg);
			}

			log.error(errMsg);
		} else if (maximumSize != null && maximumSize.longValue() < file.length()) {
			String errMsg = getTextMessage("struts.messages.error.file.too.large", new Object[] { inputName, file.getName(), "" + file.length() }, locale);
			if (validation != null) {
				validation.addFieldError(inputName, errMsg);
			}

			log.error(errMsg);
		} else if ((!allowedTypesSet.isEmpty()) && (!containsItem(allowedTypesSet, contentType))) {
			String errMsg = getTextMessage("struts.messages.error.content.type.not.allowed", new Object[] { inputName, file.getName(), contentType }, locale);
			if (validation != null) {
				validation.addFieldError(inputName, errMsg);
			}

			log.error(errMsg);
		} else {
			fileIsAcceptable = true;
		}

		return fileIsAcceptable;
	}

	/**
	 * @param itemCollection - Collection of string items (all lowercase).
	 * @param key - Key to search for.
	 * @return true if itemCollection contains the key, false otherwise.
	 */
	private static boolean containsItem(Collection<?> itemCollection, String key) {
		return itemCollection.contains(key.toLowerCase());
	}

	private static Set<?> getDelimitedValues(String delimitedString) {
		Set<String> delimitedValues = new HashSet<String>();
		if (delimitedString != null) {
			StringTokenizer stringTokenizer = new StringTokenizer(delimitedString, DEFAULT_DELIMITER);
			while (stringTokenizer.hasMoreTokens()) {
				String nextToken = stringTokenizer.nextToken().toLowerCase().trim();
				if (nextToken.length() > 0) {
					delimitedValues.add(nextToken);
				}
			}
		}
		return delimitedValues;
	}

	private String getTextMessage(String messageKey, Object[] args, Locale locale) {
		if (args == null || args.length == 0) {
			return LocalizedTextUtil.findText(this.getClass(), messageKey, locale);
		} else {
			return LocalizedTextUtil.findText(this.getClass(), messageKey, locale, DEFAULT_MESSAGE, args);
		}
	}
}
