package org.eclipselabs.tapiji.translator.rap.utils;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipselabs.tapiji.translator.Activator;

public class UIUtils {
	
	public static final String IMAGE_REFRESH = "refresh.gif";
	public static final String IMAGE_RENAME = "rename.gif";
	public static final String IMAGE_RESOURCE_BUNDLE = "resourcebundle.gif";
	public static final String IMAGE_PROPERTIES_FILE = "propertiesfile.gif";
	public static final String IMAGE_RESOURCE_BUNDLE_TEMPORARY = "resourcebundle_grey.gif";
	public static final String IMAGE_PROPERTIES_FILE_TEMPORARY = "propertiesfile_grey.gif";
	
	/**
     * Gets an image descriptor.
     * @param name image name
     * @return image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String name) {
        String iconPath = "icons/"; //$NON-NLS-1$
        try {
            URL installURL = Activator.getDefault().getBundle().getEntry(
                    "/"); //$NON-NLS-1$
            URL url = new URL(installURL, iconPath + name);
            return ImageDescriptor.createFromURL(url);
        } catch (MalformedURLException e) {
            // should not happen
            return ImageDescriptor.getMissingImageDescriptor();
        }
    }
}
