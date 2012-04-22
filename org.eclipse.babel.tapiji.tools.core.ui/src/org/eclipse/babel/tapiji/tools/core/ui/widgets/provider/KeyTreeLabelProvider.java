/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Reiterer - initial API and implementation
 ******************************************************************************/
/*
+ * Copyright (C) 2003, 2004  Pascal Essiembre, Essiembre Consultant Inc.
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
package org.eclipse.babel.tapiji.tools.core.ui.widgets.provider;

import org.eclipse.babel.tapiji.tools.core.Activator;
import org.eclipse.babel.tapiji.tools.core.util.FontUtils;
import org.eclipse.babel.tapiji.translator.rbe.babel.bundle.IKeyTreeNode;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * Label provider for key tree viewer.
 * 
 * @author Pascal Essiembre (essiembre@users.sourceforge.net)
 * @version $Author: nl_carnage $ $Revision: 1.11 $ $Date: 2007/09/11 16:11:09 $
 */
public class KeyTreeLabelProvider extends StyledCellLabelProvider /*
																 * implements
																 * IFontProvider
																 * ,
																 * IColorProvider
																 */{

	private static final int KEY_DEFAULT = 1 << 1;
	private static final int KEY_COMMENTED = 1 << 2;
	private static final int KEY_NOT = 1 << 3;
	private static final int WARNING = 1 << 4;
	private static final int WARNING_GREY = 1 << 5;

	/** Registry instead of UIUtils one for image not keyed by file name. */
	private static ImageRegistry imageRegistry = new ImageRegistry();

	private Color commentedColor = FontUtils.getSystemColor(SWT.COLOR_GRAY);

	/** Group font. */
	private Font groupFontKey = FontUtils.createFont(SWT.BOLD);
	private Font groupFontNoKey = FontUtils.createFont(SWT.BOLD | SWT.ITALIC);

	/**
	 * @see ILabelProvider#getImage(Object)
	 */
	public Image getImage(Object element) {
		IKeyTreeNode treeItem = ((IKeyTreeNode) element);

		int iconFlags = 0;

		// Figure out background icon
		if (treeItem.getMessagesBundleGroup() != null
		        && treeItem.getMessagesBundleGroup().isKey(
		                treeItem.getMessageKey())) {
			iconFlags += KEY_DEFAULT;
		} else {
			iconFlags += KEY_NOT;
		}

		return generateImage(iconFlags);
	}

	/**
	 * @see ILabelProvider#getText(Object)
	 */
	public String getText(Object element) {
		return ((IKeyTreeNode) element).getName();
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() {
		groupFontKey.dispose();
		groupFontNoKey.dispose();
	}

	/**
	 * @see org.eclipse.jface.viewers.IFontProvider#getFont(java.lang.Object)
	 */
	public Font getFont(Object element) {
		IKeyTreeNode item = (IKeyTreeNode) element;
		if (item.getChildren().length > 0
		        && item.getMessagesBundleGroup() != null) {
			if (item.getMessagesBundleGroup().isKey(item.getMessageKey())) {
				return groupFontKey;
			}
			return groupFontNoKey;
		}
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.IColorProvider#getForeground(java.lang.Object)
	 */
	public Color getForeground(Object element) {
		IKeyTreeNode treeItem = (IKeyTreeNode) element;
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.IColorProvider#getBackground(java.lang.Object)
	 */
	public Color getBackground(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Generates an image based on icon flags.
	 * 
	 * @param iconFlags
	 * @return generated image
	 */
	private Image generateImage(int iconFlags) {
		Image image = imageRegistry.get("" + iconFlags); //$NON-NLS-1$
		if (image == null) {
			// Figure background image
			if ((iconFlags & KEY_COMMENTED) != 0) {
				image = getRegistryImage("keyCommented.gif"); //$NON-NLS-1$
			} else if ((iconFlags & KEY_NOT) != 0) {
				image = getRegistryImage("key.gif"); //$NON-NLS-1$
			} else {
				image = getRegistryImage("key.gif"); //$NON-NLS-1$
			}

		}
		return image;
	}

	private Image getRegistryImage(String imageName) {
		Image image = imageRegistry.get(imageName);
		if (image == null) {
			image = Activator.getImageDescriptor(imageName).createImage();
			imageRegistry.put(imageName, image);
		}
		return image;
	}

	@Override
	public void update(ViewerCell cell) {
		cell.setBackground(getBackground(cell.getElement()));
		cell.setFont(getFont(cell.getElement()));
		cell.setForeground(getForeground(cell.getElement()));

		cell.setText(getText(cell.getElement()));
		cell.setImage(getImage(cell.getElement()));
		super.update(cell);
	}

}
