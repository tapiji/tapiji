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
package com.essiembre.eclipse.rbe.ui.editor.i18n;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;

import com.essiembre.eclipse.rbe.model.DeltaEvent;
import com.essiembre.eclipse.rbe.model.IDeltaListener;
import com.essiembre.eclipse.rbe.model.tree.KeyTree;
import com.essiembre.eclipse.rbe.model.tree.KeyTreeItem;
import com.essiembre.eclipse.rbe.model.workbench.RBEPreferences;
import com.essiembre.eclipse.rbe.ui.UIUtils;
import com.essiembre.eclipse.rbe.ui.editor.i18n.tree.KeyTreeComposite;
import com.essiembre.eclipse.rbe.ui.editor.resources.ResourceManager;

/**
 * Internationalization page where one can edit all resource bundle entries 
 * at once for all supported locales.
 * @author Pascal Essiembre (essiembre@users.sourceforge.net)
 * @version $Author: nl_carnage $ $Revision: 1.15 $ $Date: 2007/09/12 16:02:51 $
 */
public class I18nPage extends ScrolledComposite implements ISelectionProvider {

    /** Minimum height of text fields. */
    private static final int TEXT_MIN_HEIGHT = 100;

    private final ResourceManager resourceMediator;
    private final KeyTreeComposite keysComposite;
    private final List<ExpandItem> entryComposites = new ArrayList<ExpandItem>(); 
    private final LocalBehaviour localBehaviour = new LocalBehaviour();
    private final ScrolledComposite editingComposite;
    
    private List<ISelectionChangedListener> selectionChangedListeners;
    
    /*default*/ BundleEntryComposite activeEntry;
    
    /**
     * Constructor.
     * @param parent parent component.
     * @param style  style to apply to this component
     * @param resourceMediator resource manager
     */
    public I18nPage (
            Composite parent, int style, 
            final ResourceManager resourceMediator) {
        super(parent, style);
        this.resourceMediator = resourceMediator; 

        if(RBEPreferences.getNoTreeInEditor()) {
            keysComposite = null;
            editingComposite = this;
            createEditingPart(this);        	
        } else {
                    // Create screen        
            SashForm sashForm = new SashForm(this, SWT.NONE);
    
            setContent(sashForm);
            
            keysComposite = new KeyTreeComposite(
            		    sashForm, 
                        resourceMediator.getKeyTree());
            keysComposite.getTreeViewer().addSelectionChangedListener(localBehaviour);
            
            editingComposite = new ScrolledComposite(sashForm, SWT.V_SCROLL | SWT.H_SCROLL);
            createSashRightSide();
                    
            sashForm.setWeights(new int[]{25, 75});
            
        }

        setExpandHorizontal(true);
        setExpandVertical(true);
        setMinWidth(400);
        
        resourceMediator.getKeyTree().addListener(localBehaviour);
        
    }
    
    
    /**
     * Gets selected key.
     * @return selected key
     */
    private String getSelectedKey() {
        return (resourceMediator.getKeyTree().getSelectedKey());
    }


    /**
     * Creates right side of main sash form.
     * @param sashForm parent sash form
     */
    private void createSashRightSide() {
        editingComposite.setExpandHorizontal(true);
        editingComposite.setExpandVertical(true);
        editingComposite.setSize(SWT.DEFAULT, 100);
        createEditingPart(editingComposite);
    }
    
    
    /**
     * Creates the editing parts which are display within the supplied
     * parental ScrolledComposite instance.
     *
     * @param parent   A container to collect the bundle entry editors.
     */
    private void createEditingPart(ScrolledComposite parent) {
        Control[] children = parent.getChildren();
        for (int i = 0; i < children.length; i++) {
            children[i].dispose();
        }
        ExpandBar rightComposite = new ExpandBar(parent, SWT.BORDER);
        parent.setContent(rightComposite);
        parent.setMinSize(rightComposite.computeSize(
               SWT.DEFAULT,
               resourceMediator.getLocales().size() * (TEXT_MIN_HEIGHT + 30)));
        //rightComposite.setLayout(new GridLayout(1, false));
        entryComposites.clear();
        for (Iterator iter = resourceMediator.getLocales().iterator();
                iter.hasNext();) {
            Locale locale = (Locale) iter.next();
            BundleEntryComposite entryComposite = new BundleEntryComposite(
                    rightComposite, resourceMediator, locale, this);
            entryComposite.addFocusListener(localBehaviour);
            ExpandItem expandItem = new ExpandItem (rightComposite, SWT.BORDER);
            expandItem.setText((locale == null ? "Vorgabe" : locale.getDisplayName()));
            expandItem.setHeight(TEXT_MIN_HEIGHT);
            expandItem.setExpanded(true);
            expandItem.setImage(loadCountryIcon(locale));
            expandItem.setControl(entryComposite);
            entryComposites.add(expandItem);
        }
    }
    
