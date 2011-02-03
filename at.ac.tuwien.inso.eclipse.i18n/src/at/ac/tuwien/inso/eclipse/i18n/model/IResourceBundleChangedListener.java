package at.ac.tuwien.inso.eclipse.i18n.model;

import at.ac.tuwien.inso.eclipse.i18n.model.manager.ResourceBundleChangedEvent;

public interface IResourceBundleChangedListener {

	public void resourceBundleChanged (ResourceBundleChangedEvent event);
	
}
