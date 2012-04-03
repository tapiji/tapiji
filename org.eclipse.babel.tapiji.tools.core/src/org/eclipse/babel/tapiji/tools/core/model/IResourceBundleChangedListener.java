package org.eclipse.babel.tapiji.tools.core.model;

import org.eclipse.babel.tapiji.tools.core.model.manager.ResourceBundleChangedEvent;

public interface IResourceBundleChangedListener {

	public void resourceBundleChanged (ResourceBundleChangedEvent event);
	
}
