/**
 * 
 */
package org.eclipse.babel.editor.preferences;

import org.eclipse.babel.core.configuration.ConfigurationManager;
import org.eclipse.babel.core.message.resource.ser.IPropertiesDeserializerConfig;
import org.eclipse.babel.editor.plugin.MessagesEditorPlugin;
import org.eclipse.core.runtime.Preferences;

/**
 * The concrete implementation of {@link IPropertiesDeserializerConfig}.
 * 
 * @author Alexej Strelzow
 */
public class PropertiesDeserializerConfig implements
		IPropertiesDeserializerConfig { //  Moved from MsgEditorPreferences, to make it more flexible.

    /** MsgEditorPreferences. */
    private static final Preferences PREFS = 
            MessagesEditorPlugin.getDefault().getPluginPreferences();

    PropertiesDeserializerConfig() {
    	ConfigurationManager.getInstance().setDeserializerConfig(this);
    }
    
    /**
     * Gets whether to convert encoded strings to unicode characters when
     * reading file.
     * @return <code>true</code> if converting
     */
    public boolean isUnicodeUnescapeEnabled() {
        return PREFS.getBoolean(MsgEditorPreferences.UNICODE_UNESCAPE_ENABLED);
    }

}
