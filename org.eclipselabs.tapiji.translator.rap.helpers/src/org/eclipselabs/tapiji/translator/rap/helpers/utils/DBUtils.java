package org.eclipselabs.tapiji.translator.rap.helpers.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.teneo.PersistenceOptions;
import org.eclipse.emf.teneo.hibernate.HbDataStore;
import org.eclipse.emf.teneo.hibernate.HbHelper;
import org.eclipse.emf.teneo.hibernate.resource.HibernateResource;
import org.eclipselabs.tapiji.translator.rap.model.user.PropertiesFile;
import org.eclipselabs.tapiji.translator.rap.model.user.UserPackage;
import org.hibernate.cfg.Environment;


/**
 * Utility methods for database access
 * @author Matthias Lettmayer
 *
 */
public class DBUtils {
//	public static final String DB_NAME = "translatordb";      
//    public static final String DB_HOST = "127.8.159.1";    
//    public static final int DB_PORT = 3306;
//    public static final String DB_DRIVER = "com.mysql.jdbc.Driver";
//    public static final String DB_USER = "admin";
//    public static final String DB_PASSWORD = "mPReTZK_-5Qd";

	/** Database schema name */
	public static final String DB_NAME = "translatordb";
	/** Database host */
	public static final String DB_HOST = "127.0.0.1";
	/** Database port */
	public static final int DB_PORT = 3306;
	/** Database driver */
	public static final String DB_DRIVER = "com.mysql.jdbc.Driver";
	/** Username for logging into database */
	public static final String DB_USER = "root";
	/** Password for logging into database */
	public static final String DB_PASSWORD = "admin";
	
	/** Data store identifier name */
	public static final String DS_NAME = "UserDS";
	/** Hibernate data store */
	private static HbDataStore userDataStore = null;
	/** Hibernate resource */
	private static Resource resource = null;
	
	/**
	 * Creates an environment URL for connection to database. 
	 * Uses defined host name, port and schema name of database for creating the url and 
	 * adds a query, which creates the database if it doesn't exist yet (createDatabaseIfNotExist=true).
	 * 
	 * @return created environment URL
	 */
	public static String getEnvironmentURL() {
		// MySQL url
		return "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/"+ DB_NAME + "?createDatabaseIfNotExist=true";
	}
	
	/**
	 * Returns the hibernate data store, which will be created and initialized, if it hasn't been done yet.
	 * 
	 * @return hibernate data store
	 */
	public static HbDataStore getDataStore() {
		if (userDataStore == null)
			initDataStore();
	    return userDataStore;
	}
	
	/**
	 * Initializes hibernate data store. Sets environment properties, such as driver, URL, username, 
	 * password and dialect.
	 * Also initializes (UserPackage.eINSTANCE) and registers the EMF User Package.
	 * 
	 */
	public static void initDataStore() {
		if (userDataStore != null)
			return;
		
		userDataStore = (HbDataStore) HbHelper.INSTANCE.createRegisterDataStore(DS_NAME);
		// Set Database properties
        Properties props = new Properties();
        props.setProperty(Environment.DRIVER, DB_DRIVER);
        props.setProperty(Environment.URL, getEnvironmentURL());
        props.setProperty(Environment.USER, DB_USER);
        props.setProperty(Environment.PASS, DB_PASSWORD);
        props.setProperty(Environment.DIALECT, org.hibernate.dialect.MySQL5InnoDBDialect.class.getName());
//        props.setProperty(Environment.SHOW_SQL, "true");        
//        props.setProperty(Environment.HBM2DDL_AUTO, "create");
        props.setProperty(PersistenceOptions.CASCADE_POLICY_ON_NON_CONTAINMENT,
				"REFRESH,PERSIST,MERGE");
        userDataStore.setDataStoreProperties(props);
        // Register EMF package
        userDataStore.setEPackages(new EPackage[] { UserPackage.eINSTANCE });
        try {
        	userDataStore.initialize();
        } finally {
        	// print the generated mapping
        	System.err.println(userDataStore.getMappingXML());			
        }
        // creates tables if db empty
        userDataStore.getSessionFactory();
	}
	
	/**
	 * Creates a new resource and loads the persistent data from database through hibernate data store. 
	 * Therefore the hibernate data store must be initialized before using this method (see {@link #initDataStore()}. 
	 * @return resource persisted in database
	 */
	public static Resource getPersistentData() {		
		if (resource == null) {
			String uriStr = "hibernate://?"+HibernateResource.DS_NAME_PARAM+"="+DS_NAME;
	        final URI uri = URI.createURI(uriStr);
	        ResourceSet resourceSet = new ResourceSetImpl();
	        resource = resourceSet.createResource(uri);
		}
       
        try {
        	resource.load(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        return resource;
	}
	
	public static Resource query(String query) {
		String uriStr = "hibernate://?"+HibernateResource.DS_NAME_PARAM+"="+DS_NAME+"&query1="+query;
	    final URI uri = URI.createURI(uriStr);
        ResourceSet resourceSet = new ResourceSetImpl();
        Resource resource = resourceSet.createResource(uri);	
       
        try {
        	// load from database
			resource.load(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        return resource;
	}
	
	public static PropertiesFile getPropertiesFile(String filePath) {
		PropertiesFile propFile = null;
		String query = "FROM PropertiesFile p WHERE p.path='" + filePath+"'";
		Resource resource = DBUtils.query(query);
		List<EObject> results = resource.getContents();
		if (! results.isEmpty() && results.get(0) instanceof PropertiesFile)
			propFile = (PropertiesFile) results.get(0);
		return propFile;
	}
}
