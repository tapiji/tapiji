package org.eclipselabs.tapiji.translator.rbe.babel.bundle;

import java.util.Collection;
import java.util.Locale;




public interface IMessagesBundle {

    void dispose();
    
    void renameMessageKey(String sourceKey, String targetKey);
    
    void removeMessage(String messageKey);
    
    void duplicateMessage(String sourceKey, String targetKey);
    
    Locale getLocale();
    
    String[] getKeys();
    
    String getValue(String key);
    
    Collection<IMessage> getMessages();
    
    IMessage getMessage(String key);
    
    void addMessage(IMessage message);
    
    void removeMessages(String[] messageKeys);
    
    void setComment(String comment);
    
    String getComment();
}
