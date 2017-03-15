package org.eclipse.e4.tapiji.git.ui.part.right.unstaged;


import java.util.List;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.tapiji.git.model.exception.GitServiceException;
import org.eclipse.e4.tapiji.git.model.file.GitFile;
import org.eclipse.e4.tapiji.git.ui.constant.UIEventConstants;
import org.eclipse.e4.tapiji.resource.ITapijiResourceProvider;
import org.eclipse.e4.tapiji.resource.TapijiResourceConstants;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
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

    private Composite compositeHeader;

    @PostConstruct
    public void createPartControl(final Composite parent) {
        presenter.setView(this);
        this.parent = parent;
        parent.setLayout(new GridLayout(2, false));

        compositeHeader = new Composite(parent, SWT.NONE);
        compositeHeader.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        compositeHeader.setLayout(new GridLayout(2, false));

        Composite layoutComposite = new Composite(parent, SWT.NONE);
        layoutComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        TableColumnLayout tableLayout = new TableColumnLayout();
        layoutComposite.setLayout(tableLayout);

        table = new Table(layoutComposite, SWT.BORDER | SWT.FULL_SELECTION);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        table.setLinesVisible(false);
        table.setHeaderVisible(false);
        table.addListener(SWT.Selection, listener -> {
            if (table.getSelection()[0].getData() != null) {
                eventBroker.post(UIEventConstants.SWITCH_CONTENT_VIEW, table.getSelection()[0].getData());
            }
        });

        TableColumn column = new TableColumn(table, SWT.LEFT);
        column.pack();
        tableLayout.setColumnData(column, new ColumnWeightData(100, 100));
    }

    @Inject
    @Optional
    public void updateView(@UIEventTopic(UIEventConstants.TOPIC_RELOAD_VIEW) String empty) {
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
                    item.setData(file);
                    if (file.getImage() != null) {
                        item.setImage(resourceProvider.loadImage(file.getImage()));
                    }
                });

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
    public void sendUIEvent(String topic, String payload) {
        sync.asyncExec(() -> eventBroker.post(topic, payload));
    }

    @Override
    public void showUnstageHeader(int fileCnt) {
        sync.syncExec(() -> {
            Stream.of(compositeHeader.getChildren()).forEach(Control::dispose);

            Label lblUnstaged = new Label(compositeHeader, SWT.NONE);
            lblUnstaged.setFont(FontUtils.createFont(lblUnstaged, "Segoe UI", 10, SWT.BOLD));
            if (fileCnt >= 1) {
                lblUnstaged.setText(String.format("Unstaged Files (%1$d)", fileCnt));
            } else {
                lblUnstaged.setText("Unstaged Files");
            }

            Composite composite = new Composite(compositeHeader, SWT.NONE);
            GridLayout gl = new GridLayout(2, false);
            gl.marginWidth = 0;
            gl.verticalSpacing = 0;
            composite.setLayout(gl);
            composite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));

            Button btnDiscard = new Button(composite, SWT.NONE);
            btnDiscard.setText("Discard all changes");
            btnDiscard.addListener(SWT.MouseDown, listener -> {
                boolean result = MessageDialog
                    .openConfirm(parent.getShell(), "Discard all changes?", "This will discard all staged and unstaged changes, including new untracked files.");
                if (result) {
                    presenter.onClickDiscardChanges();
                }
            });

            Button btnStageAll = new Button(composite, SWT.NONE);
            btnStageAll.setText("Stage all files");
            btnStageAll.addListener(SWT.MouseDown, listener -> presenter.onClickStageChanges());

            compositeHeader.layout(true, true);
            eventBroker.post(UIEventConstants.TOPIC_SHOW_HIDE_UNSTAGE_BTN, true);
        });
    }

    @Override
    public void showConflictHeader(int fileCnt) {
        sync.syncExec(() -> {
            Stream.of(compositeHeader.getChildren()).forEach(Control::dispose);
            Label lblNewLabel = new Label(compositeHeader, SWT.NONE);
            lblNewLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
            lblNewLabel.setImage(resourceProvider.loadImage(TapijiResourceConstants.IMG_MERGE_WARNING_32x32));

            Label lblUnstaged = new Label(compositeHeader, SWT.NONE);
            lblUnstaged.setFont(FontUtils.createFont(lblUnstaged, "Segoe UI", 10, SWT.BOLD));
            lblUnstaged.setText(String.format("Conflicted Files (%1$d)", fileCnt));

            Button btnStageAll = new Button(compositeHeader, SWT.NONE);
            btnStageAll.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
            btnStageAll.setText("Mark all resolved");
            btnStageAll.addListener(SWT.MouseDown, listener -> presenter.onClickStageChanges());
            compositeHeader.layout(true, true);
            eventBroker.post(UIEventConstants.TOPIC_SHOW_HIDE_UNSTAGE_BTN, false);
        });
    }
}
