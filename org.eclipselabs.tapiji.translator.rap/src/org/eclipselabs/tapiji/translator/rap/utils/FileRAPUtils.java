package org.eclipselabs.tapiji.translator.rap.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.rap.rwt.RWT;
import org.eclipselabs.tapiji.translator.rap.helpers.utils.UserUtils;
import org.eclipselabs.tapiji.translator.rap.model.user.PropertiesFile;
import org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle;
import org.eclipselabs.tapiji.translator.utils.FileUtils;

/**
 * Utility methods for file operations in eclipse workspace used in RAP, which extends the {@link FileUtils} class.
 * @author Matthias Lettmayer
 *
 */
public class FileRAPUtils extends FileUtils {
	
	/**
	 * Creates new {@link IFile}s for given locations in a given project. Creates resource bundles 
	 * out of these {@link IFile}s and returns them as list.
	 * @param locations a string array which contains the location (path + filename) to files
	 * @param project the project in which the files get created
	 * @return a list of created resources bundles
	 */
	public static List<ResourceBundle> getResourceBundleRef(String[] locations, IProject project) {		
		IFile file = null;
		
		List<IFile> createdFiles = new ArrayList<IFile>();		
		for (String location : locations) {
			IPath path = new Path(location);
			
			// Create all files of the Resource-Bundle within the project space
			String filename = path.lastSegment();			
			file = project.getFile(filename);
			if (! file.exists())
				try {
					file.create(new FileInputStream(location), IResource.REPLACE, null);					
					createdFiles.add(file);
				} catch (FileNotFoundException e) {		
					e.printStackTrace();
				} catch (CoreException e) {
					e.printStackTrace();
				}			
		}
		
		return StorageUtils.createResourceBundles(createdFiles);
	}
	
	/**
	 * Returns the files contained in a given project.
	 * @param project eclipse project
	 * @return a list of files which are stored in project
	 */
	public static List<IFile> getFilesFromProject(IProject project) {
		List<IFile> iFiles = new ArrayList<IFile>();
		try {		
			IResource[] resources = project.members();		
			
			for (IResource resource : resources) {
				if (resource instanceof IFile) {
					IFile ifile = (IFile) resource;
					iFiles.add(ifile);						
				}
			}			
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		return iFiles;
	}
	
	/**
	 * Returns all files which have the same bundle name and would be in the same resource bundle as the given file.
	 * @param file a eclipse resource file
	 * @return a list of files which have the same bundle name as file
	 */
//	public static List<IFile> getOtherLocalFiles(IFile file) {
//		List<IFile> iFiles = new ArrayList<IFile>();
//		
//		for (IFile ifile : getFilesFromProject(file.getProject())) {
//			if (ifile.getName().matches(getPropertiesFileRegEx(file.getLocation())) && ! file.equals(ifile))
//				iFiles.add(ifile);
//		}
//			
//		return iFiles;
//	}
	
	
	/**
	 * Returns the local of a given filename as string.
	 * Examples (Input -> Output): "bundleName_de.properties" -> "de", "bundleName.properties" -> "".
	 * @param filename name of a file
	 * @return a string local of filename or an empty string if filename has no local.
	 */
	public static String getLocal(String filename) {
		// fileName -> BUNDLENAME_LOCAL.EXT
		String local = filename;
		IPath path = new Path(filename);
		String ext = path.getFileExtension();
		
		String bundleName = getBundleName(path);
		// remove bundleName (-> LOCAL.EXT)
		local = local.substring(bundleName.length()+1);
		// default local (null) -> BUNDLENAME.EXT
		if (local.equals(ext))
			return "";
		// remove file extension (-> LOCAL)
		local = local.substring(0, local.length()-ext.length()-1);
		
		return "_"+local;
	}
	
	/**
	 * Returns the project of the logged in user. Uses the username as project name. 
	 * In this project the user files are stored.
	 * @return The project of the logged in user or null if no user is logged in.
	 */
	public static IProject getUserProject() {
		if (! UserUtils.isUserLoggedIn())
			return null;
		
		try {
			return getProject(UserUtils.getUser().getUsername());
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Returns the project of the session store. Uses the session id as project name.
	 * In this project the temporary files are stored. When the session expires this 
	 * project, with all its files, will be deleted.
	 * @return The project of the session store.
	 */
	public static IProject getSessionProject() {
		try {
			return getProject(RWT.getSessionStore().getId());
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Returns the corresponding project of a given resource bundle. If the resource 
	 * bundle is temporary, it's stored in the session project, otherwise it's stored
	 * in the user project of resource bundle's owner (project name = username).
	 * @param rb resource bundle
	 * @return The project of resource bundle.
	 */
	public static IProject getProject(ResourceBundle rb) {
		IProject project = null;
		if (rb.isTemporary())
			project = getSessionProject();
		else
			try {
				project = getProject(rb.getOwner().getUsername());
			} catch (CoreException e) {
				e.printStackTrace();
			}
		return project;
	}
	
	/**
	 * Renames a given file.
	 * @param file Eclipse resource file, which gets renamed.
	 * @param newFilename The new filename of the file.
	 * @return The renamed file.
	 */
	public static IFile renameIFile(IFile file, String newFilename) {
		IPath oldPath = file.getFullPath();
		IPath newPath = new Path(oldPath.removeLastSegments(1).toOSString() + java.io.File.separator + newFilename);
		try {
			file.refreshLocal(IResource.DEPTH_ZERO, null);
			file.move(newPath, IResource.SHALLOW, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return file.getProject().getFile(newFilename);
	}

	/**
	 * Returns the file from a properties file.
	 * @param file A properties file
	 * @return a eclipse resource file, which exists in project of the given properties file
	 */
	public static IFile getFile(PropertiesFile file) {
		IProject project = getProject(file.getResourceBundle());
		
		return project.getFile(file.getFilename());
	}

	/**
	 * Checks if the file (indicated by given filename) exists in given project.
	 * @param project A Eclipse resource project
	 * @param filename Name of a file
	 * @return True if file exists in project, otherwise false.
	 */
	public static boolean existsProjectFile(IProject project, String filename) {
		IFile iFile = project.getFile(filename);
		if (! iFile.exists()) {
			// if filename is bundleName (= has no extension)
//			if (iFile.getFileExtension() == null) {
//				iFile = project.getFile(filename + ".properties");
//				if (iFile.exists())
//					return true;
//				List<IFile> iFiles = getOtherLocalFiles(iFile);
//				for (IFile iFile2 : iFiles) {
//					if (iFile2.exists())
//						return true;
//				}								
//			}
			
			return false;	
		} else {
			return true;
		}
	}
}
