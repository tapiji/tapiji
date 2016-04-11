package org.eclipselabs.tapiji.translator.rap.helpers.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.teneo.PersistenceOptions;
import org.eclipse.emf.teneo.hibernate.HbDataStore;
import org.eclipse.emf.teneo.hibernate.HbHelper;
import org.eclipse.emf.teneo.hibernate.resource.HibernateResource;
import org.eclipse.emf.teneo.resource.StoreResource;
import org.eclipselabs.tapiji.translator.rap.model.user.PropertiesFile;
import org.eclipselabs.tapiji.translator.rap.model.user.User;
import org.eclipselabs.tapiji.translator.rap.model.user.UserPackage;
import org.hibernate.cfg.Environment;


/**
 * Utility methods for database access
 * @author Matthias Lettmayer
 *
 */
public class DBUtils {
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
	public static final String DS_NAME = "TranslatorDS";
	/** Hibernate data store */
	private static HbDataStore userDataStore = null;
	/** Hibernate resource set */
	private static ResourceSet resourceSet = new ResourceSetImpl();
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
        // use C3P0 as connection pool
        props.setProperty(Environment.C3P0_MIN_SIZE, "5");				// minimum pool size, default = 1.
        props.setProperty(Environment.C3P0_MAX_SIZE, "100");				// maximum pool size, default = 100. Must not be greater than database's max connections value. To see value for MySQL: show variables like "max_connections";
//        props.setProperty(Environment.C3P0_TIMEOUT, "1800");				// seconds a connection can remain pooled but unused before being discarded. 0 means idle connections never expire.
        props.setProperty(Environment.C3P0_MAX_STATEMENTS, "50");		// size of c3p0's PreparedStatement cache. 0 means statement caching is turned off.
//        props.setProperty(Environment.C3P0_IDLE_TEST_PERIOD, "1800");		// If this is a number greater than 0, c3p0 will test all idle, pooled but unchecked-out connections, every this number of seconds. Must NOT be greater than TIMEOUT! Default = 0.
        
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
        // creates tables if db is empty
        userDataStore.getSessionFactory();
	}
	
	/**
	 * Returns all persisted data of the database as a hibernate resource (see {@link HibernateResource}). 
	 * The hibernate resource contains the session, which handles the connection to the database. 
	 * Therefore only one resource is used during the application's runtime. Loads the persistent data 
	 * from database through a hibernate data store. So the hibernate data store must be initialized before
	 * using this method (see {@link #initDataStore()}). 
	 * @return Resource, that contains the persisted data of the database.
	 */
	public static Resource getPersistentData() {
		// Use only one hibernate resource through application runtime.
		// TODO: Optimize connection handling -> Use a separate resource (session) for each user/display/session/UIThread
		if (resource == null) {
			// create new hibernate resource through hibernate datastore (specified by DS_NAME)
			String uriStr = "hibernate://?"+HibernateResource.DS_NAME_PARAM+"="+DS_NAME;
	        final URI uri = URI.createURI(uriStr);	        
	        resource = resourceSet.createResource(uri);
		}
		
		// load data from database into resource
        try {
        	resource.load(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        return resource;
	}
	
	
	/**
	 * 
	 * @param query
	 * @return
	 */
	private static Resource query(String query) {
		String uriStr = "hibernate://?"+HibernateResource.DS_NAME_PARAM+"="+DS_NAME+"&"+
				StoreResource.LOAD_STRATEGY_PARAM+"="+StoreResource.ADD_TO_CONTENTS+"&query1="+query;
	    final URI uri = URI.createURI(uriStr);
	    
	    Resource resourceQuery = resourceSet.createResource(uri);
	    
        try {
        	// load from database
			resourceQuery.load(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        return resourceQuery;
	}
	
	private static PropertiesFile queryPropertiesFile(String whereQuery) {
		PropertiesFile propFile = null;
		
		String query = "FROM PropertiesFile p WHERE " + whereQuery;
		Resource resource = query(query);
		List<EObject> results = resource.getContents();
		if (! results.isEmpty() && results.get(0) instanceof PropertiesFile)
			propFile = (PropertiesFile) results.get(0);
		
		if (propFile != null)			
			// resolve all properties files of owned resource bundle 
			// before unloading resource to avoid lazy loading later
			EcoreUtil.resolveAll(propFile.getResourceBundle());
		
		// unload resource -> close connection
		resource.unload();	
		
		return propFile;
	}
	
	public static PropertiesFile getPropertiesFile(String filePath) {		
		// replace backslash (\) with 2 backslashes to escape it in sql
		// note: "\\\\" is resolved as one backslash, java + regex escape
		String query = "p.path='" + filePath.replaceAll("\\\\", "\\\\\\\\") + "'";
		return queryPropertiesFile(query);
	}
	
	public static PropertiesFile getPropertiesFile(long pfID) {		
		String query = "p.id='" + pfID+"'";
		return queryPropertiesFile(query);
	}

	public static List<User> getAllRegisteredUsers() {
		Resource resource = getPersistentData();
		EList<EObject> registeredObjects = resource.getContents();
		
		List<User> users = new ArrayList<User>();
				
		for (EObject obj : registeredObjects)
			if (obj instanceof User)
				users.add((User) obj);			
		
		return users;
	}
}
