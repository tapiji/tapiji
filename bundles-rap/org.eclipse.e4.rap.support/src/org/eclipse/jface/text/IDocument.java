package org.eclipse.jface.text;

import javax.swing.text.BadLocationException;

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
	
	/**
	 * Returns this document's text for the specified range.
	 *
	 * @param offset the document offset
	 * @param length the length of the specified range
	 * @return the document's text for the specified range
	 * @exception BadLocationException if the range is invalid in this document
	 */
	String get(int offset, int length) throws BadLocationException;
	
	/**
	 * Returns the number of the line at which the character of the specified position is located.
	 * The first line has the line number 0. A new line starts directly after a line
	 * delimiter. <code>(offset == document length)</code> is a valid argument although there is no
	 * corresponding character.
	 *
	 * @param offset the document offset
	 * @return the number of the line
	 * @exception BadLocationException if the offset is invalid in this document
	 */
	int getLineOfOffset(int offset) throws BadLocationException;
	

	public long getModificationStamp();

}
