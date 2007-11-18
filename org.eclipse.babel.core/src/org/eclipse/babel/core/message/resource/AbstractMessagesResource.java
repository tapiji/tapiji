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
package org.eclipse.babel.core.message.resource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Base implementation of a {@link IMessagesResource} bound to a locale
 * and providing ways to add and remove {@link IMessagesResourceChangeListener}
 * instances.
 * @author Pascal Essiembre
 */
public abstract class AbstractMessagesResource implements IMessagesResource {

    private Locale locale;
    private List listeners = new ArrayList();
    
    /**
     * Constructor.
     * @param locale bound locale
     */
    public AbstractMessagesResource(Locale locale) {
        super();
        this.locale = locale;
    }

    /**
     * @see org.eclipse.babel.core.bundle.resource.IMessagesResource#getLocale()
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * @see org.eclipse.babel.core.message.resource.IMessagesResource#
     *          addMessagesResourceChangeListener(
     *          		org.eclipse.babel.core.message.resource
     *                  		.IMessagesResourceChangeListener)
     */
    public void addMessagesResourceChangeListener(
            IMessagesResourceChangeListener listener) {
        listeners.add(0, listener);
    }
    /**
     * @see org.eclipse.babel.core.message.resource.IMessagesResource#
     *          removeMessagesResourceChangeListener(
     *          		org.eclipse.babel.core.message
     *                  		.resource.IMessagesResourceChangeListener)
     */
    public void removeMessagesResourceChangeListener(
            IMessagesResourceChangeListener listener) {
        listeners.remove(listener);
    }

    /**
     * Fires notification that a {@link IMessagesResource} changed.
     * @param resource {@link IMessagesResource}
     */
    protected void fireResourceChange(IMessagesResource resource)  {
        for (Iterator iter = listeners.iterator(); iter.hasNext();) {
            ((IMessagesResourceChangeListener) iter.next()).resourceChanged(
                    resource);
        }
    }
}
