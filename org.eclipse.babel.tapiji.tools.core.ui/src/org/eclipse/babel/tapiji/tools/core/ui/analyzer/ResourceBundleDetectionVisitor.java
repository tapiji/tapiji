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
package org.eclipse.babel.tapiji.tools.core.ui.analyzer;

import org.eclipse.babel.tapiji.tools.core.Logger;
import org.eclipse.babel.tapiji.tools.core.ui.ResourceBundleManager;
import org.eclipse.babel.tapiji.tools.core.ui.utils.RBFileUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;

public class ResourceBundleDetectionVisitor implements IResourceVisitor,
        IResourceDeltaVisitor {

	private IProject project = null;

	public ResourceBundleDetectionVisitor(IProject project) {
		this.project = project;
	}

	@Override
	public boolean visit(IResource resource) throws CoreException {
		try {
			if (RBFileUtils.isResourceBundleFile(resource)) {
				Logger.logInfo("Loading Resource-Bundle file '"
				        + resource.getName() + "'");
				if (!ResourceBundleManager.isResourceExcluded(resource)) {
					ResourceBundleManager.getManager(project)
					        .addBundleResource(resource);
				}
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean visit(IResourceDelta delta) throws CoreException {
		IResource resource = delta.getResource();

		if (RBFileUtils.isResourceBundleFile(resource)) {
			// ResourceBundleManager.getManager(resource.getProject()).bundleResourceModified(delta);
			return false;
		}

		return true;
	}

}
