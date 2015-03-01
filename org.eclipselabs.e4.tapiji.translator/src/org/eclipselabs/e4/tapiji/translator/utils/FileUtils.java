/*******************************************************************************
 * Copyright (c) 2012 Martin Reiterer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Reiterer - initial API and implementation
 ******************************************************************************/
package org.eclipselabs.e4.tapiji.translator.utils;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class FileUtils {

	/** Token to replace in a regular expression with a bundle name. */
	private static final String TOKEN_BUNDLE_NAME = "BUNDLENAME";
	/** Token to replace in a regular expression with a file extension. */
	private static final String TOKEN_FILE_EXTENSION = "FILEEXTENSION";
	/** Regex to match a properties file. */
	public static final String PROPERTIES_FILE_REGEX = "^("
	        + TOKEN_BUNDLE_NAME + ")" + "((_[a-z]{2,3})|(_[a-z]{2,3}_[A-Z]{2})"
	        + "|(_[a-z]{2,3}_[A-Z]{2}_\\w*))?(\\." + TOKEN_FILE_EXTENSION
	        + ")$";

	/** Project name for external resource bundles */
	public static final String EXTERNAL_RB_PROJECT_NAME = "ExternalResourceBundles";
	
	/** The singleton instance of Workspace */
	private static IWorkspace workspace;

	public static boolean isResourceBundle(String fileName) {
		return fileName.toLowerCase().endsWith(".properties");
	}

	public static boolean isGlossary(String fileName) {
		return fileName.toLowerCase().endsWith(".xml");
	}

	public static IWorkspace getWorkspace() {
		if (workspace == null) {
			workspace = ResourcesPlugin.getWorkspace();
		}

		return workspace;
	}

	public static IProject getProject(String projectName) throws CoreException {
		IProject project = getWorkspace().getRoot().getProject(
			        projectName);
		
		if (!project.exists())
			project.create(null);
		if (!project.isOpen())
			project.open(null);

		return project;
	}

	public static void prePareEditorInputs() {
		IWorkspace workspace = getWorkspace();
	}

	public static IFile getResourceBundleRef(String location, String projectName)
	        throws CoreException {
		IPath path = new Path(location);

		/**
		 * Create all files of the Resource-Bundle within the project space and
		 * link them to the original file
		 */
		String regex = getPropertiesFileRegEx(path);
		String projPathName = toProjectRelativePathName(path);
		IProject project = getProject(projectName);
		IFile file = project.getFile(projPathName);
		file.createLink(path, IResource.REPLACE, null);

		File parentDir = new File(path.toFile().getParent());
		String[] files = parentDir.list();

		for (String fn : files) {
			File fo = new File(parentDir, fn);
			if (!fo.isFile())
				continue;

			IPath newFilePath = new Path(fo.getAbsolutePath());
			if (fo.getName().matches(regex)
			        && !path.toFile().getName()
			                .equals(newFilePath.toFile().getName())) {
				IFile newFile = project
				        .getFile(toProjectRelativePathName(newFilePath));
				newFile.createLink(newFilePath, IResource.REPLACE, null);
			}
		}

		return file;
	}
	
	protected static String toProjectRelativePathName(IPath path) {
		String projectRelativeName = "";

		projectRelativeName = path.toString().replaceAll(":", "");
		projectRelativeName = projectRelativeName.replaceAll("/", ".");

		return projectRelativeName;
	}

	protected static String getPropertiesFileRegEx(IPath file) {
		String bundleName = getBundleName(file);
		return PROPERTIES_FILE_REGEX
		        .replaceFirst(TOKEN_BUNDLE_NAME, bundleName).replaceFirst(
		                TOKEN_FILE_EXTENSION, file.getFileExtension());
	}

	public static String getBundleName(IPath file) {
		String name = file.toFile().getName();
		String regex = "^(.*?)" //$NON-NLS-1$
		        + "((_[a-z]{2,3})|(_[a-z]{2,3}_[A-Z]{2})"
		        + "|(_[a-z]{2,3}_[A-Z]{2}_\\w*))?(\\."
		        + file.getFileExtension() + ")$";
		return name.replaceFirst(regex, "$1");
	}
	
	public static String getBundleName(String filePath) {
		IPath path = new Path(filePath);		
		return getBundleName(path);
	}

	public static String[] queryFileName(Shell shell, String title,
	        int dialogOptions, String[] endings) {
		FileDialog dialog = new FileDialog(shell, dialogOptions);
		dialog.setText(title);
		dialog.setFilterExtensions(endings);
		
		String filepath = dialog.open();
		
		// if single option, return path
		if ((dialogOptions & SWT.SINGLE) == SWT.SINGLE) {
			return new String[] {filepath};
		} else {
			// [RAP] In RAP1.5 getFilterPath is always empty!!!
			String path = dialog.getFilterPath();
			// [RAP] In RAP1.5 getFileNames returns full filename (+ path)!!!
			String[] filenames = dialog.getFileNames();
	
			// append filenames to path
			if (! path.isEmpty()) {
				for (int i=0; i < filenames.length; i++) {
					filenames[i] = path + File.separator + filenames[i];
				}
					
			}			
			if (filenames.length > 0)
				return filenames;
			return null;
		}
	}

}
