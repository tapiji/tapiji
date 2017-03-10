package org.eclipse.e4.tapiji.git.ui.part.left.file;


import java.util.List;
import javax.inject.Inject;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.tapiji.git.model.exception.GitServiceException;
import org.eclipse.e4.tapiji.git.model.property.PropertyDirectory;
import org.eclipse.e4.tapiji.git.ui.constants.UIEventConstants;
import org.eclipse.e4.tapiji.git.ui.part.left.file.provider.ResourceBundleTreeContentProvider;
import org.eclipse.e4.tapiji.git.ui.part.left.file.provider.ResourceBundleTreeLabelProvider;
import org.eclipse.e4.tapiji.resource.ITapijiResourceProvider;
import org.eclipse.e4.tapiji.resource.TapijiResourceConstants;
import org.eclipse.e4.tapiji.utils.ColorUtils;
import org.eclipse.e4.tapiji.utils.FontUtils;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;


@Creatable
public class FileView implements FileContract.View {

    @Inject
    ITapijiResourceProvider resourceProvider;

    @Inject
    UISynchronize sync;

    @Inject
    FilePresenter presenter;
    @Inject
    EMenuService menuService;

    @Inject
    ESelectionService selectionService;

    @Inject
    Shell shell;

    private Label lblFiles;
    private TreeViewer treeViewer;
    private Control tree;

    private Label lblFilesCnt;

    private ScrolledComposite scrolledComposite;

    private Composite parent;

    public void createPartControl(final Composite parent, ScrolledComposite scrolledComposite) {
        this.scrolledComposite = scrolledComposite;
        this.parent = parent;
        presenter.setView(this);

        GridLayout glParent = new GridLayout(1, false);
        glParent.horizontalSpacing = 0;
        glParent.verticalSpacing = 0;
        glParent.marginWidth = 0;
        glParent.marginHeight = 0;
        parent.setLayout(glParent);

        GridLayout glCompositeMain = new GridLayout(1, false);
        glCompositeMain.verticalSpacing = 0;
        glCompositeMain.horizontalSpacing = 0;
        glCompositeMain.marginWidth = 0;
        glCompositeMain.marginHeight = 0;

        Composite compositeMain = new Composite(parent, SWT.NONE);
        compositeMain.setLayout(glCompositeMain);
        GridData cg = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        compositeMain.setLayoutData(cg);
        cg.widthHint = 230;

        Composite composite = new Composite(compositeMain, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        composite.setLayout(new GridLayout(2, false));

        lblFiles = new Label(composite, SWT.NONE);
        lblFiles.setBackground(ColorUtils.getSystemColor(SWT.COLOR_TRANSPARENT));
        lblFiles.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblFiles.setBounds(0, 0, 55, 15);
        lblFiles.setFont(FontUtils.createFont(lblFiles, "Segoe UI", 10, SWT.BOLD));
        lblFiles.setText("Files");

        lblFilesCnt = new Label(composite, SWT.NONE);
        lblFilesCnt.setBackground(ColorUtils.getSystemColor(SWT.COLOR_TRANSPARENT));
        lblFilesCnt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        lblFilesCnt.setBounds(0, 0, 55, 15);
        lblFilesCnt.setText("0");
        lblFilesCnt.setFont(FontUtils.createFont(lblFilesCnt, "Segoe UI", 8, SWT.BOLD));

        treeViewer = new TreeViewer(composite, SWT.BORDER);
        treeViewer.setAutoExpandLevel(2);
        treeViewer.addSelectionChangedListener((event) -> {
            selectionService.setSelection(((IStructuredSelection) treeViewer.getSelection()).getFirstElement());
        });
        tree = treeViewer.getTree();
        GridData gd2 = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);

        tree.setLayoutData(gd2);
        treeViewer.setContentProvider(new ResourceBundleTreeContentProvider());
        treeViewer.setLabelProvider(new ResourceBundleTreeLabelProvider(resourceProvider.loadImage(TapijiResourceConstants.IMG_FOLDER), resourceProvider
            .loadImage(TapijiResourceConstants.IMG_RESOURCE_PROPERTY)));

        lblFiles.addListener(SWT.MouseDown, listener -> viewVisibility(((GridData) tree.getLayoutData()).exclude));
        lblFilesCnt.addListener(SWT.MouseDown, listener -> viewVisibility(((GridData) tree.getLayoutData()).exclude));

        presenter.loadLogs();
    }

    public void collapseView() {
        viewVisibility(false);
    }

    private void viewVisibility(boolean visibility) {
        ((GridData) tree.getLayoutData()).exclude = !visibility;
        tree.setVisible(visibility);
        scrolledComposite.setMinSize(parent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        parent.layout(true, true);
    }

    public void expandView() {
        viewVisibility(true);
    }

    private void registerTreeMenu() {
        this.menuService.registerContextMenu(this.treeViewer.getControl(), UIEventConstants.PROPERTY_TREE_VIEWER_MENU_ID);
    }

    @Override
    public FilePresenter getPresenter() {
        return presenter;
    }

    public void setPresenter(FilePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showFiles(List<PropertyDirectory> result, int cntFiles) {
        sync.syncExec(() -> {
            setLabelProperties(cntFiles);
            treeViewer.setSelection(null);
            treeViewer.setInput(result);
            registerTreeMenu();
            expandView();
        });
    }

    private void setLabelProperties(int cntFiles) {
        lblFilesCnt.setText(String.valueOf(cntFiles));
    }

    @Override
    public void showError(GitServiceException exception) {
        sync.asyncExec(() -> {
            MessageDialog.openError(shell, "Error: ", exception.getMessage());
        });
    }

}
