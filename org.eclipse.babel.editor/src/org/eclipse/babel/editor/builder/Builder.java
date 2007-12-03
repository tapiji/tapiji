/*******************************************************************************
 * Copyright (c) 2007 Pascal Essiembre.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Pascal Essiembre - initial API and implementation
 ******************************************************************************/
package org.eclipse.babel.editor.builder;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.babel.editor.plugin.MessagesEditorPlugin;
import org.eclipse.babel.editor.resource.validator.FileMarkerStrategy;
import org.eclipse.babel.editor.resource.validator.IValidationMarkerStrategy;
import org.eclipse.babel.editor.resource.validator.ResourceValidator;

/**
 * @author Pascal Essiembre
 *
 */
public class Builder extends IncrementalProjectBuilder {

    public static final String BUILDER_ID =
            "org.eclipse.babel.editor.rbeBuilder"; //$NON-NLS-1$
   
    private IValidationMarkerStrategy markerStrategy = new FileMarkerStrategy();
    
	class SampleDeltaVisitor implements IResourceDeltaVisitor {
		/**
		 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
		 */
		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();
			switch (delta.getKind()) {
			case IResourceDelta.ADDED:
				// handle added resource
                System.out.println("RBE DELTA added");
				checkBundleResource(resource);
				break;
			case IResourceDelta.REMOVED:
                System.out.println("RBE DELTA Removed"); //$NON-NLS-1$
				// handle removed resource
				break;
			case IResourceDelta.CHANGED:
                System.out.println("RBE DELTA changed");
				// handle changed resource
				checkBundleResource(resource);
				break;
			}
			//return true to continue visiting children.
			return true;
		}
	}

	class SampleResourceVisitor implements IResourceVisitor {
		public boolean visit(IResource resource) {
			checkBundleResource(resource);
			//return true to continue visiting children.
			return true;
		}
	}

    

  
	/**
	 * @see org.eclipse.core.resources.IncrementalProjectBuilder#build(
     *          int, java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		if (kind == FULL_BUILD) {
			fullBuild(monitor);
		} else {
			IResourceDelta delta = getDelta(getProject());
			if (delta == null) {
				fullBuild(monitor);
			} else {
				incrementalBuild(delta, monitor);
			}
		}
		return null;
	}

	void checkBundleResource(IResource resource) {
        if (resource instanceof IFile && resource.getName().endsWith(
                ".properties")) { //$NON-NLS-1$ //TODO have customized?
            IFile file = (IFile) resource;
            deleteMarkers(file);
            System.out.println("Find markers"); //$NON-NLS-1$
            ResourceValidator.validate(file, markerStrategy);
        }
	}

	private void deleteMarkers(IFile file) {
		try {
            System.out.println("Builder: deleteMarkers"); //$NON-NLS-1$
			file.deleteMarkers(MessagesEditorPlugin.MARKER_TYPE, false, IResource.DEPTH_ZERO);
		} catch (CoreException ce) {
		}
	}

	protected void fullBuild(final IProgressMonitor monitor)
			throws CoreException {
        System.out.println("Builder: fullBuild"); //$NON-NLS-1$
		getProject().accept(new SampleResourceVisitor());
	}

	protected void incrementalBuild(IResourceDelta delta,
			IProgressMonitor monitor) throws CoreException {
        System.out.println("Builder: incrementalBuild"); //$NON-NLS-1$
		delta.accept(new SampleDeltaVisitor());
	}
}
