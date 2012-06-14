package org.eclipse.tapiji.rap.translator.rbe.babel.bundle;

import java.util.Collection;
import java.util.Locale;


public interface IMessagesBundleGroup {

    Collection<IMessagesBundle> getMessagesBundles();
    
    boolean containsKey(String key);
    
    IMessage[] getMessages(String key);
    
    IMessage getMessage(String key, Locale locale);
    
    IMessagesBundle getMessagesBundle(Locale locale);
    
    void removeMessages(String messageKey);
    
    boolean isKey(String key);
    
    void addMessagesBundle(Locale locale, IMessagesBundle messagesBundle);
    
    String[] getMessageKeys();
    
    void addMessages(String key);
    
    int getMessagesBundleCount();
    
    String getName();
    
    String getResourceBundleId();
    
    boolean hasPropertiesFileGroupStrategy();
    
    public boolean isMessageKey(String key);
    
    public String getProjectName();
    
    public void removeMessagesBundle(IMessagesBundle messagesBundle);
    
    public void dispose();
}
