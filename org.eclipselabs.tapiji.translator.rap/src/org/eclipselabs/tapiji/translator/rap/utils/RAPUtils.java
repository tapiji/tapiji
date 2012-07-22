package org.eclipselabs.tapiji.translator.rap.utils;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;

public class RAPUtils {

	public static String getUserPath() {
		// TODO [RAP]
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		return projects[0].getLocation().toFile().toString();
	}
	
	
}
