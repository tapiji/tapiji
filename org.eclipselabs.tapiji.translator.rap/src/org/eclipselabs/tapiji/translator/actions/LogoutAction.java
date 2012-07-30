package org.eclipselabs.tapiji.translator.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle;
import org.eclipselabs.tapiji.translator.rap.model.user.User;
import org.eclipselabs.tapiji.translator.rap.utils.EditorUtils;
import org.eclipselabs.tapiji.translator.rap.utils.StorageUtils;
import org.eclipselabs.tapiji.translator.rap.utils.UserUtils;

public class LogoutAction implements IWorkbenchWindowActionDelegate {

	IWorkbenchWindow window;
	
	@Override
	public void run(IAction action) {
		User user = UserUtils.logoutUser();
		
		// close all opened editors with user resource bundles
		for (ResourceBundle userRB : user.getStoredRBs())
			EditorUtils.closeAllEditorsOfRB(userRB, true);
		
		StorageUtils.refreshStorageView();
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

}
