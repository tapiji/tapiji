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
package auditor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.babel.tapiji.tools.core.extensions.ILocation;
import org.eclipse.babel.tapiji.tools.core.extensions.IMarkerConstants;
import org.eclipse.babel.tapiji.tools.core.model.manager.ResourceBundleManager;
import org.eclipse.babel.tapiji.tools.core.ui.extensions.I18nResourceAuditor;
import org.eclipse.babel.tapiji.tools.core.ui.quickfix.CreateResourceBundle;
import org.eclipse.babel.tapiji.tools.core.ui.quickfix.CreateResourceBundleEntry;
import org.eclipse.babel.tapiji.tools.core.ui.quickfix.IncludeResource;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.ui.IMarkerResolution;

import quickfix.ExportToResourceBundleResolution;
import quickfix.ReplaceResourceBundleDefReference;
import quickfix.ReplaceResourceBundleReference;

public class JSFResourceAuditor extends I18nResourceAuditor {

	public String[] getFileEndings() {
		return new String[] { "xhtml", "jsp" };
	}

	public void audit(IResource resource) {
		parse(resource);
	}

	private void parse(IResource resource) {

	}

	@Override
	public List<ILocation> getConstantStringLiterals() {
		return new ArrayList<ILocation>();
	}

	@Override
	public List<ILocation> getBrokenResourceReferences() {
		return new ArrayList<ILocation>();
	}

	@Override
	public List<ILocation> getBrokenBundleReferences() {
		return new ArrayList<ILocation>();
	}

	@Override
	public String getContextId() {
		return "jsf";
	}

	@Override
	public List<IMarkerResolution> getMarkerResolutions(IMarker marker) {
		List<IMarkerResolution> resolutions = new ArrayList<IMarkerResolution>();
		int cause = marker.getAttribute("cause", -1);

		switch (cause) {
		case IMarkerConstants.CAUSE_CONSTANT_LITERAL:
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

				if (bundleResources != null && bundleResources.size() > 0)
					resolutions
					        .add(new IncludeResource(bname, bundleResources));
				else
					resolutions.add(new CreateResourceBundle(bname, marker
					        .getResource(), dataStart, dataEnd));

				resolutions.add(new ReplaceResourceBundleDefReference(bname,
				        dataStart, dataEnd));
			}

			break;
		case IMarkerConstants.CAUSE_BROKEN_RB_REFERENCE:
			String bname = marker.getAttribute("key", "");

			Set<IResource> bundleResources = ResourceBundleManager.getManager(
			        marker.getResource().getProject())
			        .getAllResourceBundleResources(bname);

			if (bundleResources != null && bundleResources.size() > 0)
				resolutions.add(new IncludeResource(bname, bundleResources));
			else
				resolutions.add(new CreateResourceBundle(marker.getAttribute(
				        "key", ""), marker.getResource(), marker.getAttribute(
				        IMarker.CHAR_START, 0), marker.getAttribute(
				        IMarker.CHAR_END, 0)));
			resolutions.add(new ReplaceResourceBundleDefReference(marker
			        .getAttribute("key", ""), marker.getAttribute(
			        IMarker.CHAR_START, 0), marker.getAttribute(
			        IMarker.CHAR_END, 0)));
		}

		return resolutions;
	}

}
