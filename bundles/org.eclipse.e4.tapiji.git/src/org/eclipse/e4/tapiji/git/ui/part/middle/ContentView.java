package org.eclipse.e4.tapiji.git.ui.part.middle;


import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.tapiji.git.model.commitlog.GitLog;
import org.eclipse.e4.tapiji.git.model.diff.DiffFile;
import org.eclipse.e4.tapiji.git.model.exception.GitException;
import org.eclipse.e4.tapiji.git.model.file.GitFile;
import org.eclipse.e4.tapiji.git.model.file.GitFileStatus;
import org.eclipse.e4.tapiji.git.ui.constant.UIEventConstants;
import org.eclipse.e4.tapiji.utils.FontUtils;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.ocpsoft.prettytime.PrettyTime;


public class ContentView implements ContentContract.View {

    public static final Color GREEN = new Color(Display.getCurrent(), 144, 238, 144);
    public static final Color RED = new Color(Display.getCurrent(), 240, 128, 128);
    public static final Color ORANGE = new Color(Display.getCurrent(), 226, 189, 51);

    private ContentPresenter presenter;
    private UISynchronize sync;
    private IEventBroker eventBroker;
    private MergeView mergeView;
    private DiffView diffView;
    private Shell shell;
    private LogView logView;

    private ScrolledComposite scrollView;
    private Composite composite;
    private Label lblHeader;
    private Composite parent;
    private PrettyTime prettyTime = new PrettyTime(new Locale("de"));

    @PostConstruct
    public void createPartControl(final Composite parent, LogView logView, DiffView diffView, MergeView mergeView, IEventBroker eventBroker, UISynchronize sync, ContentPresenter presenter, Shell shell) {
        this.parent = parent;
        this.logView = logView;
        this.diffView = diffView;
        this.mergeView = mergeView;
        this.eventBroker = eventBroker;
        this.sync = sync;
        this.presenter = presenter;
        this.shell = shell;

        presenter.setView(this);
        parent.setLayout(new GridLayout(1, false));

        scrollView = new ScrolledComposite(parent, SWT.V_SCROLL);
        scrollView.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        scrollView.setExpandHorizontal(true);
        scrollView.setExpandVertical(true);
        scrollView.setAlwaysShowScrollBars(true);

        composite = new Composite(scrollView, SWT.NONE);
        composite.setLayout(new GridLayout(1, false));
        scrollView.setContent(composite);

        presenter.loadLogs();
        presenter.watchService();
    }

    @Inject
    @Optional
    public void showContentView(@UIEventTopic(UIEventConstants.SWITCH_CONTENT_VIEW) GitFile file) {
        clearScrollView();
        if (file == null) {
            presenter.loadLogs();
        } else {
            if (file.getStatus() == GitFileStatus.CONFLICT) {
                presenter.loadFileMergeDiff(file.getName(), GitFileStatus.CONFLICT);
            } else {
                presenter.loadFileContentDiff(file.getName());
            }
        }
    }

    public void registerWatchService(@UIEventTopic(UIEventConstants.TOPIC_REGISTER_WATCH_SERVICE) String payload) {
        presenter.watchService();
    }

    @Inject
    @Optional
    public void reloadLastSelectedFile(@UIEventTopic(UIEventConstants.TOPIC_RELOAD_VIEW) String empty) {
        //clearScrollView();
        //  presenter.reloadLastSelctedFile();
    }

    @Override
    public void clearScrollView() {
        Stream.of(composite.getChildren()).forEach(child -> child.dispose());
    }

    private void updateScrollView() {
        scrollView.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }

    private void fileDiffHeader(Composite composite) {
        lblHeader = new Label(composite, SWT.NONE | SWT.WRAP);
        lblHeader.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblHeader.setFont(FontUtils.createFont(lblHeader, "Segoe UI", 10, SWT.BOLD));
        lblHeader.setText("No diff available");
    }

    @Override
    public void showMergeView(DiffFile diff) {
        clearScrollView();
        fileDiffHeader(composite);
        this.lblHeader.setText(String.format("%1$s with %2$d additions and %3$d deletions", diff.getFile(), diff.getAdded(), diff.getDeleted()));
        diff.getHunks().stream().filter(section -> section != null).forEach(section -> mergeView.createMergeView(composite, section, diff));
        updateScrollView();
        this.parent.layout(true, true);
    }

    @Override
    public void showContentDiff(DiffFile diff) {
        clearScrollView();
        fileDiffHeader(composite);
        this.lblHeader.setText(String.format("%1$s with %2$d additions and %3$d deletions", diff.getFile(), diff.getAdded(), diff.getDeleted()));
        diff.getHunks().stream().filter(section -> section != null).forEach(section -> diffView.createView(composite, section));
        updateScrollView();
        this.parent.layout(true, true);
    }

    @Override
    public void sendUIEvent(String topic) {
        sync.asyncExec(() -> eventBroker.post(topic, ""));
    }

    @Override
    public void showError(GitException exception) {
        sync.asyncExec(() -> {
            MessageDialog.openError(shell, "Error: ", exception.getMessage());
        });
    }

    @Override
    public void showError(Exception exception) {
        sync.asyncExec(() -> {
            MessageDialog.openError(shell, "Error: ", exception.getMessage());
        });
    }

    @Override
    public void showLogs(List<GitLog> logs) {
        clearScrollView();
        logView.createView(logs, composite, lblHeader, prettyTime);
        updateScrollView();
        parent.layout(true, true);
    }
}
