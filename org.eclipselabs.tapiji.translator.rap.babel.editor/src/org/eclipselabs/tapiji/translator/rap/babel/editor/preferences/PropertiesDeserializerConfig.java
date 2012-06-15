/**
 * 
 */
package org.eclipselabs.tapiji.translator.rap.babel.editor.preferences;

import org.eclipse.babel.core.configuration.ConfigurationManager;
import org.eclipse.babel.core.message.resource.ser.IPropertiesDeserializerConfig;
import org.eclipse.core.runtime.Preferences;
import org.eclipselabs.tapiji.translator.rap.babel.editor.plugin.MessagesEditorPlugin;

/**
 * @author ala
 *
 */
public class PropertiesDeserializerConfig implements
		IPropertiesDeserializerConfig {

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
