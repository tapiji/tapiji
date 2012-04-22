/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Reiterer - initial API and implementation
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.core.util;

import java.util.Locale;
import java.util.Set;

import org.eclipse.babel.tapiji.tools.core.model.manager.ResourceBundleManager;

public class LocaleUtils {

	public static Locale getLocaleByDisplayName(Set<Locale> locales,
	        String displayName) {
		for (Locale l : locales) {
			String name = l == null ? ResourceBundleManager.defaultLocaleTag
			        : l.getDisplayName();
			if (name.equals(displayName)
			        || (name.trim().length() == 0 && displayName
			                .equals(ResourceBundleManager.defaultLocaleTag))) {
				return l;
			}
		}

		return null;
	}

	public static boolean containsLocaleByDisplayName(Set<Locale> locales,
	        String displayName) {
		for (Locale l : locales) {
			String name = l == null ? ResourceBundleManager.defaultLocaleTag
			        : l.getDisplayName();
			if (name.equals(displayName)
			        || (name.trim().length() == 0 && displayName
			                .equals(ResourceBundleManager.defaultLocaleTag))) {
				return true;
			}
		}

		return false;
	}

}
