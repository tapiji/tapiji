/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.core.model.manager;

import org.eclipse.core.resources.IProject;

public class ResourceBundleChangedEvent {

	public final static int ADDED = 0;
	public final static int DELETED = 1;
	public final static int MODIFIED = 2;
	public final static int EXCLUDED = 3;
	public final static int INCLUDED = 4;
	
	private IProject project;
	private String bundle = "";
	private int type = -1;
	
	public ResourceBundleChangedEvent (int type, String bundle, IProject project) {
		this.type = type;
		this.bundle = bundle;
		this.project = project;
	}
	
	public IProject getProject() {
		return project;
	}
	
	public void setProject(IProject project) {
		this.project = project;
	}
	
	public String getBundle() {
		return bundle;
	}
	
	public void setBundle(String bundle) {
		this.bundle = bundle;
	}

	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
}
