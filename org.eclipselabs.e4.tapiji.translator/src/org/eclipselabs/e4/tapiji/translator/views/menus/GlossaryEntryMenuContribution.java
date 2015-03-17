/*******************************************************************************
 * Copyright (c) 2012 Martin Reiterer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Reiterer - initial API and implementation
 ******************************************************************************/
package org.eclipselabs.e4.tapiji.translator.views.menus;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
//import org.eclipse.ui.ISharedImages;
//import org.eclipse.ui.PlatformUI;
import org.eclipselabs.e4.tapiji.translator.views.widgets.TreeViewerWidget;

public class GlossaryEntryMenuContribution extends ContributionItem implements
	ISelectionChangedListener {

    private TreeViewerWidget parentView;
    private boolean legalSelection = false;

    // Menu-Items
    private MenuItem addItem;
    private MenuItem removeItem;

    public GlossaryEntryMenuContribution() {
    }

    public GlossaryEntryMenuContribution(TreeViewerWidget view,
	    boolean legalSelection) {
	this.legalSelection = legalSelection;
	this.parentView = view;
        //parentView.addSelectionChangedListener(this);
    }

    @Override
    public void fill(Menu menu, int index) {

	// MenuItem for adding a new entry
	addItem = new MenuItem(menu, SWT.NONE, index);
	addItem.setText("Add ...");
//	addItem.setImage(PlatformUI.getWorkbench().getSharedImages()
//		.getImageDescriptor(ISharedImages.IMG_OBJ_ADD).createImage());
	addItem.addSelectionListener(new SelectionListener() {

	    @Override
	    public void widgetSelected(SelectionEvent e) {
                //	parentView.addNewItem();
	    }

	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {

	    }
	});

	if ((parentView == null && legalSelection) || parentView != null) {
	    // MenuItem for deleting the currently selected entry
	    removeItem = new MenuItem(menu, SWT.NONE, index + 1);
	    removeItem.setText("Remove");
//	    removeItem.setImage(PlatformUI.getWorkbench().getSharedImages()
//		    .getImageDescriptor(ISharedImages.IMG_ETOOL_DELETE)
//		    .createImage());
	    removeItem.addSelectionListener(new SelectionListener() {

		@Override
		public void widgetSelected(SelectionEvent e) {
                    //   parentView.deleteSelectedItems();
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {

		}
	    });
	    enableMenuItems();
	}
    }

    protected void enableMenuItems() {
	try {
	    removeItem.setEnabled(legalSelection);
	} catch (Exception e) {
	}
    }

    @Override
    public void selectionChanged(SelectionChangedEvent event) {
	legalSelection = !event.getSelection().isEmpty();
	// enableMenuItems ();
    }

}
