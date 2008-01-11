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
package org.eclipse.babel.core.message.resource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import org.eclipse.babel.core.message.resource.ser.PropertiesDeserializer;
import org.eclipse.babel.core.message.resource.ser.PropertiesSerializer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.CoreException;


/**
 * Properties file, where the underlying storage is a {@link IFile}.
 * When dealing with {@link File} as opposed to {@link IFile}, 
 * implementors should use {@link PropertiesFileResource}.
 * 
 * @author Pascal Essiembre
 * @see PropertiesFileResource
 */
public class PropertiesIFileResource extends AbstractPropertiesResource{

    private final IFile file;
    
    /**
     * Constructor.
     * @param locale the resource locale
     * @param serializer resource serializer
     * @param deserializer resource deserializer
     * @param file the underlying {@link IFile}
     */
    public PropertiesIFileResource(
            Locale locale,
            PropertiesSerializer serializer,
            PropertiesDeserializer deserializer,
            final IFile file) {
        super(locale, serializer, deserializer);
        this.file = file;
        file.getWorkspace().addResourceChangeListener(
                new IResourceChangeListener() {
                    public void resourceChanged(IResourceChangeEvent event) {
                        if (event.getResource().equals(file)) {
                            fireResourceChange(PropertiesIFileResource.this);
                        }
                    }
                });
    }

    /**
     * @see org.eclipse.babel.core.message.resource.AbstractPropertiesResource
     * 			#getText()
     */
    public String getText() {
        try {
            InputStream is = file.getContents();
            int byteCount = is.available();
            byte[] b = new byte[byteCount];
            is.read(b);
            String content = new String(b, file.getCharset());
            return content;
        } catch (IOException e) {
            throw new RuntimeException(e); //TODO handle better
        } catch (CoreException e) {
            throw new RuntimeException(e); //TODO handle better
        }
    }

    /**
     * @see org.eclipse.babel.core.message.resource.TextResource#setText(
     *              java.lang.String)
     */
    public void setText(String text) {
        try {
        	String charset = file.getCharset();
        	ByteArrayInputStream is = new ByteArrayInputStream(
        			text.getBytes(charset));
            file.setContents(is, IFile.KEEP_HISTORY, null);
        } catch (Exception e) {
            //TODO handle better
            throw new RuntimeException(
                    "Cannot set content on properties file.", e); //$NON-NLS-1$
        }
    }
    
    /**
     * @see org.eclipse.babel.core.message.resource.IMessagesResource
     * 		#getSource()
     */
    public Object getSource() {
        return file;
    }    
    
    /**
     * @return The resource location label. or null if unknown.
     */
    public String getResourceLocationLabel() {
    	return file.getFullPath().toString();
    }
    
    /**
     * Checks whether this source editor is read-only.
     * @return <code>true</code> if read-only.
     */
    public boolean isReadOnly() {
        //TODO needed?
        return file.isReadOnly();
    }
}
