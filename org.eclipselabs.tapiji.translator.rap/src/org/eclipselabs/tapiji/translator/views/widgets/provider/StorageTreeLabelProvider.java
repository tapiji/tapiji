package org.eclipselabs.tapiji.translator.views.widgets.provider;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipselabs.tapiji.translator.rap.helpers.managers.PFLock;
import org.eclipselabs.tapiji.translator.rap.helpers.managers.RBLockManager;
import org.eclipselabs.tapiji.translator.rap.helpers.utils.UserUtils;
import org.eclipselabs.tapiji.translator.rap.model.user.PropertiesFile;
import org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle;
import org.eclipselabs.tapiji.translator.rap.model.user.User;
import org.eclipselabs.tapiji.translator.rap.utils.UIUtils;

public class StorageTreeLabelProvider extends ColumnLabelProvider {
	@Override
	public String getText(Object element) {
		String text = "";
		if (element instanceof ResourceBundle) {
			ResourceBundle rb = (ResourceBundle) element;
			//text = "<span style=\"vertical-align:middle\">";
			if (rb.isTemporary())
				// italic
				text += "<i>" + rb.getName() + " (temp)</i>";
			else
				text += rb.getName() + " ("+rb.getOwner().getUsername()+")";
			//text += "</span>";
		} else if (element instanceof PropertiesFile){
			PropertiesFile file = (PropertiesFile) element;
			text = file.getFilename();
			if (file.getResourceBundle().isTemporary())
				text = "<i>" + text + "</i>";
		} else {
			text = "not supported";
		}		
		return text;
	}

	@Override
	public Color getForeground(Object element) {
		if (element instanceof ResourceBundle) {
			ResourceBundle rb = (ResourceBundle) element;			
			if (! rb.isTemporary() && RBLockManager.INSTANCE.isRBLocked(rb) &&
					! isUserOwnerOfAllPFLocks(rb))
				return Display.getCurrent().getSystemColor(SWT.COLOR_RED);		
		} else if (element instanceof PropertiesFile) {
			PropertiesFile pf = (PropertiesFile) element;			
			if (! pf.getResourceBundle().isTemporary() && 
					RBLockManager.INSTANCE.isPFLocked(pf.getId()) &&
					! isUserOwnerOfPFLock(pf))
				return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
		}						
		return super.getForeground(element);
	}
	
	@Override
	public Image getImage(Object element) {		
		ResourceBundle rb;
		if (element instanceof ResourceBundle) {
			rb = (ResourceBundle) element;
			if (rb.isTemporary()) {
				ImageDescriptor descriptor = UIUtils.getImageDescriptor(UIUtils.IMAGE_RESOURCE_BUNDLE_TEMPORARY);
				return descriptor.createImage();
			} else if (RBLockManager.INSTANCE.isRBLocked(rb) && ! isUserOwnerOfAllPFLocks(rb)) {
				ImageDescriptor descriptor = UIUtils.getImageDescriptor(UIUtils.IMAGE_LOCKED_RB);
				return descriptor.createImage();
			} else {
				ImageDescriptor descriptor = UIUtils.getImageDescriptor(UIUtils.IMAGE_RESOURCE_BUNDLE);
				return descriptor.createImage();
			}
				
		}
		else if (element instanceof PropertiesFile) {
			PropertiesFile pf = (PropertiesFile) element;
			rb = pf.getResourceBundle();
			if (rb.isTemporary()) {
				ImageDescriptor descriptor = UIUtils.getImageDescriptor(UIUtils.IMAGE_PROPERTIES_FILE_TEMPORARY);
				return descriptor.createImage();
			} else if (RBLockManager.INSTANCE.isPFLocked(pf.getId()) &&
					! isUserOwnerOfPFLock(pf)) {
				ImageDescriptor descriptor = UIUtils.getImageDescriptor(UIUtils.IMAGE_LOCKED_PF);
				return descriptor.createImage();
			} else {
				ImageDescriptor descriptor = UIUtils.getImageDescriptor(UIUtils.IMAGE_PROPERTIES_FILE);
				return descriptor.createImage();
			}
		}		
		
		return null;
	}
	
	private boolean isUserOwnerOfPFLock(PropertiesFile pf) {
		PFLock lock = RBLockManager.INSTANCE.getPFLock(pf.getId());
		if (lock.isReleased() || ! UserUtils.getUser().equals(lock.getOwner()))
			return false;
		return true;
	}
	
	private boolean isUserOwnerOfAllPFLocks(ResourceBundle rb) {
		if (UserUtils.isUserLoggedIn()) {
			for (PropertiesFile pf : rb.getPropertiesFiles()) {				
				if (RBLockManager.INSTANCE.isPFLocked(pf.getId()) && ! isUserOwnerOfPFLock(pf))
					return false;
			}
			return true;
		}
		
		return false;		
	}
}
