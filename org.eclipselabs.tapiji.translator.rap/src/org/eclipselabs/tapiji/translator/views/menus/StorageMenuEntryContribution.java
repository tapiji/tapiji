package org.eclipselabs.tapiji.translator.views.menus;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipselabs.tapiji.translator.rap.utils.UIUtils;
import org.eclipselabs.tapiji.translator.rap.utils.UserUtils;
import org.eclipselabs.tapiji.translator.views.StorageView;

public class StorageMenuEntryContribution extends ContributionItem implements
		ISelectionChangedListener {

	private MenuItem storeItem;
	private MenuItem removeItem;
	private MenuItem renameItem;
	private MenuItem addNewLocalItem;
	
	private StorageView parentView;
	
	public StorageMenuEntryContribution(StorageView view) {
		this.parentView = view;
	}
	
	@Override
	public void fill(Menu menu, int index) {
		
		if (parentView.isValidSelection()) {
			if (parentView.isSelectionUnstoredRB() && UserUtils.isUserLoggedIn()) {
				// MenuItem for adding a new entry to storage
				storeItem = new MenuItem(menu, SWT.NONE, index);
				storeItem.setText("Store");
				storeItem.setImage(PlatformUI.getWorkbench().getSharedImages()
				        .getImageDescriptor(ISharedImages.IMG_OBJ_ADD).createImage());
				storeItem.addSelectionListener(new SelectionListener() {
		
					@Override
					public void widgetSelected(SelectionEvent e) {
						parentView.storeSelectedItem();
					}
		
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
		
					}
				});			
				index++;
			}
			// MenuItem for adding a new locale to the currently selected rb
			addNewLocalItem = new MenuItem(menu, SWT.NONE, index);
			addNewLocalItem.setText("Add New Locale ...");
			ImageDescriptor descriptor = UIUtils.getImageDescriptor(UIUtils.IMAGE_NEW_PROPERTIES_FILE);
			addNewLocalItem.setImage(descriptor.createImage());
			addNewLocalItem.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					parentView.addNewLocaleToSelectedRB();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {

				}
			});	
			index++;
						
			if ((parentView.isSelectionStoredRB() && UserUtils.isUserLoggedIn()) || parentView.isSelectionUnstoredRB()) {		
				// MenuItem for renaming the currently selected entry
				renameItem = new MenuItem(menu, SWT.NONE, index);
				renameItem.setText("Rename");
				descriptor = UIUtils.getImageDescriptor(UIUtils.IMAGE_RENAME);
				renameItem.setImage(descriptor.createImage());
				renameItem.addSelectionListener(new SelectionListener() {
	
					@Override
					public void widgetSelected(SelectionEvent e) {
						parentView.renameSelectedItem();
					}
	
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
	
					}
				});
				index++;				
			}
			
			// MenuItem for deleting the currently selected entry
			removeItem = new MenuItem(menu, SWT.NONE, index);
			removeItem.setText("Remove");
			removeItem.setImage(PlatformUI.getWorkbench().getSharedImages()
			        .getImageDescriptor(ISharedImages.IMG_ETOOL_DELETE)
			        .createImage());
			removeItem.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					parentView.deleteSelectedItem();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {

				}
			});	
			index++;
		}
	}
	
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		// TODO Auto-generated method stub

	}

}
