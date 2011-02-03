package at.ac.tuwien.inso.eclipse.i18n.model;

import at.ac.tuwien.inso.eclipse.i18n.model.manager.ResourceExclusionEvent;

public interface IResourceExclusionListener {

	public void exclusionChanged(ResourceExclusionEvent event);

}
