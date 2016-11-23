/*******************************************************************************
 * Copyright (c) 2012 Martin Reiterer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * @author Martin Reiterer
 * @author Christian Behon
 * @author Pascal Essiembre
 *
 ******************************************************************************/
package org.eclipse.e4.tapiji.utils;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;


public final class LocaleUtils {

    private LocaleUtils() {
        // Only static access
    }

    /**
     * Create Locale from language code
     *
     * @param languageCode e.g. ar_BH, ar_JO
     * @return Locale
     */
    public static Locale getLocaleFromLanguageCode(final String languageCode) {
        final String[] locDef = languageCode.split("_");
        return locDef.length < 3 ? (locDef.length < 2 ? new Locale(locDef[0]) : new Locale(locDef[0], locDef[1])) : new Locale(locDef[0], locDef[1], locDef[2]);
    }

    /**
     * Sort the Locales alphabetically.
     * Make sure the root Locale is first.
     *
     * @param locales
     */
    public static final void sortLocales(final List<Locale> locales) {
        Collections.sort(locales, LOCALE_COMPERATOR);
    }

    private static final Comparator<Locale> LOCALE_COMPERATOR = new Comparator<Locale>() {

        @Override
        public int compare(final Locale localeOne, final Locale localeTwo) {
            return getDisplayName(localeOne).compareToIgnoreCase(getDisplayName(localeTwo));
        }

        private String getDisplayName(final Locale locale) {
            String displayName;
            if (locale == null) {
                displayName = "Default";
            } else {
                displayName = locale.getDisplayName();
            }
            return displayName;
        }
    };

}
