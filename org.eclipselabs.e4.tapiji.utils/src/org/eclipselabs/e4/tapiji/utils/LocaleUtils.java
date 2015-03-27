package org.eclipselabs.e4.tapiji.utils;


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

}
