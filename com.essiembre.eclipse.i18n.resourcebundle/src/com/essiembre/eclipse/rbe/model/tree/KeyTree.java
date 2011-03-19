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
package com.essiembre.eclipse.rbe.model.tree;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipselabs.tapiji.translator.rbe.model.bundle.IBundleGroup;
import org.eclipselabs.tapiji.translator.rbe.model.tree.IKeyTree;
import org.eclipselabs.tapiji.translator.rbe.model.tree.updater.IKeyTreeUpdater;

import com.essiembre.eclipse.rbe.model.DeltaEvent;
import com.essiembre.eclipse.rbe.model.IDeltaListener;
import com.essiembre.eclipse.rbe.model.Model;
import com.essiembre.eclipse.rbe.model.bundle.Bundle;
import com.essiembre.eclipse.rbe.model.bundle.BundleEntry;
import com.essiembre.eclipse.rbe.model.bundle.BundleGroup;

/**
 * Tree representation of a bundle group.
 * @author Pascal Essiembre (essiembre@users.sourceforge.net)
 * @version $Author: nl_carnage $ $Revision: 1.11 $ $Date: 2007/09/12 16:02:52 $
 */
public class KeyTree extends Model implements IKeyTreeVisitable, IKeyTree {

    /** Caching of key tree items (key=ID; value=KeyTreeItem). **/
    private final Map<String, KeyTreeItem> keyItemsCache = new TreeMap<String, KeyTreeItem>();
    /** Items found at root level. */
    private final Set<KeyTreeItem> rootKeyItems = new TreeSet<KeyTreeItem>();
    /** Updater responsible for tree changes. */
    private IKeyTreeUpdater updater;
    /** Bundle group used to build the tree. */
    private IBundleGroup bundleGroup;
    /** the currently selected key. */
    private String selectedKey;
    
    /**
     * Constructor.
     * @param iBundleGroup bundle group used to build this tree
     * @param updater2 updater used to handle tree modifications
     */
    public KeyTree(IBundleGroup iBundleGroup, IKeyTreeUpdater updater2) {
        super();
        this.bundleGroup = iBundleGroup;
        this.updater = updater2;
        
        // Set listeners
        ((BundleGroup)iBundleGroup).addListener(new IDeltaListener() {
            public void add(DeltaEvent event) {
                initBundle((Bundle) event.receiver());
            }
            public void remove(DeltaEvent event) {
                // do nothing
            }
            public void modify(DeltaEvent event) {
                // do nothing
            }
            public void select(DeltaEvent event) {
                // do nothing
            }
        });
        for (Iterator iter = iBundleGroup.iterator(); iter.hasNext();) {
            initBundle((Bundle) iter.next());
        }
        // Initial tree creation
        load();
    }

    /**
     * Initializes the given bundle by adding propser listeners on it.
     * @param bundle the bundle to initialize
     */
    protected void initBundle(final Bundle bundle) {
        bundle.addListener(new IDeltaListener() {
            public void add(DeltaEvent event) {
                //TODO figure out how to filter event that do not add keys.
                //Probably not necessary with plugin rewrite.
                String key = ((BundleEntry) event.receiver()).getKey();
                addKey(key);
            }
            public void remove(DeltaEvent event) {
                String key = ((BundleEntry) event.receiver()).getKey();
                Collection entries = bundleGroup.getBundleEntries(key);
                if (entries.size() == 0) {
                    removeKey(((BundleEntry) event.receiver()).getKey());
                }
            }
            public void modify(DeltaEvent event) {
                //TODO figure out how to filter event that do not modify keys.
                //Probably not necessary with plugin rewrite.
                String key = ((BundleEntry) event.receiver()).getKey();
                modifyKey(key);
            }
            public void select(DeltaEvent event) {
            }
        });
    }

    /**
     * Gets a key tree item.
     * @param key key of item to get
     * @return a key tree item
     */
    public KeyTreeItem getKeyTreeItem(String key) {
        return (KeyTreeItem) keyItemsCache.get(key);
    }
    
    /**
     * Returns the currently selected key.
     * 
     * @return   The currently selected key. null = no selection.
     */
    public String getSelectedKey() {
        return (selectedKey);
    }
    
