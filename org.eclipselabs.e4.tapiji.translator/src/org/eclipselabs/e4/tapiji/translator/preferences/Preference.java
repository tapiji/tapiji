package org.eclipselabs.e4.tapiji.translator.preferences;


import javax.inject.Singleton;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipselabs.e4.tapiji.logger.Log;
import org.osgi.service.prefs.BackingStoreException;


@Creatable
@Singleton
public class Preference {

    private static final String TAG = Preference.class.getSimpleName();

    private static final String NODE = "org.eclipselabs.e4.tapiji.translator.preferences";
    private static final String IS_EDIT_MODE = "isEditMode";
    private static final String IS_FUZZY_MODE = "isFuzzyMode";

    private static final boolean BOOLEAN_DEFAULT = false;


    public boolean isEditMode() {
        return getBoolean(IS_EDIT_MODE);
    }

    public void setEditMode(boolean isEditMode) {
        putBoolean(IS_EDIT_MODE, isEditMode);
    }

    public boolean isFuzzyMatchingMode() {
        return getBoolean(IS_FUZZY_MODE);
    }

    public void setFuzzyMatchingMode(final boolean isFuzzyMode) {
        putBoolean(IS_FUZZY_MODE, isFuzzyMode);
    }

    private void putBoolean(final String key, final boolean isValue) {
        Log.i(TAG, String.format("Put %s with value %s ", key, isValue));
        final IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(NODE);
        preferences.putBoolean(key, isValue);
        flushQuietly(preferences);
    }

    private boolean getBoolean(final String key) {
        final IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(NODE);
        return preferences.getBoolean(key, BOOLEAN_DEFAULT);
    }

    private static void flushQuietly(final IEclipsePreferences preferences) {
        try {
            preferences.flush();
        } catch (BackingStoreException exception) {
            Log.e(TAG, exception);
        }
    }



}
