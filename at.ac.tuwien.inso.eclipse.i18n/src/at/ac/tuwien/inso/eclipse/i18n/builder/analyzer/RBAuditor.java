package at.ac.tuwien.inso.eclipse.i18n.builder.analyzer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.ui.IMarkerResolution;

import at.ac.tuwien.inso.eclipse.i18n.extensions.I18nResourceAuditor;
import at.ac.tuwien.inso.eclipse.i18n.extensions.ILocation;
import at.ac.tuwien.inso.eclipse.i18n.model.manager.ResourceBundleManager;

public class RBAuditor extends I18nResourceAuditor {

	@Override
	public void audit(IResource resource) {
		ResourceBundleManager.getManager(resource.getProject()).addBundleResource (resource);
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
	public List<IMarkerResolution> getMarkerResolutions(IMarker marker,
			int cause) {
		return null;
	}

}
