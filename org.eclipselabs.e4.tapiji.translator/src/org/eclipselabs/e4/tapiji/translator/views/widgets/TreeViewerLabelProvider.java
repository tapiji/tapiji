package org.eclipselabs.e4.tapiji.translator.views.widgets;


import javax.annotation.PreDestroy;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipselabs.e4.tapiji.translator.model.Term;
import org.eclipselabs.e4.tapiji.translator.views.Constants;


class TreeViewerLabelProvider extends StyledCellLabelProvider implements ITableLabelProvider {

    private final TreeViewer treeViewer;

    public TreeViewerLabelProvider(final TreeViewer treeViewer) {
        this.treeViewer = treeViewer;
    }


    @Override
    public void update(final ViewerCell cell) {
        final Object element = cell.getElement();
        final int columnIndex = cell.getColumnIndex();
        cell.setText(this.getColumnText(element, columnIndex));

        //if (isCrossRefRegion(cell.getText())) {
        //    cell.setFont(bold);
        //    cell.setBackground(info_crossref);
        //    cell.setForeground(info_crossref_foreground);
        //} else {
        cell.setFont(getColumnFont(element, columnIndex));
        cell.setBackground(Constants.COLOR_WHITE);
        //}

        /* if (isSearchEnabled()) {
             if (isMatchingToPattern(element, columnIndex)) {
                 List<StyleRange> styleRanges = new ArrayList<StyleRange>();
                 FilterInfo filterInfo = (FilterInfo) ((Term) element).getInfo();

                 for (Region reg : filterInfo
                         .getFoundInTranslationRanges(translations
                                 .get(columnIndex < referenceColumn ? columnIndex + 1
                                         : columnIndex))) {
                     styleRanges.add(new StyleRange(reg.getOffset(), reg
                             .getLength(), black, info_color, SWT.BOLD));
                 }

                 cell.setStyleRanges(styleRanges
                         .toArray(new StyleRange[styleRanges.size()]));
             } else {
                 cell.setForeground(gray);
             }
         }*/
    }

    protected Font getColumnFont(final Object element, final int columnIndex) {
        if (columnIndex == 0) {
            return Constants.FONT_ITALIC;
        }
        return null;
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
                // Translation transl = term.getTranslation(this.translations.get(columnIndex));
                return "BLA BLA"; //transl != null ? transl.value : "";
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

    public static TreeViewerLabelProvider newInstance(final TreeViewer treeViewer) {
        return new TreeViewerLabelProvider(treeViewer);
    }
}
