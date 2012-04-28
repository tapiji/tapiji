package org.eclipse.babel.tapiji.tools.core.ui.utils;

import java.util.Iterator;

import org.eclipse.babel.editor.IMessagesEditor;
import org.eclipse.babel.tapiji.tools.core.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.AbstractMarkerAnnotationModel;
import org.eclipse.ui.texteditor.SimpleMarkerAnnotation;

public class EditorUtils {

	/** Editor ids **/
	public static final String RESOURCE_BUNDLE_EDITOR = "com.essiembre.rbe.eclipse.editor.ResourceBundleEditor";
	
	public static IEditorPart openEditor(IWorkbenchPage page, IFile file,
	        String editor) {
		// open the rb-editor for this file type
		try {
			return IDE.openEditor(page, file, editor);
		} catch (PartInitException e) {
			Logger.logError(e);
		}
		return null;
	}

	public static IEditorPart openEditor(IWorkbenchPage page, IFile file,
	        String editor, String key) {
		// open the rb-editor for this file type and selects given msg key
		IEditorPart part = openEditor(page, file, editor);
		if (part instanceof IMessagesEditor) {
			IMessagesEditor msgEditor = (IMessagesEditor) part;
			msgEditor.setSelectedKey(key);
		}
		return part;
	}
	
	public static void updateMarker(IMarker marker) {
		FileEditorInput input = new FileEditorInput(
		        (IFile) marker.getResource());

		AbstractMarkerAnnotationModel model = (AbstractMarkerAnnotationModel) getAnnotationModel(marker);
		IDocument doc = JavaUI.getDocumentProvider().getDocument(input);

		try {
			model.updateMarker(doc, marker, getCurPosition(marker, model));
		} catch (CoreException e) {
			Logger.logError(e);
		}
	}

	public static IAnnotationModel getAnnotationModel(IMarker marker) {
		FileEditorInput input = new FileEditorInput(
		        (IFile) marker.getResource());

		return JavaUI.getDocumentProvider().getAnnotationModel(input);
	}
	
	private static Position getCurPosition(IMarker marker,
	        IAnnotationModel model) {
		Iterator iter = model.getAnnotationIterator();
		Logger.logInfo("Updates Position!");
		while (iter.hasNext()) {
			Object curr = iter.next();
			if (curr instanceof SimpleMarkerAnnotation) {
				SimpleMarkerAnnotation annot = (SimpleMarkerAnnotation) curr;
				if (marker.equals(annot.getMarker())) {
					return model.getPosition(annot);
				}
			}
		}
		return null;
	}
	
}
