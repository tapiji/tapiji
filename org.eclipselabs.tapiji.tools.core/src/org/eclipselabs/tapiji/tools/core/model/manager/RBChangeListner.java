package org.eclipselabs.tapiji.tools.core.model.manager;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipselabs.tapiji.tools.core.util.RBFileUtils;

public class RBChangeListner implements IResourceChangeListener {

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		try {
			event.getDelta().accept(new IResourceDeltaVisitor() {
				@Override
				public boolean visit(IResourceDelta delta) throws CoreException {					
					IResource resource = delta.getResource();
					if (RBFileUtils.isResourceBundleFile(resource)) {
//						ResourceBundleManager.getManager(resource.getProject()).bundleResourceModified(delta);
						return false;
					}
					return true;
				}
			});
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

}
