package org.eclipselabs.tapiji.tools.rbmanager.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipselabs.tapiji.tools.core.util.FragmentProjectUtils;

/**
 * Representation of a project
 *
 */
public class VirtualProject extends VirtualContainer{
	private boolean isFragment;
	private IProject hostProject;
	private List<IProject> fragmentProjects = new LinkedList<IProject>();
	
	//Slow
	public VirtualProject(IProject project, boolean countResourceBundles) {
		super(project, countResourceBundles);
		isFragment = FragmentProjectUtils.isFragment(project);
		if (isFragment) {
			hostProject = FragmentProjectUtils.getFragmentHost(project);
		} else
			fragmentProjects = FragmentProjectUtils.getFragments(project);
	}
	
	/*
	 * No fragment search
	 */
	public VirtualProject(final IProject project, boolean isFragment, boolean countResourceBundles){
		super(project, countResourceBundles);
		this.isFragment = isFragment;
//		Display.getDefault().asyncExec(new Runnable() {
//			@Override
//			public void run() {
//				hostProject = FragmentProjectUtils.getFragmentHost(project);
//			}
//		});
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

	public boolean hasFragments() {
		return !fragmentProjects.isEmpty();
	}
	
	public List<IProject> getFragmets(){
		return fragmentProjects;
	}
}
