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

import java.util.Locale;

import org.eclipse.babel.core.message.MessagesBundleGroup;
import org.eclipse.babel.core.message.checks.DuplicateValueCheck;
import org.eclipse.babel.core.message.checks.IMessageCheck;
import org.eclipse.babel.core.message.checks.MissingValueCheck;


/**
 * @author Pascal Essiembre
 *
 */
public class MessagesBundleGroupValidator {
    

    
    private static IMessageCheck missingCheck =
            new MissingValueCheck();
    //TODO have above react to preferences
    
    
    //TODO Re-think... ??

    public static void validate(
            MessagesBundleGroup messagesBundleGroup, IValidationMarkerStrategy markerStrategy) {
        Locale[] locales = messagesBundleGroup.getLocales();
        for (int i = 0; i < locales.length; i++) {
            Locale locale = locales[i];
            validate(messagesBundleGroup, locale, markerStrategy);
        }
    }
    
    public static void validate(
            MessagesBundleGroup messagesBundleGroup,
            Locale locale,
            IValidationMarkerStrategy markerStrategy) {
        //TODO check if there is a matching EclipsePropertiesEditorResource already open.
        //else, create MessagesBundle from PropertiesIFileResource
        
    	String[] keys = messagesBundleGroup.getMessageKeys();
    	for (int i = 0; i < keys.length; i++) {
			String key = keys[i];
            //TODO call only those supported by preferences
            if (missingCheck.checkKey(
                    messagesBundleGroup,
                    messagesBundleGroup.getMessage(key, locale))) {
                markerStrategy.markFailed(new ValidationFailureEvent(
                        messagesBundleGroup, locale, key, missingCheck));
            }
            DuplicateValueCheck duplicateCheck = new DuplicateValueCheck();
            if (duplicateCheck.checkKey(
                    messagesBundleGroup,
                    messagesBundleGroup.getMessage(key, locale))) {
                markerStrategy.markFailed(new ValidationFailureEvent(
                        messagesBundleGroup, locale, key, duplicateCheck));
            }
        }
    }
    
}
