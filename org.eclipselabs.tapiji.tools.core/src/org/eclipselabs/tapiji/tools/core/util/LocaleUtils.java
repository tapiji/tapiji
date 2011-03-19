package org.eclipselabs.tapiji.tools.core.util;

import java.util.Locale;
import java.util.Set;

public class LocaleUtils {

	public static Locale getLocaleByDisplayName (Set<Locale> locales, String displayName) {
		Locale locale = null;
		
		for (Locale l : locales) {
			if (l.getDisplayName().equals(displayName) || 
				(l.getDisplayName().trim().length() == 0 && displayName.equals("[default]"))) {
				locale = l;
				break;
			}
		}
		
		return locale;
	}
	
}
