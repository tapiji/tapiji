package org.eclipselabs.e4.tapiji.utils;


import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;


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

    /*
     * public static Image getImage(Class<Object> clazz, String path) {
     * Bundle bundle = FrameworkUtil.getBundle(clazz);
     * IPath paths = (IPath) new Path("ss");
     * URL url = FileLocator.find(bundle, paths, null);
     * ImageDescriptor image = ImageDescriptor.createFromURL(url);
     * return image.createImage();
     * }
     */
}
