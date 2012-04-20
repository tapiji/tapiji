/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.core.ui.views.messagesview;

import org.eclipse.babel.tapiji.tools.core.ui.widgets.PropertyKeySelectionTree;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;


public class ResourceBundleEntry extends ContributionItem implements
		ISelectionChangedListener {

	private PropertyKeySelectionTree parentView;
	private ISelection selection;
	private boolean legalSelection = false;

	// Menu-Items
	private MenuItem addItem;
	private MenuItem editItem;
	private MenuItem removeItem;

	public ResourceBundleEntry() {
	}

	public ResourceBundleEntry(PropertyKeySelectionTree view, ISelection selection) {
		this.selection = selection;
		this.legalSelection = ! selection.isEmpty();
		this.parentView = view;
		parentView.addSelectionChangedListener(this);
	}

	@Override
	public void fill(Menu menu, int index) {

		// MenuItem for adding a new entry
		addItem = new MenuItem(menu, SWT.NONE, index);
		addItem.setText("Add ...");
		addItem.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJ_ADD).createImage());
		addItem.addSelectionListener( new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				parentView.addNewItem(selection);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});

		if ((parentView == null && legalSelection) || parentView != null) {
			// MenuItem for editing the currently selected entry
			editItem = new MenuItem(menu, SWT.NONE, index + 1);
			editItem.setText("Edit");
			editItem.addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					parentView.editSelectedItem();
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					
				}
			});

			// MenuItem for deleting the currently selected entry
			removeItem = new MenuItem(menu, SWT.NONE, index + 2);
			removeItem.setText("Remove");
			removeItem.setImage(PlatformUI.getWorkbench().getSharedImages()
					.getImageDescriptor(ISharedImages.IMG_ETOOL_DELETE)
					.createImage());
			removeItem.addSelectionListener( new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					parentView.deleteSelectedItems();
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
			editItem.setEnabled(legalSelection);
			removeItem.setEnabled(legalSelection);
		} catch (Exception e) {
			// silent catch
		}
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		legalSelection = !event.getSelection().isEmpty();
		// enableMenuItems ();
	}

}
