/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Michael Gasser - initial API and implementation
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.rbmanager.model;

import java.util.Collection;

import org.eclipse.babel.tapiji.tools.core.ui.ResourceBundleManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

public class VirtualResourceBundle {
	private String resourcebundlename;
	private String resourcebundleId;
	private ResourceBundleManager rbmanager;

	public VirtualResourceBundle(String rbname, String rbId,
	        ResourceBundleManager rbmanager) {
		this.rbmanager = rbmanager;
		resourcebundlename = rbname;
		resourcebundleId = rbId;
	}

	public ResourceBundleManager getResourceBundleManager() {
		return rbmanager;
	}

	public String getResourceBundleId() {
		return resourcebundleId;
	}

	@Override
	public String toString() {
		return resourcebundleId;
	}

	public IPath getFullPath() {
		return rbmanager.getRandomFile(resourcebundleId).getFullPath();
	}

	public String getName() {
		return resourcebundlename;
	}

	public Collection<IResource> getFiles() {
		return rbmanager.getResourceBundles(resourcebundleId);
	}

	public IFile getRandomFile() {
		return rbmanager.getRandomFile(resourcebundleId);
	}
}
