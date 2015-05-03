/*******************************************************************************
 * Copyright (c) 2012 Martin Reiterer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * Christian Behon - refactor from e3 to e4
 ******************************************************************************/
package org.eclipselabs.e4.tapiji.translator.views.widgets.storage;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipselabs.e4.tapiji.logger.Log;


@Creatable
@Singleton
public class StoreInstanceState {

    private static final String STORE_DISPLAYED_LOCALES = "STORE/DISPLAYED_LOCALES";
    private static final String STORE_REFERENCE_LANGUAGE = "STORE/REFERENCE_LANGUAGE";
    private static final String STORE_MATCHING_PRECISION = "STORE/MATCHING_PRECISION";
    private static final String STORE_SEARCH_STRING = "STORE/SEARCH_STRING";
    private static final String STORE_GLOSSARY_FILE = "STORE/GLOSSARY_FILE";
    private static final String STORE_EDIT_MODE = "STORE/EDIT_MODE";
    private static final String STORE_FUZZY_MATCHING_MODE = "STORE/FUZZY_MODE";
    private static final String STORE_SORT_COLUMN_INDEX = "STORE/SORT_COLUMN_INDEX";
    private static final String STORE_SORT_ORDER = "STORE/SORT_ORDER";

    private static final String TAG = StoreInstanceState.class.getSimpleName();

    private final Map<String, String> persistedState;

    @Inject
    public StoreInstanceState(final MPart part) {
        persistedState = part.getPersistedState();
    }

    public void setEditMode(final boolean isEditMode) {
        persistedState.put(STORE_EDIT_MODE, String.valueOf(isEditMode));
    }

    public void setColumnIndex(final int columnIndex) {
        persistedState.put(STORE_SORT_COLUMN_INDEX, String.valueOf(columnIndex));
    }

    public int getColumnIndex() {
        if (persistedState.containsKey(STORE_SORT_COLUMN_INDEX)) {
            return Integer.valueOf(persistedState.get(STORE_SORT_COLUMN_INDEX));
        } else {
            return 0;
        }
    }

    public void setSortOrder(final boolean sortOrder) {
        persistedState.put(STORE_SORT_ORDER, String.valueOf(sortOrder));
    }

    public boolean getSortOrder() {
        if (persistedState.containsKey(STORE_SORT_ORDER)) {
            return Boolean.valueOf(persistedState.get(STORE_SORT_ORDER));
        } else {
            return false;
        }
    }

    public boolean isEditMode() {
        if (persistedState.containsKey(STORE_EDIT_MODE)) {
            return Boolean.valueOf(persistedState.get(STORE_EDIT_MODE));
        } else {
            return false;
        }
    }

    public void setFuzzyMode(final boolean isFuzzyMode) {
        persistedState.put(STORE_FUZZY_MATCHING_MODE, String.valueOf(isFuzzyMode));
    }

    public boolean isFuzzyMode() {
        if (persistedState.containsKey(STORE_FUZZY_MATCHING_MODE)) {
            return Boolean.valueOf(persistedState.get(STORE_FUZZY_MATCHING_MODE));
        } else {
            return false;
        }
    }

    public void setGlossaryFile(final String file) {
        persistedState.put(STORE_GLOSSARY_FILE, file);
    }

    public String getGlossaryFile() {
        if (persistedState.containsKey(STORE_GLOSSARY_FILE)) {
            return persistedState.get(STORE_GLOSSARY_FILE);
        } else {
            return "";
        }
    }

    public void setSearchValue(final String searchValue) {
        persistedState.put(STORE_SEARCH_STRING, searchValue);
    }

    public String getSearchValue() {
        if (persistedState.containsKey(STORE_SEARCH_STRING)) {
            return persistedState.get(STORE_SEARCH_STRING);
        } else {
            return "";
        }
    }

    public void setMatchingPrecision(final float matchingPrecision) {
        persistedState.put(STORE_MATCHING_PRECISION, String.valueOf(matchingPrecision));
    }

    public float getMatchingPrecision() {
        if (persistedState.containsKey(STORE_MATCHING_PRECISION)) {
            return Float.valueOf(persistedState.get(STORE_MATCHING_PRECISION));
        } else {
            return 0f;
        }
    }

    public void setReferenceLanguage(final String referenceLanguage) {
        persistedState.put(STORE_REFERENCE_LANGUAGE, referenceLanguage);
    }

    public String getReferenceLanguage() {
        if (persistedState.containsKey(STORE_REFERENCE_LANGUAGE)) {
            return persistedState.get(STORE_REFERENCE_LANGUAGE);
        } else {
            return "";
        }
    }

    public void hideLocale(final String locale) {
        final List<String> hiddenLocales = getHiddenLocales();
        if (!hiddenLocales.contains(locale)) {
            hiddenLocales.add(locale);
            // persistedState.put(STORE_DISPLAYED_LOCALES, hiddenLocales.toString().replaceAll("\\[|\\]", ""));
        }
    }

    public void showLocale(final String locale) {
        final List<String> hiddenLocales = getHiddenLocales();
        if (hiddenLocales.contains(locale)) {
            hiddenLocales.remove(locale);
            // persistedState.put(STORE_DISPLAYED_LOCALES, hiddenLocales.toString().replaceAll("\\[|\\]", ""));
        }
    }

    public List<String> getHiddenLocales() {
        final String locales = persistedState.get(STORE_DISPLAYED_LOCALES);
        if (locales != null) {
            Log.d("asdas", locales.toString());
            return new ArrayList<String>(Arrays.asList(locales.split("\\s*,\\s*")));
        } else {
            return new ArrayList<String>();
        }
    }

    @Override
    public String toString() {
        return "StoreInstanceState [persistedState=" + persistedState + "]";
    }
}
