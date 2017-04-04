package org.eclipse.e4.tapiji.git.ui.part.left.remotebranch;


import java.util.List;
import javax.inject.Inject;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.tapiji.git.model.Reference;
import org.eclipse.e4.tapiji.git.model.exception.GitException;
import org.eclipse.e4.tapiji.git.ui.constant.UIEventConstants;
import org.eclipse.e4.tapiji.resource.ITapijiResourceProvider;
import org.eclipse.e4.tapiji.resource.TapijiResourceConstants;
import org.eclipse.e4.tapiji.utils.ColorUtils;
import org.eclipse.e4.tapiji.utils.FontUtils;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
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
public class RemoteBranchView implements RemoteBranchContract.View {

    @Inject
    ITapijiResourceProvider resourceProvider;

    @Inject
    UISynchronize sync;

    @Inject
    RemoteBranchPresenter presenter;
    @Inject
    EMenuService menuService;

    @Inject
    ESelectionService selectionService;

    @Inject
    Shell shell;

    private Composite parent;

    private ScrolledComposite scrolledComposite;

    private Label lblRemoteBranchCnt;

    private Composite layoutComposite;

    private Table table;

    private TableViewer tableViewer;

    public void createPartControl(final Composite parent, ScrolledComposite scrolledComposite) {
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
        lblTags.setText("Remote");

        lblRemoteBranchCnt = new Label(composite, SWT.NONE);
        lblRemoteBranchCnt.setBackground(ColorUtils.getSystemColor(SWT.COLOR_TRANSPARENT));
        lblRemoteBranchCnt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        lblRemoteBranchCnt.setBounds(0, 0, 55, 15);
        lblRemoteBranchCnt.setText("1");
        lblRemoteBranchCnt.setFont(FontUtils.createFont(lblRemoteBranchCnt, "Segoe UI", 8, SWT.BOLD));

        layoutComposite = new Composite(composite_1, SWT.NONE);
        layoutComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        TableColumnLayout tableLayout = new TableColumnLayout();
        layoutComposite.setLayout(tableLayout);

        tableViewer = new TableViewer(layoutComposite, SWT.BORDER | SWT.FULL_SELECTION);
        table = tableViewer.getTable();
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        tableViewer.addSelectionChangedListener(event -> {
            selectionService.setSelection(table.getSelection()[0].getText());
        });

        TableColumn column = new TableColumn(table, SWT.LEFT);
        column.pack();
        tableLayout.setColumnData(column, new ColumnWeightData(100, 100));

        lblTags.addListener(SWT.MouseDown, listener -> viewVisibility(((GridData) layoutComposite.getLayoutData()).exclude));
        lblRemoteBranchCnt.addListener(SWT.MouseDown, listener -> viewVisibility(((GridData) layoutComposite.getLayoutData()).exclude));
    }

    @Override
    public void collapseView() {
        viewVisibility(false);
    }

    private void expandView() {
        viewVisibility(true);
    }

    @Override
    public void showBranches(List<Reference> branches) {
        System.out.println(branches);
        sync.asyncExec(() -> {
            table.removeAll();
            table.clearAll();
            if (!branches.isEmpty()) {
                lblRemoteBranchCnt.setText(String.valueOf(branches.size()));
                branches.stream().forEach(branch -> {
                    TableItem item = new TableItem(table, SWT.NONE);
                    item.setText(branch.getName());
                    item.setImage(resourceProvider.loadImage(TapijiResourceConstants.IMG_BRANCH));
                });
                registerTreeMenu();
            } else {
                lblRemoteBranchCnt.setText("0");
            }
            collapseView();
        });
    }

    private void registerTreeMenu() {
        this.menuService.registerContextMenu(this.tableViewer.getControl(), UIEventConstants.PROPERTY_TABLE_VIEW_BRANCH_MENU_ID);
    }

    private void viewVisibility(boolean visibility) {
        sync.asyncExec(() -> {
            ((GridData) layoutComposite.getLayoutData()).exclude = !visibility;
            layoutComposite.setVisible(visibility);
            scrolledComposite.setMinSize(parent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
            parent.layout(true, true);
        });
    }

    @Override
    public void showError(GitException exception) {
        sync.asyncExec(() -> {
            MessageDialog.openError(shell, "Error: ", exception.getMessage());
        });
    }

    @Override
    public RemoteBranchPresenter getPresenter() {
        return presenter;
    }

}
