/*******************************************************************************
 * Copyright (c) 2012 Martin Reiterer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Reiterer - initial API and implementation
 ******************************************************************************/
package org.eclipselabs.e4.tapiji.translator.utils;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
//import org.eclipselabs.tapiji.translator.Activator;

public class FontUtils {

    /**
     * Gets a system color.
     * 
     * @param colorId
     *            SWT constant
     * @return system color
     */
    public static Color getSystemColor(int colorId) {
	return null;//Activator.getDefault().getWorkbench().getDisplay()
		//.getSystemColor(colorId);
    }

    /**
     * Creates a font by altering the font associated with the given control and
     * applying the provided style (size is unaffected).
     * 
     * @param control
     *            control we base our font data on
     * @param style
     *            style to apply to the new font
     * @return newly created font
     */
    public static Font createFont(Control control, int style) {
	// TODO consider dropping in favor of control-less version?
	return createFont(control, style, 0);
    }

    /**
     * Creates a font by altering the font associated with the given control and
     * applying the provided style and relative size.
     * 
     * @param control
     *            control we base our font data on
     * @param style
     *            style to apply to the new font
     * @param relSize
     *            size to add or remove from the control size
     * @return newly created font
     */
    public static Font createFont(Control control, int style, int relSize) {
	// TODO consider dropping in favor of control-less version?
	FontData[] fontData = control.getFont().getFontData();
	for (int i = 0; i < fontData.length; i++) {
	    fontData[i].setHeight(fontData[i].getHeight() + relSize);
	    fontData[i].setStyle(style);
	}
	return new Font(control.getDisplay(), fontData);
    }

    /**
     * Creates a font by altering the system font and applying the provided
     * style and relative size.
     * 
     * @param style
     *            style to apply to the new font
     * @return newly created font
     */
    public static Font createFont(int style) {
	return createFont(style, 0);
    }

    /**
     * Creates a font by altering the system font and applying the provided
     * style and relative size.
     * 
     * @param style
     *            style to apply to the new font
     * @param relSize
     *            size to add or remove from the control size
     * @return newly created font
     */
    public static Font createFont(int style, int relSize) {
	/*Display display = Activator.getDefault().getWorkbench().getDisplay();
	FontData[] fontData = display.getSystemFont().getFontData();
	for (int i = 0; i < fontData.length; i++) {
	    fontData[i].setHeight(fontData[i].getHeight() + relSize);
	    fontData[i].setStyle(style);
	}
	return new Font(display, fontData);*/
      return null;
    }
}
