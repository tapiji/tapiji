package org.eclipse.e4.tapiji.git.ui.filediff;


import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.tapiji.git.model.diff.DiffFile;
import org.eclipse.e4.tapiji.git.model.diff.DiffSection;
import org.eclipse.e4.tapiji.git.model.exception.GitServiceException;
import org.eclipse.e4.tapiji.git.ui.constants.UIEventConstants;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;


public class FileDiffView implements FileDiffContract.View {

    @Inject
    FileDiffPresenter presenter;
    @Inject
    UISynchronize sync;
    private Composite parent;
    private ScrolledComposite scrolledComposite;
    private Composite composite_1;
    private TableColumnLayout tableViewerLayout;

    @PostConstruct
    public void createPartControl(final Composite parent) {
        this.parent = parent;
        presenter.setView(this);
        parent.setLayout(new GridLayout(1, false));

        Label lblNewLabel = new Label(parent, SWT.NONE);

        lblNewLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblNewLabel.setText("README.md with 2 additions and  3 deletions");

        scrolledComposite = new ScrolledComposite(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);

        composite_1 = new Composite(scrolledComposite, SWT.NONE);
        composite_1.setLayout(new FillLayout(SWT.VERTICAL));
    }

    @Inject
    @Optional
    public void closeHandler(@UIEventTopic(UIEventConstants.TOPIC_RELOAD) String payload) {
        presenter.loadFileDiffFrom("README.md");
    }

    @Override
    public void showFileDiff(DiffFile diff) {
        sync.syncExec(() -> {
            diff.getSections().stream().forEach(section -> createSections(section));
        });
    }

    private void createSections(DiffSection section) {
        Stream.of(composite_1.getChildren()).forEach(child -> child.dispose());

        Composite composite = new Composite(composite_1, SWT.NONE);
        composite.setLayout(new GridLayout(1, false));

        Label lblNewLabel_1 = new Label(composite, SWT.NONE);
        lblNewLabel_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblNewLabel_1.setText("@@ 1,1 12,18 @@");

        Composite composite_2 = new Composite(composite, SWT.NONE);
        composite_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        TableColumnLayout layout = new TableColumnLayout();
        composite_2.setLayout(layout);

        Table table = new Table(composite_2, SWT.BORDER | SWT.FULL_SELECTION);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        scrolledComposite.setContent(composite_1);
        scrolledComposite.setMinSize(composite_1.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        int[] columnWidths = new int[] {35, 35, 100};
        int[] columnAlignments = new int[] {SWT.CENTER, SWT.CENTER, SWT.LEFT};
        for (int i = 0; i < 3; i++) {
            TableColumn tableColumn = new TableColumn(table, columnAlignments[i]);
            tableColumn.setWidth(columnWidths[i]);
        }
        section.getLines().stream().forEach(line -> {
            TableItem item = new TableItem(table, SWT.NONE);
            item.setText(0, line.getLineNumberLeft());
            item.setText(1, line.getLineNumberRight());
            item.setText(2, line.getLine());
        });
        for (int i = 0; i < 3; i++) {
            table.getColumn(i).pack();
        }

    }

    @Override
    public void showError(GitServiceException exception) {
        // TODO Auto-generated method stub

    }

}
