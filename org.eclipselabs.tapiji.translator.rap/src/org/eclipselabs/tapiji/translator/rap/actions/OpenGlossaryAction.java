package org.eclipselabs.tapiji.translator.rap.actions;

import java.io.File;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipselabs.tapiji.translator.rap.core.GlossaryManager;
import org.eclipselabs.tapiji.translator.rap.utils.FileUtils;


public class OpenGlossaryAction implements IWorkbenchWindowActionDelegate {
	
	/** The workbench window */
	private IWorkbenchWindow window;

	@Override
	public void run(IAction action) {
		String fileName= FileUtils.queryFileName(window.getShell(), "Open Glossary", SWT.OPEN, new String[] {"*.xml"} );
		if (!FileUtils.isGlossary(fileName)) {
			MessageDialog.openError(window.getShell(), 
					"Cannot open Glossary", "The choosen file does not represent a Glossary!");
			return;
		}
		
		if (fileName != null) {
			IWorkbenchPage page= window.getActivePage();
			if (fileName != null) {
				GlossaryManager.loadGlossary(new File (fileName));
			}
		} 
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {

	}

	@Override
	public void dispose() {
		this.window = null;
	}

	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

}
