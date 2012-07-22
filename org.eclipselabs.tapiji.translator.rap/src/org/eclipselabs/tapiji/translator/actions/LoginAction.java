package org.eclipselabs.tapiji.translator.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipselabs.tapiji.translator.rap.dialogs.LoginDialog;
import org.eclipselabs.tapiji.translator.views.StorageView;

public class LoginAction extends Action implements IWorkbenchWindowActionDelegate {

	IWorkbenchWindow window;
	
	@Override
	public void run(IAction action) {
        LoginDialog login = new LoginDialog(window.getShell());
        login.open();
        
        // refreshing storage view
 		IViewPart viewPart = window.getActivePage().findView(StorageView.ID);
 		if (viewPart instanceof StorageView)
 			((StorageView) viewPart).refresh();
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		window = null;
	}

	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

}
