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
package org.eclipse.babel.editor.resource;

import java.util.Locale;

import org.eclipse.babel.core.message.resource.AbstractPropertiesResource;
import org.eclipse.babel.core.message.resource.ser.PropertiesDeserializer;
import org.eclipse.babel.core.message.resource.ser.PropertiesSerializer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.editors.text.TextEditor;


/**
 * Editor which contains file content being edited, which may not be saved yet
 * which may not take effect in a IResource (which make a difference with
 * markers).
 * @author Pascal Essiembre
 *
 */
public class EclipsePropertiesEditorResource extends AbstractPropertiesResource{

    private TextEditor textEditor;
    
    /**
     * Constructor.
     * @param locale the resource locale
     * @param serializer resource serializer
     * @param deserializer resource deserializer
     * @param textEditor the editor
     */
    public EclipsePropertiesEditorResource(
            Locale locale,
            PropertiesSerializer serializer,
            PropertiesDeserializer deserializer,
            TextEditor textEditor) {
        super(locale, serializer, deserializer);
        this.textEditor = textEditor;

        
        IResourceChangeListener rcl = new IResourceChangeListener() {
        public void resourceChanged(IResourceChangeEvent event) {
            IResource resource = event.getResource();
            System.out.println("RESOURCE CHANGED:" + resource);
            if (resource.getFileExtension().equals("escript")) {
               // run the compiler
            }
         }
      };
      ResourcesPlugin.getWorkspace().addResourceChangeListener(rcl);        
        
        
        IDocument document = textEditor.getDocumentProvider().getDocument(
                textEditor.getEditorInput());
        System.out.println("DOCUMENT:" + document);
        document.addDocumentListener(new IDocumentListener() {
            public void documentAboutToBeChanged(DocumentEvent event) {
                //do nothing
                System.out.println("DOCUMENT ABOUT to CHANG:");
            }
            public void documentChanged(DocumentEvent event) {
                System.out.println("DOCUMENT CHANGED:");
                fireResourceChange(EclipsePropertiesEditorResource.this);
            }
        });
        
//        IDocumentProvider docProvider = textEditor.getDocumentProvider();
////        PropertiesFileDocumentProvider
//        //        textEditor.getEditorInput().
//        
////        textEditor.sets
//        
//        docProvider.addElementStateListener(new IElementStateListener() {
//            public void elementContentAboutToBeReplaced(Object element) {
//                System.out.println("about:" + element);
//            }            
//            public void elementContentReplaced(Object element) {
//                System.out.println("replaced:" + element);
//            }
//            public void elementDeleted(Object element) {
//                System.out.println("deleted:" + element);
//            }
//            public void elementDirtyStateChanged(Object element, boolean isDirty) {
//                System.out.println("dirty:" + element + " " + isDirty);
//            }
//            public void elementMoved(Object originalElement, Object movedElement) {
//                System.out.println("moved from:" + originalElement
//                        + " to " + movedElement);
//            }
//        });
        
        
//        textEditor.addPropertyListener(new IPropertyListener() {
//            public void propertyChanged(Object source, int propId) {
//                System.out.println(
//                        "text editor changed. source:"
//                        + source
//                        + " propId: " + propId);
//                fireResourceChange(EclipsePropertiesEditorResource.this);
//            }
//        });
    }

    /**
     * @see org.eclipse.babel.core.bundle.resource.TextResource#getText()
     */
    public String getText() {
        return textEditor.getDocumentProvider().getDocument(
                textEditor.getEditorInput()).get();
    }

    /**
     * @see org.eclipse.babel.core.bundle.resource.TextResource#setText(
     *              java.lang.String)
     */
    public void setText(String content) {
        textEditor.getDocumentProvider().getDocument(
                textEditor.getEditorInput()).set(content);
    }

    /**
     * @see org.eclipse.babel.core.bundle.resource.IMessagesResource#getSource()
     */
    public Object getSource() {
        return textEditor;
    }
    
    public IResource getResource() {
        IEditorInput input = textEditor.getEditorInput();
        if (input instanceof IFileEditorInput) {
            return ((IFileEditorInput) input).getFile();
        }
        return null;
    }
    
    
    /**
     * Checks whether this source editor is read-only.
     * @return <code>true</code> if read-only.
     */
    public boolean isReadOnly() {
        //TODO needed?
        return textEditor.isEditorInputReadOnly();
    }
}
