/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Reiterer - initial API and implementation
 *     Matthias Lettmayer - added update marker utils to update and get right position of marker (fixed issue 8)
 *     					  - fixed openEditor() to open an editor and selecting a key (fixed issue 59)
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.core.util;

import java.text.MessageFormat;

import org.eclipse.core.resources.IMarker;

public class EditorUtils {

	/** Marker constants **/
	public static final String MARKER_ID = "org.eclipse.babel.tapiji.tools.core.ui.StringLiteralAuditMarker";
	public static final String RB_MARKER_ID = "org.eclipse.babel.tapiji.tools.core.ui.ResourceBundleAuditMarker";

	/** Error messages **/
	public static final String MESSAGE_NON_LOCALIZED_LITERAL = "Non-localized string literal ''{0}'' has been found";
	public static final String MESSAGE_BROKEN_RESOURCE_REFERENCE = "Cannot find the key ''{0}'' within the resource-bundle ''{1}''";
	public static final String MESSAGE_BROKEN_RESOURCE_BUNDLE_REFERENCE = "The resource bundle with id ''{0}'' cannot be found";

	public static final String MESSAGE_UNSPECIFIED_KEYS = "Missing or unspecified key ''{0}'' has been found in ''{1}''";
	public static final String MESSAGE_SAME_VALUE = "''{0}'' and ''{1}'' have the same translation for the key ''{2}''";
	public static final String MESSAGE_MISSING_LANGUAGE = "ResourceBundle ''{0}'' lacks a translation for ''{1}''";

	public static String getFormattedMessage(String pattern, Object[] arguments) {
		String formattedMessage = "";

		MessageFormat formatter = new MessageFormat(pattern);
		formattedMessage = formatter.format(arguments);

		return formattedMessage;
	}

	public static IMarker[] concatMarkerArray(IMarker[] ms, IMarker[] ms_to_add) {
		IMarker[] old_ms = ms;
		ms = new IMarker[old_ms.length + ms_to_add.length];

		System.arraycopy(old_ms, 0, ms, 0, old_ms.length);
		System.arraycopy(ms_to_add, 0, ms, old_ms.length, ms_to_add.length);

		return ms;
	}

}
