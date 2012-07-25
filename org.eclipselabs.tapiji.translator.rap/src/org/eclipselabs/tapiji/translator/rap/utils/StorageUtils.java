package org.eclipselabs.tapiji.translator.rap.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.rwt.RWT;
import org.eclipselabs.tapiji.translator.rap.model.user.File;
import org.eclipselabs.tapiji.translator.rap.model.user.User;
import org.eclipselabs.tapiji.translator.rap.model.user.UserFactory;

public class StorageUtils {
	
	public static boolean existsUserFile(String filename) {
		User user = (User) RWT.getSessionStore().getAttribute(UserUtils.SESSION_USER_ATT);
		
		List<File> storedFiles = user.getStoredFiles();
		
		for (File file : storedFiles) {
			if (file.getName().equals(filename))
				return true;
		}
		
		return false;
	}
	
	public static boolean existsProjectFile(String filename) {		
		IFile iFile = getProject().getFile(filename);
		if (! iFile.exists()) {
			// if filename is bundleName (= has no extension)
			if (iFile.getFileExtension() == null) {
				iFile = getProject().getFile(filename + ".properties");
				if (iFile.exists())
					return true;
				List<IFile> iFiles = FileRAPUtils.getOtherLocalFiles(iFile);
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
	
	public static void syncUserFilesWithProject() {
		boolean save = false;
		User user = (User) RWT.getSessionStore().getAttribute(UserUtils.SESSION_USER_ATT);
		List<File> files = new ArrayList<File>();
		files.addAll(user.getStoredFiles());
		
		for (File file : files) {
			if (! existsProjectFile(file.getName())) {
				user.getStoredFiles().remove(file);
				
				if (save == false)
					save = true;
			}
		}
		
		if (save == true)
			try {
				user.eResource().save(null);
			} catch (IOException e) {
				e.printStackTrace();
			}		
	}
	
	public static File storeFile(IFile ifile) {
		IFile newIFile = getNewFile(ifile);
		
		User user = (User) RWT.getSessionStore().getAttribute(UserUtils.SESSION_USER_ATT);
		File file = UserFactory.eINSTANCE.createFile();
		try {
			newIFile.create(ifile.getContents(), false, null);
			
			file.setName(newIFile.getName());
			file.setPath(newIFile.getLocation().toOSString());			
					
			user.getStoredFiles().add(file);
			// update user
			user.eResource().save(null);
			
			// add other local files
			for (IFile otherLocal : FileRAPUtils.getOtherLocalFiles(ifile)) {
				String baseName = FileRAPUtils.getBundleName(newIFile.getFullPath());
				String newLocalFileName = getRenamedLocal(otherLocal, baseName);
				
				IFile newLocalIFile = getProject().getFile(newLocalFileName);
				newLocalIFile.create(otherLocal.getContents(), false, null);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 		
		return file;
	}
	
	public static void unstoreFile(File file) {
		IFile ifile = getIFile(file.getPath());
		
		try {
			ifile.delete(true, null);
			User user = (User) RWT.getSessionStore().getAttribute(UserUtils.SESSION_USER_ATT);
			user.getStoredFiles().remove(file);
			user.eResource().save(null);
			// or
			//file.eResource().delete(null); 
			
			// delete other local files
			for (IFile otherLocal : FileRAPUtils.getOtherLocalFiles(ifile)) {
				otherLocal.delete(true, null);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void renameFile(File file, String newName) {
		// rename project files
		IFile ifile = getIFile(file.getPath());
		
		String newFilename = getRenamedLocal(ifile, newName);
		IFile newIFile = FileRAPUtils.renameIFile(ifile, newFilename);
		
		for (IFile otherLocal : FileRAPUtils.getOtherLocalFiles(ifile)) {
			newFilename = getRenamedLocal(otherLocal, newName);
			FileRAPUtils.renameIFile(otherLocal, newFilename);
		}
		
		file.setName(newFilename);
		file.setPath(newIFile.getLocation().toOSString());
		try {
			file.eResource().save(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static IProject getProject() {
		User user = (User) RWT.getSessionStore().getAttribute(UserUtils.SESSION_USER_ATT);
		
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(user.getUsername());
		try {
			if (!project.exists())
				project.create(null);
			if (!project.isOpen())
				project.open(null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return project;		
	}
	
	public static IFile getIFile(String filepath) {
		IPath path = new Path(filepath);
		return getProject().getFile(path.lastSegment());
	}
	
	private static IFile getNewFile(IFile ifile) {
		String filename = getNextNonExistingFilename(ifile);
		return getProject().getFile(filename);
	}
	
	private static String getNextNonExistingFilename(IFile ifile) {
		int counter = 2;
		String filenameBase = FileRAPUtils.getBundleName(ifile.getFullPath());
		String extension = ifile.getFileExtension();
		String local = FileRAPUtils.getLocal(ifile.getName());
		String filename = filenameBase + local + "." + extension;
		
		while (existsProjectFile(filename)) {
			filename = filenameBase + "(" + counter + ")" + local + "." + extension;
			counter++;
		}
		
		return filename;
	}
	
	private static String getRenamedLocal(IFile ifile, String newFileBaseName) {
		String ext = ifile.getFileExtension();
		String local = FileRAPUtils.getLocal(ifile.getName());
		String newLocalFileName = newFileBaseName + local + "." + ext;
		
		return newLocalFileName;
	}
}
