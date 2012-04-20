/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.rbmanager.viewer;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.ui.navigator.ILinkHelper;

/*
 * Allows 'link with editor'
 */
public class LinkHelper implements ILinkHelper {

	public static IStructuredSelection viewer;
	
	@Override
	public IStructuredSelection findSelection(IEditorInput anInput) {		
		IFile file = ResourceUtil.getFile(anInput);
		 if (file != null) {
			 return new StructuredSelection(file);
			 }
		return StructuredSelection.EMPTY;
	}

	@Override
	public void activateEditor(IWorkbenchPage aPage,IStructuredSelection aSelection) {
		if (aSelection.getFirstElement() instanceof IFile)
			try {
				IDE.openEditor(aPage, (IFile) aSelection.getFirstElement());
			} catch (PartInitException e) {/**/}
	}

}
