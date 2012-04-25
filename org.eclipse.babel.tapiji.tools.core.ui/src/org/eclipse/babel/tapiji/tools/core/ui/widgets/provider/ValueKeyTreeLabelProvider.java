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
package org.eclipse.babel.tapiji.tools.core.ui.widgets.provider;

import org.eclipse.babel.core.message.IMessage;
import org.eclipse.babel.core.message.IMessagesBundle;
import org.eclipse.babel.core.message.tree.IKeyTreeNode;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

public class ValueKeyTreeLabelProvider extends KeyTreeLabelProvider implements
        ITableColorProvider, ITableFontProvider {

	private IMessagesBundle locale;

	public ValueKeyTreeLabelProvider(IMessagesBundle iBundle) {
		this.locale = iBundle;
	}

	// @Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	// @Override
	public String getColumnText(Object element, int columnIndex) {
		try {
			IKeyTreeNode item = (IKeyTreeNode) element;
			IMessage entry = locale.getMessage(item.getMessageKey());
			if (entry != null) {
				String value = entry.getValue();
				if (value.length() > 40)
					value = value.substring(0, 39) + "...";
			}
		} catch (Exception e) {
		}
		return "";
	}

	@Override
	public Color getBackground(Object element, int columnIndex) {
		return null;// return new Color(Display.getDefault(), 255, 0, 0);
	}

	@Override
	public Color getForeground(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Font getFont(Object element, int columnIndex) {
		return null; // UIUtils.createFont(SWT.BOLD);
	}

	@Override
	public void update(ViewerCell cell) {
		Object element = cell.getElement();
		int columnIndex = cell.getColumnIndex();
		cell.setImage(this.getColumnImage(element, columnIndex));
		cell.setText(this.getColumnText(element, columnIndex));

		super.update(cell);
	}

}
