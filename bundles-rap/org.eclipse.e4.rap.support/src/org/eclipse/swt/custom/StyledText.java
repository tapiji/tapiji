package org.eclipse.swt.custom;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * StyledText class is a wrapper for a Label in RAP
 * 
 * @author Matthias Lettmayer
 *
 */
public class StyledText extends Label {
	private static final long serialVersionUID = -5801418312829430710L;
	
	public StyledText(Composite parent, int style) {
		super(parent, style);
	}
	
	/**
	 * Sets the selection.
	 * <p>
	 * The new selection may not be visible. Call showSelection to scroll
	 * the selection into view.
	 * </p>
	 *
	 * @param start offset of the first selected character, start >= 0 must be true.
	 * @param length number of characters to select, 0 <= start + length
	 * 	<= getCharCount() must be true.
	 * 	A negative length places the caret at the selection start.
	 * @param sendEvent a Selection event is sent when set to true and when
	 * 	the selection is reset.
	 */
	void setSelection(int start, int length, boolean sendEvent, boolean doBlock) {
		
	}
	
	/**
	 * Returns the caret position relative to the start of the text.
	 *
	 * @return the caret position relative to the start of the text.
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public int getCaretOffset() {
		return 0;
	}
	
	/**
	 * Sets whether the widget content can be edited.
	 * </p>
	 *
	 * @param editable if true content can be edited, if false content can not be
	 * 	edited
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public void setEditable(boolean editable) {

	}
		
	/**
	 * Sets the selection to the given position and scrolls it into view.  Equivalent to setSelection(start,start).
	 *
	 * @param start new caret position
	 * @see #setSelection(int,int)
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 * @exception IllegalArgumentException <ul>
	 *   <li>ERROR_INVALID_ARGUMENT when either the start or the end of the selection range is inside a
	 * multi byte line delimiter (and thus neither clearly in front of or after the line delimiter)
	 * </ul>
	 */
	public void setSelection(int start) {

	}
}
