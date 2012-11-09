/*******************************************************************************
 * Copyright (c) 2012 Alexej Strelzow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Alexej Strelzow - initial API and implementation
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.core.model.manager;

import java.util.Set;

import org.eclipse.babel.tapiji.tools.core.model.IResourceDescriptor;

/**
 * Interface for state loading.
 * 
 * @author Alexej Strelzow
 */
public interface IStateLoader {

    /**
     * Loads the state from a xml-file
     */
    void loadState();

    /**
     * Stores the state into a xml-file
     */
    void saveState();

    /**
     * @return The excluded resources
     */
    Set<IResourceDescriptor> getExcludedResources();
}
