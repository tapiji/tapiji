package org.eclipselabs.tapiji.translator.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipselabs.tapiji.translator.utils.FileUtils;

public class FileOpenAction extends AbstractFileOpenAction {

	@Override
	public void run(IAction action) {
		String[] fileNames = FileUtils.queryFileName(window.getShell(),
		        "Open Resource-Bundle", SWT.OPEN,
		        new String[] { "*.properties" });
		
		// cancel option
		if (fileNames == null)
			return;
		
		String fileName = fileNames[0];
		
		if (!FileUtils.isResourceBundle(fileName)) {
			MessageDialog.openError(window.getShell(),
			        "Cannot open Resource-Bundle",
			        "The choosen file does not represent a Resource-Bundle!");
			return;
		}

		IWorkbenchPage page = window.getActivePage();
		try {
			page.openEditor(
			        new FileEditorInput(FileUtils.getResourceBundleRef(fileName, 
			        		FileUtils.EXTERNAL_RB_PROJECT_NAME)),
			        RESOURCE_BUNDLE_EDITOR);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

}
