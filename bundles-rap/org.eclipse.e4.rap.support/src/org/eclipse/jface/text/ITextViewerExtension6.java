package org.eclipse.jface.text;

/**
 * Extension interface for {@link org.eclipse.jface.text.ITextViewer}.
 * Introduces the concept of text hyperlinks and adds access to the undo manager.
 *
 * @see org.eclipse.jface.text.hyperlink.IHyperlink
 * @see org.eclipse.jface.text.hyperlink.IHyperlinkDetector
 * @since 3.1
 */
public interface ITextViewerExtension6 {

	/**
	 * Returns this viewer's undo manager.
	 *
	 * @return the undo manager or <code>null</code> if it has not been plugged-in
	 * @since 3.1
	 */
	IUndoManager getUndoManager();

}
