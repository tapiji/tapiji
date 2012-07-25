package org.eclipselabs.tapiji.translator.rap.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.rwt.RWT;
import org.eclipselabs.tapiji.translator.rap.model.user.User;
import org.eclipselabs.tapiji.translator.utils.FileUtils;

public class FileRAPUtils extends FileUtils {
	public static IFile getResourceBundleRef(String[] locations, String projectName)
	        throws CoreException {
		
		IFile file = null;
		
		for (String location : locations) {
			IPath path = new Path(location);
	
			/**
			 * Create all files of the Resource-Bundle within the project space and
			 * link them to the original file
			 */
			String filename = path.lastSegment();
			IProject project = getProject(projectName);
			file = project.getFile(filename);
			if (! file.exists())
				file.createLink(path, IResource.REPLACE, null);
		}
		
		return file;
	}
	
	public static List<IFile> getOtherLocalFiles(IFile file) {
		List<IFile> iFiles = new ArrayList<IFile>();
		try {
			IProject project = file.getProject();			
			IResource[] resources = project.members();		
			
			for (IResource resource : resources) {
				if (resource instanceof IFile) {
					IFile ifile = (IFile) resource;
					if (ifile.getName().matches(getPropertiesFileRegEx(file.getLocation())) && ! file.equals(ifile))
						iFiles.add(ifile);						
				}
			}			
		} catch (CoreException e) {
			e.printStackTrace();
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
		
		if (user != null)
			try {
				project = getProject(user.getUsername());
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		return project;
	}

	static IFile renameIFile(IFile file, String newFilename) {
		IPath oldPath = file.getFullPath();
		IPath newPath = new Path(oldPath.removeLastSegments(1).toOSString() + java.io.File.separator + newFilename);
		try {
			file.move(newPath, false, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return file.getProject().getFile(newFilename);
	}
}
