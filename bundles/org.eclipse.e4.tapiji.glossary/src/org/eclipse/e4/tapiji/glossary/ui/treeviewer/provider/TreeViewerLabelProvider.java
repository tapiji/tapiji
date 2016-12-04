/*******************************************************************************
 * Copyright (c) 2012 Martin Reiterer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * Martin Reiterer - initial API and implementation
 * Christian Behon - refactor from e3 to e4
 ******************************************************************************/
package org.eclipse.e4.tapiji.glossary.ui.treeviewer.provider;


import static org.eclipse.e4.tapiji.glossary.constant.TranslatorConstant.COLOR_BLACK;
import static org.eclipse.e4.tapiji.glossary.constant.TranslatorConstant.COLOR_INFO;
import static org.eclipse.e4.tapiji.glossary.constant.TranslatorConstant.FONT_ITALIC;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PreDestroy;

import org.eclipse.e4.tapiji.glossary.model.Term;
import org.eclipse.e4.tapiji.glossary.model.Translation;
import org.eclipse.e4.tapiji.glossary.model.filter.FilterInfo;
import org.eclipse.e4.tapiji.glossary.model.filter.FilterRegion;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Font;


public final class TreeViewerLabelProvider extends CellLabelProvider {

    private static final String TAG = TreeViewerLabelProvider.class.getSimpleName();
    private static final List<StyleRange> STYLE_RANGES = new ArrayList<StyleRange>();

    private final TreeViewer treeViewer;
    private final List<String> translations;
    private boolean isSearchEnabled;
    private final int referenceColumn;

    public TreeViewerLabelProvider(final TreeViewer treeViewer, final List<String> displayedTranslations, final int referenceColumn) {
        super();
        this.treeViewer = treeViewer;
        this.translations = displayedTranslations;
        this.referenceColumn = referenceColumn;
    }

    @Override
    public void update(final ViewerCell cell) {
        final Object element = cell.getElement();
        final int columnIndex = cell.getColumnIndex();
        cell.setText(this.getColumnText(element, columnIndex));

        /*if (isCrossRefRegion(cell.getText())) {
            cell.setFont(FONT_BOLD);
            cell.setBackground(COLOR_CROSSREFERENCE_BACKGROUND);
            cell.setForeground(COLOR_CROSSREFERENCE_FOREGROUND);
        } else {
            cell.setFont(getColumnFont(element, columnIndex));
            cell.setBackground(COLOR_WHITE);
            cell.setForeground(COLOR_BLACK);
        }*/

       if (isSearchEnabled) {
            searchStyle(cell, (Term) element, columnIndex);
        } else {
          //  cell.setStyleRanges(null);
        }
       /// super.update(cell);
    }

    private void searchStyle(final ViewerCell cell, final Term term, final int columnIndex) {
        if (isMatchingToPattern(term, columnIndex)) {
            final List<FilterRegion> regions = ((FilterInfo) term.getInfo()).getFoundInTranslationRanges(getColumnLocale(columnIndex));

            StyleRange style;
            for (final FilterRegion reg : regions) {
                style = new StyleRange(reg.getOffset(), reg.getLength(), COLOR_BLACK, COLOR_INFO, SWT.BOLD);
                STYLE_RANGES.add(style);
            }
           // cell.setStyleRanges(STYLE_RANGES.toArray(new StyleRange[STYLE_RANGES.size()]));
        } else {
            cell.setForeground(COLOR_BLACK);
        }
    }

    private String getColumnLocale(final int columnIndex) {
        int index = 0;
        if (columnIndex < referenceColumn) {
            index = columnIndex + 1;
        } else {
            index = columnIndex;
        }
        return translations.get(index);
    }

    public void isSearchEnabled(final boolean isSearchEnabled) {
        this.isSearchEnabled = isSearchEnabled;
    }

    private boolean isMatchingToPattern(final Term element, final int columnIndex) {
        boolean matching = false;
        final Term term = element;
        if (term.getInfo() != null) {
            matching = ((FilterInfo) term.getInfo()).hasFoundInTranslation(translations.get(columnIndex));
        }
        return matching;
    }

    protected Font getColumnFont(final Object element, final int columnIndex) {
        if (columnIndex == 0) {
            return FONT_ITALIC;
        }
        return null;
    }

    protected boolean isCrossRefRegion(final String cellText) {
        return false;
    }


    public String getColumnText(final Object element, final int columnIndex) {
        if (null != element) {
            final Term term = (Term) element;
            final Translation transl = term.getTranslation(translations.get(columnIndex));
            return transl != null ? transl.value : "";
        } else {
            return "";
        }
    }


    @PreDestroy
    public void dispose() {
        STYLE_RANGES.clear();
    }

    public static TreeViewerLabelProvider newInstance(final TreeViewer treeViewer, final List<String> displayedTranslations, final int referenceColumn) {
        return new TreeViewerLabelProvider(treeViewer, displayedTranslations, referenceColumn);
    }

}
