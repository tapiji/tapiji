package org.eclipselabs.tapiji.translator.rap;

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.service.SessionStoreEvent;
import org.eclipse.rwt.service.SessionStoreListener;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipselabs.tapiji.translator.actions.LogoutAction;
import org.eclipselabs.tapiji.translator.rap.model.user.File;
import org.eclipselabs.tapiji.translator.rap.model.user.User;
import org.eclipselabs.tapiji.translator.rap.model.user.UserFactory;
import org.eclipselabs.tapiji.translator.rap.utils.DBUtils;
import org.eclipselabs.tapiji.translator.rap.utils.FileRAPUtils;


public class TranslatorRAPStartup implements IStartup {

	@Override
	public void earlyStartup() {
		// *************** Initialize Teneo Hibernate DataStore *************************************
        DBUtils.initDataStore();
        
        // *************** Initialize Database Content Data *************************************
        Resource resource = DBUtils.getPersistentData();
        try {
        	// insert test data
            if (resource.getContents().size() == 0) {            
	            User user = UserFactory.eINSTANCE.createUser();
	            user.setUsername("test");
	            user.setPassword("123");
	        
	            User fileUser = UserFactory.eINSTANCE.createUser();
	            fileUser.setUsername("file");
	            fileUser.setPassword("123");
	       
	            File file = UserFactory.eINSTANCE.createFile();
	            file.setName("testFile");
	            file.setPath("/tmp/testPath/testFile");
	            fileUser.getStoredFiles().add(file);
	            
	            // persist users
	            resource.getContents().add(user);
	            resource.getContents().add(fileUser);
	            resource.save(null);	            
            }
         } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        // delete unregistered user project (name = sessionID) when session ends
    	RWT.getSessionStore().addSessionStoreListener( new SessionStoreListener() {
    		  public void beforeDestroy( SessionStoreEvent event ) {
    			try {
					IProject tempProject = FileRAPUtils.getProject(RWT.getSessionStore().getId());
					tempProject.delete(false, null);
					
					// close all opened editors
					// page = null
					/*PlatformUI.getWorkbench().getActiveWorkbenchWindow().
						getActivePage().closeAllEditors(false);*/
				} catch (CoreException e) {
					e.printStackTrace();
				}
    		  }
    		 
    	} );
    	
	}
	
	
}
