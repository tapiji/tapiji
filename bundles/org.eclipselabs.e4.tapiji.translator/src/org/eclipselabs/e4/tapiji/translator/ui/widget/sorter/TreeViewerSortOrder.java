/*******************************************************************************
 * Copyright (c) 2012 Martin Reiterer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * Martin Reiterer - initial API and implementation
 ******************************************************************************/
package org.eclipselabs.e4.tapiji.translator.ui.widget.sorter;


import java.util.List;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipselabs.e4.tapiji.translator.model.Term;
import org.eclipselabs.e4.tapiji.translator.model.Translation;


public final class TreeViewerSortOrder extends ViewerSorter {

    private StructuredViewer viewer;
    private SortInfo sortInfo;
    private final int referenceCol;
    private final List<String> translations;

    public TreeViewerSortOrder(final StructuredViewer viewer, final SortInfo sortInfo, final int referenceCol,
                    final List<String> translations) {
        super();
        this.viewer = viewer;
        this.referenceCol = referenceCol;
        this.translations = translations;

        if (sortInfo != null) {
            this.sortInfo = sortInfo;
        } else {
            this.sortInfo = new SortInfo();
        }
    }

    public StructuredViewer getViewer() {
        return viewer;
    }

    public void setViewer(final StructuredViewer viewer) {
        this.viewer = viewer;
    }

    public SortInfo getSortInfo() {
        return sortInfo;
    }

    public void setSortInfo(final SortInfo sortInfo) {
        this.sortInfo = sortInfo;
    }

    @Override
    public int compare(final Viewer viewer, final Object e1, final Object e2) {
        try {
            if (!((e1 instanceof Term) && (e2 instanceof Term))) {
                return super.compare(viewer, e1, e2);
            }

            if (sortInfo == null) {
                return 0;
            }

            final Term comp1 = (Term) e1;
            final Term comp2 = (Term) e2;

            int result = 0;

            if (sortInfo.getColumnIndex() == 0) {
                final Translation transComp1 = comp1.getTranslation(translations.get(referenceCol));
                final Translation transComp2 = comp2.getTranslation(translations.get(referenceCol));
                if ((transComp1 != null) && (transComp2 != null)) {
                    result = transComp1.value.compareTo(transComp2.value);
                }
            } else {
                final int col = sortInfo.getColumnIndex() < referenceCol ? sortInfo.getColumnIndex() + 1 : sortInfo
                                .getColumnIndex();
                Translation transComp1 = comp1.getTranslation(translations.get(col));
                Translation transComp2 = comp2.getTranslation(translations.get(col));

                if (transComp1 == null) {
                    transComp1 = Translation.newInstance();
                }
                if (transComp2 == null) {
                    transComp2 = Translation.newInstance();
                }
                result = transComp1.value.compareTo(transComp2.value);
            }

            return result * (sortInfo.isDescending() ? -1 : 1);
        } catch (final Exception e) {
            return 0;
        }
    }

}
