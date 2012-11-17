/*******************************************************************************
 * Copyright (c) 2012 Stefan Reiterer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stefan Reiterer - initial API and implementation
 ******************************************************************************/

package org.eclipse.babel.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.osgi.service.resolver.HostSpecification;
import org.eclipse.pde.core.plugin.IFragmentModel;
import org.eclipse.pde.core.plugin.IPluginBase;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.PluginRegistry;

public class PDEUtils {

	// The same as PDE.PLUGIN_NATURE, because the PDE provided constant is not accessible (internal class) 
	private static final String PLUGIN_NATURE = "org.eclipse.pde.PluginNature";
	
	public static String getPluginId(IProject project) {

		if (project == null || !isPluginProject(project)) {
			return null;
		}

		IPluginModelBase pluginModelBase = PluginRegistry.findModel(project);
		
		if (pluginModelBase == null) {
			// plugin not found in registry
			return null;
		}
		
		IPluginBase pluginBase = pluginModelBase.getPluginBase();

		return pluginBase.getId();
	}
	
	public static boolean isFragment(IProject pluginProject) {
		if (pluginProject == null) {
			return false;
		}
		
		IPluginModelBase pModel = PluginRegistry.findModel(pluginProject);
		
		if (pModel == null) {
			// this project is not a plugin/fragment
			return false;
		}
		
		return pModel.isFragmentModel();
	}
	
	public static List<IProject> getFragments(IProject hostProject) {
		// Check preconditions
		String hostId = getPluginId(hostProject);
		if (hostProject == null || hostId == null) {
			// no valid host project given.
			return Collections.emptyList();
		}

		// Get the fragments of the host project
		IPluginModelBase pModelBase = PluginRegistry.findModel(hostProject);
		BundleDescription desc = pModelBase.getBundleDescription();

		ArrayList<IPluginModelBase> fragmentModels = new ArrayList<IPluginModelBase>();
		if (desc == null) {
			// There is no bundle description for the host project
			return Collections.emptyList();
		}
		
		BundleDescription[] f = desc.getFragments();
		for (BundleDescription candidateDesc : f) {
			IPluginModelBase candidate = PluginRegistry.findModel(candidateDesc);
			if (candidate instanceof IFragmentModel) {
				fragmentModels.add(candidate);
			}
		}
		
		// Get the fragment project which is in the current workspace
		ArrayList<IProject> fragments = getFragmentsAsWorkspaceProjects(hostProject, fragmentModels);
		
		return fragments;		
	}
	
	public static String getFragmentId(IProject project, String hostPluginId) {
		if (!isFragment(project) || hostPluginId == null) {
			return null;
		}
		
		IPluginModelBase pluginModelBase = PluginRegistry.findModel(project);
		if (pluginModelBase instanceof IFragmentModel) {
			IFragmentModel fragmentModel = (IFragmentModel) pluginModelBase;
			BundleDescription description = fragmentModel.getBundleDescription();
			HostSpecification hostSpecification = description.getHost();

			if (hostPluginId.equals(hostSpecification.getName())) {
				return getPluginId(project);
			}
		}
		return null;
	}
	
	public static IProject getFragmentHost(IProject fragment) {
		if (!isFragment(fragment)) {
			return null;
		}
		
		IPluginModelBase pluginModelBase = PluginRegistry.findModel(fragment);
		if (pluginModelBase instanceof IFragmentModel) {
			IFragmentModel fragmentModel = (IFragmentModel) pluginModelBase;
			BundleDescription description = fragmentModel.getBundleDescription();
			HostSpecification hostSpecification = description.getHost();
			
			IPluginModelBase hostProject = PluginRegistry.findModel(hostSpecification.getName());
			IProject[] projects = fragment.getWorkspace().getRoot().getProjects();
			ArrayList<IProject> hostProjects = getPluginProjects(Arrays.asList(hostProject), projects);
			
			if (hostProjects.size() != 1) {
				// hostproject not in workspace
				return null;
			} else {
				return hostProjects.get(0);
			}
		}
		
		return null;
	}

	/**
	 * Returns all project containing plugin/fragment of the specified project.
	 * If the specified project itself is a fragment, then only this is
	 * returned.
	 * 
	 * @param pluginProject
	 *            the plugin project
	 * @return the all project containing a fragment or null if none
	 */
	public static IProject[] lookupFragment(IProject pluginProject) {
		if (isFragment(pluginProject) && pluginProject.isOpen()) {
			return new IProject[] {pluginProject};
		}
		
		IProject[] workspaceProjects = pluginProject.getWorkspace().getRoot().getProjects();
		String hostPluginId = getPluginId(pluginProject);
		
		if (hostPluginId == null) {
			// project is not a plugin project
			return null;
		}
		
		List<IProject> fragmentProjects = new ArrayList<IProject>();
		for (IProject project : workspaceProjects) {
			if (!project.isOpen() || getFragmentId(project, hostPluginId) == null) {
				// project is not open or it is no fragment where given project is the host project.
				continue;
			}
			fragmentProjects.add(project);
		}

		if (fragmentProjects.isEmpty()) {
			return null;
		}
		
		return fragmentProjects.toArray(new IProject[0]);
	}
	
	private static ArrayList<IProject> getFragmentsAsWorkspaceProjects(IProject hostProject, ArrayList<IPluginModelBase> fragmentModels) {
		IProject[] projects = hostProject.getWorkspace().getRoot().getProjects();
		
		ArrayList<IProject> fragments = getPluginProjects(fragmentModels, projects);
		
		return fragments;
	}

	private static ArrayList<IProject> getPluginProjects(List<IPluginModelBase> fragmentModels, IProject[] projects) {
		ArrayList<IProject> fragments = new ArrayList<IProject>();
		for (IProject project : projects) {
			IPluginModelBase pModel = PluginRegistry.findModel(project);
			
			if (fragmentModels.contains(pModel)) {
				fragments.add(project);
			}
		}
		
		return fragments;
	}
	
	private static boolean isPluginProject(IProject project) {
		try {
			return project.hasNature(PLUGIN_NATURE);
		} catch (CoreException ce) {
			//Logger.logError(ce);
		}
		return false;
	}

}