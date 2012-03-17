package org.eclipselabs.tapiji.tools.core.model;

import org.eclipselabs.tapiji.tools.core.model.manager.ResourceBundleChangedEvent;

public interface IResourceBundleChangedListener {

	public void resourceBundleChanged (ResourceBundleChangedEvent event);
	
}
