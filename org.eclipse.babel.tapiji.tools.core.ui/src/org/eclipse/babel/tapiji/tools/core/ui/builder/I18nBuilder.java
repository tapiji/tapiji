/*******************************************************************************
 * Copyright (c) 2012 Martin Reiterer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Reiterer - initial API and implementation
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.core.ui.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.babel.core.configuration.ConfigurationManager;
import org.eclipse.babel.core.configuration.IConfiguration;
import org.eclipse.babel.tapiji.tools.core.Logger;
import org.eclipse.babel.tapiji.tools.core.extensions.ILocation;
import org.eclipse.babel.tapiji.tools.core.extensions.IMarkerConstants;
import org.eclipse.babel.tapiji.tools.core.model.exception.NoSuchResourceAuditorException;
import org.eclipse.babel.tapiji.tools.core.ui.ResourceBundleManager;
import org.eclipse.babel.tapiji.tools.core.ui.analyzer.ResourceFinder;
import org.eclipse.babel.tapiji.tools.core.ui.extensions.I18nAuditor;
import org.eclipse.babel.tapiji.tools.core.ui.extensions.I18nRBAuditor;
import org.eclipse.babel.tapiji.tools.core.ui.extensions.I18nResourceAuditor;
import org.eclipse.babel.tapiji.tools.core.ui.utils.EditorUtils;
import org.eclipse.babel.tapiji.tools.core.ui.utils.RBFileUtils;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;

public class I18nBuilder extends IncrementalProjectBuilder {

    public static final String BUILDER_ID = ResourceBundleManager.BUILDER_ID;

    public static I18nAuditor getI18nAuditorByContext(String contextId)
	    throws NoSuchResourceAuditorException {
	for (I18nAuditor auditor : ExtensionManager.getRegisteredI18nAuditors()) {
	    if (auditor.getContextId().equals(contextId)) {
		return auditor;
	    }
	}
	throw new NoSuchResourceAuditorException();
    }

    public static boolean isResourceAuditable(IResource resource,
	    Set<String> supportedExtensions) {
	for (String ext : supportedExtensions) {
	    if (resource.getType() == IResource.FILE && !resource.isDerived()
		    && resource.getFileExtension() != null
		    && (resource.getFileExtension().equalsIgnoreCase(ext))) {
		return true;
	    }
	}
	return false;
    }

    @Override
    protected IProject[] build(final int kind, Map args,
	    IProgressMonitor monitor) throws CoreException {

	ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {
	    @Override
	    public void run(IProgressMonitor monitor) throws CoreException {
		if (kind == FULL_BUILD) {
		    fullBuild(monitor);
		} else {
		    // only perform audit if the resource delta is not empty
		    IResourceDelta resDelta = getDelta(getProject());

		    if (resDelta == null) {
			return;
		    }

		    if (resDelta.getAffectedChildren() == null) {
			return;
		    }

		    incrementalBuild(monitor, resDelta);
		}
	    }
	}, monitor);

	return null;
    }

    private void incrementalBuild(IProgressMonitor monitor,
	    IResourceDelta resDelta) throws CoreException {
	try {
	    // inspect resource delta
	    ResourceFinder csrav = new ResourceFinder(
		    ExtensionManager.getSupportedFileEndings());
	    resDelta.accept(csrav);
	    auditResources(csrav.getResources(), monitor, getProject());
	} catch (CoreException e) {
	    Logger.logError(e);
	}
    }

    public void buildResource(IResource resource, IProgressMonitor monitor) {
	if (isResourceAuditable(resource,
		ExtensionManager.getSupportedFileEndings())) {
	    List<IResource> resources = new ArrayList<IResource>();
	    resources.add(resource);
	    // TODO: create instance of progressmonitor and hand it over to
	    // auditResources
	    try {
		auditResources(resources, monitor, resource.getProject());
	    } catch (Exception e) {
		Logger.logError(e);
	    }
	}
    }

    public void buildProject(IProgressMonitor monitor, IProject proj) {
	try {
	    ResourceFinder csrav = new ResourceFinder(
		    ExtensionManager.getSupportedFileEndings());
	    proj.accept(csrav);
	    auditResources(csrav.getResources(), monitor, proj);
	} catch (CoreException e) {
	    Logger.logError(e);
	}
    }

    private void fullBuild(IProgressMonitor monitor) {
	buildProject(monitor, getProject());
    }

    private void auditResources(List<IResource> resources,
	    IProgressMonitor monitor, IProject project) {
	IConfiguration configuration = ConfigurationManager.getInstance()
		.getConfiguration();

	int work = resources.size();
	int actWork = 0;
	if (monitor == null) {
	    monitor = new NullProgressMonitor();
	}

	monitor.beginTask(
		"Audit resource file for Internationalization problems", work);

	for (IResource resource : resources) {
	    monitor.subTask("'" + resource.getFullPath().toOSString() + "'");
	    if (monitor.isCanceled()) {
		throw new OperationCanceledException();
	    }

	    if (!EditorUtils.deleteAuditMarkersForResource(resource)) {
		continue;
	    }

	    if (ResourceBundleManager.isResourceExcluded(resource)) {
		continue;
	    }

	    if (!resource.exists()) {
		continue;
	    }

	    for (I18nAuditor ra : ExtensionManager.getRegisteredI18nAuditors()) {
		if (ra instanceof I18nResourceAuditor
			&& !(configuration.getAuditResource())) {
		    continue;
		}
		if (ra instanceof I18nRBAuditor
			&& !(configuration.getAuditRb())) {
		    continue;
		}

		try {
		    if (monitor.isCanceled()) {
			monitor.done();
			break;
		    }

		    if (ra.isResourceOfType(resource)) {
			ra.audit(resource);
		    }
		} catch (Exception e) {
		    Logger.logError(
			    "Error during auditing '" + resource.getFullPath()
				    + "'", e);
		}
	    }

	    if (monitor != null) {
		monitor.worked(1);
	    }
	}

	for (I18nAuditor a : ExtensionManager.getRegisteredI18nAuditors()) {
	    if (a instanceof I18nResourceAuditor) {
		handleI18NAuditorMarkers((I18nResourceAuditor) a);
	    }
	    if (a instanceof I18nRBAuditor) {
		handleI18NAuditorMarkers((I18nRBAuditor) a);
		((I18nRBAuditor) a).resetProblems();
	    }
	}

	monitor.done();
    }

    private void handleI18NAuditorMarkers(I18nResourceAuditor ra) {
	try {
	    for (ILocation problem : ra.getConstantStringLiterals()) {
		EditorUtils
			.reportToMarker(
				org.eclipse.babel.tapiji.tools.core.util.EditorUtils
					.getFormattedMessage(
						org.eclipse.babel.tapiji.tools.core.util.EditorUtils.MESSAGE_NON_LOCALIZED_LITERAL,
						new String[] { problem
							.getLiteral() }),
				problem,
				IMarkerConstants.CAUSE_CONSTANT_LITERAL, "",
				(ILocation) problem.getData(), ra
					.getContextId());
	    }

	    // Report all broken Resource-Bundle references
	    for (ILocation brokenLiteral : ra.getBrokenResourceReferences()) {
		EditorUtils
			.reportToMarker(
				org.eclipse.babel.tapiji.tools.core.util.EditorUtils
					.getFormattedMessage(
						org.eclipse.babel.tapiji.tools.core.util.EditorUtils.MESSAGE_BROKEN_RESOURCE_REFERENCE,
						new String[] {
							brokenLiteral
								.getLiteral(),
							((ILocation) brokenLiteral
								.getData())
								.getLiteral() }),
				brokenLiteral,
				IMarkerConstants.CAUSE_BROKEN_REFERENCE,
				brokenLiteral.getLiteral(),
				(ILocation) brokenLiteral.getData(), ra
					.getContextId());
	    }

	    // Report all broken definitions to Resource-Bundle
	    // references
	    for (ILocation brokenLiteral : ra.getBrokenBundleReferences()) {
		EditorUtils
			.reportToMarker(
				org.eclipse.babel.tapiji.tools.core.util.EditorUtils
					.getFormattedMessage(
						org.eclipse.babel.tapiji.tools.core.util.EditorUtils.MESSAGE_BROKEN_RESOURCE_BUNDLE_REFERENCE,
						new String[] { brokenLiteral
							.getLiteral() }),
				brokenLiteral,
				IMarkerConstants.CAUSE_BROKEN_RB_REFERENCE,
				brokenLiteral.getLiteral(),
				(ILocation) brokenLiteral.getData(), ra
					.getContextId());
	    }
	} catch (Exception e) {
	    Logger.logError(
		    "Exception during reporting of Internationalization errors",
		    e);
	}
    }

    private void handleI18NAuditorMarkers(I18nRBAuditor ra) {
	IConfiguration configuration = ConfigurationManager.getInstance()
		.getConfiguration();
	try {
	    // Report all unspecified keys
	    if (configuration.getAuditMissingValue()) {
		for (ILocation problem : ra.getUnspecifiedKeyReferences()) {
		    EditorUtils
			    .reportToRBMarker(
				    org.eclipse.babel.tapiji.tools.core.util.EditorUtils
					    .getFormattedMessage(
						    org.eclipse.babel.tapiji.tools.core.util.EditorUtils.MESSAGE_UNSPECIFIED_KEYS,
						    new String[] {
							    problem.getLiteral(),
							    problem.getFile()
								    .getName() }),
				    problem,
				    IMarkerConstants.CAUSE_UNSPEZIFIED_KEY,
				    problem.getLiteral(), "",
				    (ILocation) problem.getData(), ra
					    .getContextId());
		}
	    }

	    // Report all same values
	    if (configuration.getAuditSameValue()) {
		Map<ILocation, ILocation> sameValues = ra
			.getSameValuesReferences();
		for (ILocation problem : sameValues.keySet()) {
		    EditorUtils
			    .reportToRBMarker(
				    org.eclipse.babel.tapiji.tools.core.util.EditorUtils
					    .getFormattedMessage(
						    org.eclipse.babel.tapiji.tools.core.util.EditorUtils.MESSAGE_SAME_VALUE,
						    new String[] {
							    problem.getFile()
								    .getName(),
							    sameValues
								    .get(problem)
								    .getFile()
								    .getName(),
							    problem.getLiteral() }),
				    problem,
				    IMarkerConstants.CAUSE_SAME_VALUE,
				    problem.getLiteral(),
				    sameValues.get(problem).getFile().getName(),
				    (ILocation) problem.getData(), ra
					    .getContextId());
		}
	    }
	    // Report all missing languages
	    if (configuration.getAuditMissingLanguage()) {
		for (ILocation problem : ra.getMissingLanguageReferences()) {
		    EditorUtils
			    .reportToRBMarker(
				    org.eclipse.babel.tapiji.tools.core.util.EditorUtils
					    .getFormattedMessage(
						    org.eclipse.babel.tapiji.tools.core.util.EditorUtils.MESSAGE_MISSING_LANGUAGE,
						    new String[] {
							    RBFileUtils
								    .getCorrespondingResourceBundleId(problem
									    .getFile()),
							    problem.getLiteral() }),
				    problem,
				    IMarkerConstants.CAUSE_MISSING_LANGUAGE,
				    problem.getLiteral(), "",
				    (ILocation) problem.getData(), ra
					    .getContextId());
		}
	    }
	} catch (Exception e) {
	    Logger.logError(
		    "Exception during reporting of Internationalization errors",
		    e);
	}
    }

    @SuppressWarnings("unused")
    private void setProgress(IProgressMonitor monitor, int progress)
	    throws InterruptedException {
	monitor.worked(progress);

	if (monitor.isCanceled()) {
	    throw new OperationCanceledException();
	}

	if (isInterrupted()) {
	    throw new InterruptedException();
	}
    }

    @Override
    protected void clean(IProgressMonitor monitor) throws CoreException {
	// TODO Auto-generated method stub
	super.clean(monitor);
    }

    public static void addBuilderToProject(IProject project) {
	Logger.logInfo("Internationalization-Builder registered for '"
		+ project.getName() + "'");

	// Only for open projects
	if (!project.isOpen()) {
	    return;
	}

	IProjectDescription description = null;
	try {
	    description = project.getDescription();
	} catch (CoreException e) {
	    Logger.logError(e);
	    return;
	}

	// Check if the builder is already associated to the specified project
	ICommand[] commands = description.getBuildSpec();
	for (ICommand command : commands) {
	    if (command.getBuilderName().equals(BUILDER_ID)) {
		return;
	    }
	}

	// Associate the builder with the project
	ICommand builderCmd = description.newCommand();
	builderCmd.setBuilderName(BUILDER_ID);
	List<ICommand> newCommands = new ArrayList<ICommand>();
	newCommands.addAll(Arrays.asList(commands));
	newCommands.add(builderCmd);
	description.setBuildSpec(newCommands.toArray(new ICommand[newCommands
		.size()]));

	try {
	    project.setDescription(description, null);
	} catch (CoreException e) {
	    Logger.logError(e);
	}
    }

    public static void removeBuilderFromProject(IProject project) {
	// Only for open projects
	if (!project.isOpen()) {
	    return;
	}

	try {
	    project.deleteMarkers(EditorUtils.MARKER_ID, false,
		    IResource.DEPTH_INFINITE);
	    project.deleteMarkers(EditorUtils.RB_MARKER_ID, false,
		    IResource.DEPTH_INFINITE);
	} catch (CoreException e1) {
	    Logger.logError(e1);
	}

	IProjectDescription description = null;
	try {
	    description = project.getDescription();
	} catch (CoreException e) {
	    Logger.logError(e);
	    return;
	}

	// remove builder from project
	int idx = -1;
	ICommand[] commands = description.getBuildSpec();
	for (int i = 0; i < commands.length; i++) {
	    if (commands[i].getBuilderName().equals(BUILDER_ID)) {
		idx = i;
		break;
	    }
	}
	if (idx == -1) {
	    return;
	}

	List<ICommand> newCommands = new ArrayList<ICommand>();
	newCommands.addAll(Arrays.asList(commands));
	newCommands.remove(idx);
	description.setBuildSpec(newCommands.toArray(new ICommand[newCommands
		.size()]));

	try {
	    project.setDescription(description, null);
	} catch (CoreException e) {
	    Logger.logError(e);
	}

    }

}
