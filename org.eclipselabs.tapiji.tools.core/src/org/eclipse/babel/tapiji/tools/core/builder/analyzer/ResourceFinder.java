package org.eclipse.babel.tapiji.tools.core.builder.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.babel.tapiji.tools.core.Logger;
import org.eclipse.babel.tapiji.tools.core.builder.StringLiteralAuditor;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;


public class ResourceFinder implements IResourceVisitor,
		IResourceDeltaVisitor {

	List<IResource> javaResources = null;
	Set<String> supportedExtensions = null;

	public ResourceFinder(Set<String> ext) {
		javaResources = new ArrayList<IResource>();
		supportedExtensions = ext;
	}

	@Override
	public boolean visit(IResource resource) throws CoreException {
		if (StringLiteralAuditor.isResourceAuditable(resource, supportedExtensions)) {
			Logger.logInfo("Audit necessary for resource '" + resource.getFullPath().toOSString() + "'");
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
		visit (delta.getResource());
		return true;
	}

}
