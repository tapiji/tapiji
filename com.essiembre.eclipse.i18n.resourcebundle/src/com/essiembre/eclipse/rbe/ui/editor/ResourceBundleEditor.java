/*
 * Copyright (C) 2003, 2004  Pascal Essiembre, Essiembre Consultant Inc.
 * 
 * This file is part of Essiembre ResourceBundle Editor.
 * 
 * Essiembre ResourceBundle Editor is free software; you can redistribute it 
 * and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * Essiembre ResourceBundle Editor is distributed in the hope that it will be 
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with Essiembre ResourceBundle Editor; if not, write to the 
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, 
 * Boston, MA  02111-1307  USA
 */
package com.essiembre.eclipse.rbe.ui.editor;


import java.util.Locale;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipselabs.tapiji.translator.rbe.model.tree.IKeyTree;

import com.essiembre.eclipse.rbe.RBEPlugin;
import com.essiembre.eclipse.rbe.model.tree.KeyTree;
import com.essiembre.eclipse.rbe.model.tree.KeyTreeItem;
import com.essiembre.eclipse.rbe.ui.UIUtils;
import com.essiembre.eclipse.rbe.ui.editor.i18n.I18nPage;
import com.essiembre.eclipse.rbe.ui.editor.locale.NewLocalePage;
import com.essiembre.eclipse.rbe.ui.editor.resources.BundleDelta;
import com.essiembre.eclipse.rbe.ui.editor.resources.IBundleChangeListener;
import com.essiembre.eclipse.rbe.ui.editor.resources.ResourceManager;
import com.essiembre.eclipse.rbe.ui.editor.resources.SourceEditor;
import com.essiembre.eclipse.rbe.ui.views.ResourceBundleOutline;
/**
 * Multi-page editor for editing resource bundles.
 */
