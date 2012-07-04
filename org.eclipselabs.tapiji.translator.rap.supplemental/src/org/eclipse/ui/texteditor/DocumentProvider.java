package org.eclipse.ui.texteditor;

import org.eclipse.jface.text.Document;
import org.eclipse.ui.IEditorInput;

/**
 * Simple DocumentProvider class, only holds an Document object.
 * 
 * @author Matthias Lettmayer
 *
 */
public class DocumentProvider {
	
	private Document document;
	
	public DocumentProvider(Document document) {
		this.document = document;
	}
	
	public DocumentProvider(String source) {
		this.document = new Document(source);
	}
	// TODO [RAP] handle EditorInputs
	public Document getDocument(IEditorInput editorInput) {
		return document;
	}
}
