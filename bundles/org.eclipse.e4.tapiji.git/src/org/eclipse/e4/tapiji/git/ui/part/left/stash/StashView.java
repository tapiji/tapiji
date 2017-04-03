package org.eclipse.e4.tapiji.git.ui.part.left.stash;


import java.util.List;
import javax.inject.Inject;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.tapiji.git.model.CommitReference;
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
public class StashView implements StashContract.View {

    @Inject
    StashPresenter presenter;

    @Inject
    UISynchronize sync;

    @Inject
    Shell shell;

    @Inject
    ITapijiResourceProvider resourceProvider;

    @Inject
    EMenuService menuService;

    @Inject
    ESelectionService selectionService;

    private Label lblStashCnt;

    private Table table;

    private ScrolledComposite scrolledComposite;

    private Composite parent;

    private TableViewer tableViewer;

    private Composite layoutComposite;

    public void createPartControl(Composite parent, ScrolledComposite scrolledComposite) {
        this.parent = parent;
        this.scrolledComposite = scrolledComposite;

        presenter.setView(this);

        GridLayout glParent = new GridLayout(1, false);
        glParent.horizontalSpacing = 0;
        glParent.verticalSpacing = 0;
        glParent.marginWidth = 0;
        glParent.marginHeight = 0;
        parent.setLayout(glParent);

        Composite compositeMain = new Composite(parent, SWT.NONE);
        GridLayout glComposite = new GridLayout(1, false);
        glComposite.verticalSpacing = 0;
        glComposite.horizontalSpacing = 0;
        glComposite.marginWidth = 0;
        glComposite.marginHeight = 0;
        compositeMain.setLayout(glComposite);
        compositeMain.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        Composite composite = new Composite(compositeMain, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        GridLayout gl_composite = new GridLayout(2, false);
        composite.setLayout(gl_composite);

        Label lblStashes = new Label(composite, SWT.NONE);
        lblStashes.setBackground(ColorUtils.getSystemColor(SWT.COLOR_TRANSPARENT));
        lblStashes.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblStashes.setBounds(0, 0, 55, 15);
        lblStashes.setFont(FontUtils.createFont(lblStashes, "Segoe UI", 10, SWT.BOLD));

        lblStashes.setText("Stash");

        lblStashCnt = new Label(composite, SWT.NONE);
        lblStashCnt.setBackground(ColorUtils.getSystemColor(SWT.COLOR_TRANSPARENT));
        lblStashCnt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        lblStashCnt.setBounds(0, 0, 55, 15);
        lblStashCnt.setText("0");
        lblStashCnt.setFont(FontUtils.createFont(lblStashCnt, "Segoe UI", 8, SWT.BOLD));

        layoutComposite = new Composite(compositeMain, SWT.NONE);
        layoutComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        TableColumnLayout tableLayout = new TableColumnLayout();
        layoutComposite.setLayout(tableLayout);

        tableViewer = new TableViewer(layoutComposite, SWT.BORDER | SWT.FULL_SELECTION);
        tableViewer.addSelectionChangedListener(event -> selectionService.setSelection(table.getSelection()[0].getData()));
        table = tableViewer.getTable();
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        TableColumn column = new TableColumn(table, SWT.LEFT);
        column.pack();
        tableLayout.setColumnData(column, new ColumnWeightData(100, 100));

        lblStashes.addListener(SWT.MouseDown, listener -> viewVisibility(((GridData) layoutComposite.getLayoutData()).exclude));
        lblStashCnt.addListener(SWT.MouseDown, listener -> viewVisibility(((GridData) layoutComposite.getLayoutData()).exclude));
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
    public void collapseView() {
        viewVisibility(false);
    }

    private void expandView() {
        viewVisibility(true);
    }

    private void registerTreeMenu() {
        this.menuService.registerContextMenu(this.tableViewer.getControl(), UIEventConstants.PROPERTY_TABLE_VIEW_STASH_MENU_ID);
    }

    @Override
    public StashPresenter getPresenter() {
        return presenter;
    }

    @Override
    public void showStashes(List<CommitReference> stashes) {
        sync.asyncExec(() -> {
            table.removeAll();
            table.clearAll();
            if (!stashes.isEmpty()) {
                stashes.stream().forEach(stash -> {
                    TableItem item = new TableItem(table, SWT.NONE);
                    item.setText(stash.getName());
                    item.setData(stash.getHash());
                    item.setImage(resourceProvider.loadImage(TapijiResourceConstants.IMG_STASH_SAVED));
                });
                registerTreeMenu();
                lblStashCnt.setText(String.valueOf(stashes.size()));
                expandView();
            } else {
                lblStashCnt.setText("0");
                collapseView();
            }
        });
    }

    @Override
    public void showError(GitException exception) {
        sync.asyncExec(() -> {
            MessageDialog.openError(shell, "Error: ", exception.getMessage());
        });
    }
}
