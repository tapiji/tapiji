package org.eclipse.jface.text;

/**
 * IDocument: added only needed parts, just to get the source code compiled in RAP.
 * 
 * @author Matthias Lettmayer
 *
 */
public interface IDocument {
	/**
	 * Returns this document's complete text.
	 *
	 * @return the document's complete text
	 */
	String get();
	
	/**
	 * Returns the number of characters in this document.
	 *
	 * @return the number of characters in this document
	 */
	int getLength();
	
	/**
	 * Replaces the content of the document with the given text.
	 * Sends a <code>DocumentEvent</code> to all registered <code>IDocumentListener</code>.
	 * This method is a convenience method for <code>replace(0, getLength(), text)</code>.
	 *
	 * @param text the new content of the document
	 *
	 * @see DocumentEvent
	 * @see IDocumentListener
	 */
	void set(String text);
}
