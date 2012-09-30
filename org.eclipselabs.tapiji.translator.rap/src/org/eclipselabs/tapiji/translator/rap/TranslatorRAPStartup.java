package org.eclipselabs.tapiji.translator.rap;

import org.eclipse.ui.IStartup;
import org.eclipselabs.tapiji.translator.rap.helpers.utils.DBUtils;


public class TranslatorRAPStartup implements IStartup {

	@Override
	public void earlyStartup() {
		// *************** Initialize Teneo Hibernate DataStore *************************************
        DBUtils.initDataStore();
        
        // delete unregistered user project (name = sessionID) when session ends
//    	RWT.getSessionStore().addSessionStoreListener( new SessionStoreListener() {
//    		  public void beforeDestroy( SessionStoreEvent event ) {
////    			try {
////					IProject tempProject = FileRAPUtils.getProject(RWT.getSessionStore().getId());
////					tempProject.delete(false, null);
////					
////					// close all opened editors
////					// page is null
////					/*PlatformUI.getWorkbench().getActiveWorkbenchWindow().
////						getActivePage().closeAllEditors(false);*/
////				} catch (CoreException e) {
////					e.printStackTrace();
////				}
//    		  }
//    		  
//    		 
//    	} );
    	
	}
	
	
}
