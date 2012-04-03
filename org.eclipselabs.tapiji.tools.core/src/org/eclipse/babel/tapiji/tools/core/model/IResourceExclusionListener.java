package org.eclipse.babel.tapiji.tools.core.model;

import org.eclipse.babel.tapiji.tools.core.model.manager.ResourceExclusionEvent;

public interface IResourceExclusionListener {

	public void exclusionChanged(ResourceExclusionEvent event);

}
