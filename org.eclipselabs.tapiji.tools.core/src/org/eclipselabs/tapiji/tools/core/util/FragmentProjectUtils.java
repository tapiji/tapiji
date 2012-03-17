package org.eclipselabs.tapiji.tools.core.util;

import java.util.List;

import org.eclipse.babel.core.util.PDEUtils;
import org.eclipse.core.resources.IProject;

public class FragmentProjectUtils{
	
	public static String getPluginId(IProject project){
		return PDEUtils.getPluginId(project);
	}

	
	public static IProject[] lookupFragment(IProject pluginProject){
		return PDEUtils.lookupFragment(pluginProject);
	}
	
	public static boolean isFragment(IProject pluginProject){
		return PDEUtils.isFragment(pluginProject);
	}
	
	public static List<IProject> getFragments(IProject hostProject){
		return PDEUtils.getFragments(hostProject);
	}
	 
	public static String getFragmentId(IProject project, String hostPluginId){
		return PDEUtils.getFragmentId(project, hostPluginId);
	}
	
	public static IProject getFragmentHost(IProject fragment){
		return PDEUtils.getFragmentHost(fragment);
	}
	
}
