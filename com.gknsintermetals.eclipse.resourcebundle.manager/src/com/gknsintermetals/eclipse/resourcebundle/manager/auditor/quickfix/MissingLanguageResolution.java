package com.gknsintermetals.eclipse.resourcebundle.manager.auditor.quickfix;

import java.util.Locale;

import org.eclipse.core.resources.IMarker;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution2;

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
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getDescription() {
		return "Create a new localized properties-file with the same basename as the resourcebundle";
	}

	@Override
	public Image getImage() {
		return null;
	}

}
