package util;

import org.eclipse.core.resources.IProject;

import at.ac.tuwien.inso.eclipse.i18n.model.manager.ResourceBundleManager;

public class ELUtils {
	
	public static String getResource (IProject project, String bundleName, String key) {
		ResourceBundleManager manager = ResourceBundleManager.getManager(project);
		if (manager.isResourceExisting(bundleName, key))
			return manager.getKeyHoverString(bundleName, key);
		else
			return null;
	}
}
