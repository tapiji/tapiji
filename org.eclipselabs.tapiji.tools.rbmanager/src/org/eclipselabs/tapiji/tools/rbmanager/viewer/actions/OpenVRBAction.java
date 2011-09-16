package org.eclipselabs.tapiji.tools.rbmanager.viewer.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipselabs.tapiji.tools.core.util.EditorUtils;
import org.eclipselabs.tapiji.tools.rbmanager.RBManagerActivator;
import org.eclipselabs.tapiji.tools.rbmanager.model.VirtualResourceBundle;



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
