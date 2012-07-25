package org.eclipselabs.tapiji.translator.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipselabs.tapiji.translator.rap.utils.FileRAPUtils;
import org.eclipselabs.tapiji.translator.utils.FileUtils;

public class FileOpenAction extends AbstractFileOpenAction {

	@Override
	public void run(IAction action) {
		String[] filenames = FileUtils.queryFileName(window.getShell(),
		        "Open Resource-Bundle", SWT.OPEN | SWT.MULTI,
		        new String[] { "*.properties" });
		
		// cancel option
		if (filenames == null)
			return;
		
		List<String> copy = new ArrayList<String>();
		copy.addAll(Arrays.asList(filenames));
		boolean showError = false;
		for (String filename : filenames) {			
			if (! FileUtils.isResourceBundle(filename)) {
				showError = true;
				copy.remove(filename);
			}
		}
		if (showError) {
			MessageDialog.openError(window.getShell(),
			        "Cannot open a messages file",
			        "At least one of your choosen files does not represent a messages file!\nIt wasn't added to the editor!");
		}
		filenames = copy.toArray(new String[0]);
		
		if (filenames.length == 0)
			return;
		
		IWorkbenchPage page = window.getActivePage();
		try {
			page.openEditor(
			        new FileEditorInput(FileRAPUtils
			                .getResourceBundleRef(filenames, RWT.getSessionStore().getId())),
			        RESOURCE_BUNDLE_EDITOR);

		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

}
