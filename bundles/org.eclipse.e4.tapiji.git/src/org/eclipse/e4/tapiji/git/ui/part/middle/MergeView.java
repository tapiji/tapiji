package org.eclipse.e4.tapiji.git.ui.part.middle;


import javax.inject.Inject;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.tapiji.git.model.diff.DiffHunk;
import org.eclipse.e4.tapiji.git.model.diff.DiffLine;
import org.eclipse.e4.tapiji.git.model.diff.DiffLineStatus;
import org.eclipse.e4.tapiji.git.ui.util.UIUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;


@Creatable
public class MergeView {

    private static final int[] COLUMN_ALIGNMENTS_MERGE = new int[] {SWT.CENTER, SWT.CENTER, SWT.CENTER, SWT.LEFT};
    private static final int[] COLUMN_WEIGHTS_MERGE = new int[] {10, 10, 10, 100};
    private static final int EDITABLE_COLUMN = 3;
    private ContentPresenter presenter;

    @Inject
    public MergeView(ContentPresenter presenter) {
        this.presenter = presenter;
    }

    private Button btnMarkResolved;

    public void createMergeView(Composite composite, DiffHunk section) {
        Label lblDiffHeader = new Label(composite, SWT.NONE);
        lblDiffHeader.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 1, 1));
        lblDiffHeader.setText(section.getHeader());

        btnMarkResolved = new Button(composite, SWT.NONE);
        GridData gd = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
        gd.widthHint = 250;
        gd.verticalIndent = 11;
        btnMarkResolved.setLayoutData(gd);
        btnMarkResolved.setText("Mark resolved");
        btnMarkResolved.addListener(SWT.Selection, listener -> presenter.stageResolvedFile(presenter.getSelectedFileName()));

        Composite layoutComposite = new Composite(composite, SWT.NONE);
        layoutComposite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 1, 1));

        Table table = new Table(layoutComposite, SWT.FULL_SELECTION);
        table.setHeaderVisible(false);
        table.setLinesVisible(false);

        UIUtil.createColumns(4, table, COLUMN_ALIGNMENTS_MERGE);

        section.getLines().stream().forEach(line -> {
            TableItem item = new TableItem(table, SWT.NONE);
            item.setData(line);
            TableEditor editor = new TableEditor(table);
            Button checkBox = new Button(table, SWT.CHECK);

            checkBox.addListener(SWT.Selection, listener -> {
                if (line.getStatus() == DiffLineStatus.UNCHECKED) {
                    item.setBackground(ContentView.ORANGE);
                    line.setStatus(DiffLineStatus.CHECKED);
                } else {
                    UIUtil.colorizeLineBackground(line, item);
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

            UIUtil.colorizeLineBackground(line, item);
            checkBoxVisibility(line, checkBox);
            setButtonMarkResolvedVisibility(true);
        });
        layoutComposite.setLayout(UIUtil.setColumnWeights(table, COLUMN_WEIGHTS_MERGE));
        createEditTable(table);
    }

    private void createEditTable(Table table) {
        final TableEditor editor = new TableEditor(table);
        editor.horizontalAlignment = SWT.LEFT;
        editor.grabHorizontal = true;
        table.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                final Control oldEditor = editor.getEditor();
                if (oldEditor != null) {
                    oldEditor.dispose();
                }

                TableItem item = (TableItem) e.item;
                if (item != null) {
                    Text newEditor = new Text(table, SWT.NONE);
                    newEditor.setText(item.getText(EDITABLE_COLUMN));
                    newEditor.addModifyListener(listener -> {
                        Text text = (Text) editor.getEditor();
                        editor.getItem().setText(EDITABLE_COLUMN, text.getText());
                        if (item.getData() != null) {
                            ((DiffLine) item.getData()).setText(text.getText());
                        }
                    });
                    newEditor.selectAll();
                    newEditor.setFocus();
                    editor.setEditor(newEditor, item, EDITABLE_COLUMN);
                    super.widgetSelected(e);
                }
            }
        });
    }

    private void setButtonMarkResolvedVisibility(boolean visibility) {
        ((GridData) btnMarkResolved.getLayoutData()).exclude = !visibility;
        btnMarkResolved.setVisible(visibility);
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
}
