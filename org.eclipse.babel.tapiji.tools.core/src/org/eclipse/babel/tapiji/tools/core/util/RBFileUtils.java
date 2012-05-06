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
package org.eclipse.babel.tapiji.tools.core.util;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;

/**
 * 
 * @author mgasser
 * 
 */
public class RBFileUtils extends Action {
	public static final String PROPERTIES_EXT = "properties";

	/**
	 * Checks whether a RB-file has a problem-marker
	 */
	public static boolean hasResourceBundleMarker(IResource r) {
		try {
			if (r.findMarkers(EditorUtils.RB_MARKER_ID, true,
			        IResource.DEPTH_INFINITE).length > 0) {
				return true;
			} else {
				return false;
			}
		} catch (CoreException e) {
			return false;
		}
	}

}
