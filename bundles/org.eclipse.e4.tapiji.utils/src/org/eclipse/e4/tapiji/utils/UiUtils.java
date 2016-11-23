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


import java.net.URL;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;


/**
 * Utility methods related to application UI.
 */
public final class UiUtils {

    private UiUtils() {
        super();
        // Hide constructor only static access
    }

    /**
     * Gets the approximate width required to display a given number of
     * characters in a control.
     *
     * @param control the control on which to get width
     * @param numOfChars the number of chars
     * @return width
     */
    public static int getWidthInChars(final Control control, final int numOfChars) {
        final GC gc = new GC(control);
        final Point pointExtent = gc.textExtent("W");//$NON-NLS-1$
        gc.dispose();
        return numOfChars * pointExtent.x;
    }

    public static Image getImage(Class<Object> clazz, String path) {
        Bundle bundle = FrameworkUtil.getBundle(clazz);
        URL url = FileLocator.find(bundle, new Path(path), null);
        ImageDescriptor descriptor = ImageDescriptor.createFromURL(url);
        return descriptor.createImage();

    }
}
