package org.eclipselabs.tapiji.tools.rbmanager.auditor.quickfix;

import java.util.Locale;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipselabs.tapiji.tools.core.model.manager.ResourceBundleManager;
import org.eclipselabs.tapiji.tools.core.util.LanguageUtils;

public class MissingLanguageResolution implements IMarkerResolution2 {

	private Locale language;
	
	public MissingLanguageResolution(Locale language){
		this.language = language;
	}
	
	@Override
	public String getLabel() {
		return "Add missing language '"+ language +"'";
	}

	@Override
	public void run(IMarker marker) {
		IResource res = marker.getResource();
		String rbId = ResourceBundleManager.getResourceBundleId(res);
		LanguageUtils.addLanguageToResourceBundle(res.getProject(), rbId, language);
	}

	@Override
	public String getDescription() {
		return "Creates a new localized properties-file with the same basename as the resourcebundle";
	}

	@Override
	public Image getImage() {
		return null;
	}

}
