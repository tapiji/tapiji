package org.eclipse.e4.tapiji.git.ui.filediff;


import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.tapiji.git.model.diff.DiffFile;
import org.eclipse.e4.tapiji.git.model.diff.DiffSection;
import org.eclipse.e4.tapiji.git.model.exception.GitServiceException;
import org.eclipse.e4.tapiji.git.ui.constants.UIEventConstants;
import org.eclipse.e4.tapiji.utils.ColorUtils;
import org.eclipse.e4.tapiji.utils.FontUtils;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;


public class FileDiffView implements FileDiffContract.View {
    private static final int[] COLUMN_ALIGNMENTS = new int[] {SWT.CENTER, SWT.CENTER, SWT.LEFT};
    private static final int[] COLUMN_WEIGHTS = new int[] {10,10,100};
	
    @Inject
    FileDiffPresenter presenter;
    @Inject
    UISynchronize sync;
    private Composite parent;
    private ScrolledComposite scrolledComposite;
    private Composite composite_1;
    private TableColumnLayout tableViewerLayout;
	private Label lblNewLabel;

    @PostConstruct
    public void createPartControl(final Composite parent) {
        this.parent = parent;
        presenter.setView(this);
        parent.setLayout(new GridLayout(1, false));

        lblNewLabel = new Label(parent, SWT.NONE);
        lblNewLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblNewLabel.setFont(FontUtils.createFont(lblNewLabel, "Segoe UI", 10, SWT.BOLD));
        lblNewLabel.setText("README.md with 2 additions and  3 deletions");

        scrolledComposite = new ScrolledComposite(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);

        composite_1 = new Composite(scrolledComposite, SWT.NONE);
        composite_1.setLayout(new FillLayout(SWT.VERTICAL));
        Stream.of(composite_1.getChildren()).forEach(child -> child.dispose());
        presenter.loadFileDiffFrom("README.md");
    }

    @Inject
    @Optional
    public void closeHandler(@UIEventTopic(UIEventConstants.LOAD_DIFF) String payload) {
    	Stream.of(composite_1.getChildren()).forEach(child -> child.dispose());
     System.out.println(payload);
     presenter.loadFileDiffFrom(payload);
    }

    @Override
    public void showFileDiff(DiffFile diff) {
        sync.syncExec(() -> {
            diff.getSections().stream().forEach(section -> createSections(section, diff.getAdded(), diff.getDeleted()));
        });
    }

    private void createSections(DiffSection section, int additions, int deletions) {
    	lblNewLabel.setText(String.format("README.md with %1$d additions and  %2$d deletions", additions, deletions));

        Composite composite = new Composite(composite_1, SWT.NONE);
        composite.setLayout(new GridLayout(1, false));

        Label lblDiffHeader = new Label(composite, SWT.NONE);
        lblDiffHeader.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblDiffHeader.setText(section.getHeader());

        Composite layoutComposite = new Composite(composite, SWT.NONE);
        layoutComposite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, true, 1, 1));
        
      

        Table table = new Table(layoutComposite, SWT.BORDER | SWT.FULL_SELECTION);
        table.setHeaderVisible(false);
        table.setLinesVisible(true);
        
        for (int i = 0; i < 3; i++) {
           TableColumn column = new TableColumn(table, COLUMN_ALIGNMENTS[i]);
           column.pack();
        }
        section.getLines().stream().forEach(line -> {
            TableItem item = new TableItem(table, SWT.NONE);
            item.setText(0, line.getLineNumberLeft());
            item.setText(1, line.getLineNumberRight());
            if(line.getLineNumberRight().contains("+") || line.getLineNumberLeft().contains("+")) {
            	item.setBackground(2, new Color (Display.getCurrent(), 144,238,144));
            } else if(line.getLineNumberRight().contains("-") || line.getLineNumberLeft().contains("-")) {
            	item.setBackground(2, new Color (Display.getCurrent(), 240,128,128));
            }
            item.setText(2, line.getLine());
        });

        TableColumnLayout layout = new TableColumnLayout();
        for(int i = 0; i< table.getColumnCount();i++) {
        	layout.setColumnData(table.getColumns()[i], new ColumnWeightData(COLUMN_WEIGHTS[i], COLUMN_WEIGHTS[i]));
        }
        layoutComposite.setLayout(layout);
       
        scrolledComposite.setContent(composite_1);
        scrolledComposite.setMinSize(composite_1.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }

    @Override
    public void showError(GitServiceException exception) {
        // TODO Auto-generated method stub

    }

}
