package org.eclipse.e4.tapiji.git.ui.panel.right.staged;


import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.tapiji.git.model.exception.GitServiceException;
import org.eclipse.e4.tapiji.git.model.file.GitFile;
import org.eclipse.e4.tapiji.git.ui.constants.UIEventConstants;
import org.eclipse.e4.tapiji.resource.ITapijiResourceProvider;
import org.eclipse.e4.tapiji.utils.FontUtils;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;


public class StagedView implements StagedContract.View {

    @Inject
    UISynchronize sync;

    @Inject
    StagedPresenter presenter;

    @Inject
    IEventBroker eventBroker;

    @Inject
    ITapijiResourceProvider resourceProvider;

    private Label lblStagedFiles;

    private Table table;

    private Composite parent;

    @PostConstruct
    public void createPartControl(final Composite parent) {
        presenter.setView(this);
        this.parent = parent;
        parent.setLayout(new GridLayout(2, false));

        lblStagedFiles = new Label(parent, SWT.NONE);
        lblStagedFiles.setFont(FontUtils.createFont(lblStagedFiles, "Segoe UI", 10, SWT.BOLD));
        lblStagedFiles.setText("Staged Files");

        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));
        composite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));

        Button btnUnstageChanges = new Button(composite, SWT.NONE);
        btnUnstageChanges.setText("Unstage all changes");
        btnUnstageChanges.addListener(SWT.MouseDown, event -> presenter.unstageChanges());

        Composite layoutComposite = new Composite(parent, SWT.NONE);
        layoutComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        TableColumnLayout tableLayout = new TableColumnLayout();
        layoutComposite.setLayout(tableLayout);

        table = new Table(layoutComposite, SWT.BORDER | SWT.FULL_SELECTION);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 2));
        table.setHeaderVisible(false);
        table.setLinesVisible(false);

        TableColumn column = new TableColumn(table, SWT.LEFT);
        column.pack();
        tableLayout.setColumnData(column, new ColumnWeightData(100, 100));

    }

    @Inject
    @Optional
    public void updateView(@UIEventTopic(UIEventConstants.TOPIC_RELOAD_STAGE_VIEW) String payload) {
        presenter.loadStagedFiles();
    }

    @Override
    public void showError(GitServiceException exception) {
        sync.asyncExec(() -> {
            parent.setCursor(new Cursor(parent.getDisplay(), SWT.CURSOR_ARROW));
            MessageDialog.openError(parent.getShell(), "Error: ", exception.getMessage());
        });
    }

    @Override
    public void showStagedChanges(List<GitFile> files) {
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
                lblStagedFiles.setText(String.format("Staged Files (%1$d)", files.size()));
                sendUIEvent(UIEventConstants.TOPIC_ON_FILES_STAGED);
            } else {
                lblStagedFiles.setText("Staged Files");
                sendUIEvent(UIEventConstants.TOPIC_ON_FILES_UNSTAGED);
            }
            parent.setCursor(new Cursor(parent.getDisplay(), SWT.CURSOR_ARROW));
            parent.layout();
        });
    }

    @Override
    public void sendUIEvent(String topic) {
        sync.asyncExec(() -> eventBroker.post(topic, null));
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
}
