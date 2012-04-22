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
package org.eclipselabs.tapiji.translator.views.widgets.provider;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.babel.editor.api.EditorUtil;
import org.eclipse.babel.tapiji.translator.rbe.babel.bundle.IKeyTreeNode;
import org.eclipse.babel.tapiji.translator.rbe.babel.bundle.IMessage;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipselabs.tapiji.translator.model.Term;
import org.eclipselabs.tapiji.translator.model.Translation;
import org.eclipselabs.tapiji.translator.utils.FontUtils;
import org.eclipselabs.tapiji.translator.views.widgets.filter.FilterInfo;

public class GlossaryLabelProvider extends StyledCellLabelProvider implements
				ISelectionListener, ISelectionChangedListener {

	private boolean searchEnabled = false;
	private int referenceColumn = 0;
	private List<String> translations;
	private IKeyTreeNode selectedItem;
	
	/*** COLORS ***/
	private Color gray = FontUtils.getSystemColor(SWT.COLOR_GRAY);
	private Color black = FontUtils.getSystemColor(SWT.COLOR_BLACK);
	private Color info_color = FontUtils.getSystemColor(SWT.COLOR_YELLOW); 
	private Color info_crossref = FontUtils.getSystemColor(SWT.COLOR_INFO_BACKGROUND); 
	private Color info_crossref_foreground = FontUtils.getSystemColor(SWT.COLOR_INFO_FOREGROUND);
	private Color transparent = FontUtils.getSystemColor(SWT.COLOR_WHITE);

    /*** FONTS ***/
    private Font bold = FontUtils.createFont(SWT.BOLD);
    private Font bold_italic = FontUtils.createFont(SWT.ITALIC);
	
	public void setSearchEnabled(boolean b) {
		this.searchEnabled = b;
	}

	public GlossaryLabelProvider(int referenceColumn, List<String> translations, IWorkbenchPage page) {
		this.referenceColumn = referenceColumn;
		this.translations = translations;
		if (page.getActiveEditor() != null) {
			selectedItem = EditorUtil.getSelectedKeyTreeNode(page);
		}
	}

	public String getColumnText(Object element, int columnIndex) {
		try {
			Term term = (Term) element;
			if (term != null) {
				Translation transl = term.getTranslation(this.translations.get(columnIndex));
				return transl != null ? transl.value : "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public boolean isSearchEnabled () {
		return this.searchEnabled;
	}
	
	protected boolean isMatchingToPattern (Object element, int columnIndex) {
		boolean matching = false;
		
		if (element instanceof Term) {
			Term term = (Term) element;
			
			if (term.getInfo() == null)
				return false;
			
			FilterInfo filterInfo = (FilterInfo) term.getInfo();
			
			matching = filterInfo.hasFoundInTranslation(translations.get(columnIndex));
		}
		
		return matching;
	}
	
	@Override
	public void update(ViewerCell cell) {
		Object element = cell.getElement();
		int columnIndex = cell.getColumnIndex();
		cell.setText(this.getColumnText(element, columnIndex));
		
		if (isCrossRefRegion(cell.getText())) {
			cell.setFont (bold);
			cell.setBackground(info_crossref);
			cell.setForeground(info_crossref_foreground);
		} else {
			cell.setFont(this.getColumnFont(element, columnIndex));
			cell.setBackground(transparent);
		}
		
		if (isSearchEnabled()) {
			if (isMatchingToPattern(element, columnIndex) ) {
				List<StyleRange> styleRanges = new ArrayList<StyleRange>();
				FilterInfo filterInfo = (FilterInfo) ((Term)element).getInfo();
			
				for (Region reg : filterInfo.getFoundInTranslationRanges(translations.get(columnIndex < referenceColumn ? columnIndex + 1 : columnIndex))) {
					styleRanges.add(new StyleRange(reg.getOffset(), reg.getLength(), black, info_color, SWT.BOLD));
				}
				
				cell.setStyleRanges(styleRanges.toArray(new StyleRange[styleRanges.size()]));
			} else {
				cell.setForeground(gray);
			}
		} 
	}

	private boolean isCrossRefRegion(String cellText) {
		if (selectedItem != null) {
			for (IMessage entry : selectedItem.getMessagesBundleGroup().getMessages(selectedItem.getMessageKey())) {
				String value = entry.getValue();
				String[] subValues = value.split("[\\s\\p{Punct}]+");
				for (String v : subValues) {
					if (v.trim().equalsIgnoreCase(cellText.trim()))
						return true;
				}
			}
		}
		
		return false;
	}

	private Font getColumnFont(Object element, int columnIndex) {
		if (columnIndex == 0) {
			return bold_italic;
		}
		return null;
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		try {
			if (selection.isEmpty())
				return;
			
			if (!(selection instanceof IStructuredSelection))
				return;
			
			IStructuredSelection sel = (IStructuredSelection) selection;
			selectedItem = (IKeyTreeNode) sel.iterator().next();
			this.getViewer().refresh();
		} catch (Exception e) {
			// silent catch
		}
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		event.getSelection();
	}

}
