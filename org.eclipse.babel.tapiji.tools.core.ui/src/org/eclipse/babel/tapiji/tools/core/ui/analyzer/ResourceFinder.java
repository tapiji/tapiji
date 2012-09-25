/*******************************************************************************
 * Copyright (c) 2012 Martin Reiterer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Reiterer - initial API and implementation
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.core.ui.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.babel.tapiji.tools.core.Logger;
import org.eclipse.babel.tapiji.tools.core.ui.builder.I18nBuilder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;

public class ResourceFinder implements IResourceVisitor, IResourceDeltaVisitor {

    List<IResource> javaResources = null;
    Set<String> supportedExtensions = null;

    public ResourceFinder(Set<String> ext) {
	javaResources = new ArrayList<IResource>();
	supportedExtensions = ext;
    }

    @Override
    public boolean visit(IResource resource) throws CoreException {
	if (I18nBuilder.isResourceAuditable(resource, supportedExtensions)) {
	    Logger.logInfo("Audit necessary for resource '"
		    + resource.getFullPath().toOSString() + "'");
	    javaResources.add(resource);
	    return false;
	} else
	    return true;
    }

    public List<IResource> getResources() {
	return javaResources;
    }

    @Override
    public boolean visit(IResourceDelta delta) throws CoreException {
	visit(delta.getResource());
	return true;
    }

}
