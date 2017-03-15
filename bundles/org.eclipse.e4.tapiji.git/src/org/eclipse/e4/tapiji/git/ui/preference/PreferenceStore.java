package org.eclipse.e4.tapiji.git.ui.preference;


import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import at.nucle.e4.plugin.preferences.core.api.IPreferenceScope;


public class PreferenceStore implements IPreferenceScope {

    public static String PREFERENCES_NODE_PATH = "org.eclipse.e4.tapiji.git";

    public static final String COLOR_LINE_ADDED = "color_line_added";

    public PreferenceStore() {
        //  IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(PREFERENCES_NODE_PATH);

        //  PreferenceConverter.setDefault(getScopeContext(), COLOR_LINE_ADDED, "");
    }

    @Override
    public String getNode() {
        return PREFERENCES_NODE_PATH;
    }

    @Override
    public IScopeContext getScopeContext() {
        return InstanceScope.INSTANCE;
    }
}
