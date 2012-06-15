package org.eclipselabs.tapiji.translator.rap.babel.editor.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.babel.core.message.Message;
import org.eclipse.babel.core.message.MessageException;
import org.eclipse.babel.core.message.MessagesBundle;
import org.eclipse.babel.core.message.resource.PropertiesFileResource;
import org.eclipse.babel.core.message.resource.ser.PropertiesDeserializer;
import org.eclipse.babel.core.message.resource.ser.PropertiesSerializer;
import org.eclipselabs.tapiji.translator.rap.babel.editor.preferences.MsgEditorPreferences;
import org.eclipselabs.tapiji.translator.rbe.babel.bundle.IMessage;
import org.eclipselabs.tapiji.translator.rbe.babel.bundle.IMessagesBundle;


public class MessagesBundleFactory {

    static Logger logger = Logger.getLogger(MessagesBundleFactory.class.getSimpleName());
    
//    public static IMessagesBundleGroup createBundleGroup(IResource resource) {
//        // TODO �berlegen welche Strategy wann ziehen soll
//        //zB
//        if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage() == null || 
//                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor() ==  null) {
//            File ioFile = new File(resource.getRawLocation().toFile().getPath());
//            
//            logger.log(Level.INFO, "createBundleGroup: " + resource.getName());
//            
//            return new MessagesBundleGroup(new PropertiesFileGroupStrategy(ioFile, MsgEditorPreferences.getInstance().getSerializerConfig(), 
//            		MsgEditorPreferences.getInstance().getDeserializerConfig()));
//        } else {
//            return MessagesBundleGroupFactory.createBundleGroup(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorSite(), (IFile)resource);
//        }
//        
//    }
    
    public static IMessage createMessage(String key, Locale locale) {
        String l = locale == null ? "[default]" : locale.toString();
        logger.log(Level.INFO, "createMessage, key: " + key + " locale: " + l);
        
        return new Message(key, locale);
    }
 
    public static IMessagesBundle createBundle(Locale locale, File resource)
            throws MessageException {
        try {
            //TODO have the text de/serializer tied to Eclipse preferences,
            //singleton per project, and listening for changes
            
            String l = locale == null ? "[default]" : locale.toString();
            logger.log(Level.INFO, "createBundle: " + resource.getName() + " locale: " + l);
            
            return new MessagesBundle(new PropertiesFileResource(
                    locale,
                    new PropertiesSerializer(MsgEditorPreferences.getInstance().getSerializerConfig()),
                    new PropertiesDeserializer(MsgEditorPreferences.getInstance().getDeserializerConfig()),
                    resource));
        } catch (FileNotFoundException e) {
            throw new MessageException(
                    "Cannot create bundle for locale " //$NON-NLS-1$
                  + locale + " and resource " + resource, e); //$NON-NLS-1$
        }
    }
    
}
