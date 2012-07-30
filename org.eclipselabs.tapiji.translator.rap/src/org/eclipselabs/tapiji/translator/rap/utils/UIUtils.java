package org.eclipselabs.tapiji.translator.rap.utils;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.framework.internal.core.BundleFragment;
import org.eclipse.osgi.framework.internal.core.BundleHost;
import org.eclipselabs.tapiji.translator.Activator;
import org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle;

public class UIUtils {
	
	public static final String IMAGE_REFRESH = "refresh.gif";
	public static final String IMAGE_RENAME = "rename.gif";
	public static final String IMAGE_RESOURCE_BUNDLE = "resourcebundle.gif";
	public static final String IMAGE_PROPERTIES_FILE = "propertiesfile.gif";
	public static final String IMAGE_RESOURCE_BUNDLE_TEMPORARY = "resourcebundle_grey.gif";
	public static final String IMAGE_PROPERTIES_FILE_TEMPORARY = "propertiesfile_grey.gif";
	public static final String IMAGE_NEW_PROPERTIES_FILE = "newpropertiesfile.gif";
	public static final String IMAGE_LOGIN = "login.png";
	public static final String IMAGE_LOGOUT = "logout.png";
	public static final String IMAGE_DOWNLOAD_RB = "downloadRB.gif";
	public static final String IMAGE_UPLOAD_RB = "uploadRB.gif";
	
	public static final String PLUGIN_NAME = "org.eclipselabs.tapiji.translator.rap";
	/**
     * Gets an image descriptor.
     * @param name image name
     * @return image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String name) {
        String iconPath = "icons/"; //$NON-NLS-1$
        try {
        	BundleFragment[] fragments = ((BundleHost) Activator.getDefault().getBundle()).getFragments();
        	BundleFragment rapFragment = null;
        	
        	for (BundleFragment fragment : fragments) {
        		if (fragment.getSymbolicName().equals(PLUGIN_NAME)) {
        			rapFragment = fragment;
        			break;
        		}        			
        	}        	
        	if (rapFragment != null) {
        		URL installURL = rapFragment.getEntry("/"); //$NON-NLS-1$
                URL url = new URL(installURL, iconPath + name);
                return ImageDescriptor.createFromURL(url);
        	}
        	
        	return ImageDescriptor.getMissingImageDescriptor();            
        } catch (MalformedURLException e) {
            // should not happen
            return ImageDescriptor.getMissingImageDescriptor();
        }
    }
}
