package org.eclipse.e4.tapiji.git.ui.part.middle;


import java.util.List;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.tapiji.git.model.commitlog.GitLog;
import org.eclipse.e4.tapiji.git.ui.util.UIUtil;
import org.eclipse.e4.tapiji.utils.FontUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.ocpsoft.prettytime.PrettyTime;


@Creatable
public class LogView {

    private static final int[] COLUMN_ALIGNMENTS_LOGS = new int[] {SWT.LEFT, SWT.RIGHT};
    private static final int[] COLUMN_WEIGHTS_LOGS = new int[] {100, 30};

    public void createView(List<GitLog> logs, Composite composite, Label lblHeader, PrettyTime prettyTime) {
        lblHeader = new Label(composite, SWT.NONE | SWT.WRAP);
        lblHeader.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblHeader.setFont(FontUtils.createFont(lblHeader, "Segoe UI", 10, SWT.BOLD));
        lblHeader.setText("Log overview");

        Composite layoutComposite = new Composite(composite, SWT.NONE);
        layoutComposite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 1, 1));

        Table table = new Table(layoutComposite, SWT.FULL_SELECTION);
        table.setHeaderVisible(false);
        table.setLinesVisible(true);
        UIUtil.createColumns(2, table, COLUMN_ALIGNMENTS_LOGS);
        logs.stream().forEach(log -> {
            TableItem item = new TableItem(table, SWT.NONE);
            item.setText(0, log.getShortMessage());
            item.setText(1, prettyTime.format(log.getCommitTime()));
        });
        layoutComposite.setLayout(UIUtil.setColumnWeights(table, COLUMN_WEIGHTS_LOGS));
    }
}
