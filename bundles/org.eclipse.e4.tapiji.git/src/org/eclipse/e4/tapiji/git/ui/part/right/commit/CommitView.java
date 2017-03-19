package org.eclipse.e4.tapiji.git.ui.part.right.commit;


import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.tapiji.git.model.exception.GitException;
import org.eclipse.e4.tapiji.git.ui.constant.UIEventConstants;
import org.eclipse.e4.tapiji.utils.FontUtils;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


public class CommitView implements CommitContract.View {

    @Inject
    IEventBroker eventBroker;

    @Inject
    CommitPresenter presenter;

    @Inject
    UISynchronize sync;

    private Button btnCommit;
    private Text txtSummary;
    private Composite parent;
    private Text txtDescription;

    private boolean stagedFilesAvailable;

    @PostConstruct
    public void createPartControl(final Composite parent) {
        this.parent = parent;
        presenter.setView(this);
        GridLayout glParent = new GridLayout(1, false);
        glParent.horizontalSpacing = 0;
        parent.setLayout(glParent);

        Label lblCommit = new Label(parent, SWT.NONE);
        lblCommit.setFont(FontUtils.createFont(lblCommit, "Segoe UI", 11, SWT.BOLD));
        lblCommit.setText("Commit Message");

        GridLayout glComposite = new GridLayout(1, false);
        glComposite.marginHeight = 0;
        glComposite.marginWidth = 0;
        glComposite.horizontalSpacing = 0;
        glComposite.verticalSpacing = 0;

        Composite txtComposite = new Composite(parent, SWT.BORDER);
        txtComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        txtComposite.setLayout(glComposite);

        GridData gdTxtSummary = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        gdTxtSummary.heightHint = 25;

        txtSummary = new Text(txtComposite, SWT.NONE);
        txtSummary.setMessage("Summary");
        txtSummary.addListener(SWT.CHANGED, listener -> presenter.checkTextSummary(txtSummary.getText()));
        txtSummary.setFont(FontUtils.createFont(txtSummary, "Segoe UI", 10, SWT.BOLD));
        txtSummary.setLayoutData(gdTxtSummary);

        txtDescription = new Text(txtComposite, SWT.MULTI | SWT.NONE);
        txtDescription.setFont(FontUtils.createFont(txtDescription, "Segoe UI", 8, SWT.BOLD));
        txtDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        btnCommit = new Button(parent, SWT.NONE);
        btnCommit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        btnCommit.addListener(SWT.MouseDown, listener -> {
            presenter.commitChanges(txtSummary.getText(), txtDescription.getText());
        });
        disableCommitButton();
    }

    @Override
    public void disableCommitButton() {
        btnCommit.setEnabled(false);
        if (!stagedFilesAvailable && !txtSummary.getText().isEmpty()) {
            btnCommit.setText("Stage files/changes to commit");
        } else {
            btnCommit.setText("Type a message to commit");
        }
    }

    @Override
    public void enableCommitButton() {
        if (stagedFilesAvailable) {
            btnCommit.setEnabled(true);
            btnCommit.setText("Commit");
        } else {
            btnCommit.setText("Stage files/changes to commit");
        }
    }

    @Override
    public void sendUIEvent(String topic, String content) {
        sync.asyncExec(() -> eventBroker.post(topic, content));
    }

    @Inject
    @Optional
    public void stagedUnstaged(@UIEventTopic(UIEventConstants.TOPIC_STAGED_UNSTAGED) String payload) {
        if (payload.equals("staged")) {
            stagedFilesAvailable = true;
            presenter.checkTextSummary(txtSummary.getText());
        } else {
            stagedFilesAvailable = false;
            disableCommitButton();
        }
    }

    @Override
    public void setCursorWaitVisibility(boolean visibility) {
        sync.asyncExec(() -> {
            if (visibility) {
                parent.setCursor(new Cursor(parent.getDisplay(), SWT.CURSOR_WAIT));
            } else {
                parent.setCursor(new Cursor(parent.getDisplay(), SWT.CURSOR_ARROW));
            }
        });
    }

    @Override
    public void resetCommitView() {
        sync.asyncExec(() -> {
            txtSummary.setText("");
            txtDescription.setText("");
            disableCommitButton();
        });
    }

    @Override
    public void showError(GitException exception) {
        sync.asyncExec(() -> {
            parent.setCursor(new Cursor(parent.getDisplay(), SWT.CURSOR_ARROW));
            MessageDialog.openError(parent.getShell(), "Error: ", exception.getMessage());
        });
    }
}
