package org.eclipse.jface.text;



public interface ITextListener {
	/**
	 * The visual representation of a text viewer this listener is registered with
	 * has been changed.
	 *
	 * @param event the description of the change
	 */
	void textChanged(TextEvent event);
}
