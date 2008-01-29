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
package org.eclipse.babel.editor.plugin;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.eclipse.babel.editor.builder.ToggleNatureAction;
import org.eclipse.babel.editor.preferences.MsgEditorPreferences;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 * @author Pascal Essiembre (pascal@essiembre.com)
 */
public class MessagesEditorPlugin extends AbstractUIPlugin {

	//TODO move somewhere more appropriate
    public static final String MARKER_TYPE =
        "org.eclipse.babel.editor.nlsproblem"; //$NON-NLS-1$
	
	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.babel.editor";

	// The shared instance
	private static MessagesEditorPlugin plugin;
	
	//Resource bundle.
	//TODO Use Eclipse MessagesBundle instead.
	private ResourceBundle resourceBundle;
	
	/**
	 * The constructor
	 */
	public MessagesEditorPlugin() {
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(
	 *         org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		//make sure the rbe nature and builder are set on java projects
		//if that is what the users prefers.
		if (MsgEditorPreferences.getInstance().isBuilderSetupAutomatically()) {
			ToggleNatureAction.addOrRemoveNatureOnAllJavaProjects(true);
		}

		//TODO replace deprecated
        try {
            URL messagesUrl = FileLocator.find(getBundle(),
                    new Path("$nl$/messages.properties"), null);//$NON-NLS-1$
            if(messagesUrl != null) {
                resourceBundle = new PropertyResourceBundle(
                        messagesUrl.openStream());
            }
        } catch (IOException x) {
            resourceBundle = null;
        }
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(
	 *         org.osgi.framework.BundleContext)
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
	public static MessagesEditorPlugin getDefault() {
		return plugin;
	}

	//--------------------------------------------------------------------------
	//TODO Better way/location for these methods?
	
	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
     * @param key the key for which to fetch a localized text
     * @return localized string corresponding to key
	 */
	public static String getString(String key) {
		ResourceBundle bundle = 
                MessagesEditorPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

    /**
     * Returns the string from the plugin's resource bundle,
     * or 'key' if not found.
     * @param key the key for which to fetch a localized text
     * @param arg1 runtime argument to replace in key value 
     * @return localized string corresponding to key
     */
    public static String getString(String key, String arg1) {
        return MessageFormat.format(getString(key), new String[]{arg1});
    }
    /**
     * Returns the string from the plugin's resource bundle,
     * or 'key' if not found.
     * @param key the key for which to fetch a localized text
     * @param arg1 runtime first argument to replace in key value
     * @param arg2 runtime second argument to replace in key value
     * @return localized string corresponding to key
     */
    public static String getString(String key, String arg1, String arg2) {
        return MessageFormat.format(
                getString(key), new String[]{arg1, arg2});
    }
    /**
     * Returns the string from the plugin's resource bundle,
     * or 'key' if not found.
     * @param key the key for which to fetch a localized text
     * @param arg1 runtime argument to replace in key value 
     * @param arg2 runtime second argument to replace in key value
     * @param arg3 runtime third argument to replace in key value
     * @return localized string corresponding to key
     */
    public static String getString(
            String key, String arg1, String arg2, String arg3) {
        return MessageFormat.format(
                getString(key), new String[]{arg1, arg2, arg3});
    }
	/**
	 * Returns the plugin's resource bundle.
     * @return resource bundle
	 */
	protected ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
}
