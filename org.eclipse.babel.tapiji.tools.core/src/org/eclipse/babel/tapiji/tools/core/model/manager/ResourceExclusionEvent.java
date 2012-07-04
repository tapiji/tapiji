/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Reiterer - initial API and implementation
 ******************************************************************************/
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
