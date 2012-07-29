package org.eclipselabs.tapiji.translator.rap.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.rwt.RWT;
import org.eclipselabs.tapiji.translator.rap.model.user.PropertiesFile;
import org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle;
import org.eclipselabs.tapiji.translator.rap.model.user.User;
import org.eclipselabs.tapiji.translator.rap.model.user.UserFactory;

public class StorageUtils {
	
	public static boolean existsRBName(String bundleName) {
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
		
		for (ResourceBundle rb : rbs) {
			List<PropertiesFile> localFiles = new ArrayList<PropertiesFile>(rb.getLocalFiles());
			for (PropertiesFile file : localFiles)
				if (! FileRAPUtils.existsProjectFile(file.getFilename())) {
					rb.getLocalFiles().remove(file);
				}
			try {
				if (rb.getLocalFiles().isEmpty())
					user.getStoredRBs().remove(rb);
				// update database
				user.eResource().save(null);				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static ResourceBundle storeRB(ResourceBundle rb) {
		if (! rb.isTemporary())
			return null;
								
		User user = (User) RWT.getSessionStore().getAttribute(UserUtils.SESSION_USER_ATT);
		user.getStoredRBs().add(rb);
		
		try {
			user.eResource().save(null);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
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
}
