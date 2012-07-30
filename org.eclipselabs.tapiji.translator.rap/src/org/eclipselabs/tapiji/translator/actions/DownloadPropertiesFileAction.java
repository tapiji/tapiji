package org.eclipselabs.tapiji.translator.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.service.IServiceHandler;
import org.eclipse.rwt.service.IServiceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipselabs.tapiji.translator.handler.DownloadServiceHandler;
import org.eclipselabs.tapiji.translator.rap.dialogs.DownloadDialog;
import org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle;
import org.eclipselabs.tapiji.translator.rap.utils.EditorUtils;
import org.eclipselabs.tapiji.translator.views.StorageView;

public class DownloadPropertiesFileAction extends Action implements IWorkbenchWindowActionDelegate {
	
	private static final long serialVersionUID = 1854301925848519466L;
	private IWorkbenchWindow window;
	private IServiceHandler handler;
	
	
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
