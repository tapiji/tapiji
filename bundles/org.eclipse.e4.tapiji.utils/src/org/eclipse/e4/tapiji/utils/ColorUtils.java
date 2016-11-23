/*******************************************************************************
 * Copyright (c) 2012 Martin Reiterer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * @author Martin Reiterer
 * @author Christian Behon
 * @author Pascal Essiembre
 *
 * @since 0.0.1
 ******************************************************************************/
package org.eclipse.e4.tapiji.utils;


import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;


public final class ColorUtils {

    private ColorUtils() {
        // Only static access
    }

    /**
     * Gets a system color.
     *
     * @param colorId SWT constant
     * @return system color
     */
    public static Color getSystemColor(final int colorId) {
        final Display display = Display.getCurrent();
        return display.getSystemColor(colorId);
    }

}
