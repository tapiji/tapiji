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
import org.eclipselabs.tapiji.translator.rap.dialogs.DownloadDialog;
import org.eclipselabs.tapiji.translator.rap.dialogs.ShareDialog;
import org.eclipselabs.tapiji.translator.rap.helpers.managers.RBLock;
import org.eclipselabs.tapiji.translator.rap.helpers.managers.RBLockManager;
import org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle;
import org.eclipselabs.tapiji.translator.rap.model.user.User;
import org.eclipselabs.tapiji.translator.rap.utils.UIUtils;
import org.eclipselabs.tapiji.translator.rap.utils.UserUtils;
import org.eclipselabs.tapiji.translator.views.StorageView;

public class StorageMenuEntryContribution extends ContributionItem implements
		ISelectionChangedListener {

	private MenuItem storeItem;
	private MenuItem removeItem;
	private MenuItem renameItem;
	private MenuItem addNewLocalItem;
	private MenuItem downloadItem;
	private MenuItem shareItem;
	
	private StorageView parentView;
	
	public StorageMenuEntryContribution(StorageView view) {
		this.parentView = view;
	}
	
	@Override
	public void fill(Menu menu, int index) {
		
		
		if (parentView.isValidSelection()) {
			ResourceBundle selectedRB = parentView.getSelectedRB();
			boolean isRBLocked = RBLockManager.INSTANCE.isLocked(selectedRB.getId());
			RBLock rbLock = RBLockManager.INSTANCE.getRBLock(selectedRB.getId());
			
			User currentUser = UserUtils.getUser();
			User ownerOfLock = rbLock != null ? rbLock.getOwner() : null;	
			User owernOfRB = selectedRB.getOwner();
			
			boolean isUserLoggedIn = UserUtils.isUserLoggedIn();
			boolean isRBUnstored = parentView.isSelectionUnstoredRB();
			boolean isRBStored = parentView.isSelectionStoredRB();
			boolean isCurrentUserOwnerOfRB = owernOfRB != null ? owernOfRB.equals(currentUser) : false;
			
			if (isRBUnstored && isUserLoggedIn) {
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
			// MenuItem for renaming the currently selected entry			
			if ((isRBStored && isUserLoggedIn && (! isRBLocked || currentUser.equals(ownerOfLock))) || 
					isRBUnstored) {		
				
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
			// only visible if RB isn't locked or user is owner of the lock
			if (! isUserLoggedIn || selectedRB.isTemporary() || 
					// owner of the rb are allowed to remove their rb if it isn't currently locked by another user
					isCurrentUserOwnerOfRB && (! isRBLocked || currentUser.equals(ownerOfLock)) || 
					// shared user are allowed to remove their link to the original rb
					! isCurrentUserOwnerOfRB && isRBStored) {
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
			
			// MenuItem for downloading the currently selected rb
			downloadItem = new MenuItem(menu, SWT.NONE, index);
			downloadItem.setText("Download ...");
			downloadItem.setImage(UIUtils.getImageDescriptor(UIUtils.IMAGE_DOWNLOAD_RB).createImage());
			downloadItem.addSelectionListener(new SelectionListener() {				
				@Override
				public void widgetSelected(SelectionEvent e) {					
					parentView.downloadSelectedRB();
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {					
				}
			});
			index ++;
			
			if (isRBStored && isUserLoggedIn && isCurrentUserOwnerOfRB) {				
				// MenuItem for downloading the currently selected rb
				shareItem = new MenuItem(menu, SWT.NONE, index);
				shareItem.setText("Share ...");
				//shareItem.setImage(UIUtils.getImageDescriptor(UIUtils.IMAGE_DOWNLOAD_RB).createImage());
				shareItem.addSelectionListener(new SelectionListener() {				
					@Override
					public void widgetSelected(SelectionEvent e) {					
						parentView.shareSelectedRB();
					}
					
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {					
					}
				});
				index ++;				
			}
		}
	}
	
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		// TODO Auto-generated method stub

	}

}
