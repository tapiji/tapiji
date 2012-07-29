package org.eclipselabs.tapiji.translator.rap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.service.SessionStoreEvent;
import org.eclipse.rwt.service.SessionStoreListener;
import org.eclipse.ui.IStartup;
import org.eclipselabs.tapiji.translator.rap.utils.DBUtils;
import org.eclipselabs.tapiji.translator.rap.utils.FileRAPUtils;


public class TranslatorRAPStartup implements IStartup {

	@Override
	public void earlyStartup() {
		// *************** Initialize Teneo Hibernate DataStore *************************************
        DBUtils.initDataStore();
        
        // *************** Initialize Database Content Data *************************************
        Resource resource = DBUtils.getPersistentData();
        
        // delete unregistered user project (name = sessionID) when session ends
    	RWT.getSessionStore().addSessionStoreListener( new SessionStoreListener() {
    		  public void beforeDestroy( SessionStoreEvent event ) {
    			try {
					IProject tempProject = FileRAPUtils.getProject(RWT.getSessionStore().getId());
					tempProject.delete(false, null);
					
					// close all opened editors
					// page is null
					/*PlatformUI.getWorkbench().getActiveWorkbenchWindow().
						getActivePage().closeAllEditors(false);*/
				} catch (CoreException e) {
					e.printStackTrace();
				}
    		  }
    		 
    	} );
    	
	}
	
	
}
