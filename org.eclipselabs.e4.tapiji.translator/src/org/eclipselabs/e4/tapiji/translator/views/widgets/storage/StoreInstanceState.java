package org.eclipselabs.e4.tapiji.translator.views.widgets.storage;


import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;


@Creatable
@Singleton
public class StoreInstanceState {

    /*private static final String SELECTIVE_VIEW = "selective_content";
    private static final String DISPLAYED_LOCALES = "displayed_locales";
    private static final String LOCALE = "locale";
    private static final String ENABLED = "enabled";
    private static final String VALUE = "value";*/

    private static final String STORE_REFERENCE_LANGUAGE = "STORE/REFERENCE_LANGUAGE";
    private static final String STORE_MATCHING_PRECISION = "STORE/MATCHING_PRECISION";
    private static final String STORE_SEARCH_STRING = "STORE/SEARCH_STRING";
    private static final String STORE_GLOSSARY_FILE = "STORE/GLOSSARY_FILE";
    private static final String STORE_EDIT_MODE = "STORE/EDIT_MODE";
    private static final String STORE_FUZZY_MATCHING_MODE = "STORE/FUZZY_MODE";

    private static final String TAG = StoreInstanceState.class.getSimpleName();
    private final Map<String, String> persistedState;

    @Inject
    public StoreInstanceState(final MPart part) {
        persistedState = part.getPersistedState();
    }

    public void setEditMode(final boolean isEditMode) {
        persistedState.put(STORE_EDIT_MODE, String.valueOf(isEditMode));
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


    @Override
    public String toString() {
        return "StoreInstanceState [persistedState=" + persistedState + "]";
    }
}
