package org.eclipselabs.tapiji.translator.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipselabs.tapiji.translator.utils.FileUtils;


public class FileOpenAction extends Action implements IWorkbenchWindowActionDelegate {

	/** Editor ids **/
	public static final String RESOURCE_BUNDLE_EDITOR = "com.essiembre.eclipse.rbe.ui.editor.ResourceBundleEditor";
	
	private IWorkbenchWindow window;
	
	@Override
	public void run(IAction action) {
		String fileName= FileUtils.queryFileName(window.getShell(), "Open Resource-Bundle", SWT.OPEN, new String[] {"*.properties"} );
		if (!FileUtils.isResourceBundle(fileName)) {
			MessageDialog.openError(window.getShell(), 
					"Cannot open Resource-Bundle", "The choosen file does not represent a Resource-Bundle!");
			return;
		}
		
		if (fileName != null) {
			IWorkbenchPage page= window.getActivePage();
			try {
				page.openEditor(new FileEditorInput(FileUtils.getResourceBundleRef(fileName)), 
						RESOURCE_BUNDLE_EDITOR);
			
			} catch (CoreException e) {
				e.printStackTrace();
			}
		} 
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
