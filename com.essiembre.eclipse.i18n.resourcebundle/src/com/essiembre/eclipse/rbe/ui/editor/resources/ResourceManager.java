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
package com.essiembre.eclipse.rbe.ui.editor.resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import com.essiembre.eclipse.rbe.api.PropertiesGenerator;
import com.essiembre.eclipse.rbe.api.PropertiesParser;
import com.essiembre.eclipse.rbe.model.DeltaEvent;
import com.essiembre.eclipse.rbe.model.IDeltaListener;
import com.essiembre.eclipse.rbe.model.bundle.Bundle;
import com.essiembre.eclipse.rbe.model.bundle.BundleGroup;
import com.essiembre.eclipse.rbe.model.tree.KeyTree;
import com.essiembre.eclipse.rbe.model.tree.updater.FlatKeyTreeUpdater;
import com.essiembre.eclipse.rbe.model.tree.updater.GroupedKeyTreeUpdater;
import com.essiembre.eclipse.rbe.model.tree.updater.KeyTreeUpdater;
import com.essiembre.eclipse.rbe.model.workbench.RBEPreferences;

/**
 * Mediator holding instances of commonly used items, dealing with 
 * important interactions within themselves.
 * @author Pascal Essiembre (essiembre@users.sourceforge.net)
 * @version $Author: fleque $ $Revision: 1.13 $ $Date: 2007/09/15 15:14:02 $
 */
public class ResourceManager implements IResourceChangeListener {

    private IResourceFactory resourcesFactory;
    private final BundleGroup bundleGroup;
    private final KeyTree keyTree;
    /** key=Locale;value=SourceEditor */
    /*default*/ final Map<Locale, SourceEditor> sourceEditors = new HashMap<Locale, SourceEditor>();
    private final Collection<Locale> locales = new ArrayList<Locale>();
    private Set<IBundleChangeListener> changeListeners = new HashSet<IBundleChangeListener>();
    private IDeltaListener bundleGroupDeltaListner;
    
    /**
     * Constructor.
     * @param site eclipse editor site
     * @param file file used to create manager
     * @throws CoreException problem creating resource manager
     */
    public ResourceManager(final IEditorSite site, final IFile file)
            throws CoreException {
        super();
        resourcesFactory = ResourceFactory.createFactory(site, file);
        bundleGroup = new BundleGroup();
        SourceEditor[] editors = resourcesFactory.getSourceEditors();
        for (int i = 0; i < editors.length; i++) {
            SourceEditor sourceEditor = editors[i];
            Locale locale = sourceEditor.getLocale();
            sourceEditors.put(locale, sourceEditor);
            locales.add(locale);
            bundleGroup.addBundle(
                    locale, PropertiesParser.parse(sourceEditor.getContent()));	
            // Add resource change listener to current resource bundle instance
            ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
        }
        
        bundleGroupDeltaListner = new IDeltaListener() {
            public void add(DeltaEvent event) {}    // do nothing
            public void remove(DeltaEvent event) {} // do nothing
            public void modify(DeltaEvent event) {
                final Bundle bundle = (Bundle) event.receiver();
                final SourceEditor editor = 
                        (SourceEditor) sourceEditors.get(bundle.getLocale());
                String editorContent = PropertiesGenerator.generate(bundle);
                if (editor==null) return;
                if (!editorContent.equals(editor.getContent()))
                	editor.setContent(editorContent);
            }
            public void select(DeltaEvent event) {
            }
        };
        bundleGroup.addListener(bundleGroupDeltaListner);
        
        KeyTreeUpdater treeUpdater = null;
        if (RBEPreferences.getKeyTreeHierarchical()) {
            treeUpdater = new GroupedKeyTreeUpdater(
                    RBEPreferences.getKeyGroupSeparator());
        } else {
            treeUpdater = new FlatKeyTreeUpdater();
        }
        this.keyTree = new KeyTree(bundleGroup, treeUpdater);
    }

    /**
     * Gets a bundle group.
     * @return bundle group
     */
    public BundleGroup getBundleGroup() {
        return bundleGroup;
    }
    /**
     * Gets all locales in this bundle.
     * @return locales
     */
    public Collection getLocales() {
        return locales;
    }
    /**
     * Gets the key tree for this bundle.
     * @return key tree
     */
    public KeyTree getKeyTree() {
        return keyTree;
    }
    /**
     * Gets the source editors.
     * @return source editors.
     */
    public SourceEditor[] getSourceEditors() {
        return resourcesFactory.getSourceEditors();
    }
    
    /**
     * Save all dirty editors.
     * @param monitor progress monitor
     */
    public void save(IProgressMonitor monitor) {
        SourceEditor[] editors = resourcesFactory.getSourceEditors();
        for (int i = 0; i < editors.length; i++) {
            editors[i].getEditor().doSave(monitor);
        }
    }
        
    /**
     * Gets the multi-editor display name.
     * @return display name
     */
    public String getEditorDisplayName() {
        return resourcesFactory.getEditorDisplayName();
    }

