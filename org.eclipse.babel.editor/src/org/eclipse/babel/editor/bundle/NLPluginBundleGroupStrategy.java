/*******************************************************************************
 * Copyright (c) 2007 Pascal Essiembre.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Pascal Essiembre - initial API and implementation
 ******************************************************************************/
package org.eclipse.babel.editor.bundle;

import java.util.Locale;

import org.eclipse.babel.core.message.MessagesBundle;
import org.eclipse.babel.core.message.strategy.IMessagesBundleGroupStrategy;


/**
 * MessagesBundle group strategies for dealing with Eclipse "NL"
 * directory structure within a plugin.
 * @author Pascal Essiembre
 */
public class NLPluginBundleGroupStrategy implements IMessagesBundleGroupStrategy {

    /**
     * 
     */
    public NLPluginBundleGroupStrategy() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see org.eclipse.babel.core.bundle.IBundleGroupStrategy#loadBundles()
     */
    public MessagesBundle[] loadMessagesBundles() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.eclipse.babel.core.bundle.IBundleGroupStrategy#createBundle(java.util.Locale)
     */
    public MessagesBundle createMessagesBundle(Locale locale) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.eclipse.babel.core.message.strategy.IMessagesBundleGroupStrategy#createMessagesBundleGroupName()
     */
    public String createMessagesBundleGroupName() {
        // TODO Auto-generated method stub
        return null;
    }

}
