/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.babel.tapiji.translator.rbe.babel.bundle;

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
    
    IMessagesResource getResource();

	void removeMessageAddParentKey(String key);
}
