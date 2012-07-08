package org.eclipse.ui.texteditor;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextDocument;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.editors.text.TextEditor;

/**
 * Simple DocumentProvider class, only holds an Document object.
 * 
 * @author Matthias Lettmayer
 *
 */
public class DocumentProvider {
	
	private IDocument document;
	
	public DocumentProvider(IDocument document) {
		this.document = document;
	}
	
	public DocumentProvider(TextEditor t) {
		this.document = new TextDocument(t);
	}
	
	public IDocument getDocument(IEditorInput editorInput) {
		return document;
	}
}
