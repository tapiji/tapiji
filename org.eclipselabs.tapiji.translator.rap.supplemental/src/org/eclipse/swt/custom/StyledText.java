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
}
