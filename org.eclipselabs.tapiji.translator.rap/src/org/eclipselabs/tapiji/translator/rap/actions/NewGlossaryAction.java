package org.eclipselabs.tapiji.translator.rap.actions;

import java.io.File;
import java.text.MessageFormat;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipselabs.tapiji.translator.rap.core.GlossaryManager;
import org.eclipselabs.tapiji.translator.rap.utils.FileUtils;


public class NewGlossaryAction implements IWorkbenchWindowActionDelegate {

	/** The workbench window */
	private IWorkbenchWindow window;

	@Override
	public void run(IAction action) {
		String fileName= FileUtils.queryFileName(window.getShell(), "New Glossary", SWT.SAVE, new String[] {"*.xml"} );
		
		if (!fileName.endsWith(".xml")) {
			if (fileName.endsWith("."))
				fileName += "xml";
			else
				fileName += ".xml";
		}
		
		if (new File (fileName).exists()) {
			String recallPattern = "The file \"{0}\" already exists. Do you want to replace this file with an empty translation glossary?";
			MessageFormat mf = new MessageFormat(recallPattern);
			
			if (!MessageDialog.openQuestion(window.getShell(), 
					"File already exists!", mf.format(new String[] {fileName})))
				return;
		}
		
		if (fileName != null) {
			IWorkbenchPage page= window.getActivePage();
			GlossaryManager.newGlossary (new File (fileName));
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