    /**
     * Loads country icon based on locale country.
     * @param countryLocale the locale on which to grab the country
     * @return an image, or <code>null</code> if no match could be made
     */
    private Image loadCountryIcon(Locale countryLocale) {
        Image image = null;
        String countryCode = null;
        if (countryLocale != null && countryLocale.getCountry() != null) {
            countryCode = countryLocale.getCountry().toLowerCase();
        }
        if (countryCode != null && countryCode.length() > 0) {
            String imageName = "countries/" + //$NON-NLS-1$
            countryCode.toLowerCase() + ".gif"; //$NON-NLS-1$
            image = UIUtils.getImage(imageName);
        }
        if (image == null) {
            image = UIUtils.getImage("countries/blank.gif"); //$NON-NLS-1$
        }
        return image;
    }
    
    /**
     * This method focusses the {@link BundleEntryComposite} corresponding to the given {@link Locale}. If no such composite
     * exists or the locale is null, nothing happens.
     * @param locale The locale whose {@link BundleEntryComposite} is to be focussed.
     */
    public void focusBundleEntryComposite(Locale locale) {
        for (ExpandItem ei : entryComposites) {
        	BundleEntryComposite bec =  (BundleEntryComposite) ei.getControl();
        	if ((bec.getLocale() == null) && (locale == null) || (locale != null && locale.equals(bec.getLocale()))) {
                bec.focusTextBox();
            }
        }
    }
    
    /**
     * Focusses the next {@link BundleEntryComposite}.
     */
    public void focusNextBundleEntryComposite() {
        int index = entryComposites.indexOf(activeEntry);
        ExpandItem ei;
        if (index < entryComposites.size()-1)
            ei = entryComposites.get(++index);
        else
            ei = entryComposites.get(0);
        
        BundleEntryComposite nextComposite = (BundleEntryComposite) ei.getControl();
        if (nextComposite != null)
            focusComposite(nextComposite);
    }
    
    /**
     * Focusses the previous {@link BundleEntryComposite}.
     */
    public void focusPreviousBundleEntryComposite() {
        int index = entryComposites.indexOf(activeEntry);
        ExpandItem ei;
        if (index > 0)
            ei = entryComposites.get(--index);
        else
            ei = entryComposites.get(entryComposites.size()-1);
        
        BundleEntryComposite nextComposite = (BundleEntryComposite) ei.getControl();
        if (nextComposite != null)
            focusComposite(nextComposite);
    }
    
    /**
     * Focusses the given {@link BundleEntryComposite} and scrolls the surrounding {@link ScrolledComposite}
     * in order to make it visible.
     * @param comp The {@link BundleEntryComposite} to be focussed.
     */
    private void focusComposite(BundleEntryComposite comp) {
        Point compPos = comp.getLocation();
        Point compSize = comp.getSize();
        Point size = editingComposite.getSize();
        Point origin = editingComposite.getOrigin();
        if (compPos.y + compSize.y > size.y + origin.y)
            editingComposite.setOrigin(origin.x, origin.y + (compPos.y+compSize.y) - (origin.y+size.y) + 5);
        else if (compPos.y < origin.y)
            editingComposite.setOrigin(origin.x, compPos.y);
        comp.focusTextBox();
    }
    
