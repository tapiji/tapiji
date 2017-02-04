package org.eclipse.e4.tapiji.git.ui.unstaged;


import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;


public class UnstagedView implements UnstagedContract.View {

    @Inject
    UnstagedPresenter presenter;

    @PostConstruct
    public void createPartControl(final Composite parent) {
        parent.setLayout(new GridLayout(2, false));

        Label lblNewLabel = new Label(parent, SWT.NONE);
        //lblNewLabel.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
        lblNewLabel.setText("Unstaged Files (2)");

        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));
        composite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));

        Button btnNewButton_1 = new Button(composite, SWT.NONE);
        btnNewButton_1.setBounds(0, 0, 75, 25);
        btnNewButton_1.setText("Discard all changes");

        Button btnNewButton = new Button(composite, SWT.NONE);
        btnNewButton.setBounds(0, 0, 75, 25);
        btnNewButton.setText("Stage all files");

        Table table = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 2));
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setHeaderVisible(false);

        TableColumn tblclmnFile = new TableColumn(table, SWT.NONE);
        tblclmnFile.setWidth(100);

    }
}
