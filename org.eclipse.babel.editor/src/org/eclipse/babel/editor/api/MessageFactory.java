package org.eclipse.babel.editor.api;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.babel.core.message.Message;
import org.eclipse.babel.tapiji.translator.rbe.babel.bundle.IMessage;

/**
 * Factory class for creating a {@link IMessage}:
 * <br><br>
 * 
 * @author Alexej Strelzow
 */
public class MessageFactory {

    static Logger logger = Logger.getLogger(MessageFactory.class.getSimpleName());
    
    /**
     * @param key The key of the message
     * @param locale The {@link Locale}
     * @return An instance of {@link IMessage}
     */
    public static IMessage createMessage(String key, Locale locale) {
        String l = locale == null ? "[default]" : locale.toString();
        logger.log(Level.INFO, "createMessage, key: " + key + " locale: " + l);
        
        return new Message(key, locale);
    }
    
}