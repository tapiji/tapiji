/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Reiterer - initial API and implementation
 ******************************************************************************/
package org.eclipse.babel.tapiji.translator.rbe.babel.bundle;

/**
 * Listener being notified when a {@link IMessagesResource} content changes.
 * @author Pascal Essiembre
 */
public interface IMessagesResourceChangeListener {

	/**
	 * Method called when the messages resource has changed.
	 * @param resource the resource that changed
	 */
    void resourceChanged(IMessagesResource resource);
}
