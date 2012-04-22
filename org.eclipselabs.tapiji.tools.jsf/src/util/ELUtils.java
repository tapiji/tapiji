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
package util;

import org.eclipse.babel.tapiji.tools.core.model.manager.ResourceBundleManager;
import org.eclipse.core.resources.IProject;


public class ELUtils {
	
	public static String getResource (IProject project, String bundleName, String key) {
		ResourceBundleManager manager = ResourceBundleManager.getManager(project);
		if (manager.isResourceExisting(bundleName, key))
			return manager.getKeyHoverString(bundleName, key);
		else
			return null;
	}
}
