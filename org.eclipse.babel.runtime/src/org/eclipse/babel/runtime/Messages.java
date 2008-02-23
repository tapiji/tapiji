/*******************************************************************************
 * Copyright (c) 2008 Nigel Westbury and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Nigel Westbury - initial API and implementation
 *******************************************************************************/

package org.eclipse.babel.runtime;

import org.eclipse.babel.runtime.external.DynamicNLS;
import org.eclipse.babel.runtime.external.ILocalizationText;

public class Messages extends DynamicNLS {
	private static final String BUNDLE_NAME = "org.eclipse.babel.runtime.messages"; //$NON-NLS-1$

	static {
		// A little risky using the plugin activator in a static initializer.
		// Let's hope it is in a good enough state.
		initializeMessages(BUNDLE_NAME, Messages.class, Activator.getDefault().getBundle());
	}

	public static ILocalizationText LocalizeDialog_TabTitle_EditorPart;
	public static ILocalizationText LocalizeDialog_TabTitle_ViewPart;
	public static ILocalizationText LocalizeDialog_TabTitle_OtherPart;
	public static ILocalizationText LocalizeDialog_Title;
	public static ILocalizationText LocalizeDialog_TabTitle_Plugins;
	public static ILocalizationText LocalizeDialog_Title_DialogPart;
	public static ILocalizationText LocalizeDialog_TabTitle_Menu;
	public static ILocalizationText LocalizeDialog_TabTitle_PluginXml;
	public static ILocalizationText LocalizeDialog_Command_Translate;
	public static ILocalizationText LocalizeDialog_CommandLabel_Revert;
	public static ILocalizationText LocalizeDialog_CommandTooltip_Revert;
	public static ILocalizationText LocalizeDialog_TableTooltip_Plugin;
	public static ILocalizationText LocalizeDialog_TableTooltip_ResourceBundle;
	public static ILocalizationText LocalizeDialog_TableTooltip_Key;
	public static ILocalizationText LocalizeDialog_TabTitle_Dialog; 
	public static ILocalizationText LocalizeDialog_Title_PluginPart;
	
	public static ILocalizationText exception_failedDelete;
	public static ILocalizationText exception_loadException;
	public static ILocalizationText exception_saveException;

	public static ILocalizationText AboutPluginsDialog_provider;
	public static ILocalizationText AboutPluginsDialog_pluginName;
	public static ILocalizationText AboutPluginsDialog_version;
	public static ILocalizationText AboutPluginsDialog_pluginId;
}
