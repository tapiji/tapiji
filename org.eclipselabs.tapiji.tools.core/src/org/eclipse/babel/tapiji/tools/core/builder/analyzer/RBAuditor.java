package org.eclipse.babel.tapiji.tools.core.builder.analyzer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.babel.tapiji.tools.core.extensions.I18nResourceAuditor;
import org.eclipse.babel.tapiji.tools.core.extensions.ILocation;
import org.eclipse.babel.tapiji.tools.core.model.manager.ResourceBundleManager;
import org.eclipse.babel.tapiji.tools.core.util.RBFileUtils;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.ui.IMarkerResolution;


public class RBAuditor extends I18nResourceAuditor {

	@Override
	public void audit(IResource resource) {
		if (RBFileUtils.isResourceBundleFile(resource)) {
			ResourceBundleManager.getManager(resource.getProject()).addBundleResource (resource);
		}
	}

	@Override
	public String[] getFileEndings() {
		return new String [] { "properties" };
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
		return "resource_bundle";
	}

	@Override
	public List<IMarkerResolution> getMarkerResolutions(IMarker marker) {
		return null;
	}

}
