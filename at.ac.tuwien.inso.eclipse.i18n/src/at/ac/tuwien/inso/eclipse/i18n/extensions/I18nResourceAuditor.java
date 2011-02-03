package at.ac.tuwien.inso.eclipse.i18n.extensions;

import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.ui.IMarkerResolution;

public abstract class I18nResourceAuditor {
	
	public abstract void audit (IResource resource);
	
	public abstract String[] getFileEndings();
	
	public abstract List<ILocation> getConstantStringLiterals();
	
	public abstract List<ILocation> getBrokenResourceReferences();
	
	public abstract List<ILocation> getBrokenBundleReferences();
	
	public abstract String getContextId();
	
	public abstract List<IMarkerResolution> getMarkerResolutions(IMarker marker, int cause);
	
	public boolean isResourceOfType (IResource resource) {
		for (String ending : getFileEndings()) {
			if (resource.getFileExtension().equalsIgnoreCase(ending))
				return true;
		}
		return false;
	}
	
}
