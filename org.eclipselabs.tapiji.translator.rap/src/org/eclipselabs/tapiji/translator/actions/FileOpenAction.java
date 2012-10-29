package org.eclipselabs.tapiji.translator.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipselabs.tapiji.translator.rap.helpers.utils.UserUtils;
import org.eclipselabs.tapiji.translator.rap.model.user.PropertiesFile;
import org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle;
import org.eclipselabs.tapiji.translator.rap.utils.EditorUtils;
import org.eclipselabs.tapiji.translator.rap.utils.FileRAPUtils;
import org.eclipselabs.tapiji.translator.rap.utils.StorageUtils;
import org.eclipselabs.tapiji.translator.utils.FileUtils;

public class FileOpenAction extends AbstractFileOpenAction {

	private static final String TOKEN_FILENAME = "FILENAME";
	private static final String ERROR_MSG_NOT_VALID = "The file \""+ TOKEN_FILENAME +"\" does not represent a properties file!";
	private static final String ERROR_MSG_ALREADY_EXISTS = "The file \""+ TOKEN_FILENAME +"\" exists already in your session project!";
	
	@Override
	public void run(IAction action) {
		String[] filepaths = FileUtils.queryFileName(window.getShell(),
		        "Open Resource-Bundle", SWT.OPEN | SWT.MULTI,
		        new String[] { "*.properties" });
		
		// cancel option
		if (filepaths == null)
			return;
		
		List<String> copy = new ArrayList<String>();
		copy.addAll(Arrays.asList(filepaths));
		
		List<String> errorMsgs = new ArrayList<String>();
		for (String filepath : filepaths) {		
			File file = new File(filepath);
			String bundleName = FileRAPUtils.getBundleName(filepath);
			
			if (! FileUtils.isResourceBundle(filepath)) {
				errorMsgs.add(ERROR_MSG_NOT_VALID.replaceFirst(TOKEN_FILENAME, file.getName()));
				copy.remove(filepath);
				continue;
			}
			
			// exists file already in session project
			if (FileRAPUtils.existsProjectFile(FileRAPUtils.getSessionProject(), file.getName())) {
				errorMsgs.add(ERROR_MSG_ALREADY_EXISTS.replaceFirst(TOKEN_FILENAME, file.getName()));			
			// exists bundle already
			} else {
				for (ResourceBundle rb : StorageUtils.getSessionRBs()) {
					if (rb.getName().equals(bundleName)) {
						// add to existing rb ?
						if (MessageDialog.openConfirm(window.getShell(), "Duplicated resource bundles", 
								"You have uploaded a properties file, which has the same bundle name as an already opened resource bundle.\n\n" +
								"The file \""+file.getName()+"\" will be added to the existing resource bundle \""+bundleName+"\"."))
							EditorUtils.closeEditorOfRB(rb, true);
						else
							copy.remove(filepath);
						break;
					}
				}
					
			}
		}
		
		if (! errorMsgs.isEmpty()) {
			String errString = "The following error(s) occured:\n\n";
			for (int i=0; i<errorMsgs.size(); i++) {
				String errorMsg = errorMsgs.get(i);
				errString += errorMsg;
				if (i != errorMsgs.size()-1)
					errString += "\n";
			}
			MessageDialog.openError(window.getShell(), "Error(s) occured", errString);
		}
		
		filepaths = copy.toArray(new String[0]);
		if (filepaths.length == 0)
			return;
		
		// copy files into workspace to temp/session project
		List<ResourceBundle> rbs = FileRAPUtils.getResourceBundleRef(filepaths, FileRAPUtils.getSessionProject());		
		
		// if user is logged in store rbs directly
		// move rbs to user project and store them persistently to db 
		if (UserUtils.isUserLoggedIn()) {			
			for (ResourceBundle rb : rbs) {
				// abort if a properties file with same name exists already
				boolean existsPF = false;
				for (PropertiesFile pf : rb.getPropertiesFiles()) {
					if (FileRAPUtils.existsProjectFile(FileRAPUtils.getUserProject(), pf.getFilename())) {
						existsPF = true;
						break;
					}	
				}
				if (! existsPF)
					StorageUtils.storeRB(rb);
			}
		} 
		
		// open editor(s) for resource bundle(s)
		for (ResourceBundle rb : rbs)
			EditorUtils.openEditorOfRB(rb);
		
		StorageUtils.refreshStorageView();			
	}

}
