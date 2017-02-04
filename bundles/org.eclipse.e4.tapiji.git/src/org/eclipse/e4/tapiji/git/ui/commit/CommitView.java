package org.eclipse.e4.tapiji.git.ui.commit;


import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


public class CommitView implements CommitContract.View {

    @Inject
    CommitPresenter presenter;

    @PostConstruct
    public void createPartControl(final Composite parent) {

        parent.setLayout(new GridLayout(1, false));

        Label lblCommitMessage = new Label(parent, SWT.NONE);
        //lblCommitMessage.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
        lblCommitMessage.setText("Commit Message");

        Text txtSdsd = new Text(parent, SWT.BORDER | SWT.SEARCH);
        //txtSdsd.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.BOLD));
        txtSdsd.setText("sdsd");
        txtSdsd.setToolTipText("");

        txtSdsd.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Text text_1 = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
        text_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        Button btnNewButton = new Button(parent, SWT.NONE);
        btnNewButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        btnNewButton.setText("Commmit");
    }
}
