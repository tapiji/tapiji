package org.eclipselabs.e4.tapiji.translator.views;


import java.util.List;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipselabs.e4.tapiji.logger.Log;
import org.eclipselabs.e4.tapiji.translator.model.Term;
import org.eclipselabs.e4.tapiji.translator.model.Translation;
import org.eclipselabs.e4.tapiji.translator.views.widgets.filter.FilterInfo;
import org.eclipselabs.e4.tapiji.utils.FontUtils;


public final class ViewLabelProvider extends StyledCellLabelProvider implements ITableLabelProvider {


    private final TreeViewer treeViewer;
    protected int referenceColumn = 0;
    protected List<String> translations;

    public static final Color gray = FontUtils.getSystemColor(SWT.COLOR_GRAY);
    public static final Color black = FontUtils.getSystemColor(SWT.COLOR_BLACK);
    public static final Color info_color = FontUtils.getSystemColor(SWT.COLOR_YELLOW);
    public static final Color info_crossref = FontUtils.getSystemColor(SWT.COLOR_INFO_BACKGROUND);
    public static final Color info_crossref_foreground = FontUtils.getSystemColor(SWT.COLOR_INFO_FOREGROUND);
    public static final Color transparent = FontUtils.getSystemColor(SWT.COLOR_WHITE);

    public static final Font FONT_BOLD = FontUtils.createFont(SWT.BOLD);
    public static final Font FONT_ITALIC = FontUtils.createFont(SWT.ITALIC);
    public static final Font FONT_NORMAL = FontUtils.createFont(SWT.NORMAL);
    private static final String TAG = ViewLabelProvider.class.getSimpleName();

    public ViewLabelProvider(final TreeViewer treeViewer, final int referenceColumn, final List<String> translations) {
        this.treeViewer = treeViewer;
        this.referenceColumn = referenceColumn;
        this.translations = translations;
    }

    @Override
    public String getColumnText(final Object element, final int columnIndex) {
        final Term term = (Term) element;
        if (term != null) {
            final Translation transl = term.getTranslation(this.translations.get(columnIndex));
            return transl != null ? transl.value : "";
        }
        return "-";
    }



    protected Font getColumnFont(Object element, int columnIndex) {
        if (columnIndex == 0) {
            return FONT_ITALIC;
        }
        return FONT_NORMAL;
    }


    @Override
    public void update(ViewerCell cell) {
        Object element = cell.getElement();
        int columnIndex = cell.getColumnIndex();
        cell.setText(this.getColumnText(element, columnIndex));

        if (isCrossRefRegion(cell.getText())) {
            cell.setFont(FONT_BOLD);
              cell.setBackground(info_crossref);
              cell.setForeground(info_crossref_foreground);
          } else {
              cell.setFont(this.getColumnFont(element, columnIndex));
              cell.setBackground(transparent);
          }

        /*if (isSearchEnabled()) {
            if (isMatchingToPattern(element, columnIndex)) {
                List<StyleRange> styleRanges = new ArrayList<StyleRange>();
                FilterInfo filterInfo = (FilterInfo) ((Term) element).getInfo();

                for (Region reg : filterInfo.getFoundInTranslationRanges(translations
                                .get(columnIndex < referenceColumn ? columnIndex + 1 : columnIndex))) {
                    styleRanges.add(new StyleRange(reg.getOffset(), reg.getLength(), black, info_color, SWT.BOLD));
                }

                cell.setStyleRanges(styleRanges.toArray(new StyleRange[styleRanges.size()]));
            } else {
                cell.setForeground(gray);
            }
        }*/
    }



    protected boolean isMatchingToPattern(Object element, int columnIndex) {
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

    protected boolean isCrossRefRegion(String cellText) {
        /*   if (selectedItem != null) {
             for (IMessage entry : selectedItem.getMessagesBundleGroup().getMessages(selectedItem.getMessageKey())) {
               String value = entry.getValue();
               String[] subValues = value.split("[\\s\\p{Punct}]+");
               for (String v : subValues) {
                 if (v.trim().equalsIgnoreCase(cellText.trim()))
                   return true;
               }
             }*/
        return false;
        }

    @Inject
    public void setSelection(@Named(IServiceConstants.ACTIVE_SELECTION) @Optional Object myObject) {
        Log.d(TAG, "MyObject " + myObject);
    }

    @Focus
    public void Focus() {
        treeViewer.getControl().setFocus();
    }


    @Override
    @PreDestroy
    public void dispose() {

    }

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        // TODO Auto-generated method stub
        return null;
    }
}
