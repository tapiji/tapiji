/*******************************************************************************
 * Copyright (c) 2012 Michael Gasser.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Michael Gasser - initial API and implementation
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.rbmanager.viewer;

import java.util.List;
import java.util.Locale;

import org.eclipse.babel.tapiji.tools.core.ui.ResourceBundleManager;
import org.eclipse.babel.tapiji.tools.core.ui.utils.EditorUtils;
import org.eclipse.babel.tapiji.tools.core.ui.utils.ResourceUtils;
import org.eclipse.babel.tapiji.tools.core.util.FragmentProjectUtils;
import org.eclipse.babel.tapiji.tools.rbmanager.ImageUtils;
import org.eclipse.babel.tapiji.tools.rbmanager.model.VirtualContainer;
import org.eclipse.babel.tapiji.tools.rbmanager.model.VirtualContentManager;
import org.eclipse.babel.tapiji.tools.rbmanager.model.VirtualProject;
import org.eclipse.babel.tapiji.tools.rbmanager.model.VirtualResourceBundle;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.IDescriptionProvider;

public class ResourceBundleLabelProvider extends LabelProvider implements
        ILabelProvider, IDescriptionProvider {
    VirtualContentManager vcManager;

    public ResourceBundleLabelProvider() {
        super();
        vcManager = VirtualContentManager.getVirtualContentManager();
    }

    @Override
    public Image getImage(Object element) {
        Image returnImage = null;
        if (element instanceof IProject) {
            VirtualProject p = (VirtualProject) vcManager
                    .getContainer((IProject) element);
            if (p != null && p.isFragment()) {
                return returnImage = ImageUtils
                        .getBaseImage(ImageUtils.FRAGMENT_PROJECT_IMAGE);
            } else {
                returnImage = PlatformUI.getWorkbench().getSharedImages()
                        .getImage(ISharedImages.IMG_OBJ_PROJECT);
            }
        }
        if ((element instanceof IContainer) && (returnImage == null)) {
            returnImage = PlatformUI.getWorkbench().getSharedImages()
                    .getImage(ISharedImages.IMG_OBJ_FOLDER);
        }
        if (element instanceof VirtualResourceBundle) {
            returnImage = ImageUtils
                    .getBaseImage(ImageUtils.RESOURCEBUNDLE_IMAGE);
        }
        if (element instanceof IFile) {
            if (org.eclipse.babel.tapiji.tools.core.ui.utils.RBFileUtils
                    .isResourceBundleFile((IFile) element)) {
                Locale l = org.eclipse.babel.tapiji.tools.core.ui.utils.RBFileUtils
                        .getLocale((IFile) element);
                returnImage = ImageUtils.getLocalIcon(l);

                VirtualProject p = ((VirtualProject) vcManager
                        .getContainer(((IFile) element).getProject()));
                if (p != null && p.isFragment()) {
                    returnImage = ImageUtils.getImageWithFragment(returnImage);
                }
            }
        }

        if (returnImage != null) {
            if (checkMarkers(element)) {
                // Add a Warning Image
                returnImage = ImageUtils.getImageWithWarning(returnImage);
            }
        }
        return returnImage;
    }

    @Override
    public String getText(Object element) {

        StringBuilder text = new StringBuilder();
        if (element instanceof IContainer) {
            IContainer container = (IContainer) element;
            text.append(container.getName());

            if (element instanceof IProject) {
                VirtualContainer vproject = vcManager
                        .getContainer((IProject) element);
                // if (vproject != null && vproject instanceof VirtualFragment)
                // text.append("�");
            }

            VirtualContainer vContainer = vcManager.getContainer(container);
            if (vContainer != null && vContainer.getRbCount() != 0) {
                text.append(" [" + vContainer.getRbCount() + "]");
            }

        }
        if (element instanceof VirtualResourceBundle) {
            text.append(((VirtualResourceBundle) element).getName());
        }
        if (element instanceof IFile) {
            if (org.eclipse.babel.tapiji.tools.core.ui.utils.RBFileUtils
                    .isResourceBundleFile((IFile) element)) {
                Locale locale = org.eclipse.babel.tapiji.tools.core.ui.utils.RBFileUtils
                        .getLocale((IFile) element);
                text.append("     ");
                if (locale != null) {
                    text.append(locale);
                } else {
                    text.append("default");
                }

                VirtualProject vproject = (VirtualProject) vcManager
                        .getContainer(((IFile) element).getProject());
                if (vproject != null && vproject.isFragment()) {
                    text.append("�");
                }
            }
        }
        if (element instanceof String) {
            text.append(element);
        }
        return text.toString();
    }

    @Override
    public String getDescription(Object anElement) {
        if (anElement instanceof IResource) {
            return ((IResource) anElement).getName();
        }
        if (anElement instanceof VirtualResourceBundle) {
            return ((VirtualResourceBundle) anElement).getName();
        }
        return null;
    }

    private boolean checkMarkers(Object element) {
        if (element instanceof IResource) {
            IMarker[] ms = null;
            try {
                if ((ms = ((IResource) element).findMarkers(
                        EditorUtils.RB_MARKER_ID, true,
                        IResource.DEPTH_INFINITE)).length > 0) {
                    return true;
                }

                if (element instanceof IContainer) {
                    List<IContainer> fragmentContainer = ResourceUtils
                            .getCorrespondingFolders(
                                    (IContainer) element,
                                    FragmentProjectUtils
                                            .getFragments(((IContainer) element)
                                                    .getProject()));

                    IMarker[] fragment_ms;
                    for (IContainer c : fragmentContainer) {
                        try {
                            if (c.exists()) {
                                fragment_ms = c.findMarkers(
                                        EditorUtils.RB_MARKER_ID, false,
                                        IResource.DEPTH_INFINITE);
                                ms = org.eclipse.babel.tapiji.tools.core.util.EditorUtils
                                        .concatMarkerArray(ms, fragment_ms);
                            }
                        } catch (CoreException e) {
                            e.printStackTrace();
                        }
                    }
                    if (ms.length > 0) {
                        return true;
                    }
                }
            } catch (CoreException e) {
            }
        }
        if (element instanceof VirtualResourceBundle) {
            ResourceBundleManager rbmanager = ((VirtualResourceBundle) element)
                    .getResourceBundleManager();
            String id = ((VirtualResourceBundle) element).getResourceBundleId();
            for (IResource r : rbmanager.getResourceBundles(id)) {
                if (org.eclipse.babel.tapiji.tools.core.util.RBFileUtils
                        .hasResourceBundleMarker(r)) {
                    return true;
                }
            }
        }

        return false;
    }

}
