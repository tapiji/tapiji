package org.eclipse.babel.tapiji.tools.core.model.manager;

import java.util.Collection;

public class ResourceExclusionEvent {

	private Collection<Object> changedResources;

	public ResourceExclusionEvent(Collection<Object> changedResources) {
		super();
		this.changedResources = changedResources;
	}

	public void setChangedResources(Collection<Object> changedResources) {
		this.changedResources = changedResources;
	}

	public Collection<Object> getChangedResources() {
		return changedResources;
	}
	
}
