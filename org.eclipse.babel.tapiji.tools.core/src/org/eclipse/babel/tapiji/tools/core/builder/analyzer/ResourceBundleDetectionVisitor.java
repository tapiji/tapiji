package org.eclipse.babel.tapiji.tools.core.builder.analyzer;

import org.eclipse.babel.tapiji.tools.core.Logger;
import org.eclipse.babel.tapiji.tools.core.model.manager.ResourceBundleManager;
import org.eclipse.babel.tapiji.tools.core.util.RBFileUtils;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;


public class ResourceBundleDetectionVisitor implements IResourceVisitor,
		IResourceDeltaVisitor {

	private ResourceBundleManager manager = null;
	
	public ResourceBundleDetectionVisitor(ResourceBundleManager manager) {
		this.manager = manager;
	}

	@Override
	public boolean visit(IResource resource) throws CoreException {
		try {
			if (RBFileUtils.isResourceBundleFile(resource)) {
				Logger.logInfo("Loading Resource-Bundle file '" + resource.getName() + "'");
 				if (!ResourceBundleManager.isResourceExcluded(resource))
					manager.addBundleResource(resource);
				return false;
			} else
				return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean visit(IResourceDelta delta) throws CoreException {
		IResource resource = delta.getResource();

		if (RBFileUtils.isResourceBundleFile(resource)) {
//			ResourceBundleManager.getManager(resource.getProject()).bundleResourceModified(delta);
			return false;
		}
		
		return true;
	}
	
}