    /**
     * Gets the key tree item cache.
     * @return key tree item cache.
     */
    public Map getKeyItemsCache() {
        return keyItemsCache;
    }
    
    /**
     * Gets all items contained a the root level of this tree.
     * @return a collection of <code>KeyTreeItem</code> objects.
     */
    public Set getRootKeyItems() {
        return rootKeyItems;
    }
    
    /**
     * Adds a key to this tree.
     * @param key key to add
     */
    public void addKey(String key) {
        updater.addKey(this, key);
        fireAdd(keyItemsCache.get(key));
    }
    /**
     * Removes a key from this tree.
     * @param key key to remove
     */
    public void removeKey(String key) {
        Object item = keyItemsCache.get(key);
        updater.removeKey(this, key);
        fireRemove(item);
    }
    /**
     * Modifies a key on this tree.
     * @param key key to modify
     */
    public void modifyKey(String key) {
        Object item = keyItemsCache.get(key);
        fireModify(item);
    }
    /**
     * Marks a key as selected on this tree.
     * @param key  key to select.
     */
    public void selectKey(String key) {
        Object item = keyItemsCache.get(key);
        if ((selectedKey == null) || (!selectedKey.equals(key))) {
            selectedKey = key;
            fireSelect(item);
        }
    }
    public void selectNextKey() {
        String currentKey = getSelectedKey();
        boolean takeNext = false;
        String nextKey = null;
        for (String key : keyItemsCache.keySet()) {
            if (takeNext) {
                nextKey = key;
                break;
            }
            if (key.equals(currentKey))
                takeNext = true;
        }
        
        if (nextKey != null)
            selectKey(nextKey);			
    }
    /**
     * Gets the key tree updater.
     * @return key tree updater
     */
    public IKeyTreeUpdater getUpdater() {
        return updater;
    }
    /**
     * Sets the key tree updater. Doing so will automatically refresh the tree,
     * which means, recreating it entirely.
     * @param updater key tree updater
     */
    public void setUpdater(IKeyTreeUpdater updater) {
        this.updater = updater;
        keyItemsCache.clear();
        rootKeyItems.clear();
        load();        
    }

    /**
     * @see com.essiembre.eclipse.rbe.model.tree.IKeyTreeVisitable#accept(
     *         com.essiembre.eclipse.rbe.model.tree.IKeyTreeVisitor,
     *         java.lang.Object)
     */
    public void accept(IKeyTreeVisitor visitor, Object passAlongArgument) {
        for (Iterator iter = keyItemsCache.values().iterator(); iter.hasNext();) {
            visitor.visitKeyTreeItem(
                    (KeyTreeItem) iter.next(), passAlongArgument);
        }
        visitor.visitKeyTree(this, passAlongArgument);
    }
    
    /**
     * Gets the bundle group associated with this tree.
     * @return bundle group
     */
    public IBundleGroup getBundleGroup() {
        return bundleGroup;
    }
    
    /**
     * Loads all key tree items, base on bundle group.
     */
    private final void load() {
        for (Iterator iter = bundleGroup.getKeys().iterator();
                iter.hasNext();) {
            /*
             * Do not call "fireAdd" method from here for extreme performance
             * improvement.  This is not an addition in the sense that we are
             * laying out existing keys, not adding any new ones.  We will
             * refresh the whole tree after we are done looping.
             */
            updater.addKey(this, (String) iter.next());
        }
        if (getFilter() != null)
            filterKeyItems(getFilter());
        
        fireAdd(this);
    }
    
    private String filter;
    
    /**
     * Returns the key filter that was applied using the last call to {@link #filterKeyItems(String)}
     * @return The key filter that was applied using the last call to {@link #filterKeyItems(String)}
     */
    private String getFilter() {
        return filter;
    }
    
    /**
     * Filters the key items by applying a substring-check for the given <code>filter</code> string. This information
     * can be used later in {@link ViewerFilter}s to suppress filtered items. 
     * @param filter The <code>filter</code> that should be applied to the key items.
     */
    public void filterKeyItems(String filter) {
        this.filter = filter;
        for (KeyTreeItem item : rootKeyItems) {
            item.applyFilter(filter);
        }
    }
    
    /**
     * Resets the filter that is currently used, i.e. makes all key items visible again. 
     */    
    public void resetFilter() {
        filterKeyItems("");
    }
}
