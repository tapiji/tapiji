package org.eclipselabs.tapiji.translator.rap.helpers.managers;

import java.util.EventListener;

import org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle;

public interface IPropertiesFileLockListener extends EventListener {
	void lockReleased(PFLock lock);
	void lockAcquired(PFLock lock);
}
