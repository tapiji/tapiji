package org.eclipselabs.e4.tapiji.utils;


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
 *
 * @author Pascal Essiembre
 * @author Tobias Langner
 * @author Christian behon
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
