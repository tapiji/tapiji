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
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipselabs.tapiji.tools.core.Activator;
import org.eclipselabs.tapiji.tools.core.model.manager.ResourceBundleManager;
import org.eclipselabs.tapiji.tools.core.model.preferences.CheckItem;
import org.eclipselabs.tapiji.tools.core.model.preferences.TapiJIPreferences;


/**
 * 
 * @author mgasser
 *
 */
public class RBFileUtils extends Action{
	public static final String PROPERTIES_EXT = "properties";
	
    
	/**
	 * Returns true if a file is a ResourceBundle-file
	 */
	public static boolean isResourceBundleFile(IResource file) {
		boolean isValied = false;
		
		if (file != null && file instanceof IFile && !file.isDerived() && 
				file.getFileExtension()!=null && file.getFileExtension().equalsIgnoreCase("properties")){
			isValied = true;
			
			//Check if file is not in the blacklist
			IPreferenceStore pref = null;
			if (Activator.getDefault() != null)
				pref = Activator.getDefault().getPreferenceStore();
			
			if (pref != null){
				List<CheckItem> list = TapiJIPreferences.getNonRbPattern();
				for (CheckItem item : list){
					if (item.getChecked() && file.getFullPath().toString().matches(item.getName())){
						isValied = false;
						
						//if properties-file is not RB-file and has ResouceBundleMarker, deletes all ResouceBundleMarker of the file
						if (hasResourceBundleMarker(file))
							try {
								file.deleteMarkers(EditorUtils.RB_MARKER_ID, true, IResource.DEPTH_INFINITE);
							} catch (CoreException e) {
							}
					}
				}
			}
		}
		
		return isValied;
	}
	
	/**
	 * Checks whether a RB-file has a problem-marker
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
	 * @return number of ResourceBundles in the subtree 
	 */
	public static int countRecursiveResourceBundle(IContainer container) {		
		return getSubResourceBundle(container).size();
	}
	
	
	private static List<String> getSubResourceBundle(IContainer container){
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
	 * @param container
	 * @return Set with all ResourceBundles in this container
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
	 * @return ResourceBundle-name or null if no ResourceBundle contains the file
	 */
	//TODO integrate in ResourceBundleManager
	public static String getCorrespondingResourceBundleId (IFile file) {
		ResourceBundleManager rbmanager =  ResourceBundleManager.getManager(file.getProject());
		String possibleRBId = null;
		
		if (isResourceBundleFile((IFile) file)) {
			possibleRBId = ResourceBundleManager.getResourceBundleId(file);

			for (String rbId : rbmanager.getResourceBundleIdentifiers()){
				if ( possibleRBId.equals(rbId) ) 
					return possibleRBId;
			}
		}
		return null;
	}
}
