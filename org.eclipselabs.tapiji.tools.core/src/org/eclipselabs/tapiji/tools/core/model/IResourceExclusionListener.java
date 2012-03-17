package org.eclipselabs.tapiji.tools.core.model;

import org.eclipselabs.tapiji.tools.core.model.manager.ResourceExclusionEvent;

public interface IResourceExclusionListener {

	public void exclusionChanged(ResourceExclusionEvent event);

}
