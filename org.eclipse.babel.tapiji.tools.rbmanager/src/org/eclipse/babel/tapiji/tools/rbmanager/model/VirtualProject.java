/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Reiterer - initial API and implementation
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.rbmanager.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.eclipse.babel.tapiji.tools.core.util.FragmentProjectUtils;
import org.eclipse.core.resources.IProject;

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
