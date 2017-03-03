package org.eclipse.e4.tapiji.git.ui.filediff;


import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.tapiji.git.model.diff.DiffFile;
import org.eclipse.e4.tapiji.git.model.diff.DiffHunk;
import org.eclipse.e4.tapiji.git.model.diff.DiffLine;
import org.eclipse.e4.tapiji.git.model.diff.DiffLineStatus;
import org.eclipse.e4.tapiji.git.model.exception.GitServiceException;
import org.eclipse.e4.tapiji.git.model.file.GitFile;
import org.eclipse.e4.tapiji.git.model.file.GitFileStatus;
import org.eclipse.e4.tapiji.git.ui.constants.UIEventConstants;
import org.eclipse.e4.tapiji.utils.FontUtils;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;


public class FileDiffView implements FileDiffContract.View {

    private static final int[] COLUMN_ALIGNMENTS_DIFF = new int[] {SWT.CENTER, SWT.CENTER, SWT.LEFT};
    private static final int[] COLUMN_ALIGNMENTS_MERGE = new int[] {SWT.CENTER, SWT.CENTER, SWT.CENTER, SWT.LEFT};
    private static final int[] COLUMN_WEIGHTS_DIFF = new int[] {10, 10, 100};
    private static final int[] COLUMN_WEIGHTS_MERGE = new int[] {10, 10, 10, 100};
    private static final Color GREEN = new Color(Display.getCurrent(), 144, 238, 144);
    private static final Color RED = new Color(Display.getCurrent(), 240, 128, 128);
    private static final Color ORANGE = new Color(Display.getCurrent(), 226, 189, 51);

    @Inject
    FileDiffPresenter presenter;

    @Inject
    UISynchronize sync;

    @Inject
    IEventBroker eventBroker;

    @Inject
    Shell shell;

    private ScrolledComposite scrollView;

    private Composite composite;

    private Label lblHeader;

    private Composite parent;

    private Button btnMarkResolved;

    @PostConstruct
    public void createPartControl(final Composite parent) {
        this.parent = parent;
        presenter.setView(this);
        parent.setLayout(new GridLayout(1, false));
        lblHeader = new Label(parent, SWT.NONE | SWT.WRAP);
        lblHeader.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblHeader.setFont(FontUtils.createFont(lblHeader, "Segoe UI", 10, SWT.BOLD));
        lblHeader.setText("No diff available");

        btnMarkResolved = new Button(parent, SWT.NONE);
        GridData gd = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
        gd.widthHint = 250;
        gd.verticalIndent = 11;
        btnMarkResolved.setLayoutData(gd);
        btnMarkResolved.setText("Mark resolved");
        btnMarkResolved.addListener(SWT.Selection, listener -> presenter.stageResolvedFile(presenter.getSelectedFileName()));

        scrollView = new ScrolledComposite(parent, SWT.V_SCROLL);
        scrollView.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        scrollView.setExpandHorizontal(true);
        scrollView.setExpandVertical(true);
        scrollView.setAlwaysShowScrollBars(true);

        composite = new Composite(scrollView, SWT.NONE);
        composite.setLayout(new GridLayout(1, false));
        scrollView.setContent(composite);
    }

    @Inject
    @Optional
    public void closeHandler(@UIEventTopic(UIEventConstants.LOAD_DIFF) GitFile file) {
        if (presenter.getSelectedFileName() == null || !presenter.getSelectedFileName().equals(file)) {
            clearScrollView();
            if (file.getStatus() == GitFileStatus.CONFLICT) {
                presenter.loadFileMergeDiff(file.getName(), GitFileStatus.CONFLICT);
            } else {
                presenter.loadFileContentDiff(file.getName());
            }
        }
    }

    @Inject
    @Optional
    public void reloadLastSelectedFile(@UIEventTopic(UIEventConstants.TOPIC_RELOAD_VIEW) String empty) {
        clearScrollView();
        presenter.reloadLastSelctedFile();
    }

    @Override
    public void clearScrollView() {
        Stream.of(composite.getChildren()).forEach(child -> child.dispose());
    }

    private void updateScrollView() {
        scrollView.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }

    @Override
    public void showMergeView(DiffFile diff) {
        sync.syncExec(() -> {
            this.lblHeader.setText(String.format("%1$s with %2$d additions and %3$d deletions", diff.getFile(), diff.getAdded(), diff.getDeleted()));
            diff.getHunks().stream().filter(section -> section != null).forEach(section -> createMergeView(section));
            updateScrollView();
            this.parent.layout(true, true);
        });
    }