    /**
     * Returns whether a given file is known to the resource manager (i.e.,
     * if it is part of a resource bundle).
     * @param file file to test
     * @return <code>true</code> if a known resource
     */
    public boolean isResource(IFile file) {
        SourceEditor[] editors = resourcesFactory.getSourceEditors();
        for (int i = 0; i < editors.length; i++) {
            if (editors[i].getFile().equals(file)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Creates a properties file.
     * @param locale a locale
     * @return the newly created file
     * @throws CoreException problem creating file
     * @throws IOException problem creating file
     */
    public IFile createPropertiesFile(Locale locale) 
            throws CoreException, IOException {
        return resourcesFactory.getPropertiesFileCreator().createPropertiesFile(
                locale);
    }
    
    /**
     * Gets the source editor matching the given locale.
     * @param locale locale matching requested source editor
     * @return source editor or <code>null</code> if no match
     */
    public SourceEditor getSourceEditor(Locale locale) {
        return (SourceEditor) sourceEditors.get(locale);
    }
    
    public SourceEditor addSourceEditor(IFile resource, Locale locale) throws PartInitException {
        SourceEditor sourceEditor = resourcesFactory.addResource(resource, locale);
        sourceEditors.put(sourceEditor.getLocale(), sourceEditor);
        locales.add(locale);
        bundleGroup.addBundle(
                locale, PropertiesParser.parse(sourceEditor.getContent())); 
        return sourceEditor;
    }
    /**
     * Reloads the properties files (parse them).
     */
    public void reloadProperties() {
        SourceEditor[] editors = resourcesFactory.getSourceEditors();
        for (int i = 0; i < editors.length; i++) {
            SourceEditor editor = editors[i];
            if (editor.isCacheDirty()) {
                bundleGroup.addBundle(
                        editor.getLocale(),
                        PropertiesParser.parse(editor.getContent()));
                editor.resetCache();
            }
        }
    }

	protected Locale getLocaleByName (String bundleName, String localeID) {
		// Check locale
		Locale locale = null;
		localeID = localeID.substring(0, localeID.length() - "properties".length() - 1);
		if (localeID.length() == bundleName.length()) {
			// default locale
			locale = null;
		} else {
			localeID = localeID.substring(bundleName.length() + 1);
			String[] localeTokens = localeID.split("_");
			
			switch (localeTokens.length) {
			case 1:
				locale = new Locale(localeTokens[0]);
				break;
			case 2:
				locale = new Locale(localeTokens[0], localeTokens[1]);
				break;
			case 3:
				locale = new Locale(localeTokens[0], localeTokens[1], localeTokens[2]);
				break;
				default:
					locale = null;
					break;
			}
		}
		
		return locale;
	}

	@Override
	public void resourceChanged(final IResourceChangeEvent event) {
		// TODO redraw resource bundle editor
		
		final IResourceDeltaVisitor visitor = new IResourceDeltaVisitor() {
			
			@Override
			public boolean visit(IResourceDelta delta) throws CoreException {			
	            
				IResource res = delta.getResource();
				
				if (res == null || res.getType() != IResource.FILE)
					return true;
				
				if (!res.getFileExtension().toLowerCase().equals("properties"))
					return false;

				
				String regex = "^(.*?)"
		            + "((_[a-z]{2,3})|(_[a-z]{2,3}_[A-Z]{2})" 
		            + "|(_[a-z]{2,3}_[A-Z]{2}_\\w*))?(\\." 
		            + "properties" + ")$";
		     	String bundleName = res.getName().replaceFirst(regex, "$1");
		     	Locale bundleLocale = getLocaleByName (bundleName, res.getName());
				
		     	//check if this ResourceManager is responsible for the changed file
		     	for (SourceEditor se : sourceEditors.values()){
		     		if(se.getFile().equals(res)){
		     			bundleGroup.addBundle(
			                    bundleLocale, PropertiesParser.parse( 
			                    		convertStreamToString(
			                    				res.getProject().getFile(res.getProjectRelativePath()).getContents()) ));	
		     		}
		     	}
		     	
		     	
		     	
		     	fireBundleChangedEvent(bundleGroup.getBundle(bundleLocale));
				return true;
			}
		};
		
		try {
			event.getDelta().accept(visitor);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	protected String convertStreamToString(InputStream is) {
		if (is != null) {
			StringBuilder sb = new StringBuilder();
			String line;
			
			try {
				BufferedReader reader = new BufferedReader (new InputStreamReader(is));
				while ((line = reader.readLine()) != null) {
					sb.append(line).append("\n");
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					is.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return sb.toString();
		} else {
			return "";
		}
	}
	
	protected void fireBundleChangedEvent (Bundle bundle) {
		Iterator<IBundleChangeListener> it = changeListeners.iterator();
		
		while (it.hasNext()) {
			IBundleChangeListener listener = it.next();
			try {
				listener.bundleChanged(new BundleDelta(bundle));
			} catch (Exception e) {}
		}
		
	}
	
	public void addBundleChangeListener (IBundleChangeListener listener) {
		changeListeners.add(listener);
	}

	public void removeChangeListener() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		bundleGroup.removeListener(bundleGroupDeltaListner);
	}

}
