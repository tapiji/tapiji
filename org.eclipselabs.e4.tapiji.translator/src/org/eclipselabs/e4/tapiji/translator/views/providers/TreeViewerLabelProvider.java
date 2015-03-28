/*******************************************************************************
 * Copyright (c) 2012 Martin Reiterer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Reiterer - initial API and implementation
 *     Christian Behon - refactor from e3 to e4
 ******************************************************************************/
package org.eclipselabs.e4.tapiji.translator.views.providers;


import java.util.ArrayList;
import java.util.List;
import javax.annotation.PreDestroy;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipselabs.e4.tapiji.translator.constants.TranslatorConstants;
import org.eclipselabs.e4.tapiji.translator.model.Term;
import org.eclipselabs.e4.tapiji.translator.model.Translation;
import org.eclipselabs.e4.tapiji.translator.views.widgets.filter.FilterInfo;


public class TreeViewerLabelProvider extends StyledCellLabelProvider implements ITableLabelProvider {

    private final TreeViewer treeViewer;
    private final String[] translations;
    private boolean isSearchEnabled;

    public TreeViewerLabelProvider(final TreeViewer treeViewer, final String[] translations) {
        this.treeViewer = treeViewer;
        this.translations = translations;
    }


    @Override
    public void update(final ViewerCell cell) {
        final Object element = cell.getElement();
        final int columnIndex = cell.getColumnIndex();
        cell.setText(this.getColumnText(element, columnIndex));

        if (isCrossRefRegion(cell.getText())) {
            cell.setFont(TranslatorConstants.FONT_BOLD);
            cell.setBackground(TranslatorConstants.COLOR_CROSSREFERENCE_BACKGROUND);
            cell.setForeground(TranslatorConstants.COLOR_CROSSREFERENCE_FOREGROUND);
        } else {
            cell.setFont(getColumnFont(element, columnIndex));
            cell.setBackground(TranslatorConstants.COLOR_WHITE);
        }

        if (isSearchEnabled) {
            if (isMatchingToPattern(element, columnIndex)) {
                final List<StyleRange> styleRanges = new ArrayList<StyleRange>();
                final FilterInfo filterInfo = (FilterInfo) ((Term) element).getInfo();

                /*for (Region reg : filterInfo
                        .getFoundInTranslationRanges(translations
                                .get(columnIndex < referenceColumn ? columnIndex + 1
                                        : columnIndex))) {
                    styleRanges.add(new StyleRange(reg.getOffset(), reg
                .getLength(), TranslatorConstants.COLOR_BLACK, TranslatorConstants.COLOR_INFO, SWT.BOLD));
                }*/

                cell.setStyleRanges(styleRanges.toArray(new StyleRange[styleRanges.size()]));
            } else {
                cell.setForeground(TranslatorConstants.COLOR_GRAY);
            }
        }
    }

    public void setSearchEnabled(final boolean isSearchEnabled) {
        this.isSearchEnabled = isSearchEnabled;
    }

    protected boolean isMatchingToPattern(final Object element, final int columnIndex) {
        boolean matching = false;
        if (element instanceof Term) {
            final Term term = (Term) element;
            if (term.getInfo() != null) {
                matching = ((FilterInfo) term.getInfo()).hasFoundInTranslation(translations[columnIndex]);
            }
        }
        return matching;
    }

    protected Font getColumnFont(final Object element, final int columnIndex) {
        if (columnIndex == 0) {
            return TranslatorConstants.FONT_ITALIC;
        }
        return null;
    }

    protected boolean isCrossRefRegion(final String cellText) {
        return false;
    }

    @Override
    public Image getColumnImage(final Object element, final int columnIndex) {
        return null;
    }

    @Override
    public String getColumnText(final Object element, final int columnIndex) {
        try {
            final Term term = (Term) element;
            if (term != null) {
                final Translation transl = term.getTranslation(translations[columnIndex]);
                return transl != null ? transl.value : "";
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Focus
    public void setFocus() {
        if (treeViewer != null) {
            treeViewer.getControl().setFocus();
        }
    }

    @Override
    @PreDestroy
    public void dispose() {

    }

    public static TreeViewerLabelProvider newInstance(final TreeViewer treeViewer, final String[] translations) {
        return new TreeViewerLabelProvider(treeViewer, translations);
    }
}
