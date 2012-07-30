package org.eclipselabs.tapiji.translator.rap.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.babel.core.util.FileUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipselabs.tapiji.translator.actions.FileOpenAction;
import org.eclipselabs.tapiji.translator.rap.model.user.PropertiesFile;
import org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle;

public class EditorUtils {
	public static final String MSG_EDITOR_ID = FileOpenAction.RESOURCE_BUNDLE_EDITOR;
	
	public static void openEditorOfRB(ResourceBundle rb) {
		if (isRBOpened(rb) || rb.getLocalFiles().isEmpty())
			return;
		
		try {
			getActivePage().openEditor( new FileEditorInput(FileRAPUtils.getIFile(rb.getLocalFiles().get(0))), 
					MSG_EDITOR_ID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}	
	}
	
	public static boolean closeEditorOfRB(ResourceBundle rb, boolean save) {
		List<IEditorReference> openedEditors = getOpenedEditors(rb);
		
		if (openedEditors.isEmpty())
			return false;
		
		return getActivePage().closeEditor(openedEditors.get(0).getEditor(false), save);		
	}
	
	public static void closeAllEditorsOfRB(ResourceBundle rb, boolean save) {
		List<IEditorReference> openedEditors = getOpenedEditors(rb);
		
		if (openedEditors.isEmpty())
			return;
		
		// close all opened editors
		for (IEditorReference editorRef : openedEditors) {
			getActivePage().closeEditor(editorRef.getEditor(false), save);
		}
	}
	
	public static IWorkbenchPage getActivePage() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
	}
		
	public static boolean isRBOpened(ResourceBundle rb) {
		if (getOpenedEditors(rb).isEmpty())
			return false;
		return true;
	}
	
	public static List<IEditorReference> getOpenedEditors(ResourceBundle rb) {
		List<IEditorReference> openedRB = new ArrayList<IEditorReference>();
		try {
			for (IEditorReference editor : getActivePage().getEditorReferences()) {
				if (editor.getEditorInput() instanceof IFileEditorInput) {
					IFileEditorInput editorInput = (IFileEditorInput) editor.getEditorInput();
					String ifilePath = editorInput.getFile().getLocation().toOSString();
					
					for (PropertiesFile file : rb.getLocalFiles())
						if (ifilePath.equals(file.getPath()))
								openedRB.add(editor);
				}
			}
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		return openedRB;
	}
	
	public static ResourceBundle getRBFromEditor(IEditorPart editor) {
		ResourceBundle rb = null;
		
		if (editor.getEditorInput() instanceof IFileEditorInput) {
			IFile file = ((IFileEditorInput) editor.getEditorInput()).getFile();			
			String bundleName = FileRAPUtils.getBundleName(file.getFullPath());
			boolean isFileTemporary = file.getProject().equals(FileRAPUtils.getSessionProject());			
			rb = StorageUtils.getResourceBundle(bundleName, isFileTemporary);
		}
		
		return rb;
	}
}
