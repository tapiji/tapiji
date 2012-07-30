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
import org.eclipse.rwt.RWT;
import org.eclipselabs.tapiji.translator.rap.model.user.PropertiesFile;
import org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle;
import org.eclipselabs.tapiji.translator.rap.model.user.User;
import org.eclipselabs.tapiji.translator.utils.FileUtils;

public class FileRAPUtils extends FileUtils {
	
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
	
	public static List<IFile> getOtherLocalFiles(IFile file) {
		List<IFile> iFiles = new ArrayList<IFile>();
		
		for (IFile ifile : getFilesFromProject(file.getProject())) {
			if (ifile.getName().matches(getPropertiesFileRegEx(file.getLocation())) && ! file.equals(ifile))
				iFiles.add(ifile);
		}
			
		return iFiles;
	}
	
	public static String getLocal(String fileName) {
		// fileName -> BUNDLENAME_LOCAL.EXT
		String local = fileName;
		IPath path = new Path(fileName);
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
	
	public static IProject getProject() {
		User user = (User) RWT.getSessionStore().getAttribute(UserUtils.SESSION_USER_ATT);
		IProject project = null;
		
		String projectName = RWT.getSessionStore().getId();
		if (user != null)
			projectName = user.getUsername();
		
		try {
			project = getProject(projectName);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return project;
	}
	
	public static IProject getUserProject() {
		if (! UserUtils.isUserLoggedIn())
			return null;
		
		User user = (User) RWT.getSessionStore().getAttribute(UserUtils.SESSION_USER_ATT);
		try {
			return getProject(user.getUsername());
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static IProject getSessionProject() {
		try {
			return getProject(RWT.getSessionStore().getId());
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static IProject getProject(ResourceBundle rb) {
		if (rb.isTemporary())
			return getSessionProject();
		else
			return getUserProject();
	}
	
	public static IFile renameIFile(IFile file, String newFilename) {
		IPath oldPath = file.getFullPath();
		IPath newPath = new Path(oldPath.removeLastSegments(1).toOSString() + java.io.File.separator + newFilename);
		try {
			file.move(newPath, IResource.SHALLOW, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return file.getProject().getFile(newFilename);
	}

	public static IFile getIFile(IProject project, String filepath) {
		IPath path = new Path(filepath);
		if (project == null)
			project = getProject();
		return project.getFile(path.lastSegment());
	}

	public static IFile getIFile(PropertiesFile file) {
		IProject project = getProject(file.getResourceBundle());
		
		return project.getFile(file.getFilename());
	}

	public static boolean existsProjectFile(IProject project, String filename) {
		IFile iFile = project.getFile(filename);
		if (! iFile.exists()) {
			// if filename is bundleName (= has no extension)
			if (iFile.getFileExtension() == null) {
				iFile = project.getFile(filename + ".properties");
				if (iFile.exists())
					return true;
				List<IFile> iFiles = getOtherLocalFiles(iFile);
				for (IFile iFile2 : iFiles) {
					if (iFile2.exists())
						return true;
				}								
			}
			
			return false;	
		} else {
			return true;
		}
	}
}
