package org.eclipselabs.tapiji.translator.rap.utils;

import java.io.IOException;
import java.util.Properties;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.teneo.PersistenceOptions;
import org.eclipse.emf.teneo.hibernate.HbDataStore;
import org.eclipse.emf.teneo.hibernate.HbHelper;
import org.eclipse.emf.teneo.hibernate.resource.HibernateResource;
import org.eclipselabs.tapiji.translator.rap.model.user.UserPackage;
import org.hibernate.cfg.Environment;


public class DBUtils {

	// DB specific data
	public static final String DB_NAME = "translatordb";
	public static final String DB_HOST = "10.0.0.2";
	public static final int DB_PORT = 3306;
	public static final String DB_DRIVER = "com.mysql.jdbc.Driver";
	public static final String DB_USER = "root";
	public static final String DB_PASSWORD = "admin";
	
	public static final String DS_NAME = "UserDS";
	public static final String SESSION_USER_ATT = "org.eclipselabs.tapiji.translator.rap.model.user.User";
	
	
	private static HbDataStore userDataStore = null;
	
	public static String getEnvironmentURL() {
		// MySQL url
		return "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/"+ DB_NAME + "?createDatabaseIfNotExist=true";
	}
	
	public static HbDataStore getDataStore() {
		if (userDataStore == null)
			initDataStore();
	    return userDataStore;
	}
	
	public static void initDataStore() {		
		userDataStore = (HbDataStore) HbHelper.INSTANCE.createRegisterDataStore(DS_NAME);
		// Set Database properties
        Properties props = new Properties();
        props.setProperty(Environment.DRIVER, DB_DRIVER);
        props.setProperty(Environment.URL, getEnvironmentURL());
        props.setProperty(Environment.USER, DB_USER);
        props.setProperty(Environment.PASS, DB_PASSWORD);
        props.setProperty(Environment.DIALECT, org.hibernate.dialect.MySQLDialect.class.getName());
        // props.setProperty(Environment.SHOW_SQL, "true");
        // props.setProperty(Environment.HBM2DDL_AUTO, "create");
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
	
	public static Resource getPersistentData() {
		String uriStr = "hibernate://?"+HibernateResource.DS_NAME_PARAM+"="+DS_NAME;
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
}
