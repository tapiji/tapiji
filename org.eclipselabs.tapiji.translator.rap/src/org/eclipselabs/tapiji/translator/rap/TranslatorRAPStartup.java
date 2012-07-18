package org.eclipselabs.tapiji.translator.rap;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.teneo.hibernate.HbDataStore;
import org.eclipse.emf.teneo.hibernate.HbHelper;
import org.eclipse.emf.teneo.hibernate.resource.HibernateResource;
import org.eclipse.ui.IStartup;
import org.eclipselabs.tapiji.translator.rap.model.user.File;
import org.eclipselabs.tapiji.translator.rap.model.user.User;
import org.eclipselabs.tapiji.translator.rap.model.user.UserFactory;
import org.eclipselabs.tapiji.translator.rap.model.user.UserPackage;
import org.eclipselabs.tapiji.translator.rap.utils.DBUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Environment;



public class TranslatorRAPStartup implements IStartup {

	@Override
	public void earlyStartup() {
		// *************** Initialize Teneo Hibernate DataStore *************************************
        DBUtils.initDataStore();
        
        // *************** Initialize Database Content Data *************************************
        Resource resource = DBUtils.getPersistentData();
        try {
        	// load from database
            resource.load(null);
            
            // insert test data
            if (resource.getContents().size() == 0) {            
	            User user = UserFactory.eINSTANCE.createUser();
	            user.setUsername("testUser");
	            user.setPassword("password");
	        
	            File file = UserFactory.eINSTANCE.createFile();
	            file.setName("testFile");
	            file.setPath("/tmp/testPath/testFile");
	            user.getStoredFiles().add(file);
	            
	            // persist user
	            resource.getContents().add(user);
	            resource.save(null);	            
            }
         } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
}
