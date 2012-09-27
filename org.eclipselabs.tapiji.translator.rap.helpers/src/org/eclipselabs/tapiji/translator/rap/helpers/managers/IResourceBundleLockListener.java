package org.eclipselabs.tapiji.translator.rap.helpers.managers;

import java.util.EventListener;

import org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle;

public interface IResourceBundleLockListener extends EventListener {
	void lockReleased(RBLock lock);
	void lockAcquired(RBLock lock);
}
