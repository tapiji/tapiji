package org.eclipselabs.tapiji.tools.core.util;

import org.eclipse.core.resources.IResource;
import org.eclipselabs.tapiji.tools.core.model.manager.ResourceBundleManager;


public class ResourceUtils {

	private final static String REGEXP_RESOURCE_KEY 			= "[\\p{Alnum}\\.]*";
	private final static String REGEXP_RESOURCE_NO_BUNDLENAME 	= "[^\\p{Alnum}\\.]*";
	
	public static boolean isValidResourceKey (String key) {
		boolean isValid = false;
		
		if (key != null && key.trim().length() > 0) {
			isValid = key.matches(REGEXP_RESOURCE_KEY);
		}
		
		return isValid;
	}
	
	public static String deriveNonExistingRBName (String nameProposal, ResourceBundleManager manager) {
		// Adapt the proposal to the requirements for Resource-Bundle names
		nameProposal = nameProposal.replaceAll(REGEXP_RESOURCE_NO_BUNDLENAME, "");
		
		int i = 0;
		do {
			if (manager.getResourceBundleIdentifiers().contains(nameProposal) || nameProposal.length() == 0) {
				nameProposal = nameProposal + (++i);
			} else
				break;
		} while (true);
		
		return nameProposal;
	}
	
	public static boolean isResourceBundle (IResource res) {
		boolean result = false;
		
		if (res != null && res.getType() == IResource.FILE && !res.isDerived() && res.getFileExtension() != null
			&& res.getFileExtension().equalsIgnoreCase("properties")) {
			result = true;
		}
		
		return result;
	}
	
	public static boolean isJavaCompUnit (IResource res) {
		boolean result = false;
		
		if (res.getType() == IResource.FILE && !res.isDerived() &&
			res.getFileExtension().equalsIgnoreCase("java")) {
			result = true;
		}
				
		return result;
	}

	public static boolean isJSPResource(IResource res) {
		boolean result = false;
		
		if (res.getType() == IResource.FILE && !res.isDerived() &&
		    (res.getFileExtension().equalsIgnoreCase("jsp") ||
		     res.getFileExtension().equalsIgnoreCase("xhtml"))) {
			result = true;
		}
				
		return result;
	}
}
