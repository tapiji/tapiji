package org.eclipselabs.tapiji.translator.rap.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.rwt.RWT;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipselabs.tapiji.translator.rap.model.user.PropertiesFile;
import org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle;
import org.eclipselabs.tapiji.translator.rap.model.user.User;
import org.eclipselabs.tapiji.translator.rap.model.user.UserFactory;
import org.eclipselabs.tapiji.translator.views.StorageView;
import org.hibernate.criterion.Projection;

/**
 * Utility methods to handle the storage of resource bundles. 
 * @author Matthias Lettmayer
 *
 */
public class StorageUtils {
	
	/**
	 * Checks if the given resource bundle name exists already in the storage of the logged in user.
	 * @param bundleName A resource bundle name.
	 * @return Returns true if the resource bundle with the given name exists already in user storage, otherwise false.
	 */
	public static boolean existsUserRBName(String bundleName) {
		if (! UserUtils.isUserLoggedIn())
			return false;
		
		User user = (User) RWT.getSessionStore().getAttribute(UserUtils.SESSION_USER_ATT);
		
		List<ResourceBundle> rbs = user.getStoredRBs();		
		for (ResourceBundle rb : rbs) {
			if (rb.getName().equals(bundleName))
				return true;
		}
		
		return false;
	}
	
	/**
	 * Checks if the given resource bundle name exists already in the storage of the user session.
	 * @param bundleName A resource bundle name.
	 * @return Returns true if the resource bundle with the given name exists already in user session storage, otherwise false.
	 */
	public static boolean existsTempRBName(String bundleName) {
		for (ResourceBundle rb : getSessionRBs()) {
			if (rb.getName().equals(bundleName))
				return true;
		}
		
		return false;
	}
	
