package org.eclipselabs.tapiji.tools.core.builder;

import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator2;
import org.eclipselabs.tapiji.tools.core.extensions.I18nAuditor;
import org.eclipselabs.tapiji.tools.core.extensions.I18nResourceAuditor;
import org.eclipselabs.tapiji.tools.core.model.exception.NoSuchResourceAuditorException;


public class ViolationResolutionGenerator implements IMarkerResolutionGenerator2 {

	@Override
	public boolean hasResolutions(IMarker marker) {
		return true;
	}

	@Override
	public IMarkerResolution[] getResolutions(IMarker marker) {
		String contextId = marker.getAttribute("context", "");
		
		// find resolution generator for the given context
		try {
			I18nAuditor auditor = StringLiteralAuditor.getI18nAuditorByContext(contextId);
			List<IMarkerResolution> resolutions = auditor.getMarkerResolutions(marker);
			return resolutions.toArray(new IMarkerResolution[resolutions.size()]);
		} catch (NoSuchResourceAuditorException e) {}
		
		return new IMarkerResolution[0]; 
	}

}
