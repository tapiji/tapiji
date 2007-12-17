/*******************************************************************************
 * Copyright (c) 2007 Pascal Essiembre.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Pascal Essiembre - initial API and implementation
 ******************************************************************************/
package org.eclipse.babel.editor.util;

import java.awt.ComponentOrientation;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import org.eclipse.babel.editor.plugin.MessagesEditorPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Utility methods related to application UI.
 * @author Pascal Essiembre
 */
public final class UIUtils {

    /** Name of resource bundle image. */
    public static final String IMAGE_RESOURCE_BUNDLE = 
            "resourcebundle.gif";  //$NON-NLS-1$
    /** Name of properties file image. */
    public static final String IMAGE_PROPERTIES_FILE = 
            "propertiesfile.gif";  //$NON-NLS-1$
    /** Name of new properties file image. */
    public static final String IMAGE_NEW_PROPERTIES_FILE = 
            "newpropertiesfile.gif";  //$NON-NLS-1$
    /** Name of hierarchical layout image. */
    public static final String IMAGE_LAYOUT_HIERARCHICAL =
            "hierarchicalLayout.gif";  //$NON-NLS-1$
    /** Name of flat layout image. */
    public static final String IMAGE_LAYOUT_FLAT = 
            "flatLayout.gif";  //$NON-NLS-1$

    /** Name of add icon. */
    public static final String IMAGE_ADD = "add.png";  //$NON-NLS-1$
    /** Name of edit icon. */
    public static final String IMAGE_RENAME = "rename.png";  //$NON-NLS-1$
    /** Name of "view left" icon. */
    public static final String IMAGE_VIEW_LEFT = "viewLeft.png";  //$NON-NLS-1$
    /** Name of locale icon. */
    public static final String IMAGE_LOCALE = "locale.png";  //$NON-NLS-1$
    /** Name of new locale icon. */
    public static final String IMAGE_NEW_LOCALE =
            "newLocale.png";  //$NON-NLS-1$
    /** Name of expand all icon. */
    public static final String IMAGE_EXPAND_ALL =
            "expandall.png";  //$NON-NLS-1$
    /** Name of collapse all icon. */
    public static final String IMAGE_COLLAPSE_ALL =
            "collapseall.png";  //$NON-NLS-1$

    public static final String IMAGE_INCOMPLETE_ENTRIES =
    	    "incomplete.gif";  //$NON-NLS-1$
    
    /** Image registry. */
    private static final ImageRegistry imageRegistry = new ImageRegistry();
    
    /**
     * Constructor.
     */
    private UIUtils() {
        super();
    }

    /**
     * Creates a font by altering the font associated with the given control
     * and applying the provided style (size is unaffected).
     * @param control control we base our font data on
     * @param style   style to apply to the new font
     * @return newly created font
     */
    public static Font createFont(Control control, int style) {
        //TODO consider dropping in favor of control-less version?
        return createFont(control, style, 0);
    }

    
    /**
     * Creates a font by altering the font associated with the given control
     * and applying the provided style and relative size.
     * @param control control we base our font data on
     * @param style   style to apply to the new font
     * @param relSize size to add or remove from the control size
     * @return newly created font
     */
    public static Font createFont(Control control, int style, int relSize) {
        //TODO consider dropping in favor of control-less version?
        FontData[] fontData = control.getFont().getFontData();
        for (int i = 0; i < fontData.length; i++) {
            fontData[i].setHeight(fontData[i].getHeight() + relSize);
            fontData[i].setStyle(style);
        }
        return new Font(control.getDisplay(), fontData);
    }

    /**
     * Creates a font by altering the system font
     * and applying the provided style and relative size.
     * @param style   style to apply to the new font
     * @return newly created font
     */
    public static Font createFont(int style) {
        return createFont(style, 0);
    }
    
    /**
     * Creates a font by altering the system font
     * and applying the provided style and relative size.
     * @param style   style to apply to the new font
     * @param relSize size to add or remove from the control size
     * @return newly created font
     */
    public static Font createFont(int style, int relSize) {
        Display display = MessagesEditorPlugin.getDefault().getWorkbench().getDisplay();
        FontData[] fontData = display.getSystemFont().getFontData();
        for (int i = 0; i < fontData.length; i++) {
            fontData[i].setHeight(fontData[i].getHeight() + relSize);
            fontData[i].setStyle(style);
        }
        return new Font(display, fontData);
    }

