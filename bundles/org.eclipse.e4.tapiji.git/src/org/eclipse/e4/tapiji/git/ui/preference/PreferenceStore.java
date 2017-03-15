package org.eclipse.e4.tapiji.git.ui.preference;


import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import at.nucle.e4.plugin.preferences.core.api.IPreferenceScope;


public class PreferenceStore implements IPreferenceScope {

    public static String PREFERENCES_NODE_PATH = "org.eclipse.e4.tapiji.git";

    @Override
    public String getNode() {
        return PREFERENCES_NODE_PATH;
    }

    @Override
    public IScopeContext getScopeContext() {
        return InstanceScope.INSTANCE;
    }
}
