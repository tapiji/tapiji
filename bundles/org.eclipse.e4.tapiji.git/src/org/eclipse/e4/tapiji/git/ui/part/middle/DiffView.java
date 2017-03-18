package org.eclipse.e4.tapiji.git.ui.part.middle;


import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.tapiji.git.model.diff.DiffHunk;
import org.eclipse.e4.tapiji.git.ui.util.UIUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;


@Creatable
public class DiffView {

    private static final int[] COLUMN_ALIGNMENTS_DIFF = new int[] {SWT.CENTER, SWT.CENTER, SWT.LEFT};
    private static final int[] COLUMN_WEIGHTS_DIFF = new int[] {10, 10, 100};

    public void createView(Composite composite, DiffHunk section) {
        Label lblDiffHeader = new Label(composite, SWT.NONE);
        lblDiffHeader.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 1, 1));
        lblDiffHeader.setText(section.getHeader());

        Composite layoutComposite = new Composite(composite, SWT.NONE);
        layoutComposite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 1, 1));

        Table table = new Table(layoutComposite, SWT.FULL_SELECTION);
        table.setHeaderVisible(false);
        table.setLinesVisible(false);

        UIUtil.createColumns(3, table, COLUMN_ALIGNMENTS_DIFF);

        section.getLines().stream().forEach(line -> {
            TableItem item = new TableItem(table, SWT.NONE);
            item.setText(0, line.getNumberLeft());
            item.setText(1, line.getNumberRight());
            item.setText(2, line.getText());
            UIUtil.colorizeLineBackground(line, item);
        });
        layoutComposite.setLayout(UIUtil.setColumnWeights(table, COLUMN_WEIGHTS_DIFF));
    }
}
