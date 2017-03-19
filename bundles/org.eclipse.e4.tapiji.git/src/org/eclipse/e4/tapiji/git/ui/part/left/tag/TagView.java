package org.eclipse.e4.tapiji.git.ui.part.left.tag;


import java.util.List;
import javax.inject.Inject;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.tapiji.git.model.Reference;
import org.eclipse.e4.tapiji.git.model.exception.GitException;
import org.eclipse.e4.tapiji.resource.ITapijiResourceProvider;
import org.eclipse.e4.tapiji.resource.TapijiResourceConstants;
import org.eclipse.e4.tapiji.utils.ColorUtils;
import org.eclipse.e4.tapiji.utils.FontUtils;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;


@Creatable
public class TagView implements TagContract.View {

    @Inject
    UISynchronize sync;

    @Inject
    Shell shell;

    @Inject
    TagPresenter presenter;

    @Inject
    ITapijiResourceProvider resourceProvider;

    private Composite composite;

    private Table table;

    private Label lblTagCnt;

    private Composite parent;

    private ScrolledComposite scrolledComposite;

    private Composite layoutComposite;

    public void createPartControl(Composite parent, ScrolledComposite scrolledComposite) {
        this.parent = parent;
        this.scrolledComposite = scrolledComposite;
        presenter.setView(this);
        GridLayout gl_parent = new GridLayout(1, false);
        gl_parent.horizontalSpacing = 0;
        gl_parent.verticalSpacing = 0;
        gl_parent.marginWidth = 0;
        gl_parent.marginHeight = 0;
        parent.setLayout(gl_parent);

        Composite composite_1 = new Composite(parent, SWT.NONE);
        GridLayout gl_composite_1 = new GridLayout(1, false);
        gl_composite_1.verticalSpacing = 0;
        gl_composite_1.horizontalSpacing = 0;
        gl_composite_1.marginWidth = 0;
        gl_composite_1.marginHeight = 0;
        composite_1.setLayout(gl_composite_1);
        composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        Composite composite = new Composite(composite_1, SWT.NONE);
        //composite.setBackground(new Color(Display.getCurrent(), 220, 220, 220));
        composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        GridLayout gl_composite = new GridLayout(2, false);
        composite.setLayout(gl_composite);

        Label lblTags = new Label(composite, SWT.NONE);
        lblTags.setBackground(ColorUtils.getSystemColor(SWT.COLOR_TRANSPARENT));
        lblTags.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblTags.setBounds(0, 0, 55, 15);
        lblTags.setFont(FontUtils.createFont(lblTags, "Segoe UI", 10, SWT.BOLD));
        lblTags.setText("Tags");

        lblTagCnt = new Label(composite, SWT.NONE);
        lblTagCnt.setBackground(ColorUtils.getSystemColor(SWT.COLOR_TRANSPARENT));
        lblTagCnt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        lblTagCnt.setBounds(0, 0, 55, 15);
        lblTagCnt.setText("1");
        lblTagCnt.setFont(FontUtils.createFont(lblTagCnt, "Segoe UI", 8, SWT.BOLD));

        layoutComposite = new Composite(composite_1, SWT.NONE);
        layoutComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        TableColumnLayout tableLayout = new TableColumnLayout();
        layoutComposite.setLayout(tableLayout);

        TableViewer tableViewer = new TableViewer(layoutComposite, SWT.BORDER | SWT.FULL_SELECTION);
        table = tableViewer.getTable();
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        TableColumn column = new TableColumn(table, SWT.LEFT);
        column.pack();
        tableLayout.setColumnData(column, new ColumnWeightData(100, 100));

        lblTags.addListener(SWT.MouseDown, listener -> viewVisibility(((GridData) layoutComposite.getLayoutData()).exclude));
        lblTagCnt.addListener(SWT.MouseDown, listener -> viewVisibility(((GridData) layoutComposite.getLayoutData()).exclude));
    }

    private void collapseView() {
        viewVisibility(false);
    }

    private void viewVisibility(boolean visibility) {
        ((GridData) layoutComposite.getLayoutData()).exclude = !visibility;
        layoutComposite.setVisible(visibility);
        scrolledComposite.setMinSize(parent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        parent.layout(true, true);
    }

    private void expandView() {
        viewVisibility(true);
    }

    @Override
    public TagPresenter getPresenter() {
        return presenter;
    }

    @Override
    public void showTags(List<Reference> tags) {
        sync.asyncExec(() -> {
            table.removeAll();
            table.clearAll();
            if (!tags.isEmpty()) {
                lblTagCnt.setText(String.valueOf(tags.size()));
                tags.stream().forEach(tag -> {
                    TableItem item = new TableItem(table, SWT.NONE);
                    item.setText(tag.getName());
                    item.setImage(resourceProvider.loadImage(TapijiResourceConstants.IMG_TAG));
                });
            } else {
                lblTagCnt.setText("0");
            }
            collapseView();
        });
    }

    @Override
    public void showError(GitException exception) {
        sync.asyncExec(() -> {
            MessageDialog.openError(shell, "Error: ", exception.getMessage());
        });
    }

}
