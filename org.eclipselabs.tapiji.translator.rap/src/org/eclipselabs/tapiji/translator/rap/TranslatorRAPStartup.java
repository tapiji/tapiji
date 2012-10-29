package org.eclipselabs.tapiji.translator.rap;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.service.SessionStoreEvent;
import org.eclipse.rwt.service.SessionStoreListener;
import org.eclipse.ui.IStartup;
import org.eclipselabs.tapiji.translator.rap.helpers.managers.RBLockManager;
import org.eclipselabs.tapiji.translator.rap.helpers.utils.DBUtils;


public class TranslatorRAPStartup implements IStartup {
	@Override
	public void earlyStartup() {
		// initialize Teneo Hibernate DataStore
        DBUtils.initDataStore();
        
        // release locks when RAP session expires
    	RWT.getSessionStore().addSessionStoreListener( new SessionStoreListener() {
    		  public void beforeDestroy( SessionStoreEvent event ) {
    			  RBLockManager.INSTANCE.releaseLocksHeldBySessionID(event.getSessionStore().getId());
    		  }
    	});
	}
}
