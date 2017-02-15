package org.eclipse.e4.tapiji.git.ui.unstaged;


import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.tapiji.git.model.exception.GitServiceException;
import org.eclipse.e4.tapiji.git.model.file.GitFile;
import org.eclipse.e4.tapiji.git.ui.constants.UIEventConstants;
import org.eclipse.e4.tapiji.git.ui.handler.window.PerpectiveSwitchHandler;
import org.eclipse.e4.tapiji.resource.ITapijiResourceProvider;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;


public class UnstagedView implements UnstagedContract.View {

    @Inject
    UISynchronize sync;

    @Inject
    UnstagedPresenter presenter;

    @Inject
    IEventBroker eventBroker;

    @Inject
    ITapijiResourceProvider resourceProvider;

    private Composite parent;

    private Table table;

    private Label lblUnstaged;

    @PostConstruct
    public void createPartControl(final Composite parent) {
        presenter.setView(this);
        this.parent = parent;
        parent.setLayout(new GridLayout(2, false));

        lblUnstaged = new Label(parent, SWT.NONE);
        lblUnstaged.setFont(FontUtils.createFont(lblUnstaged, "Segoe UI", 10, SWT.BOLD));
        lblUnstaged.setText("Unstaged Files");

        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));
        composite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));

        Button btnDiscard = new Button(composite, SWT.NONE);
        btnDiscard.setBounds(0, 0, 75, 25);
        btnDiscard.setText("Discard all changes");
        btnDiscard.addListener(SWT.MouseDown, listener -> {
            boolean result = MessageDialog
                .openConfirm(parent.getShell(), "Discard all changes?", "This will discard all staged and unstaged changes, including new untracked files.");
            if (result) {
                presenter.discardChanges();
            }
        });

        Button btnStageAll = new Button(composite, SWT.NONE);
        btnStageAll.setBounds(0, 0, 75, 25);
        btnStageAll.setText("Stage all files");
        btnStageAll.addListener(SWT.MouseDown, listener -> {
            presenter.stageChanges();
 
        });

        table = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 2));
        table.setLinesVisible(false);
        table.setHeaderVisible(false);
    }

    @Inject
    @Optional
    public void updateView(@UIEventTopic(UIEventConstants.TOPIC_RELOAD_UNSTAGE_VIEW) String payload) {
        presenter.loadUnCommittedChanges();
    }
    
    @Override
    public void showUnCommittedChanges(List<GitFile> files) {
        sync.asyncExec(() -> {
            table.removeAll();
            table.clearAll();

            if (!files.isEmpty()) {
                files.stream().forEach(file -> {
                    TableItem item = new TableItem(table, SWT.NONE);
                    item.setText(file.getName());
                    if (file.getImage() != null) {
                        item.setImage(resourceProvider.loadImage(file.getImage()));
                    }
                });
                lblUnstaged.setText(String.format("Unstaged Files (%1$d)", files.size()));
            } else {
                lblUnstaged.setText("Unstaged Files");
            }
            parent.layout();
        });
    }

    @Override
    public void showError(GitServiceException exception) {
        sync.asyncExec(() -> {
            parent.setCursor(new Cursor(parent.getDisplay(), SWT.CURSOR_ARROW));
            MessageDialog.openError(parent.getShell(), "Error: ", exception.getMessage());
        });
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
    public void sendUIEvent(String topic) {
        sync.asyncExec(() -> eventBroker.post(topic, ""));
    }
}
