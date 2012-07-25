package org.eclipselabs.tapiji.translator.views.widgets.provider;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipselabs.tapiji.translator.rap.model.user.File;
import org.eclipselabs.tapiji.translator.rap.utils.FileRAPUtils;

public class StorageLabelProvider extends LabelProvider implements IFontProvider, IColorProvider {

	private int tempFileCounter = 0;
	
	@Override
	public Font getFont(Object element) {
//		if (element instanceof IFile) {
//			Display display = Display.getDefault();
//			FontData data = new FontData();
//			data.setStyle(SWT.);
//			data.setHeight(10);
//			data.setName("");
//			return new Font(display, data);
//		}
		return null;
	}
	
	@Override
	public String getText(Object element) {
		if (element instanceof File) {
			File file = (File) element;
			return FileRAPUtils.getBundleName(file.getPath());
		} else if (element instanceof IFile) {
			String tempName = "Temporary File";
			if (tempFileCounter++ > 0)
				tempName += " ("+ tempFileCounter +")";
			return tempName;
		}
		
		return "not supported";
	}

	@Override
	public Color getForeground(Object element) {
		if (element instanceof IFile) {
			return Display.getDefault().getSystemColor(SWT.COLOR_GRAY);
		}
		return null;
	}

	@Override
	public Color getBackground(Object element) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
