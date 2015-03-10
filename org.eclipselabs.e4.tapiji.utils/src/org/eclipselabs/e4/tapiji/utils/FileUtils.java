/*******************************************************************************
 * Copyright (c) 2012 Martin Reiterer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Reiterer - initial API and implementation
 *     Christian Behon
 ******************************************************************************/
package org.eclipselabs.e4.tapiji.utils;


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


public final class FileUtils {

    public static final String[] XML_FILE_ENDINGS = new String[] {"*.xml"};

    public static final String[] PROPERTY_FILE_ENDINGS = new String[] {"*.properties"};

    public static final String ENCODING_TYPE_UTF_16 = "UTF-16";

    /** Token to replace in a regular expression with a bundle name. */
    private static final String TOKEN_BUNDLE_NAME = "BUNDLENAME";
    /** Token to replace in a regular expression with a file extension. */
    private static final String TOKEN_FILE_EXTENSION = "FILEEXTENSION";
    /** Regex to match a properties file. */
    public static final String PROPERTIES_FILE_REGEX = "^(" + TOKEN_BUNDLE_NAME + ")"
                    + "((_[a-z]{2,3})|(_[a-z]{2,3}_[A-Z]{2})" + "|(_[a-z]{2,3}_[A-Z]{2}_\\w*))?(\\."
                    + TOKEN_FILE_EXTENSION + ")$";
    /** Project name for external resource bundles */
    public static final String EXTERNAL_RB_PROJECT_NAME = "ExternalResourceBundles";

    /** The singleton instance of Workspace */
    private static IWorkspace workspace;


    private FileUtils() {
        // Hide constructor
    }


    public static boolean isResourceBundle(final String fileName) {
        return fileName.toLowerCase().endsWith(".properties");
    }

    public static boolean isGlossary(final String fileName) {
        return fileName.toLowerCase().endsWith(".xml");
    }

    public static IWorkspace getWorkspace() {
        if (workspace == null) {
            workspace = ResourcesPlugin.getWorkspace();
        }
        return workspace;
    }

    public static IProject getProject(final String projectName) throws CoreException {
        final IProject project = getWorkspace().getRoot().getProject(projectName);
        if (!project.exists()) {
            project.create(null);
        }
        if (!project.isOpen()) {
            project.open(null);
        }
        return project;
    }

    public static IFile getResourceBundleRef(final String location, final String projectName) throws CoreException {
        final IPath path = new Path(location);

        /**
         * Create all files of the Resource-Bundle within the project space and link them to the original file
         */
        final String regex = getPropertiesFileRegEx(path);
        final String projPathName = toProjectRelativePathName(path);
        final IProject project = getProject(projectName);
        final IFile file = project.getFile(projPathName);
        file.createLink(path, IResource.REPLACE, null);

        final File parentDir = new File(path.toFile().getParent());
        final String[] files = parentDir.list();

        for (final String fn : files) {
            final File fo = new File(parentDir, fn);
            if (!fo.isFile()) {
                continue;
            }

            final IPath newFilePath = new Path(fo.getAbsolutePath());
            if (fo.getName().matches(regex) && !path.toFile().getName().equals(newFilePath.toFile().getName())) {
                final IFile newFile = project.getFile(toProjectRelativePathName(newFilePath));
                newFile.createLink(newFilePath, IResource.REPLACE, null);
            }
        }

        return file;
    }

    protected static String toProjectRelativePathName(final IPath path) {
        String projectRelativeName = "";

        projectRelativeName = path.toString().replaceAll(":", "");
        projectRelativeName = projectRelativeName.replaceAll("/", ".");

        return projectRelativeName;
    }

    protected static String getPropertiesFileRegEx(final IPath file) {
        final String bundleName = getBundleName(file);
        return PROPERTIES_FILE_REGEX.replaceFirst(TOKEN_BUNDLE_NAME, bundleName).replaceFirst(TOKEN_FILE_EXTENSION,
                        file.getFileExtension());
    }

    public static String getBundleName(final IPath file) {
        final String name = file.toFile().getName();
        final String regex = "^(.*?)" //$NON-NLS-1$
                        + "((_[a-z]{2,3})|(_[a-z]{2,3}_[A-Z]{2})"
                        + "|(_[a-z]{2,3}_[A-Z]{2}_\\w*))?(\\."
                        + file.getFileExtension() + ")$";
        return name.replaceFirst(regex, "$1");
    }

    public static String getBundleName(final String filePath) {
        final IPath path = new Path(filePath);
        return getBundleName(path);
    }

    public static String[] queryFileName(final Shell shell, final String title, final int dialogOptions,
                    final String[] endings) {
        final FileDialog dialog = new FileDialog(shell, dialogOptions);
        dialog.setText(title);
        dialog.setFilterExtensions(endings);

        final String filepath = dialog.open();

        // if single option, return path
        if ((dialogOptions & SWT.SINGLE) == SWT.SINGLE) {
            return new String[] {filepath};
        } else {
            // [RAP] In RAP1.5 getFilterPath is always empty!!!
            final String path = dialog.getFilterPath();
            // [RAP] In RAP1.5 getFileNames returns full filename (+ path)!!!
            final String[] filenames = dialog.getFileNames();

            // append filenames to path
            if (!path.isEmpty()) {
                for (int i = 0; i < filenames.length; i++) {
                    filenames[i] = path + File.separator + filenames[i];
                }

            }
            if (filenames.length > 0) {
                return filenames;
            }
            return null;
        }
    }

    public static String checkXmlFileEnding(String fileName) {
        if (!fileName.endsWith(".xml")) {
            if (fileName.endsWith(".")) {
                fileName += "xml";
            } else {
                fileName += ".xml";
            }
        }
        return fileName;
    }
}
