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
package org.eclipse.babel.core.message.resource.ser;

/**
 * Properties deserialization configuration options.
 * @author Pascal Essiembre (pascal@essiembre.com)
 */
public class PropertiesDeserializerConfig {

    //TODO extend Model and fire property change events??
	//TODO re-design in order to closer integrate with Eclipse Preferences
    
    private boolean unicodeUnescapeEnabled = true;
    
    /**
     * Constructor.
     */
    public PropertiesDeserializerConfig() {
        super();
    }

    /**
     * Defaults true.
     * @return Returns the unicodeUnescapeEnabled.
     */
    public boolean isUnicodeUnescapeEnabled() {
        return unicodeUnescapeEnabled;
    }
    /**
     * Sets whether unicode characters should be un-escaped when read.
     * @param unicodeUnescapeEnabled The unicodeUnescapeEnabled to set.
     */
    public void setUnicodeUnescapeEnabled(boolean unicodeUnescapeEnabled) {
        this.unicodeUnescapeEnabled = unicodeUnescapeEnabled;
    }
}
