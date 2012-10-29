package org.eclipselabs.tapiji.translator.rap.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.babel.core.message.internal.MessagesBundle;
import org.eclipse.babel.core.message.internal.MessagesBundleGroupAdapter;
import org.eclipse.babel.editor.IMessagesEditor;
import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipselabs.tapiji.translator.actions.FileOpenAction;
import org.eclipselabs.tapiji.translator.rap.helpers.managers.RBLockManager;
import org.eclipselabs.tapiji.translator.rap.model.user.PropertiesFile;
import org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle;


/**
 * Utility methods handle resource bundles in editors
 * @author Matthias Lettmayer
 *
 */
public class EditorUtils {
	/** Messages Editor ID */
	public static final String MSG_EDITOR_ID = FileOpenAction.RESOURCE_BUNDLE_EDITOR;
	
	
	/**
	 * Opens a given resource bundle in a new editor only if it's not already opened. 
	 * Uses the first local file of resource bundle for creating a FileEditorInput as input for the Messages Editor.
	 * @param rb The resource bundle will be opened
	 */
	public static void openEditorOfRB(ResourceBundle rb) {
		if (isRBOpened(rb) || rb.getPropertiesFiles().isEmpty())
			return;
		
		try {
			IEditorPart openedEditor = getActivePage().openEditor( new FileEditorInput(FileRAPUtils.getFile(rb.getPropertiesFiles().get(0))), 
					MSG_EDITOR_ID);
			
			if (openedEditor instanceof IMessagesEditor) {
				// add msg bundle group listener to opened editor which refreshes storage view if new local is added
				IMessagesEditor msgEditor = (IMessagesEditor) openedEditor;
				msgEditor.getBundleGroup().addMessagesBundleGroupListener(new MessagesBundleGroupAdapter() {
					@Override
					public void messagesBundleAdded(
							MessagesBundle messagesBundle) {
						StorageUtils.refreshStorageView();
						
						// TODO inform other storage views which share resource bundle
					}
				});
				
				// override title name of opened messages editor
				String titleName = rb.getName();
				if (rb.isTemporary())
					titleName += " (temp)";
				msgEditor.setTitleName(titleName);
			}
		} catch (PartInitException e) {
			e.printStackTrace();
		}	
	}
	
	/** 
	 * Closes an opened editor of a given resource bundle. If multiple editors of the same resource bundle are opened, 
	 * only one will be closed.
	 * @param rb The resource bundle that will be closed.
	 * @param save True to give the user a chance to save the resource bundle before it will be closed.
	 * @return True if an editor was closed, otherwise false.
	 */
	public static boolean closeEditorOfRB(ResourceBundle rb, boolean save) {
		List<IEditorReference> openedEditors = getOpenedEditors(rb);
		
		if (openedEditors.isEmpty())
			return false;
		
		releaseLock(rb.getId());
		
		boolean closed = getActivePage().closeEditor(openedEditors.get(0).getEditor(false), save);		
		
		if (closed)
			releaseLock(rb.getId());
		
		return closed;
	}
	
	/**
	 * Similar to {@link #closeEditorOfRB(ResourceBundle, boolean)}, but closes all opened editors of given resource bundle,
	 * instead of only one.
	 * @param rb The resource bundle that will be closed.
	 * @param save True to give the user a chance to save the resource bundle before it will be closed.
	 * @return true if editors were closed successfully, false if at least one editor is still opened.
	 */
//	public static boolean closeAllEditorsOfRB(ResourceBundle rb, boolean save) {
//		List<IEditorReference> openedEditors = getOpenedEditors(rb);
//		
//		if (openedEditors.isEmpty())
//			return true;
//		
//		// close all opened editors
//		for (IEditorReference editorRef : openedEditors) {
//			if (! getActivePage().closeEditor(editorRef.getEditor(false), save))
//				return false;
//		}
//		
//		releaseLock(rb.getId());
//		
//		return true;
//	}
	
	/**
	 * Returns the active page of the workbench.
	 * @return active page
	 */
	public static IWorkbenchPage getActivePage() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
	}
		
	/**
	 * Returns true if an editor of a given resource bundle is already opened.
	 * @param rb resource bundle
	 * @return True if resource bundle is opened.
	 */
	public static boolean isRBOpened(ResourceBundle rb) {
		if (getOpenedEditors(rb).isEmpty())
			return false;
		return true;
	}
	
	/**
	 * Returns a list of opened editors of a given resource bundle (ONLY in this UIThread). 
	 * Searches through all opened editor references and compares the file path of the opened 
	 * editor with the path of the locale files of resource bundle.
	 * If a locale file of resource bundle matches the editor file, then the resource bundle is 
	 * opened and added to the return list.
	 * @param rb resource bundle
	 * @return a list of opened resource bundles
	 */
	public static List<IEditorReference> getOpenedEditors(ResourceBundle rb) {
		List<IEditorReference> openedRB = new ArrayList<IEditorReference>();
		try {
			for (IEditorReference editor : getActivePage().getEditorReferences()) {
				if (editor.getEditorInput() instanceof IFileEditorInput) {
					IFileEditorInput editorInput = (IFileEditorInput) editor.getEditorInput();
					String ifilePath = editorInput.getFile().getLocation().toOSString();
					
					for (PropertiesFile file : rb.getPropertiesFiles())
						if (ifilePath.equals(file.getPath()))
								openedRB.add(editor);
				}
			}
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		return openedRB;
	}
	
	/**
	 * Returns the resource bundle from a given editor. 
	 * @param editor
	 * @return resource bundle from editor or null if editor doesn't hold a resource bundle.
	 */
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
	
	public static IMessagesEditor getMessagesEditor(ResourceBundle rb) {
		List<IEditorReference> editorRefs = getOpenedEditors(rb);
		if (editorRefs.isEmpty())
			return null;
		
		IEditorPart openedEditor = editorRefs.get(0).getEditor(false);
		if (openedEditor instanceof IMessagesEditor)
			return (IMessagesEditor) openedEditor;
		
		return null;
	}
	
	private static boolean releaseLock(long rbID) {		
		boolean locked = RBLockManager.INSTANCE.isPFLocked(rbID);
		if (locked) {
			RBLockManager.INSTANCE.release(rbID);
		}
		return locked;
	}
}