    /**
     * Creates a cursor matching given style.
     * @param style   style to apply to the new font
     * @return newly created cursor
     */
    public static Cursor createCursor(int style) {
        Display display = MessagesEditorPlugin.getDefault().getWorkbench().getDisplay();
        return new Cursor(display, style);
    }
    
    /**
     * Gets a system color.
     * @param colorId SWT constant
     * @return system color
     */
    public static Color getSystemColor(int colorId) {
        return MessagesEditorPlugin.getDefault().getWorkbench()
                .getDisplay().getSystemColor(colorId);
    }
    
    /**
     * Gets the approximate width required to display a given number of
     * characters in a control.
     * @param control the control on which to get width
     * @param numOfChars the number of chars
     * @return width
     */    
    public static int getWidthInChars(Control control, int numOfChars) {
        GC gc = new GC(control);
        Point extent = gc.textExtent("W");//$NON-NLS-1$
        gc.dispose();
        return numOfChars * extent.x;
    }

    /**
     * Gets the approximate height required to display a given number of
     * characters in a control, assuming, they were laid out vertically.
     * @param control the control on which to get height
     * @param numOfChars the number of chars
     * @return height
     */    
    public static int getHeightInChars(Control control, int numOfChars) {
        GC gc = new GC(control);
        Point extent = gc.textExtent("W");//$NON-NLS-1$
        gc.dispose();
        return numOfChars * extent.y;
    }
    
    /**
     * Shows an error dialog based on the supplied arguments.
     * @param shell the shell
     * @param exception the core exception
     * @param msgKey key to the plugin message text
     */
    public static void showErrorDialog(
            Shell shell, CoreException exception, String msgKey) {
        exception.printStackTrace();
        ErrorDialog.openError(
                shell,
                MessagesEditorPlugin.getString(msgKey),
                exception.getLocalizedMessage(),
                exception.getStatus());
    }
    
    /**
     * Shows an error dialog based on the supplied arguments.
     * @param shell the shell
     * @param exception the core exception
     * @param msgKey key to the plugin message text
     */
    public static void showErrorDialog(
            Shell shell, Exception exception, String msgKey) {
        exception.printStackTrace();
        IStatus status = new Status(
                IStatus.ERROR, 
                MessagesEditorPlugin.PLUGIN_ID,
                0, 
                MessagesEditorPlugin.getString(msgKey) + " " //$NON-NLS-1$
                        + MessagesEditorPlugin.getString("error.seeLogs"), //$NON-NLS-1$
                exception);
        ErrorDialog.openError(
                shell,
                MessagesEditorPlugin.getString(msgKey),
                exception.getLocalizedMessage(),
                status);
    }
    
    /**
     * Gets a locale, null-safe, display name.
     * @param locale locale to get display name
     * @return display name
     */
    public static String getDisplayName(Locale locale) {
        if (locale == null) {
            return MessagesEditorPlugin.getString("editor.default"); //$NON-NLS-1$
        }
        return locale.getDisplayName();
    }

    /**
     * Gets an image descriptor.
     * @param name image name
     * @return image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String name) {
        String iconPath = "icons/"; //$NON-NLS-1$
        try {
            URL installURL = MessagesEditorPlugin.getDefault().getBundle().getEntry(
                    "/"); //$NON-NLS-1$
            URL url = new URL(installURL, iconPath + name);
            return ImageDescriptor.createFromURL(url);
        } catch (MalformedURLException e) {
            // should not happen
            return ImageDescriptor.getMissingImageDescriptor();
        }
    }
    
    /**
     * Gets an image.
     * @param imageName image name
     * @return image
     */
    public static Image getImage(String imageName) {
        Image image = imageRegistry.get(imageName);
        if (image == null) {
            image = getImageDescriptor(imageName).createImage();
            imageRegistry.put(imageName, image);
        }
        return image;
    }

    /**
     * Gets the orientation suited for a given locale.
     * @param locale the locale
     * @return <code>SWT.RIGHT_TO_LEFT</code> or <code>SWT.LEFT_TO_RIGHT</code>
     */
    public static int getOrientation(Locale locale){
        if(locale!=null){
            ComponentOrientation orientation =
                    ComponentOrientation.getOrientation(locale);
            if(orientation==ComponentOrientation.RIGHT_TO_LEFT){
                return SWT.RIGHT_TO_LEFT;
            }
        }
        return SWT.LEFT_TO_RIGHT;
    }
}

