package com.essiembre.eclipse.rbe.api;

import java.util.List;

import org.eclipse.core.resources.IProject;
import com.essiembre.eclipse.rbe.ui.editor.resources.PDEUtils;


public class PDEUtilsForwarder{
	
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
