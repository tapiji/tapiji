/*******************************************************************************
 * Copyright (c) 2012 Michael Gasser.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Michael Gasser - initial API and implementation
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.rbmanager.auditor.quickfix;

import java.util.Locale;

import org.eclipse.babel.tapiji.tools.core.ui.ResourceBundleManager;
import org.eclipse.babel.tapiji.tools.core.ui.utils.LanguageUtils;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution2;

public class MissingLanguageResolution implements IMarkerResolution2 {

    private Locale language;

    public MissingLanguageResolution(Locale language) {
        this.language = language;
    }

    @Override
    public String getLabel() {
        return "Add missing language '" + language + "'";
    }

    @Override
    public void run(IMarker marker) {
        IResource res = marker.getResource();
        String rbId = ResourceBundleManager.getResourceBundleId(res);
        LanguageUtils.addLanguageToResourceBundle(res.getProject(), rbId,
                language);
    }

    @Override
    public String getDescription() {
        return "Creates a new localized properties-file with the same basename as the resourcebundle";
    }

    @Override
    public Image getImage() {
        return null;
    }

}
