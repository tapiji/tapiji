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
package org.eclipse.babel.tapiji.tools.core.ui.extensions;

import java.util.List;

import org.eclipse.babel.tapiji.tools.core.extensions.ILocation;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.ui.IMarkerResolution;

/**
 * Auditor class for finding I18N problems within source code resources. The
 * objects audit method is called for a particular resource. Found errors are
 * stored within the object's internal data structure and can be queried with
 * the help of problem categorized getter methods.
 */
public abstract class I18nResourceAuditor extends I18nAuditor {
	/**
	 * Audits a project resource for I18N problems. This method is triggered
	 * during the project's build process.
	 * 
	 * @param resource
	 *            The project resource
	 */
	public abstract void audit(IResource resource);

	/**
	 * Returns a list of supported file endings.
	 * 
	 * @return The supported file endings
	 */
	public abstract String[] getFileEndings();

	/**
	 * Returns the list of found need-to-translate string literals. Each list
	 * entry describes the textual position on which this type of error has been
	 * detected.
	 * 
	 * @return The list of need-to-translate string literal positions
	 */
	public abstract List<ILocation> getConstantStringLiterals();

	/**
	 * Returns the list of broken Resource-Bundle references. Each list entry
	 * describes the textual position on which this type of error has been
	 * detected.
	 * 
	 * @return The list of positions of broken Resource-Bundle references
	 */
	public abstract List<ILocation> getBrokenResourceReferences();

	/**
	 * Returns the list of broken references to Resource-Bundle entries. Each
	 * list entry describes the textual position on which this type of error has
	 * been detected
	 * 
	 * @return The list of positions of broken references to locale-sensitive
	 *         resources
	 */
	public abstract List<ILocation> getBrokenBundleReferences();

	/**
	 * Returns a characterizing identifier of the implemented auditing
	 * functionality. The specified identifier is used for discriminating
	 * registered builder extensions.
	 * 
	 * @return The String id of the implemented auditing functionality
	 */
	public abstract String getContextId();

	/**
	 * Returns a list of quick fixes of a reported Internationalization problem.
	 * 
	 * @param marker
	 *            The warning marker of the Internationalization problem
	 * @param cause
	 *            The problem type
	 * @return The list of marker resolution proposals
	 */
	public abstract List<IMarkerResolution> getMarkerResolutions(IMarker marker);

	/**
	 * Checks if the provided resource auditor is responsible for a particular
	 * resource.
	 * 
	 * @param resource
	 *            The resource reference
	 * @return True if the resource auditor is responsible for the referenced
	 *         resource
	 */
	public boolean isResourceOfType(IResource resource) {
		for (String ending : getFileEndings()) {
			if (resource.getFileExtension().equalsIgnoreCase(ending))
				return true;
		}
		return false;
	}
}
