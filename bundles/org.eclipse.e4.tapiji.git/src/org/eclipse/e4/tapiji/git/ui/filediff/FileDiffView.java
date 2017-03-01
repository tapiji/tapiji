package org.eclipse.e4.tapiji.git.ui.filediff;


import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.tapiji.git.model.diff.DiffFile;
import org.eclipse.e4.tapiji.git.model.diff.DiffHunk;
import org.eclipse.e4.tapiji.git.model.diff.DiffLine;
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

    private ScrolledComposite scrollView;
    private Composite composite;
    private Label lblHeader;

    @Inject
    FileDiffPresenter presenter;

    @Inject
    UISynchronize sync;

    @Inject
    Shell shell;
    private Composite parent;
    private String selectedFile;

    @PostConstruct
    public void createPartControl(final Composite parent) {
        this.parent = parent;
        presenter.setView(this);
        parent.setLayout(new GridLayout(1, false));

        lblHeader = new Label(parent, SWT.NONE | SWT.WRAP);
        lblHeader.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblHeader.setFont(FontUtils.createFont(lblHeader, "Segoe UI", 10, SWT.BOLD));
        lblHeader.setText("No diff available");

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
        if (selectedFile == null || !selectedFile.equals(file)) {
            clearScrollView();
            if (file.getStatus() == GitFileStatus.CONFLICT) {
                presenter.loadFileMergeDiff(file.getName(), GitFileStatus.CONFLICT);
            } else {
                presenter.loadFileContentDiff(file.getName());
            }
        }
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
            this.selectedFile = diff.getFile();
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
                if (!line.isAccepted()) {
                    item.setBackground(ORANGE);
                } else {
                    colorizeLineBackground(line, item);
                }
                presenter.onClickCheckBox(line);
            });
            checkBox.pack();

            editor.minimumWidth = checkBox.getSize().x;
            editor.horizontalAlignment = SWT.CENTER;
            editor.setEditor(checkBox, item, 0);

            item.setText(1, line.getLineNumberLeft());
            item.setText(2, line.getLineNumberRight());
            item.setText(3, line.getLine());

            colorizeLineBackground(line, item);
            checkBoxVisibility(line, checkBox);
        });
        layoutComposite.setLayout(setColumnWeights(table, COLUMN_WEIGHTS_MERGE));
    }

    private void checkBoxVisibility(DiffLine line, Button checkBox) {
        if (line.getLineNumberRight().contains("+") || line.getLineNumberLeft().contains("+")) {
            checkBox.setVisible(true);
        } else if (line.getLineNumberRight().contains("-") || line.getLineNumberLeft().contains("-")) {
            checkBox.setVisible(true);
        } else {
            checkBox.setVisible(false);
        }
    }

    private void colorizeLineBackground(DiffLine line, TableItem item) {
        if (line.getLineNumberRight().contains("+") || line.getLineNumberLeft().contains("+")) {
            item.setBackground(GREEN);
        } else if (line.getLineNumberRight().contains("-") || line.getLineNumberLeft().contains("-")) {
            item.setBackground(RED);
        }
    }

    private void createColumns(int columns, Table table, int[] alignment) {
        for (int i = 0; i < columns; i++) {
            TableColumn column = new TableColumn(table, alignment[i]);
            column.pack();
        }
    }

    private TableColumnLayout setColumnWeights(Table table, int[] weights) {
        TableColumnLayout layout = new TableColumnLayout();
        for (int i = 0; i < table.getColumnCount(); i++) {
            layout.setColumnData(table.getColumns()[i], new ColumnWeightData(weights[i], weights[i]));
        }
        return layout;
    }

    @Override
    public void showContentDiff(DiffFile diff) {
        sync.syncExec(() -> {
            this.selectedFile = diff.getFile();
            this.lblHeader.setText(String.format("%1$s with %2$d additions and %3$d deletions", diff.getFile(), diff.getAdded(), diff.getDeleted()));
            diff.getHunks().stream().filter(section -> section != null).forEach(section -> createContentView(section));
            updateScrollView();
            this.parent.layout(true, true);
        });
    }

    private void createContentView(DiffHunk section) {
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
            item.setText(0, line.getLineNumberLeft());
            item.setText(1, line.getLineNumberRight());
            item.setText(2, line.getLine());
            colorizeLineBackground(line, item);
        });
        layoutComposite.setLayout(setColumnWeights(table, COLUMN_WEIGHTS_DIFF));
    }

    @Override
    public void showError(GitServiceException exception) {
        sync.asyncExec(() -> {
            MessageDialog.openError(shell, "Error: ", exception.getMessage());
        });
    }
}
