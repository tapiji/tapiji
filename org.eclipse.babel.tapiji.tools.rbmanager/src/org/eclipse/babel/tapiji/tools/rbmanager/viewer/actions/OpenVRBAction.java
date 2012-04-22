/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Michael Gasser - initial API and implementation
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.rbmanager.viewer.actions;

import org.eclipse.babel.tapiji.tools.core.util.EditorUtils;
import org.eclipse.babel.tapiji.tools.rbmanager.RBManagerActivator;
import org.eclipse.babel.tapiji.tools.rbmanager.model.VirtualResourceBundle;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;



public class OpenVRBAction extends Action {
	private ISelectionProvider selectionProvider;
	
	public OpenVRBAction(ISelectionProvider selectionProvider) {
		this.selectionProvider = selectionProvider;
	}
	
	@Override
	public boolean isEnabled(){
		IStructuredSelection sSelection = (IStructuredSelection) selectionProvider.getSelection();
		if (sSelection.size()==1) return true;
		else return false;
	}
	
	@Override
	public void run(){
		IStructuredSelection sSelection = (IStructuredSelection) selectionProvider.getSelection();
		if (sSelection.size()==1 && sSelection.getFirstElement() instanceof VirtualResourceBundle){
			VirtualResourceBundle vRB = (VirtualResourceBundle) sSelection.getFirstElement();
			IWorkbenchPage wp = RBManagerActivator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
		
			EditorUtils.openEditor(wp, vRB.getRandomFile(), EditorUtils.RESOURCE_BUNDLE_EDITOR);
		}
	}
}
