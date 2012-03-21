package org.eclipse.babel.editor.api;

import org.eclipse.babel.core.message.resource.ser.PropertiesSerializer;
import org.eclipse.babel.editor.preferences.MsgEditorPreferences;
import org.eclipselabs.tapiji.translator.rbe.babel.bundle.IMessagesBundle;


public class PropertiesGenerator {

    public static final String GENERATED_BY = PropertiesSerializer.GENERATED_BY;
    
    public static String generate(IMessagesBundle messagesBundle) {
        PropertiesSerializer ps = new PropertiesSerializer(MsgEditorPreferences.getInstance().getSerializerConfig());
        return ps.serialize(messagesBundle);
    }
    
}
