/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.java.ui.quickfix;

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

public class ExcludeResourceFromInternationalization implements
        IMarkerResolution2 {

	@Override
	public String getLabel() {
		return "Exclude Resource";
	}

	@Override
	public void run(IMarker marker) {
		final IResource resource = marker.getResource();

		IWorkbench wb = PlatformUI.getWorkbench();
		IProgressService ps = wb.getProgressService();
		try {
			ps.busyCursorWhile(new IRunnableWithProgress() {
				public void run(IProgressMonitor pm) {

					ResourceBundleManager manager = null;
					pm.beginTask(
					        "Excluding Resource from Internationalization", 1);

					if (manager == null
					        || (manager.getProject() != resource.getProject()))
						manager = ResourceBundleManager.getManager(resource
						        .getProject());
					manager.excludeResource(resource, pm);
					pm.worked(1);
					pm.done();
				}
			});
		} catch (Exception e) {
		}
	}

	@Override
	public String getDescription() {
		return "Exclude Resource from Internationalization";
	}

	@Override
	public Image getImage() {
		return null;
	}

}
