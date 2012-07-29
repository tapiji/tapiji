package org.eclipselabs.tapiji.translator.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipselabs.tapiji.translator.rap.dialogs.RegisterDialog;

public class RegisterAction extends Action implements IWorkbenchWindowActionDelegate {

	IWorkbenchWindow window;
	
	@Override
	public void run(IAction action) {
        RegisterDialog registerDialog = new RegisterDialog(window.getShell());
        registerDialog.open();
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
