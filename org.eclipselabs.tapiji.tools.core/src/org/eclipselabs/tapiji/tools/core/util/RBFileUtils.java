package org.eclipselabs.tapiji.tools.core.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipselabs.tapiji.tools.core.model.manager.ResourceBundleManager;


/**
 * 
 * @author mgasser
 *
 */
public class RBFileUtils extends Action{
	public static final String PROPERTIES_EXT = "properties";
	
    
	/**
	 * Check whether a file is a properties-file/resourcebundle-file
	 */
	public static boolean checkIsResourceBundleFile(IResource file) {
		if (!(file instanceof IFile)) return false;
		if (file.getName().equals("build.properties")) return false;
		if (!ResourceUtils.isResourceBundle(file)) return false;
		return true;
	}
	
	/**
	 * Checks whether a RB-file has a problemmarker
	 */
	public static boolean hasResourceBundleMarker(IResource r){
		try {
			if(r.findMarkers(EditorUtils.RB_MARKER_ID, true, IResource.DEPTH_INFINITE).length > 0)
						return true;
			else return false;
		} catch (CoreException e) {
			return false;
		}
	}

	
	/**
	 * @return the locale of a given properties-file
	 */
	public static Locale getLocale(IFile file){
		String localeID = file.getName();
		localeID = localeID.substring(0, localeID.length() - "properties".length() - 1);
		String baseBundleName = ResourceBundleManager.getResourceBundleName(file);
		
		Locale locale;
		if (localeID.length() == baseBundleName.length()) {
			locale = new Locale("");			//Default locale
		} else {
			localeID = localeID.substring(baseBundleName.length() + 1);
			String[] localeTokens = localeID.split("_");
			switch (localeTokens.length) {
			case 1:
				locale = new Locale(localeTokens[0]);
				break;
			case 2:
				locale = new Locale(localeTokens[0], localeTokens[1]);
				break;
			case 3:
				locale = new Locale(localeTokens[0], localeTokens[1], localeTokens[2]);
				break;
			default:
				locale = new Locale("");
				break;
			}
		}
		return locale;
	}

	/**
	 * @return number of resourcebundles in the subtree 
	 */
	public static int countRecursiveResourceBundle(IContainer container) {		
		return getSubResourceBundle(container).size();
	}
	
	/**
	 * 
	 * @param container
	 * @return
	 */
	private static List<String> getSubResourceBundle(IContainer container){
		// Slower for not deep folder-structure
		ResourceBundleManager rbmanager = ResourceBundleManager.getManager(container.getProject());
		
		String conatinerId = container.getFullPath().toString();
		List<String> subResourceBundles = new ArrayList<String>();

		for (String rbId : rbmanager.getResourceBundleIdentifiers()) {
			for(IResource r : rbmanager.getResourceBundles(rbId)){
				if (r.getFullPath().toString().contains(conatinerId) && (!subResourceBundles.contains(rbId))){
					subResourceBundles.add(rbId);
				}
			}
		}
		return subResourceBundles;
	}
	
	/**
	 * 
	 * @param container
	 * @return List with all resourcebundles in this container
	 */
	public static Set<String> getResourceBundleIds (IContainer container) {
		Set<String> resourcebundles = new HashSet<String>();
		
		try {
			for(IResource r : container.members()){
				if (r instanceof IFile) {
					String resourcebundle = getCorrespondingResourceBundleId((IFile)r);
					if (resourcebundle != null) resourcebundles.add(resourcebundle);
				}
			}
		} catch (CoreException e) {/*resourcebundle.size()==0*/}
		
		return resourcebundles;
	}
	
	/**
	 * 
	 * @param file
	 * @return ResourceBundle name or null if no ResourceBundle contains file
	 */
	//TODO integrate in ResourceBundleManager
	public static String getCorrespondingResourceBundleId (IFile file) {
		ResourceBundleManager rbmanager =  ResourceBundleManager.getManager(file.getProject());
		String possibleRBId = null;
		
		if (checkIsResourceBundleFile((IFile) file)) {
			possibleRBId = ResourceBundleManager.getResourceBundleId(file);

			for (String rbId : rbmanager.getResourceBundleIdentifiers()){
				if ( possibleRBId.equals(rbId) ) 
					return possibleRBId;
			}
		}
		return null;
	}
}
