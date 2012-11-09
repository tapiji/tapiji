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
package org.eclipse.babel.tapiji.tools.core.ui.quickfix;

import java.util.Set;

import org.eclipse.babel.tapiji.tools.core.ui.ResourceBundleManager;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

public class IncludeResource implements IMarkerResolution2 {

    private String bundleName;
    private Set<IResource> bundleResources;

    public IncludeResource(String bundleName, Set<IResource> bundleResources) {
        this.bundleResources = bundleResources;
        this.bundleName = bundleName;
    }

    @Override
    public String getDescription() {
        return "The Resource-Bundle with id '"
                + bundleName
                + "' has been "
                + "excluded from Internationalization. Based on this fact, no internationalization "
                + "supoort is provided for this Resource-Bundle. Performing this action, internationalization "
                + "support for '" + bundleName + "' will be enabled";
    }

    @Override
    public Image getImage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getLabel() {
        return "Include excluded Resource-Bundle '" + bundleName + "'";
    }

    @Override
    public void run(final IMarker marker) {
        IWorkbench wb = PlatformUI.getWorkbench();
        IProgressService ps = wb.getProgressService();
        try {
            ps.busyCursorWhile(new IRunnableWithProgress() {
                public void run(IProgressMonitor pm) {
                    ResourceBundleManager manager = ResourceBundleManager
                            .getManager(marker.getResource().getProject());
                    pm.beginTask("Including resources to Internationalization",
                            bundleResources.size());
                    for (IResource resource : bundleResources) {
                        manager.includeResource(resource, pm);
                        pm.worked(1);
                    }
                    pm.done();
                }
            });
        } catch (Exception e) {
        }
    }

}
