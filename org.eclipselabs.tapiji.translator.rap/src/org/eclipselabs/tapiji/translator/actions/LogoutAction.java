package org.eclipselabs.tapiji.translator.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipselabs.tapiji.translator.rap.model.user.File;
import org.eclipselabs.tapiji.translator.rap.model.user.User;
import org.eclipselabs.tapiji.translator.rap.utils.UserUtils;
import org.eclipselabs.tapiji.translator.views.StorageView;

public class LogoutAction implements IWorkbenchWindowActionDelegate {

	IWorkbenchWindow window;
	
	@Override
	public void run(IAction action) {
		User user = UserUtils.logoutUser();
		
		// close opened editors with user files
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorReference[] editors = page.getEditorReferences();		
		for (int i=0; i < editors.length; i++) {
			try {
				IEditorInput editorInput = editors[i].getEditorInput();						
				if (editorInput instanceof IFileEditorInput) {
					IFileEditorInput fileInput = (IFileEditorInput) editorInput;
					IFile iFile = fileInput.getFile();
					for (File userFile : user.getStoredFiles()) {
						if (iFile.getLocation().toOSString().equals(userFile.getPath()))
							page.closeEditor(editors[i].getEditor(false), false);
					}	
				}
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
		
		// refreshing storage view
		if (window != null) {
			IViewPart viewPart = window.getActivePage().findView(StorageView.ID);
			if (viewPart instanceof StorageView)
				((StorageView) viewPart).refresh();
		}
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
