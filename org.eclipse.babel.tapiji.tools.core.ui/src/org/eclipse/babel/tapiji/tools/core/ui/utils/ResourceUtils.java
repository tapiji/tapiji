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
package org.eclipse.babel.tapiji.tools.core.ui.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.babel.tapiji.tools.core.ui.ResourceBundleManager;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

public class ResourceUtils {

    private final static String REGEXP_RESOURCE_KEY = "[\\p{Alnum}\\.]*";
    private final static String REGEXP_RESOURCE_NO_BUNDLENAME = "[^\\p{Alnum}\\.]*";

    public static boolean isValidResourceKey(String key) {
        boolean isValid = false;

        if (key != null && key.trim().length() > 0) {
            isValid = key.matches(REGEXP_RESOURCE_KEY);
        }

        return isValid;
    }

    public static String deriveNonExistingRBName(String nameProposal,
            ResourceBundleManager manager) {
        // Adapt the proposal to the requirements for Resource-Bundle names
        nameProposal = nameProposal.replaceAll(REGEXP_RESOURCE_NO_BUNDLENAME,
                "");

        int i = 0;
        do {
            if (manager.getResourceBundleIdentifiers().contains(nameProposal)
                    || nameProposal.length() == 0) {
                nameProposal = nameProposal + (++i);
            } else {
                break;
            }
        } while (true);

        return nameProposal;
    }

    public static boolean isJavaCompUnit(IResource res) {
        boolean result = false;

        if (res.getType() == IResource.FILE && !res.isDerived()
                && res.getFileExtension().equalsIgnoreCase("java")) {
            result = true;
        }

        return result;
    }

    public static boolean isJSPResource(IResource res) {
        boolean result = false;

        if (res.getType() == IResource.FILE
                && !res.isDerived()
                && (res.getFileExtension().equalsIgnoreCase("jsp") || res
                        .getFileExtension().equalsIgnoreCase("xhtml"))) {
            result = true;
        }

        return result;
    }

    /**
     * 
     * @param baseFolder
     * @param targetProjects
     *            Projects with a same structure
     * @return List of
     */
    public static List<IContainer> getCorrespondingFolders(
            IContainer baseFolder, List<IProject> targetProjects) {
        List<IContainer> correspondingFolder = new ArrayList<IContainer>();

        for (IProject p : targetProjects) {
            IContainer c = getCorrespondingFolders(baseFolder, p);
            if (c.exists()) {
                correspondingFolder.add(c);
            }
        }

        return correspondingFolder;
    }

    /**
     * 
     * @param baseFolder
     * @param targetProject
     * @return a Container with the corresponding path as the baseFolder. The
     *         Container doesn't must exist.
     */
    public static IContainer getCorrespondingFolders(IContainer baseFolder,
            IProject targetProject) {
        IPath relativ_folder = baseFolder.getFullPath().makeRelativeTo(
                baseFolder.getProject().getFullPath());

        if (!relativ_folder.isEmpty()) {
            return targetProject.getFolder(relativ_folder);
        } else {
            return targetProject;
        }
    }

}
