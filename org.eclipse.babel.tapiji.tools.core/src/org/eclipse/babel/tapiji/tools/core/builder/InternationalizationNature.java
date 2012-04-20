/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.core.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.babel.tapiji.tools.core.Activator;
import org.eclipse.babel.tapiji.tools.core.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public class InternationalizationNature implements IProjectNature {

    private static final String NATURE_ID = Activator.PLUGIN_ID + ".nature";
    private IProject project;

    @Override
    public void configure() throws CoreException {
	I18nBuilder.addBuilderToProject(project);
	new Job("Audit source files") {

	    @Override
	    protected IStatus run(IProgressMonitor monitor) {
		try {
		    project.build(I18nBuilder.FULL_BUILD,
			    I18nBuilder.BUILDER_ID, null, monitor);
		} catch (CoreException e) {
		    Logger.logError(e);
		}
		return Status.OK_STATUS;
	    }

	}.schedule();
    }

    @Override
    public void deconfigure() throws CoreException {
	I18nBuilder.removeBuilderFromProject(project);
    }

    @Override
    public IProject getProject() {
	return project;
    }

    @Override
    public void setProject(IProject project) {
	this.project = project;
    }

    public static void addNature(IProject project) {
	if (!project.isOpen())
	    return;

	IProjectDescription description = null;

	try {
	    description = project.getDescription();
	} catch (CoreException e) {
	    Logger.logError(e);
	    return;
	}

	// Check if the project has already this nature
	List<String> newIds = new ArrayList<String>();
	newIds.addAll(Arrays.asList(description.getNatureIds()));
	int index = newIds.indexOf(NATURE_ID);
	if (index != -1)
	    return;

	// Add the nature
	newIds.add(NATURE_ID);
	description.setNatureIds(newIds.toArray(new String[newIds.size()]));

	try {
	    project.setDescription(description, null);
	} catch (CoreException e) {
	    Logger.logError(e);
	}
    }

    public static boolean supportsNature(IProject project) {
	return project.isOpen();
    }

    public static boolean hasNature(IProject project) {
	try {
	    return project.isOpen() && project.hasNature(NATURE_ID);
	} catch (CoreException e) {
	    Logger.logError(e);
	    return false;
	}
    }

    public static void removeNature(IProject project) {
	if (!project.isOpen())
	    return;

	IProjectDescription description = null;

	try {
	    description = project.getDescription();
	} catch (CoreException e) {
	    Logger.logError(e);
	    return;
	}

	// Check if the project has already this nature
	List<String> newIds = new ArrayList<String>();
	newIds.addAll(Arrays.asList(description.getNatureIds()));
	int index = newIds.indexOf(NATURE_ID);
	if (index == -1)
	    return;

	// remove the nature
	newIds.remove(NATURE_ID);
	description.setNatureIds(newIds.toArray(new String[newIds.size()]));

	try {
	    project.setDescription(description, null);
	} catch (CoreException e) {
	    Logger.logError(e);
	}
    }
}
