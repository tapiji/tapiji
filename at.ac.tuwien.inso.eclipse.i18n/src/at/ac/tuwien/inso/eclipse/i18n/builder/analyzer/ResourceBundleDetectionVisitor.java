package at.ac.tuwien.inso.eclipse.i18n.builder.analyzer;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;

import at.ac.tuwien.inso.eclipse.i18n.model.manager.ResourceBundleManager;
import at.ac.tuwien.inso.eclipse.i18n.util.ResourceUtils;

public class ResourceBundleDetectionVisitor implements IResourceVisitor,
		IResourceDeltaVisitor {

	private ResourceBundleManager manager = null;
	
	public ResourceBundleDetectionVisitor(ResourceBundleManager manager) {
		this.manager = manager;
	}

	@Override
	public boolean visit(IResource resource) throws CoreException {
		try {
			if (ResourceUtils.isResourceBundle(resource)) {
				System.out.println("resource bundle found: " + resource.getName());
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
		
		if (ResourceUtils.isResourceBundle(resource)) {
			ResourceBundleManager.getManager(resource.getProject()).bundleResourceModified(delta);
			return false;
		} else
			return true;
	}

}