    private void createMergeView(DiffHunk section) {

        Label lblDiffHeader = new Label(composite, SWT.NONE);
        lblDiffHeader.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 1, 1));
        lblDiffHeader.setText(section.getHeader());

        Composite layoutComposite = new Composite(composite, SWT.NONE);
        layoutComposite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 1, 1));

        Table table = new Table(layoutComposite, SWT.FULL_SELECTION);
        table.setHeaderVisible(false);
        table.setLinesVisible(false);

        createColumns(4, table, COLUMN_ALIGNMENTS_MERGE);

        section.getLines().stream().forEach(line -> {
            TableItem item = new TableItem(table, SWT.NONE);
            TableEditor editor = new TableEditor(table);
            Button checkBox = new Button(table, SWT.CHECK);

            checkBox.addListener(SWT.Selection, listener -> {
                if (line.getStatus() == DiffLineStatus.UNCHECKED) {
                    item.setBackground(ORANGE);
                    line.setStatus(DiffLineStatus.CHECKED);
                } else {
                    colorizeLineBackground(line, item);
                    line.setStatus(DiffLineStatus.UNCHECKED);
                }
            });
            checkBox.pack();

            editor.minimumWidth = checkBox.getSize().x;
            editor.horizontalAlignment = SWT.CENTER;
            editor.setEditor(checkBox, item, 0);

            item.setText(1, line.getNumberLeft());
            item.setText(2, line.getNumberRight());
            item.setText(3, line.getText());

            colorizeLineBackground(line, item);
            checkBoxVisibility(line, checkBox);
            setButtonMarkResolvedVisibility(true);
        });
        layoutComposite.setLayout(setColumnWeights(table, COLUMN_WEIGHTS_MERGE));
    }

    private void checkBoxVisibility(DiffLine line, Button checkBox) {
        if (line.getNumberRight().contains("+") || line.getNumberLeft().contains("+")) {
            checkBox.setVisible(true);
            line.setStatus(DiffLineStatus.UNCHECKED);
        } else if (line.getNumberRight().contains("-") || line.getNumberLeft().contains("-")) {
            checkBox.setVisible(true);
            line.setStatus(DiffLineStatus.UNCHECKED);
        } else {
            checkBox.setVisible(false);
        }
    }

    private void colorizeLineBackground(DiffLine line, TableItem item) {
        if (line.getNumberRight().contains("+") || line.getNumberLeft().contains("+")) {
            item.setBackground(GREEN);
        } else if (line.getNumberRight().contains("-") || line.getNumberLeft().contains("-")) {
            item.setBackground(RED);
        }
    }

    private void createColumns(int columns, Table table, int[] alignment) {
        IntStream.rangeClosed(0, columns - 1).forEach(i -> {
            TableColumn column = new TableColumn(table, alignment[i]);
            column.pack();
        });
    }

    private TableColumnLayout setColumnWeights(Table table, int[] weights) {
        TableColumnLayout layout = new TableColumnLayout();
        IntStream.rangeClosed(0, table.getColumnCount() - 1).forEach(i -> layout.setColumnData(table.getColumns()[i], new ColumnWeightData(weights[i], weights[i])));
        return layout;
    }

    @Override
    public void showContentDiff(DiffFile diff) {
        sync.syncExec(() -> {
            this.lblHeader.setText(String.format("%1$s with %2$d additions and %3$d deletions", diff.getFile(), diff.getAdded(), diff.getDeleted()));
            diff.getHunks().stream().filter(section -> section != null).forEach(section -> createContentDiffView(section));
            updateScrollView();
            this.parent.layout(true, true);
        });
    }

    @Override
    public void sendUIEvent(String topic) {
        sync.asyncExec(() -> eventBroker.post(topic, ""));
    }

    private void createContentDiffView(DiffHunk section) {
        setButtonMarkResolvedVisibility(false);
        Label lblDiffHeader = new Label(composite, SWT.NONE);
        lblDiffHeader.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 1, 1));
        lblDiffHeader.setText(section.getHeader());

        Composite layoutComposite = new Composite(composite, SWT.NONE);
        layoutComposite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 1, 1));

        Table table = new Table(layoutComposite, SWT.FULL_SELECTION);
        table.setHeaderVisible(false);
        table.setLinesVisible(false);

        createColumns(3, table, COLUMN_ALIGNMENTS_DIFF);
        section.getLines().stream().forEach(line -> {
            TableItem item = new TableItem(table, SWT.NONE);
            item.setText(0, line.getNumberLeft());
            item.setText(1, line.getNumberRight());
            item.setText(2, line.getText());
            colorizeLineBackground(line, item);
        });
        layoutComposite.setLayout(setColumnWeights(table, COLUMN_WEIGHTS_DIFF));
    }

    private void setButtonMarkResolvedVisibility(boolean visibility) {
        ((GridData) btnMarkResolved.getLayoutData()).exclude = !visibility;
        btnMarkResolved.setVisible(visibility);
    }

    @Override
    public void showError(GitServiceException exception) {
        sync.asyncExec(() -> {
            MessageDialog.openError(shell, "Error: ", exception.getMessage());
        });
    }

    @Override
    public void showError(Exception exception) {
        MessageDialog.openError(shell, "Error: ", exception.getMessage());
    }
}
