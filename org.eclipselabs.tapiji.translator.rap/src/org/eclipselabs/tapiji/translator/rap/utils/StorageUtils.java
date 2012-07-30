package org.eclipselabs.tapiji.translator.rap.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.rwt.RWT;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipselabs.tapiji.translator.rap.model.user.PropertiesFile;
import org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle;
import org.eclipselabs.tapiji.translator.rap.model.user.User;
import org.eclipselabs.tapiji.translator.rap.model.user.UserFactory;
import org.eclipselabs.tapiji.translator.views.StorageView;

public class StorageUtils {
	
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
	
	public static boolean existsTempRBName(String bundleName) {
		for (ResourceBundle rb : getSessionRBs()) {
			if (rb.getName().equals(bundleName))
				return true;
		}
		
		return false;
	}
	
	public static void syncUserRBsWithProject() {
		User user = (User) RWT.getSessionStore().getAttribute(UserUtils.SESSION_USER_ATT);
		List<ResourceBundle> rbs = new ArrayList<ResourceBundle>(user.getStoredRBs());
		List<PropertiesFile> allUserFiles = new ArrayList<PropertiesFile>();
		boolean saveToDB = false;
		
		// delete non existing properties files from db
		for (ResourceBundle rb : rbs) {
			List<PropertiesFile> localFiles = new ArrayList<PropertiesFile>(rb.getLocalFiles());			
			for (PropertiesFile file : localFiles) {
				if (! FileRAPUtils.existsProjectFile(FileRAPUtils.getUserProject(), file.getFilename())) {
					rb.getLocalFiles().remove(file);
					saveToDB = true;
				}
			}
			
			if (rb.getLocalFiles().isEmpty()) {
				user.getStoredRBs().remove(rb);
				saveToDB = true;
			}
								
			
			allUserFiles.addAll(rb.getLocalFiles());
		}
		
		// add existing properties files which are not in db to resource bundle
		for (IFile ifile : FileRAPUtils.getFilesFromProject(FileRAPUtils.getUserProject())) {
			PropertiesFile file = createFile(ifile);
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
	
	public static ResourceBundle storeRB(ResourceBundle rb) {
		if (! rb.isTemporary())
			return null;
								
		User user = (User) RWT.getSessionStore().getAttribute(UserUtils.SESSION_USER_ATT);
		user.getStoredRBs().add(rb);
		
		// move local files into project dir
		for (PropertiesFile file : rb.getLocalFiles()) {
			IFile ifile = FileRAPUtils.getIFile(FileRAPUtils.getSessionProject(), file.getPath());
			ifile.exists();
			IPath newPath = new Path(FileRAPUtils.getUserProject().getFullPath()+java.io.File.separator+file.getFilename());
			try {
				ifile.move(newPath, IResource.KEEP_HISTORY, null);
			} catch (CoreException e) {
				e.printStackTrace();
			}
			
			IFile movedIFile = FileRAPUtils.getUserProject().getFile(file.getFilename());
			file.setPath(movedIFile.getLocation().toOSString());
		}
		
		// persist rb and properties files
		try {
			user.eResource().save(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return rb;
	}
	
	public static void renameRB(ResourceBundle rb, String newBundleName) {
		rb.setName(newBundleName);
		
		for (PropertiesFile file : rb.getLocalFiles()) {
			IFile ifile = FileRAPUtils.getIFile(file);
			ifile.exists();
			String local = FileRAPUtils.getLocal(file.getFilename());			
			String newFilename = newBundleName+local+"."+ifile.getFileExtension();
			
			FileRAPUtils.renameIFile(ifile, newFilename);
			file.setFilename(newFilename);
		}
		
		if (! rb.isTemporary())
			try {
				rb.getUser().eResource().save(null);
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	public static List<ResourceBundle> getSessionRBs() {
		return createResourceBundles(FileRAPUtils.getFilesFromProject(FileRAPUtils.getSessionProject()));
	}
	
	public static ResourceBundle createResourceBundle(IFile ifile) {
		List<IFile> list = new ArrayList<IFile>();
		list.add(ifile);
		List<ResourceBundle> result = createResourceBundles(list);
		if (result.isEmpty())
			return null;
		return result.get(0);
	}
	
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
				rb.getLocalFiles().add(createFile(ifile));
				sessionRBs.put(bundleName, rb);
			// add new local to rb
			} else {
				rb.getLocalFiles().add(createFile(ifile));
			}
		}
		
		return new ArrayList<ResourceBundle>( sessionRBs.values() );
	}
	
	public static PropertiesFile createFile(IFile ifile) {
		PropertiesFile file = UserFactory.eINSTANCE.createPropertiesFile();
		file.setPath(ifile.getLocation().toOSString());
		return file;
	}
	
	public static void refreshStorageView() {
		// refreshing storage view
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
 		IViewPart viewPart = window.getActivePage().findView(StorageView.ID);
 		if (viewPart instanceof StorageView)
 			((StorageView) viewPart).refresh();
	}
	
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
