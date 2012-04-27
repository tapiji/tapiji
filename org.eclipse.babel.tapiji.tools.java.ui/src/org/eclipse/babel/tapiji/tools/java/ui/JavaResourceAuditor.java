/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.java.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.babel.tapiji.tools.core.extensions.I18nResourceAuditor;
import org.eclipse.babel.tapiji.tools.core.extensions.ILocation;
import org.eclipse.babel.tapiji.tools.core.extensions.IMarkerConstants;
import org.eclipse.babel.tapiji.tools.core.model.SLLocation;
import org.eclipse.babel.tapiji.tools.core.model.manager.ResourceBundleManager;
import org.eclipse.babel.tapiji.tools.core.ui.quickfix.CreateResourceBundle;
import org.eclipse.babel.tapiji.tools.core.ui.quickfix.CreateResourceBundleEntry;
import org.eclipse.babel.tapiji.tools.core.ui.quickfix.IncludeResource;
import org.eclipse.babel.tapiji.tools.java.ui.quickfix.ExcludeResourceFromInternationalization;
import org.eclipse.babel.tapiji.tools.java.ui.quickfix.ExportToResourceBundleResolution;
import org.eclipse.babel.tapiji.tools.java.ui.quickfix.IgnoreStringFromInternationalization;
import org.eclipse.babel.tapiji.tools.java.ui.quickfix.ReplaceResourceBundleDefReference;
import org.eclipse.babel.tapiji.tools.java.ui.quickfix.ReplaceResourceBundleReference;
import org.eclipse.babel.tapiji.tools.java.ui.util.ASTutilsUI;
import org.eclipse.babel.tapiji.tools.java.visitor.ResourceAuditVisitor;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.ui.IMarkerResolution;

public class JavaResourceAuditor extends I18nResourceAuditor {

	protected List<SLLocation> constantLiterals = new ArrayList<SLLocation>();
	protected List<SLLocation> brokenResourceReferences = new ArrayList<SLLocation>();
	protected List<SLLocation> brokenBundleReferences = new ArrayList<SLLocation>();

	@Override
	public String[] getFileEndings() {
		return new String[] { "java" };
	}

	@Override
	public void audit(IResource resource) {

		ResourceAuditVisitor csav = new ResourceAuditVisitor(resource
		        .getProject().getFile(resource.getProjectRelativePath()),
		        resource.getProject().getName());

		// get a reference to the shared AST of the loaded CompilationUnit
		CompilationUnit cu = ASTutilsUI.getCompilationUnit(resource);
		if (cu == null) {
			System.out.println("Cannot audit resource: "
			        + resource.getFullPath());
			return;
		}
		cu.accept(csav);

		// Report all constant string literals
		constantLiterals = csav.getConstantStringLiterals();

		// Report all broken Resource-Bundle references
		brokenResourceReferences = csav.getBrokenResourceReferences();

		// Report all broken definitions to Resource-Bundle references
		brokenBundleReferences = csav.getBrokenRBReferences();
	}

	@Override
	public List<ILocation> getConstantStringLiterals() {
		return new ArrayList<ILocation>(constantLiterals);
	}

	@Override
	public List<ILocation> getBrokenResourceReferences() {
		return new ArrayList<ILocation>(brokenResourceReferences);
	}

	@Override
	public List<ILocation> getBrokenBundleReferences() {
		return new ArrayList<ILocation>(brokenBundleReferences);
	}

	@Override
	public String getContextId() {
		return "java";
	}

	@Override
	public List<IMarkerResolution> getMarkerResolutions(IMarker marker) {
		List<IMarkerResolution> resolutions = new ArrayList<IMarkerResolution>();

		switch (marker.getAttribute("cause", -1)) {
		case IMarkerConstants.CAUSE_CONSTANT_LITERAL:
			resolutions.add(new IgnoreStringFromInternationalization());
			resolutions.add(new ExcludeResourceFromInternationalization());
			resolutions.add(new ExportToResourceBundleResolution());
			break;
		case IMarkerConstants.CAUSE_BROKEN_REFERENCE:
			String dataName = marker.getAttribute("bundleName", "");
			int dataStart = marker.getAttribute("bundleStart", 0);
			int dataEnd = marker.getAttribute("bundleEnd", 0);

			IProject project = marker.getResource().getProject();
			ResourceBundleManager manager = ResourceBundleManager
			        .getManager(project);

			if (manager.getResourceBundle(dataName) != null) {
				String key = marker.getAttribute("key", "");

				resolutions.add(new CreateResourceBundleEntry(key, dataName));
				resolutions.add(new ReplaceResourceBundleReference(key,
				        dataName));
				resolutions.add(new ReplaceResourceBundleDefReference(dataName,
				        dataStart, dataEnd));
			} else {
				String bname = dataName;

				Set<IResource> bundleResources = ResourceBundleManager
				        .getManager(marker.getResource().getProject())
				        .getAllResourceBundleResources(bname);

				if (bundleResources != null && bundleResources.size() > 0) {
					resolutions
					        .add(new IncludeResource(bname, bundleResources));
				} else {
					resolutions.add(new CreateResourceBundle(bname, marker
					        .getResource(), dataStart, dataEnd));
				}
				resolutions.add(new ReplaceResourceBundleDefReference(bname,
				        dataStart, dataEnd));
			}

			break;
		case IMarkerConstants.CAUSE_BROKEN_RB_REFERENCE:
			String bname = marker.getAttribute("key", "");

			Set<IResource> bundleResources = ResourceBundleManager.getManager(
			        marker.getResource().getProject())
			        .getAllResourceBundleResources(bname);

			if (bundleResources != null && bundleResources.size() > 0) {
				resolutions.add(new IncludeResource(bname, bundleResources));
			} else {
				resolutions.add(new CreateResourceBundle(marker.getAttribute(
				        "key", ""), marker.getResource(), marker.getAttribute(
				        IMarker.CHAR_START, 0), marker.getAttribute(
				        IMarker.CHAR_END, 0)));
			}
			resolutions.add(new ReplaceResourceBundleDefReference(marker
			        .getAttribute("key", ""), marker.getAttribute(
			        IMarker.CHAR_START, 0), marker.getAttribute(
			        IMarker.CHAR_END, 0)));
		}

		return resolutions;
	}

}
