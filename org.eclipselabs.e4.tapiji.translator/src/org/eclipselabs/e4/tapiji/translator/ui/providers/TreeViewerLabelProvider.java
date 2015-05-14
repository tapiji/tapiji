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
package org.eclipselabs.e4.tapiji.translator.ui.providers;


import static org.eclipselabs.e4.tapiji.translator.constants.TranslatorConstants.COLOR_BLACK;
import static org.eclipselabs.e4.tapiji.translator.constants.TranslatorConstants.COLOR_CROSSREFERENCE_BACKGROUND;
import static org.eclipselabs.e4.tapiji.translator.constants.TranslatorConstants.COLOR_CROSSREFERENCE_FOREGROUND;
import static org.eclipselabs.e4.tapiji.translator.constants.TranslatorConstants.COLOR_GRAY;
import static org.eclipselabs.e4.tapiji.translator.constants.TranslatorConstants.COLOR_INFO;
import static org.eclipselabs.e4.tapiji.translator.constants.TranslatorConstants.COLOR_WHITE;
import static org.eclipselabs.e4.tapiji.translator.constants.TranslatorConstants.FONT_BOLD;
import static org.eclipselabs.e4.tapiji.translator.constants.TranslatorConstants.FONT_ITALIC;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PreDestroy;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Font;
import org.eclipselabs.e4.tapiji.translator.model.Term;
import org.eclipselabs.e4.tapiji.translator.model.Translation;
import org.eclipselabs.e4.tapiji.translator.ui.treeviewer.filter.FilterInfo;


public final class TreeViewerLabelProvider extends StyledCellLabelProvider {

    private static final String TAG = TreeViewerLabelProvider.class.getSimpleName();
    private static final List<StyleRange> STYLE_RANGES = new ArrayList<StyleRange>();

    private final TreeViewer treeViewer;
    private final String[] translations;
    private boolean isSearchEnabled;
    private final int referenceColumn;

    public TreeViewerLabelProvider(final TreeViewer treeViewer, final String[] translations) {
        super();
        this.treeViewer = treeViewer;
        this.translations = translations;
        this.referenceColumn = 0;
    }

    @Override
    public void update(final ViewerCell cell) {
        final Object element = cell.getElement();
        final int columnIndex = cell.getColumnIndex();
        cell.setText(this.getColumnText(element, columnIndex));

        if (isCrossRefRegion(cell.getText())) {
            cell.setFont(FONT_BOLD);
            cell.setBackground(COLOR_CROSSREFERENCE_BACKGROUND);
            cell.setForeground(COLOR_CROSSREFERENCE_FOREGROUND);
        } else {
            cell.setFont(getColumnFont(element, columnIndex));
            cell.setBackground(COLOR_WHITE);
            cell.setForeground(COLOR_BLACK);
        }

        if (isSearchEnabled) {
            searchStyle(cell, (Term) element, columnIndex);
        } else {
            cell.setStyleRanges(null);
        }
        super.update(cell);
    }

    private void searchStyle(final ViewerCell cell, final Term term, final int columnIndex) {
        if (isMatchingToPattern(term, columnIndex)) {
            final List<Region> regions = ((FilterInfo) term.getInfo())
                            .getFoundInTranslationRanges(getColumnLocale(columnIndex));

            STYLE_RANGES.clear();
            StyleRange style;
            for (final Region reg : regions) {
                style = new StyleRange(reg.getOffset(), reg.getLength(), COLOR_BLACK, COLOR_INFO, SWT.BOLD);
                STYLE_RANGES.add(style);
            }
            cell.setStyleRanges(STYLE_RANGES.toArray(new StyleRange[STYLE_RANGES.size()]));
        } else {
            cell.setForeground(COLOR_GRAY);
        }
    }

    private String getColumnLocale(final int columnIndex) {
        int index = 0;
        if (columnIndex < referenceColumn) {
            index = columnIndex + 1;
        } else {
            index = columnIndex;
        }
        return translations[index];
    }

    public void isSearchEnabled(final boolean isSearchEnabled) {
        this.isSearchEnabled = isSearchEnabled;
    }

    private boolean isMatchingToPattern(final Term element, final int columnIndex) {
        boolean matching = false;
        final Term term = element;
        if (term.getInfo() != null) {
            matching = ((FilterInfo) term.getInfo()).hasFoundInTranslation(translations[columnIndex]);
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
        // TODO
        return false;
    }


    public String getColumnText(final Object element, final int columnIndex) {
        if (null != element) {
            final Term term = (Term) element;
            final Translation transl = term.getTranslation(translations[columnIndex]);
            return transl != null ? transl.value : "";
        } else {
            return "";
        }
    }


    @Override
    @PreDestroy
    public void dispose() {
        STYLE_RANGES.clear();
    }

    public static TreeViewerLabelProvider newInstance(final TreeViewer treeViewer, final String[] translations) {
        return new TreeViewerLabelProvider(treeViewer, translations);
    }
}
