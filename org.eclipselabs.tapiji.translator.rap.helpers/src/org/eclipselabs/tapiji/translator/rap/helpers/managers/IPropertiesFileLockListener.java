package org.eclipselabs.tapiji.translator.rap.helpers.managers;

import java.util.EventListener;

public interface IPropertiesFileLockListener extends EventListener {
	void lockReleased(PFLock lock);
	void lockAcquired(PFLock lock);
}
