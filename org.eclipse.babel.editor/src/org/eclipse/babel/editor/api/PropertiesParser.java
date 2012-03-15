package org.eclipse.babel.editor.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;

import org.eclipse.babel.core.message.resource.ser.PropertiesDeserializer;
import org.eclipse.babel.editor.preferences.MsgEditorPreferences;
import org.eclipse.core.resources.IResource;
import org.eclipselabs.tapiji.translator.rbe.babel.bundle.IMessagesBundle;


public class PropertiesParser {
    
    public static IMessagesBundle parse(Locale locale, IResource resource) {
        PropertiesDeserializer pd = new PropertiesDeserializer(MsgEditorPreferences.getInstance());
        
        File file = resource.getRawLocation().toFile();
        File ioFile = new File(file.getPath());
        IMessagesBundle messagesBundle = MessagesBundleFactory.createBundle(locale, ioFile);
        pd.deserialize(messagesBundle, readFileAsString(file));
        return messagesBundle;
    }
    
    private static String readFileAsString(File filePath) {
        String content = "";

        if (!filePath.exists()) {
            return content;
        }
        BufferedReader fileReader = null;
        try {
            fileReader = new BufferedReader(new FileReader(filePath));
            String line = "";

            while ((line = fileReader.readLine()) != null) {
                content += line + "\n";
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return content;
    }
    
}
