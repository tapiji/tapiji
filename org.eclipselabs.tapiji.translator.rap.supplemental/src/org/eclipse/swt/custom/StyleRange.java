package org.eclipse.swt.custom;

import org.eclipse.swt.graphics.Color;

/**
 * StyleRange without logic, just to get the source code compiled in RAP
 * 
 * @author Matthias Lettmayer
 *
 */
public class StyleRange {
	public int start;
	public int length;
	public int fontStyle;
	
	
	public StyleRange() {
		
	}
	
	/** 
	 * Create a new style range.
	 *
	 * @param start start offset of the style
	 * @param length length of the style 
	 * @param foreground foreground color of the style, null if none; UNUSED in RAP
	 * @param background background color of the style, null if none; UNUSED in RAP
	 */
	public StyleRange(int start, int length, Color foreground, Color background) {
		this.start = start;
		this.length = length;
	}
	
	/** 
	 * Create a new style range.
	 *
	 * @param start start offset of the style
	 * @param length length of the style 
	 * @param foreground foreground color of the style, null if none; UNUSED in RAP
	 * @param background background color of the style, null if none; UNUSED in RAP
	 * @param fontStyle font style of the style, may be SWT.NORMAL, SWT.ITALIC or SWT.BOLD
	 */
	public StyleRange(int start, int length, Color foreground, Color background, int fontStyle) {
		this(start, length, foreground, background);
		this.fontStyle = fontStyle;
	}
}