    /**
     * Selects the next entry in the {@link KeyTree}.
     */
    public void selectNextTreeEntry() {
        activeEntry.updateBundleOnChanges();
        String nextKey = resourceMediator.getBundleGroup().getNextKey(getSelectedKey());
        if (nextKey == null)
            return;

        Locale currentLocale = activeEntry.getLocale();
        resourceMediator.getKeyTree().selectKey(nextKey);
        focusBundleEntryComposite(currentLocale);
    }
    
    /**
     * Selects the previous entry in the {@link KeyTree}.
     */
    public void selectPreviousTreeEntry() {
        activeEntry.updateBundleOnChanges();
        String prevKey = resourceMediator.getBundleGroup().getPreviousKey(getSelectedKey());
        if (prevKey == null)
            return;
        
        Locale currentLocale = activeEntry.getLocale();
        resourceMediator.getKeyTree().selectKey(prevKey);
        focusBundleEntryComposite(currentLocale);
    }
    

    /**
     * Refreshes the editor associated with the active text box (if any)
     * if it has changed.
     */
    public void refreshEditorOnChanges(){
        if (activeEntry != null) {
            activeEntry.updateBundleOnChanges();
        }
    }
        
    /**
     * Refreshes all value-holding text boxes in this page.
     */
    public void refreshTextBoxes() {
        String key = getSelectedKey();
        for (Iterator<ExpandItem> iter = entryComposites.iterator(); iter.hasNext();) {
            BundleEntryComposite entryComposite = 
                    (BundleEntryComposite) iter.next().getControl();
            entryComposite.refresh(key);
        }
    }
    
    /**
     * Refreshes the tree and recreates the editing part.
     */
    public void refreshPage() {
        if (keysComposite != null)
            keysComposite.getTreeViewer().refresh(true);
        createEditingPart(editingComposite);
        editingComposite.layout(true, true);
    }
    
    
    /**
     * @see org.eclipse.swt.widgets.Widget#dispose()
     */
    public void dispose() {
    	try {
	        if(keysComposite != null) {
	            keysComposite.dispose();
	        }
	        for (Iterator<ExpandItem> iter = entryComposites.iterator(); iter.hasNext();) {
	            ((BundleEntryComposite) iter.next().getControl()).dispose();
	        }
    	} catch (Exception e) {}
        super.dispose();
    }
    
    /**
     * Implementation of custom behaviour.
     */
    private class LocalBehaviour implements FocusListener, IDeltaListener, ISelectionChangedListener {

        /**
         * {@inheritDoc}
         */
        public void focusGained(FocusEvent event) {
            activeEntry = (BundleEntryComposite) event.widget;
        }

        /**
         * {@inheritDoc}
         */
        public void focusLost(FocusEvent event) {
            activeEntry = null;
        }

        /**
         * {@inheritDoc}
         */
        public void selectionChanged(SelectionChangedEvent event) {
            refreshTextBoxes();
            String selected = getSelectedKey();
            if(selected != null) {
                resourceMediator.getKeyTree().selectKey(selected);
            }
        }
        
        /**
         * {@inheritDoc}
         */
        public void add(DeltaEvent event) {
        }

        /**
         * {@inheritDoc}
         */
        public void remove(DeltaEvent event) {
        }

        /**
         * {@inheritDoc}
         */
        public void modify(DeltaEvent event) {
        }

        /**
         * {@inheritDoc}
         */
        public void select(DeltaEvent event) {
            KeyTreeItem item = (KeyTreeItem) event.receiver();
            if(keysComposite != null) {
                if(item != null) {
                    keysComposite.getTreeViewer().setSelection(new StructuredSelection(item));
                }
            } else {
                refreshTextBoxes();
            }
        }
        
    } /* ENDCLASS */
    
	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		keysComposite.getTreeViewer().addSelectionChangedListener(listener);
	}


	@Override
	public ISelection getSelection() {
		return keysComposite.getTreeViewer().getSelection();
	}


	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		keysComposite.getTreeViewer().removeSelectionChangedListener(listener);
	}


	@Override
	public void setSelection(ISelection selection) {
		keysComposite.getTreeViewer().setSelection(selection);
	}
}
