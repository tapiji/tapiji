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
package org.eclipse.babel.editor.resource.validator;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;


import org.eclipse.babel.core.message.checks.DuplicateValueCheck;
import org.eclipse.babel.core.message.checks.MissingValueCheck;
import org.eclipse.babel.core.util.BabelUtils;
import org.eclipse.babel.editor.plugin.MessagesEditorPlugin;

/**
 * @author Pascal Essiembre
 *
 */
public class FileMarkerStrategy implements IValidationMarkerStrategy {

    
    /**
     * @see org.eclipse.babel.editor.resource.validator.IValidationMarkerStrategy#markFailed(org.eclipse.core.resources.IResource, org.eclipse.babel.core.bundle.checks.IBundleEntryCheck)
     */
    public void markFailed(ValidationFailureEvent event) {
        if (event.getCheck() instanceof MissingValueCheck) {
            addMarker((IResource) event.getBundleGroup().getMessagesBundle(
                    event.getLocale()).getResource().getSource(),
//            addMarker(event.getResource(),
                    event.getKey(),
                    "Key \"" + event.getKey() //$NON-NLS-1$
                    + "\" is missing a value.", //$NON-NLS-1$
              IMarker.SEVERITY_WARNING);
            
        } else if (event.getCheck() instanceof DuplicateValueCheck) {
            String duplicates = BabelUtils.join(
                    ((DuplicateValueCheck) event.getCheck())
                            .getDuplicateKeys(), ", ");
            addMarker((IResource) event.getBundleGroup().getMessagesBundle(
                    event.getLocale()).getResource().getSource(),
//            addMarker(event.getResource(),
                    event.getKey(),
                    "Key \"" + event.getKey() //$NON-NLS-1$
                          + "\" is a duplicate of: " + duplicates, //$NON-NLS-1$
                    IMarker.SEVERITY_WARNING);
        }
    }

    private void addMarker(
            IResource resource, 
            String key,
            String message, //int lineNumber,
            int severity) {
        try {
            //TODO move MARKER_TYPE elsewhere.
            IMarker marker = resource.createMarker(MessagesEditorPlugin.MARKER_TYPE);
            marker.setAttribute(IMarker.MESSAGE, message);
            marker.setAttribute(IMarker.SEVERITY, severity);
            marker.setAttribute(IMarker.LOCATION, key);
//            if (lineNumber == -1) {
//                lineNumber = 1;
//            }
//            marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
        } catch (CoreException e) {
            throw new RuntimeException("Cannot add marker.", e); //$NON-NLS-1$
        }
    }

}
