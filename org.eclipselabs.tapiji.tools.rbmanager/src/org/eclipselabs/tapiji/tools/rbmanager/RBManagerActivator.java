package org.eclipselabs.tapiji.tools.rbmanager;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class RBManagerActivator extends AbstractUIPlugin {
	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipselabs.tapiji.tools.rbmanager"; //$NON-NLS-1$
	// The shared instance
	private static RBManagerActivator plugin;
	
	
	/**
	 * The constructor
	 */
	public RBManagerActivator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static RBManagerActivator getDefault() {
		return plugin;
	}
	
	public static ImageDescriptor getImageDescriptor(String name){
		String path = "icons/" + name;
		
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

}