	/**
	 *  Synchronizes the storage (of logged in user) of database with file system.
	 *  Removes resource bundles and properties files, which don't exist anymore in user project, 
	 *  from database. Adds resource bundles and properties files, which don't exist in database, but
	 *  on file system, to database.
	 */
	public static void syncStorageWithDatabase() {
		User user = (User) RWT.getSessionStore().getAttribute(UserUtils.SESSION_USER_ATT);
		List<ResourceBundle> rbs = new ArrayList<ResourceBundle>(user.getStoredRBs());
		List<PropertiesFile> allUserFiles = new ArrayList<PropertiesFile>();
		boolean saveToDB = false;
		
		// delete non existing properties files from db
		for (ResourceBundle rb : rbs) {
			List<PropertiesFile> localFiles = new ArrayList<PropertiesFile>(rb.getLocalFiles());
			IProject project = null;
			try {
				project = FileRAPUtils.getProject(rb.getOwner().getUsername());
			} catch (CoreException e) {
				e.printStackTrace();
			}			
			for (PropertiesFile file : localFiles) {
				if (! FileRAPUtils.existsProjectFile(project, file.getFilename())) {
					rb.getLocalFiles().remove(file);
					saveToDB = true;
				}
			}
			if (rb.getLocalFiles().isEmpty()) {
				EcoreUtil.delete(rb);
				user.getStoredRBs().remove(rb);
				// TODO remove resource bundle from DB				
				saveToDB = true;
			}							
			allUserFiles.addAll(rb.getLocalFiles());
		}
		
		// add existing properties files which are not in db to resource bundle
		for (IFile ifile : FileRAPUtils.getFilesFromProject(FileRAPUtils.getUserProject())) {
			PropertiesFile file = createPropertiesFile(ifile);
			// file doesn't exists in db yet
			if (! allUserFiles.contains(file)) {				
				String bundleName = FileRAPUtils.getBundleName(file.getPath());
				// find resource bundle with bundle name of new file
				ResourceBundle rb = getResourceBundle(bundleName, false);
				// add file to existing rb and persist
				if (rb != null) {
					rb.getLocalFiles().add(file);
					saveToDB = true;
				// rb doesn't exist yet -> create new rb and add file
				} else {
					ResourceBundle newRB = createResourceBundle(ifile);
					if (newRB != null) {
						user.getStoredRBs().add(newRB);
						newRB.setOwner(user);
						saveToDB = true;
					}
				}
			}
		}
		
		if (saveToDB)
			try {
				user.eResource().save(null);
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	/**
	 * Stores and persists a new resource bundle on file system and saves meta data in database.
	 * Moves files from session project to user project. User must be logged in.
	 * @param rb The new resource bundle, which gets stored.
	 * @return the stored resource bundle
	 */
	public static ResourceBundle storeRB(ResourceBundle rb) {
		if (! rb.isTemporary())
			return null;
		
		// move local files into project dir
		for (PropertiesFile file : rb.getLocalFiles()) {
			IFile ifile = FileRAPUtils.getFile(file);
			IPath newPath = new Path(FileRAPUtils.getUserProject().getFullPath()+java.io.File.separator+file.getFilename());
			try {
				ifile.refreshLocal(IResource.DEPTH_ZERO, null);
				ifile.move(newPath, IResource.KEEP_HISTORY, null);
			} catch (CoreException e) {
				e.printStackTrace();
			}
			
			IFile movedIFile = FileRAPUtils.getUserProject().getFile(file.getFilename());
			file.setPath(movedIFile.getLocation().toOSString());
		}
		
		// add rb to user (set rb to non temporary)
		User user = UserUtils.getUser();
		user.getStoredRBs().add(rb);
		// set owner (set rb to non temporary)
		rb.setOwner(user);
		
		// persist rb and properties files
		try {
			user.eResource().save(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return rb;
	}
	
	/**
	 * Renames a existing resource bundle. The resource bundle can be temporary or stored.
	 * @param rb The resource bundle, which shall be renamed.
	 * @param newBundleName The new name of the resource bundle.
	 */
	public static void renameRB(ResourceBundle rb, String newBundleName) {
		rb.setName(newBundleName);
		
		for (PropertiesFile file : rb.getLocalFiles()) {
			IFile ifile = FileRAPUtils.getFile(file);
			ifile.exists();
			String local = FileRAPUtils.getLocal(file.getFilename());			
			String newFilename = newBundleName+local+"."+ifile.getFileExtension();
			
			FileRAPUtils.renameIFile(ifile, newFilename);
			file.setFilename(newFilename);
		}
		
		if (! rb.isTemporary())
			try {
				rb.getOwner().eResource().save(null);
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	/**
	 * Returns all resource bundles from the user session storage.
	 * @return A list of resource bundles stored in the user session project.
	 */
	public static List<ResourceBundle> getSessionRBs() {
		return createResourceBundles(FileRAPUtils.getFilesFromProject(FileRAPUtils.getSessionProject()));
	}
	
	/**
	 * Creates a new temporary resource bundle (has no association to an user) from an existing properties file. 
	 * No entry in database will be created and the existing file won't be modified.
	 * @param ifile An existing eclipse resource file which represents a properties file.
	 * @return A new temporary resource bundle, which contains the given file.
	 */
	public static ResourceBundle createResourceBundle(IFile ifile) {
		List<IFile> list = new ArrayList<IFile>();
		list.add(ifile);
		List<ResourceBundle> result = createResourceBundles(list);
		if (result.isEmpty())
			return null;
		return result.get(0);
	}
	
	/**
	 * Creates new temporary resource bundles (have no association to an user) from a list of 
	 * existing properties files. Files with same bundle name, will be added to same resource bundle.
	 * No entry in database will be created and the existing files won't be modified.
	 * 
	 * @param ifiles A list of existing eclipse resource files which represent properties files. 
	 * May have different bundle names.
	 * @return A list of temporary resource bundles. A resource bundle contains all files with same bundle name.
	 */
	public static List<ResourceBundle> createResourceBundles(List<IFile> ifiles) {
		Map<String,ResourceBundle> sessionRBs = new HashMap<String,ResourceBundle>();
		
		for (IFile ifile : ifiles) {
			String bundleName = FileRAPUtils.getBundleName(ifile.getFullPath());
			// ignore .project
			if (bundleName.equals("") || ifile.getName().equals(".project"))
				continue;
			
			ResourceBundle rb = sessionRBs.get(bundleName);			
			
			// create new rb
			if (rb == null) {
				rb = UserFactory.eINSTANCE.createResourceBundle();
				rb.setName(bundleName);
				rb.getLocalFiles().add(createPropertiesFile(ifile));
				sessionRBs.put(bundleName, rb);
			// add new local to rb
			} else {
				rb.getLocalFiles().add(createPropertiesFile(ifile));
			}
		}
		
		return new ArrayList<ResourceBundle>( sessionRBs.values() );
	}
	
	/**
	 * Creates a new properties file object from an existing eclipse resource file.
	 * @param ifile An existing eclipse resource fule.
	 * @return The new created properties file.
	 */
	public static PropertiesFile createPropertiesFile(IFile ifile) {
		PropertiesFile file = UserFactory.eINSTANCE.createPropertiesFile();
		file.setPath(ifile.getLocation().toOSString());
		return file;
	}
	
	/**
	 * Refreshes the storage view if it is opened.
	 */
	public static void refreshStorageView() {
		// refreshing storage view
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
 		IViewPart viewPart = window.getActivePage().findView(StorageView.ID);
 		if (viewPart instanceof StorageView)
 			((StorageView) viewPart).refresh();
	}
	
	/**
	 * Searches for a resource bundle indicated by a given name.
	 * @param bundleName The name of the resource bundle, which should be search for.
	 * @param isTemporary True if the wanted resource bundle is temporary (stored in session storage), 
	 * false if it is stored in user storage)
	 * @return The wanted resource bundle if it could be found, otherwise null.
	 */
	public static ResourceBundle getResourceBundle(String bundleName, boolean isTemporary) {
		List<ResourceBundle> storedRBs;
		
		if (isTemporary) {
			storedRBs = getSessionRBs();
		} else {
			storedRBs = UserUtils.getUser().getStoredRBs();
		}
		
		for (ResourceBundle existingRB : storedRBs) {
			if (existingRB.getName().equals(bundleName))
				return existingRB;
		}
		
		return null;
	}
}
