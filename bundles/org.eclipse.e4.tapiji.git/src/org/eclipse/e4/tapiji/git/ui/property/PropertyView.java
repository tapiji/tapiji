package org.eclipse.e4.tapiji.git.ui.property;


import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.tapiji.git.model.exception.GitServiceException;
import org.eclipse.e4.tapiji.git.model.property.PropertyDirectory;
import org.eclipse.e4.tapiji.git.ui.constants.UIEventConstants;
import org.eclipse.e4.tapiji.git.ui.property.provider.PropertyTreeContentProvider;
import org.eclipse.e4.tapiji.git.ui.property.provider.PropertyTreeLabelProvider;
import org.eclipse.e4.tapiji.resource.ITapijiResourceProvider;
import org.eclipse.e4.tapiji.resource.TapijiResourceConstants;
import org.eclipse.e4.tapiji.utils.FontUtils;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;


public class PropertyView implements PropertyContract.View {

    private static final String TREE_VIEWER_MENU_ID = "org.eclipse.e4.tapiji.git.popupmenu.pv.treeviewer";

    @Inject
    ITapijiResourceProvider resourceProvider;

    @Inject
    UISynchronize sync;

    @Inject
    EMenuService menuService;

    @Inject
    ESelectionService selectionService;

    @Inject
    PropertyPresenter presenter;

    private Composite parent;

    private TreeViewer treeViewer;

    private Tree tree;

    private Label lblProperties;

    @PostConstruct
    public void createPartControl(final Composite parent) {
        this.parent = parent;
        presenter.setView(this);

        parent.setLayout(new FillLayout(SWT.VERTICAL));

        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(1, false));

        Composite composite_1 = new Composite(composite, SWT.NONE);
        composite_1.setLayout(new GridLayout(2, false));
        composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        lblProperties = new Label(composite_1, SWT.NONE);
        lblProperties.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblProperties.setBounds(0, 0, 55, 15);
        lblProperties.setFont(FontUtils.createFont(lblProperties, "Segoe UI", 10, SWT.BOLD));
        lblProperties.setText("Property Files");

        treeViewer = new TreeViewer(composite, SWT.BORDER);
        treeViewer.setAutoExpandLevel(2);
        treeViewer.addSelectionChangedListener((event) -> {
            selectionService.setSelection(((IStructuredSelection) treeViewer.getSelection()).getFirstElement());
        });
        tree = treeViewer.getTree();
        tree.setLayoutData(new GridData(GridData.FILL_BOTH));
        treeViewer.setContentProvider(new PropertyTreeContentProvider());
        treeViewer.setLabelProvider(new PropertyTreeLabelProvider(image(TapijiResourceConstants.IMG_FOLDER), image(TapijiResourceConstants.IMG_RESOURCE_PROPERTY)));
    }

    private Image image(String image) {
        return resourceProvider.loadImage(image);
    }

    @Override
    public void showError(GitServiceException exception) {
        sync.asyncExec(() -> {
            MessageDialog.openError(parent.getShell(), "Error: ", exception.getMessage());
        });
    }

    @Inject
    @Optional
    public void closeHandler(@UIEventTopic(UIEventConstants.TOPIC_RELOAD_PROPERTY_VIEW) String payload) {
        presenter.loadProperties();
    }

    private void registerTreeMenu() {
        this.menuService.registerContextMenu(this.treeViewer.getControl(), TREE_VIEWER_MENU_ID);
    }

    @Override
    public void showProperties(List<PropertyDirectory> directories, int cntFiles) {
        sync.syncExec(() -> {
            setLabelProperties(cntFiles);
            treeViewer.setSelection(null);
            treeViewer.setInput(directories);
            registerTreeMenu();
        });
    }

    private void setLabelProperties(int cntFiles) {
        if (cntFiles >= 0) {
            lblProperties.setText("Property Files (" + String.valueOf(cntFiles) + ")");
        } else {
            lblProperties.setText("Property Files");
        }
    }
}
