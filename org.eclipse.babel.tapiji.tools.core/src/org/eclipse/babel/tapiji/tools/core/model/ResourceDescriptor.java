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
package org.eclipse.babel.tapiji.tools.core.model;

import org.eclipse.core.resources.IResource;

public class ResourceDescriptor implements IResourceDescriptor {

	private String projectName;
	private String relativePath;
	private String absolutePath;
	private String bundleId;
	
	public ResourceDescriptor (IResource resource) {
		projectName = resource.getProject().getName();
		relativePath = resource.getProjectRelativePath().toString();
		absolutePath = resource.getRawLocation().toString();
	}
	
	public ResourceDescriptor() {
	}

	@Override
	public String getAbsolutePath() {
		return absolutePath;
	}

	@Override
	public String getProjectName() {
		return projectName;
	}

	@Override
	public String getRelativePath() {
		return relativePath;
	}

	@Override
	public int hashCode() {
		return projectName.hashCode() + relativePath.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof ResourceDescriptor))
			return false;
		
		return absolutePath.equals(absolutePath);
	}

	@Override
	public void setAbsolutePath(String absPath) {
		this.absolutePath = absPath;
	}

	@Override
	public void setProjectName(String projName) {
		this.projectName = projName;
	}

	@Override
	public void setRelativePath(String relPath) {
		this.relativePath = relPath;
	}

	@Override
	public String getBundleId() {
		return this.bundleId;
	}

	@Override
	public void setBundleId(String bundleId) {
		this.bundleId = bundleId;
	}

	
}
