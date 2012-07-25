package org.eclipselabs.tapiji.translator.views.menus;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipselabs.tapiji.translator.views.StorageView;
import org.eclipselabs.tapiji.translator.views.widgets.GlossaryWidget;

public class StorageMenuEntryContribution extends ContributionItem implements
		ISelectionChangedListener {

	MenuItem storeItem;
	MenuItem removeItem;
	MenuItem renameItem;
	
	StorageView parentView;
	
	public StorageMenuEntryContribution(StorageView view) {
		this.parentView = view;
	}
	
	@Override
	public void fill(Menu menu, int index) {

		if (parentView.isSelectionUnstoredFile()) {
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
			
		} else {
			// MenuItem for renaming the currently selected entry
			renameItem = new MenuItem(menu, SWT.NONE, index);
			renameItem.setText("Rename");
			renameItem.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					parentView.renameSelectedItem();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {

				}
			});
			// MenuItem for deleting the currently selected entry
			removeItem = new MenuItem(menu, SWT.NONE, index+1);
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
		}		
	}
	
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		// TODO Auto-generated method stub

	}

}
