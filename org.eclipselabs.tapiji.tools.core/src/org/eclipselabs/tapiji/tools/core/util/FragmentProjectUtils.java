package org.eclipselabs.tapiji.tools.core.util;

import java.util.List;

import org.eclipse.core.resources.IProject;

import com.essiembre.eclipse.rbe.api.PDEUtilsForwarder;


public class FragmentProjectUtils{
	
	public static String getPluginId(IProject project){
		return PDEUtilsForwarder.getPluginId(project);
	}

	
	public static IProject[] lookupFragment(IProject pluginProject){
		return PDEUtilsForwarder.lookupFragment(pluginProject);
	}
	
	public static boolean isFragment(IProject pluginProject){
		return PDEUtilsForwarder.isFragment(pluginProject);
	}
	
	public static List<IProject> getFragments(IProject hostProject){
		return PDEUtilsForwarder.getFragments(hostProject);
	}
	 
	public static String getFragmentId(IProject project, String hostPluginId){
		return PDEUtilsForwarder.getFragmentId(project, hostPluginId);
	}
	
	public static IProject getFragmentHost(IProject fragment){
		return PDEUtilsForwarder.getFragmentHost(fragment);
	}
	
}
