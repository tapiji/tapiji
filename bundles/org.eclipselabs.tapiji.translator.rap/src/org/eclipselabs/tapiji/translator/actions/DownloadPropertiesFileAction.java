package org.eclipselabs.tapiji.translator.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipselabs.tapiji.translator.rap.dialogs.DownloadDialog;
import org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle;
import org.eclipselabs.tapiji.translator.rap.utils.EditorUtils;

public class DownloadPropertiesFileAction extends Action implements IWorkbenchWindowActionDelegate {
	
	private static final long serialVersionUID = 1854301925848519466L;
	private IWorkbenchWindow window;
	
	
	@Override
	public void run(IAction action) {
		// open download dialog
		if (window != null) {
			DownloadDialog dialog = new DownloadDialog(window.getShell());
			
			IWorkbenchPage activePage = window.getActivePage();
			IEditorPart activeEditor = null;
			if (activePage != null && (activeEditor = activePage.getActiveEditor()) != null) {
				ResourceBundle rb = EditorUtils.getRBFromEditor(activeEditor);
				dialog.setRB(rb);
				dialog.open();			
			}
		}
			
	}
	
	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
	
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		window = null;
	}
}
