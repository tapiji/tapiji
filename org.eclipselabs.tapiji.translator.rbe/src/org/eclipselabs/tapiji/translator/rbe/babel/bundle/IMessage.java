package org.eclipselabs.tapiji.translator.rbe.babel.bundle;

import java.util.Locale;



public interface IMessage {

    String getKey();
    
    String getValue();
    
    Locale getLocale();
    
    String getComment();
    
    boolean isActive();
    
    String toString();
    
    void setActive(boolean active);
    
    void setComment(String comment);
    
    void setComment(String comment, boolean silent);
    
    void setText(String test);
    
    void setText(String test, boolean silent);
    
}
