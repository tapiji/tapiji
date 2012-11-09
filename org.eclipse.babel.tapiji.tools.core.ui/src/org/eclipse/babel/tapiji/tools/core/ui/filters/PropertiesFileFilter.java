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
package org.eclipse.babel.tapiji.tools.core.ui.filters;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class PropertiesFileFilter extends ViewerFilter {

    private boolean debugEnabled = true;

    public PropertiesFileFilter() {

    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        if (debugEnabled)
            return true;

        if (element.getClass().getSimpleName().equals("CompilationUnit"))
            return false;

        if (!(element instanceof IFile))
            return true;

        IFile file = (IFile) element;

        return file.getFileExtension().equalsIgnoreCase("properties");
    }

}
