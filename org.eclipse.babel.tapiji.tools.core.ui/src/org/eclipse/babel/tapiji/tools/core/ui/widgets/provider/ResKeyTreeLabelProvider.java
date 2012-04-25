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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.babel.core.message.IMessage;
import org.eclipse.babel.core.message.tree.IKeyTreeNode;
import org.eclipse.babel.editor.api.IValuedKeyTreeNode;
import org.eclipse.babel.tapiji.tools.core.ui.widgets.filter.FilterInfo;
import org.eclipse.babel.tapiji.tools.core.util.FontUtils;
import org.eclipse.babel.tapiji.tools.core.util.ImageUtils;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

public class ResKeyTreeLabelProvider extends KeyTreeLabelProvider {

	private List<Locale> locales;
	private boolean searchEnabled = false;

	/*** COLORS ***/
	private Color gray = FontUtils.getSystemColor(SWT.COLOR_GRAY);
	private Color black = FontUtils.getSystemColor(SWT.COLOR_BLACK);
	private Color info_color = FontUtils.getSystemColor(SWT.COLOR_YELLOW);

	/*** FONTS ***/
	private Font bold = FontUtils.createFont(SWT.BOLD);
	private Font bold_italic = FontUtils.createFont(SWT.BOLD | SWT.ITALIC);

	public ResKeyTreeLabelProvider(List<Locale> locales) {
		this.locales = locales;
	}

	// @Override
	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex == 0) {
			IKeyTreeNode kti = (IKeyTreeNode) element;
			IMessage[] be = kti.getMessagesBundleGroup().getMessages(
			        kti.getMessageKey());
			boolean incomplete = false;

			if (be.length != kti.getMessagesBundleGroup()
			        .getMessagesBundleCount())
				incomplete = true;
			else {
				for (IMessage b : be) {
					if (b.getValue() == null
					        || b.getValue().trim().length() == 0) {
						incomplete = true;
						break;
					}
				}
			}

			if (incomplete)
				return ImageUtils.getImage(ImageUtils.ICON_RESOURCE_INCOMPLETE);
			else
				return ImageUtils.getImage(ImageUtils.ICON_RESOURCE);
		}
		return null;
	}

	// @Override
	public String getColumnText(Object element, int columnIndex) {
		if (columnIndex == 0)
			return super.getText(element);

		if (columnIndex <= locales.size()) {
			IValuedKeyTreeNode item = (IValuedKeyTreeNode) element;
			String entry = item.getValue(locales.get(columnIndex - 1));
			if (entry != null) {
				return entry;
			}
		}
		return "";
	}

	public void setSearchEnabled(boolean enabled) {
		this.searchEnabled = enabled;
	}

	public boolean isSearchEnabled() {
		return this.searchEnabled;
	}

	public void setLocales(List<Locale> visibleLocales) {
		locales = visibleLocales;
	}

	protected boolean isMatchingToPattern(Object element, int columnIndex) {
		boolean matching = false;

		if (element instanceof IValuedKeyTreeNode) {
			IValuedKeyTreeNode vkti = (IValuedKeyTreeNode) element;

			if (vkti.getInfo() == null)
				return false;

			FilterInfo filterInfo = (FilterInfo) vkti.getInfo();

			if (columnIndex == 0) {
				matching = filterInfo.isFoundInKey();
			} else {
				matching = filterInfo.hasFoundInLocale(locales
				        .get(columnIndex - 1));
			}
		}

		return matching;
	}

	protected boolean isSearchEnabled(Object element) {
		return (element instanceof IValuedKeyTreeNode && searchEnabled);
	}

	@Override
	public void update(ViewerCell cell) {
		Object element = cell.getElement();
		int columnIndex = cell.getColumnIndex();

		if (isSearchEnabled(element)) {
			if (isMatchingToPattern(element, columnIndex)) {
				List<StyleRange> styleRanges = new ArrayList<StyleRange>();
				FilterInfo filterInfo = (FilterInfo) ((IValuedKeyTreeNode) element)
				        .getInfo();

				if (columnIndex > 0) {
					for (Region reg : filterInfo.getFoundInLocaleRanges(locales
					        .get(columnIndex - 1))) {
						styleRanges.add(new StyleRange(reg.getOffset(), reg
						        .getLength(), black, info_color, SWT.BOLD));
					}
				} else {
					// check if the pattern has been found within the key
					// section
					if (filterInfo.isFoundInKey()) {
						for (Region reg : filterInfo.getKeyOccurrences()) {
							StyleRange sr = new StyleRange(reg.getOffset(),
							        reg.getLength(), black, info_color,
							        SWT.BOLD);
							styleRanges.add(sr);
						}
					}
				}
				cell.setStyleRanges(styleRanges
				        .toArray(new StyleRange[styleRanges.size()]));
			} else {
				cell.setForeground(gray);
			}
		} else if (columnIndex == 0)
			super.update(cell);

		cell.setImage(this.getColumnImage(element, columnIndex));
		cell.setText(this.getColumnText(element, columnIndex));
	}

}
