/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.core.builder;

import java.util.List;

import org.eclipse.babel.tapiji.tools.core.extensions.I18nAuditor;
import org.eclipse.babel.tapiji.tools.core.model.exception.NoSuchResourceAuditorException;
import org.eclipse.babel.tapiji.tools.core.util.EditorUtils;
import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator2;

public class ViolationResolutionGenerator implements
	IMarkerResolutionGenerator2 {

    @Override
    public boolean hasResolutions(IMarker marker) {
	return true;
    }

    @Override
    public IMarkerResolution[] getResolutions(IMarker marker) {

	EditorUtils.updateMarker(marker);

	String contextId = marker.getAttribute("context", "");

	// find resolution generator for the given context
	try {
	    I18nAuditor auditor = I18nBuilder
		    .getI18nAuditorByContext(contextId);
	    List<IMarkerResolution> resolutions = auditor
		    .getMarkerResolutions(marker);
	    return resolutions
		    .toArray(new IMarkerResolution[resolutions.size()]);
	} catch (NoSuchResourceAuditorException e) {
	}

	return new IMarkerResolution[0];
    }

}
