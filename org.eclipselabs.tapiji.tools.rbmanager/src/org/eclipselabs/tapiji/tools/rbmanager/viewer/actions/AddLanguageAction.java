package org.eclipselabs.tapiji.tools.rbmanager.viewer.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;


public class AddLanguageAction extends Action {
	private ISelectionProvider selectionProvider;
	
	public AddLanguageAction(ISelectionProvider selectionProvider) {
		this.selectionProvider = selectionProvider;
		setText("add language to project");
		setToolTipText("creates new properties-files for a language in all ResourceBundles of a project");
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
//		new SelectLocalePage(sSelection);
		
		
	}
}
