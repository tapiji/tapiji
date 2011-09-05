package com.gknsintermetals.eclipse.resourcebundle.manager.model;

import java.util.Locale;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipselabs.tapiji.tools.core.model.manager.ResourceBundleManager;
import org.eclipselabs.tapiji.tools.core.util.PDEUtils;

/**
 * Representation of a project
 *
 */
public class VirtualProject extends VirtualContainer{
	private boolean isFragment;
	private IProject hostProject;
	
	public VirtualProject(IProject project, boolean countResourceBundles) {
		super(project, countResourceBundles);
		isFragment = PDEUtils.isFragment(project);
		if (isFragment) hostProject = PDEUtils.getFragmentHost(project);
	}
	
	public Set<Locale> getProvidedLocales(){
		return rbmanager.getProjectProvidedLocales();
	}
	
	public boolean isFragment(){
		return isFragment;
	}
	
	public IProject getHostProject(){
		return hostProject;
	}
}
