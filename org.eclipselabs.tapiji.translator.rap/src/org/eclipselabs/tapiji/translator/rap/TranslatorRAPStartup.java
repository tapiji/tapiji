package org.eclipselabs.tapiji.translator.rap;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.service.UISessionEvent;
import org.eclipse.rap.rwt.service.UISessionListener;
import org.eclipse.ui.IStartup;
import org.eclipselabs.tapiji.translator.rap.helpers.managers.RBLockManager;
import org.eclipselabs.tapiji.translator.rap.helpers.utils.DBUtils;


public class TranslatorRAPStartup implements IStartup {
	@Override
	public void earlyStartup() {
		// initialize Teneo Hibernate DataStore
        DBUtils.initDataStore();
        
        // release locks when RAP session expires
        RWT.getUISession().addUISessionListener(new UISessionListener() {			
			@Override
			public void beforeDestroy(UISessionEvent event) {
				RBLockManager.INSTANCE.releaseLocksHeldBySessionID(event.getUISession().getId());
			}
		});
	}
}