public class ResourceBundleEditor extends MultiPageEditorPart
        implements IGotoMarker, ISelectionListener, ISelectionChangedListener, IBundleChangeListener {

    /** Editor ID, as defined in plugin.xml. */
    public static final String EDITOR_ID = 
       "com.essiembre.eclipse.rbe.ui.editor.ResourceBundleEditor"; //$NON-NLS-1$
    
    private ResourceManager resourceMediator;
    private I18nPage i18nPage;
    /** New locale page. */
    private NewLocalePage newLocalePage;
    
    /** the outline which additionally allows to navigate through the keys. */
    private ResourceBundleOutline outline;
    
    /**
     * Creates a multi-page editor example.
     */
    public ResourceBundleEditor() {
        super();
    }

    /**
     * The <code>MultiPageEditorExample</code> implementation of this method
     * checks that the input is an instance of <code>IFileEditorInput</code>.
     */
    public void init(IEditorSite site, IEditorInput editorInput)
        throws PartInitException {
        super.init(site, editorInput);
        if (editorInput instanceof IFileEditorInput) {
            IFile file = ((IFileEditorInput) editorInput).getFile();
            try {
                resourceMediator = new ResourceManager(site, file);
            } catch (CoreException e) {
            	e.printStackTrace();
                UIUtils.showErrorDialog(
                        site.getShell(), e, "error.init.ui"); //$NON-NLS-1$
                return;
            }    
            setPartName(resourceMediator.getEditorDisplayName());
            setContentDescription(
                    RBEPlugin.getString("editor.content.desc") //$NON-NLS-1$
                  + resourceMediator.getEditorDisplayName() + ".");//$NON-NLS-1$
            setTitleImage(UIUtils.getImage(UIUtils.IMAGE_RESOURCE_BUNDLE));
            resourceMediator.addBundleChangeListener(this);
            closeIfAreadyOpen(site, file);
            
            // Register ResourceBundleEditor as selection changed listener
            getSite().getPage().addPostSelectionListener(this);
        } else {
            throw new PartInitException(
                    "Invalid Input: Must be IFileEditorInput"); //$NON-NLS-1$
        }
    }
    
    /**
     * Gets the resource manager.
     * @return the resource manager
     */
    public ResourceManager getResourceManager() {
        return resourceMediator;
    }
    
    /**
     * Creates the pages of the multi-page editor.
     */
    protected void createPages() {
        // Create I18N page
        i18nPage = new I18nPage(
                getContainer(), SWT.NONE, resourceMediator);
        int index = addPage(i18nPage);
        setPageText(index, RBEPlugin.getString(
                "editor.properties")); //$NON-NLS-1$
        setPageImage(index, UIUtils.getImage(UIUtils.IMAGE_RESOURCE_BUNDLE));
        getSite().setSelectionProvider(i18nPage);
        
        // Create text editor pages for each locales
        try {
            SourceEditor[] sourceEditors = resourceMediator.getSourceEditors();
            for (int i = 0; i < sourceEditors.length; i++) {
                SourceEditor sourceEditor = sourceEditors[i];
                index = addPage(
                        sourceEditor.getEditor(), 
                        sourceEditor.getEditor().getEditorInput());
                setPageText(index, UIUtils.getDisplayName(
                        sourceEditor.getLocale()));
                setPageImage(index, 
                        UIUtils.getImage(UIUtils.IMAGE_PROPERTIES_FILE));
            }
            outline = new ResourceBundleOutline(resourceMediator.getKeyTree());
            // register ResourceBundleEditor as SelectionChangeListener for the ResourceBundleOutline
            outline.addSelectionChangedListener(this);
            
            
        } catch (PartInitException e) {
            ErrorDialog.openError(getSite().getShell(), 
                "Error creating text editor page.", //$NON-NLS-1$
                null, e.getStatus());
        }
        
        // Add "new locale" page
        newLocalePage = new NewLocalePage(getContainer(), resourceMediator, this);
        index = addPage(newLocalePage);
        setPageText(index, RBEPlugin.getString("editor.new.tab")); //$NON-NLS-1$
        setPageImage(
                index, UIUtils.getImage(UIUtils.IMAGE_NEW_PROPERTIES_FILE));
    }
    
    public void addResource(IFile resource, Locale locale) {
        try {    		
            SourceEditor sourceEditor = resourceMediator.addSourceEditor(resource, locale);
            int index = getPageCount() - 1;
            addPage(index,
                    sourceEditor.getEditor(), 
                    sourceEditor.getEditor().getEditorInput());
            setPageText(index, UIUtils.getDisplayName(
                    sourceEditor.getLocale()));
            setPageImage(index, 
                    UIUtils.getImage(UIUtils.IMAGE_PROPERTIES_FILE));
            i18nPage.refreshPage();
            setActivePage(0);
            sourceEditor.setContent(sourceEditor.getContent()); // re-set the content to trigger dirty state 
        } catch (PartInitException e) {
            ErrorDialog.openError(getSite().getShell(), 
                    "Error creating resource mediaotr.", //$NON-NLS-1$
                    null, e.getStatus());
        }
    }

    
    /**
     * {@inheritDoc}
     */
    public Object getAdapter(Class adapter) {
        Object obj = super.getAdapter(adapter);
        if (obj == null) {
            if (IContentOutlinePage.class.equals(adapter)) {
                return (outline);
            }
        }
        return (obj);
    }
    
    
    /**
     * Saves the multi-page editor's document.
     */
    public void doSave(IProgressMonitor monitor) {
        IKeyTree keyTree = resourceMediator.getKeyTree();
        String key = keyTree.getSelectedKey();

        i18nPage.refreshEditorOnChanges();
        resourceMediator.save(monitor);
        
        keyTree.setUpdater(keyTree.getUpdater());
        if (key != null)
            keyTree.selectKey(key);
    }
    
    /**
     * @see org.eclipse.ui.ISaveablePart#doSaveAs()
     */
    public void doSaveAs() {
        // Save As not allowed.
    }
    
    /**
     * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
     */
    public boolean isSaveAsAllowed() {
        return false;
    }

    /**
     * Change current page based on locale.  If there is no editors associated
     * with current locale, do nothing.
     * @param locale locale used to identify the page to change to
     */
    public void setActivePage(Locale locale) {
        SourceEditor[] editors = resourceMediator.getSourceEditors();
        int index = -1;
        for (int i = 0; i < editors.length; i++) {
            SourceEditor editor = editors[i];
            Locale editorLocale = editor.getLocale();
            if (editorLocale != null && editorLocale.equals(locale)
                    || editorLocale == null && locale == null) {
                index = i;
                break;
            }
        }
        if (index > -1) {
            setActivePage(index + 1);
        }
    }

    /**
     * @see org.eclipse.ui.ide.IGotoMarker#gotoMarker(
     *         org.eclipse.core.resources.IMarker)
     */
    public void gotoMarker(IMarker marker) {
        IPath markerPath = marker.getResource().getProjectRelativePath();
        SourceEditor[] sourceEditors = resourceMediator.getSourceEditors();
        for (int i = 0; i < sourceEditors.length; i++) {
            SourceEditor editor = sourceEditors[i];
            IPath editorPath = editor.getFile().getProjectRelativePath();
            if (markerPath.equals(editorPath)) {
                setActivePage(editor.getLocale());
                IDE.gotoMarker(editor.getEditor(), marker);
                break;
            }
        }
    }
    
    private SourceEditor lastEditor;
    
    /**
     * Calculates the contents of page GUI page when it is activated.
     */
    protected void pageChange(int newPageIndex) {
        super.pageChange(newPageIndex);
        KeyTree keyTree = resourceMediator.getKeyTree();
        
        if (lastEditor != null) {
            String lastEditorKey = lastEditor.getCurrentKey();
            if (lastEditorKey != null)
                keyTree.selectKey(lastEditor.getCurrentKey());
        }
        
        if (newPageIndex == 0) {  // switched to first page
            resourceMediator.reloadProperties();
            i18nPage.refreshTextBoxes();
            lastEditor = null; // reset lastEditor
            
            // update selection based on outline and messages view
            updateSelection();
            return;
        }
        
        if (newPageIndex == getPageCount()-1) // switched to last page
            return;
        
        int editorIndex = newPageIndex - 1; // adjust because first page is tree page		
        if (editorIndex >= 0 && editorIndex < resourceMediator.getSourceEditors().length) {
            lastEditor = resourceMediator.getSourceEditors()[editorIndex];
            if (keyTree.getSelectedKey() != null)
                lastEditor.selectKey(keyTree.getSelectedKey());
        }
    }

    
    /**
     * Is the given file a member of this resource bundle.
     * @param file file to test
     * @return <code>true</code> if file is part of bundle
     */
    public boolean isBundleMember(IFile file) {
        return resourceMediator.isResource(file);
    }

    private void closeIfAreadyOpen(final IEditorSite site, final IFile file) {
    	IWorkbenchPage[] pages = site.getWorkbenchWindow().getPages();
    	for (int i = 0; i < pages.length; i++) {
    		final IWorkbenchPage page = pages[i];
    		IEditorReference[] editors = page.getEditorReferences();
    		for (int j = 0; j < editors.length; j++) {
    			final IEditorPart editor = editors[j].getEditor(false);
    			if (editor instanceof ResourceBundleEditor) {
    				ResourceBundleEditor rbe = (ResourceBundleEditor) editor;
    				if (rbe.isBundleMember(file)) {
    					// putting the close operation into the queue
    					// closing during opening caused errors.
    					Display.getDefault().asyncExec(new Runnable() {
							public void run() {
		    					page.closeEditor(editor, true);
							}
						});
    				}
    			}
    		}
    	}
    }

    
    
    /**
     * @see org.eclipse.ui.IWorkbenchPart#dispose()
     */
    public void dispose() {
        i18nPage.dispose();
        newLocalePage.dispose();
        // unregister SelectionChangedListener
        getSite().getPage().removePostSelectionListener(this);
        resourceMediator.removeChangeListener();
        super.dispose();
    }

    protected void updateSelection () {
    	ISelectionService service = (ISelectionService) getSite().getService(ISelectionService.class);
    	if (service != null) {
    		selectionChanged(null, service.getSelection());
    	}
    }
    
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		try {
			if (part == this)
				return;
			
			if (selection.isEmpty())
				return;
			
			if (!(selection instanceof IStructuredSelection))
				return;
			
			IStructuredSelection sel = (IStructuredSelection) selection;
			
			KeyTreeItem item = (KeyTreeItem) sel.iterator().next();
	        String selected = item.getId();
	        if(selected != null) {
	        	resourceMediator.getKeyTree().selectKey(selected);
	        }
		} catch (Exception e) {
			// silent catch
		}
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		// e.g. Selection of records within the ResouceBundleOutline
		selectionChanged(null, event.getSelection());
	}

	@Override
	public void bundleChanged(BundleDelta delta) {
		try {
			i18nPage.refreshTextBoxes();
		} catch (Exception e) {}
	}
}
