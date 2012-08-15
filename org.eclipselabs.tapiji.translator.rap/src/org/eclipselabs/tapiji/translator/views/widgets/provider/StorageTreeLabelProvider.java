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
import org.eclipselabs.tapiji.translator.rap.model.user.PropertiesFile;
import org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle;
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
				text += rb.getName() + " ("+rb.getUser().getUsername()+")";
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
	public Image getImage(Object element) {		
		ResourceBundle rb;
		if (element instanceof ResourceBundle) {
			rb = (ResourceBundle) element;
			if (rb.isTemporary()) {
				ImageDescriptor descriptor = UIUtils.getImageDescriptor(UIUtils.IMAGE_RESOURCE_BUNDLE_TEMPORARY);
				return descriptor.createImage();
			} else {
				ImageDescriptor descriptor = UIUtils.getImageDescriptor(UIUtils.IMAGE_RESOURCE_BUNDLE);
				return descriptor.createImage();
			}
				
		}
		else if (element instanceof PropertiesFile) {
			rb = ((PropertiesFile) element).getResourceBundle();
			if (rb.isTemporary()) {
				ImageDescriptor descriptor = UIUtils.getImageDescriptor(UIUtils.IMAGE_PROPERTIES_FILE_TEMPORARY);
				return descriptor.createImage();
			} else {
				ImageDescriptor descriptor = UIUtils.getImageDescriptor(UIUtils.IMAGE_PROPERTIES_FILE);
				return descriptor.createImage();
			}
		}		
		
		return null;
	}
}
