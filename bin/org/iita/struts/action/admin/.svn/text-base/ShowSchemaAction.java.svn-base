/**
 * iita-common-web.struts Oct 23, 2009
 */
package org.iita.struts.action.admin;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.ejb.packaging.ClassFilter;
import org.hibernate.ejb.packaging.Entry;
import org.hibernate.ejb.packaging.Filter;
import org.hibernate.ejb.packaging.JarVisitor;
import org.hibernate.ejb.packaging.JarVisitorFactory;
import org.hibernate.ejb.packaging.PackageFilter;
import org.hibernate.ejb.packaging.PersistenceMetadata;
import org.hibernate.ejb.packaging.PersistenceXmlLoader;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.iita.struts.BaseAction;

import com.opensymphony.xwork2.Action;

/**
 * Generate database schema SQL
 * 
 * @author mobreza
 * 
 */
@SuppressWarnings("serial")
public class ShowSchemaAction extends BaseAction {
	private List<Exception> exceptions;
	private String schema;
	private static final String PERSISTENCE_XML_LOCATION = "META-INF/persistence.xml";

	/**
	 * @return the exceptions
	 */
	public List<Exception> getExceptions() {
		return this.exceptions;
	}

	/**
	 * @return the schema
	 */
	public String getSchema() {
		return this.schema;
	}

	/**
	 * @see org.iita.struts.BaseAction#execute()
	 */
	@Override
	public String execute() {
		File outputFile;		
		StringBuilder sb = new StringBuilder();

		try {
			outputFile = File.createTempFile("hib_", ".sql");
			Configuration cfg = createAnnotationConfiguration();
			SchemaExport se = new SchemaExport(cfg);
			se.setOutputFile(outputFile.getCanonicalPath());
			se.setFormat(true);
			se.create(false, false);
			FileInputStream fis = new FileInputStream(outputFile);
			InputStreamReader reader = new InputStreamReader(fis);
			int len = 0;
			char[] cbuf = new char[1024];
			do {
				len = reader.read(cbuf);
				if (len > 0)
					sb.append(cbuf, 0, len);
			} while (len > 0);
			reader.close();
			fis.close();
			outputFile.delete();

			this.schema = sb.toString().replaceAll("\\n\\n|$", ";\n\n");
			return Action.SUCCESS;
		} catch (IOException e) {
			LOG.error(e);
			addActionError(e.getMessage());
			return Action.ERROR;
		} catch (Exception e) {
			LOG.error(e);
			addActionError(e.getMessage());
			return Action.ERROR;
		}
	}

	@SuppressWarnings("unchecked")
	private AnnotationConfiguration createAnnotationConfiguration() throws Exception {
		// look up all existing 'META-INF/persistence.xml'
		Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(PERSISTENCE_XML_LOCATION);

		// ////////////////////////////////////////////////////////////////
		// Just some funny way to figure out the connection pool name
		// Don' t care much.
		List<URL> urlList = new ArrayList<URL>();
		
		while (resources.hasMoreElements()) {
			URL xmlRes = resources.nextElement();
			LOG.info("Resource: " + xmlRes);
			urlList.add(xmlRes);			
		}
	
		// ESSENTIAL: That's the Object from which the SessionFactory will be created.
		// All this code is about telling this cfg object about the (Annotation) mapped classes.
		AnnotationConfiguration cfg = new AnnotationConfiguration();
		Properties props = new Properties();

		props.put(Environment.DIALECT, "org.hibernate.dialect.MySQLInnoDBDialect");

		cfg.setProperties(props);

		Filter[] filters = new Filter[2];
		filters[0] = new PackageFilter(false, null) {
			public boolean accept(String javaElementName) {
				return true;
			}
		};
		filters[1] = new ClassFilter(false, new Class[] { Entity.class, MappedSuperclass.class, Embeddable.class }) {
			public boolean accept(String javaElementName) {
				return true;
			}
		};

		for (URL persXmlUrl : urlList) {
			List<PersistenceMetadata> metadataFiles = PersistenceXmlLoader.deploy(persXmlUrl, new HashMap(), cfg.getEntityResolver());

			LOG.info("persXmlUrl: " + persXmlUrl);

			// URL jarURL = JarVisitor.getJarURLFromURLEntry(persXmlUrl, PERSISTENCE_XML_LOCATION);
			for (PersistenceMetadata data : metadataFiles) {
				LOG.info("PersistenceMetadata: " + data.getName() + " " + data);

				for (String jarFile : data.getJarFiles()) {
					File jar = new File(getPath(), jarFile);
					URL jarUrl = new URL("file:" + jar.getCanonicalPath());
					LOG.info(jarUrl);
					JarVisitor j = JarVisitorFactory.getVisitor(jarUrl, filters);
					Set<org.hibernate.ejb.packaging.Entry>[] entries = j.getMatchingEntries();
					for (int i = 0; i < entries.length; i++) {
						LOG.info("" + entries[i] + " " + entries[i].getClass().getName());
						for (Entry c : entries[i]) {
							LOG.info("" + c + " " + c.getName());
							cfg.addAnnotatedClass(Class.forName(c.getName()));
						}
					}
				}

			}
		}

		final List<String> classes=new ArrayList<String>();
		
		// scan /WEB-INF/classes/
		File classesDir = new File(getPath(), "WEB-INF/classes");
		scanRecursively(classesDir, new ClassFound() {
			@Override
			public void accept(String arg0) {
				if (!classes.contains(arg0)) {
					LOG.info("Adding " + arg0);
					classes.add(arg0);
				}
			}
		});
		
		for (String annotatedClass : classes) {
			cfg.addAnnotatedClass(Class.forName(annotatedClass));
		}

		// ESSENTIAL: Something like a compile
		cfg.buildMappings();

		return cfg;
	}

	private interface ClassFound {
		public void accept(String className);
	};
	
	/**
	 * @param classesDir
	 * @throws IOException
	 */
	private void scanRecursively(File classesDir, ClassFound classFound) throws IOException {
		LOG.info("Scanning " + classesDir.getCanonicalPath());
		File[] classFiles = classesDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".class");
			}
		});

		for (File classFile : classFiles) {
			LOG.info("Scanning file " + classFile.getName());
			DataInputStream dstream = new DataInputStream(new FileInputStream(classFile));
			ClassFile cf = new ClassFile(dstream);
			String className = cf.getName();
			LOG.info("Found class " + className);
			AnnotationsAttribute visible = (AnnotationsAttribute) cf.getAttribute(AnnotationsAttribute.visibleTag);
			// AnnotationsAttribute invisible = (AnnotationsAttribute) cf.getAttribute(AnnotationsAttribute.invisibleTag);
			if (visible != null)
				for (javassist.bytecode.annotation.Annotation ann : visible.getAnnotations()) {
					LOG.info(" @" + ann.getTypeName());
					//Entity.class, MappedSuperclass.class, Embeddable.class
					if (ann.getTypeName().equalsIgnoreCase(Entity.class.getName()) || 
							ann.getTypeName().equalsIgnoreCase(MappedSuperclass.class.getName()) || 
							ann.getTypeName().equalsIgnoreCase(Embeddable.class.getName())) {
						
						classFound.accept(className);
						break;
					}
				}
		}

		File[] subDirs = classesDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});

		for (File subDir : subDirs) {
			scanRecursively(subDir, classFound);
		}
	}
}
