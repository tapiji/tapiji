package org.eclipse.e4.tapiji.git.ui.unstaged;


import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.tapiji.git.model.GitServiceException;
import org.eclipse.e4.tapiji.git.model.GitStatus;
import org.eclipse.e4.tapiji.git.ui.handler.window.PerpectiveSwitchHandler;
import org.eclipse.e4.tapiji.logger.Log;
import org.eclipse.e4.tapiji.resource.ITapijiResourceProvider;
import org.eclipse.e4.tapiji.resource.TapijiResourceConstants;
import org.eclipse.e4.ui.di.UIEventTopic;
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
    UnstagedPresenter presenter;
    @Inject
    IEventBroker eventBroker;

    @Inject
    ITapijiResourceProvider resourceProvider;
    private Composite parent;
    private Table table;

    @PostConstruct
    public void createPartControl(final Composite parent) {
        presenter.setView(this);
        this.parent = parent;
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

        table = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 2));
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setHeaderVisible(false);

        //TableColumn tblclmnFile = new TableColumn(table, SWT.NONE);
        //tblclmnFile.setWidth(100);

    }

    @Inject
    @Optional
    public void closeHandler(@UIEventTopic(PerpectiveSwitchHandler.TOPIC_UPDATE_FILES) String payload) {
        Log.d("EVENT", "asddsdsd");
        parent.setCursor(new Cursor(parent.getDisplay(), SWT.CURSOR_WAIT));
        presenter.loadUnCommittedChanges();
    }

    @Override
    public void showUnCommittedChanges(Map<GitStatus, Set<String>> result) {
        table.removeAll();
        parent.setCursor(new Cursor(parent.getDisplay(), SWT.CURSOR_ARROW));
        result.entrySet().stream().forEach(map -> {

            for (String str : map.getValue()) {

                TableItem item = new TableItem(table, SWT.NONE);
                item.setText(str);
                if (map.getKey() == GitStatus.ADDED) {
                    item.setImage(resourceProvider.loadImage(TapijiResourceConstants.IMG_GIT_ADDED));
                } else if (map.getKey() == GitStatus.MODIFIED) {
                    item.setImage(resourceProvider.loadImage(TapijiResourceConstants.IMG_GIT_MODIFIED));
                }
            }
        });
        Log.d("EVENT", "asddsdsd" + result.toString());
    }

    @Override
    public void showError(GitServiceException exception) {
        parent.setCursor(new Cursor(parent.getDisplay(), SWT.CURSOR_ARROW));
        MessageDialog.openError(parent.getShell(), "Error: ", exception.getMessage());
    }

}
