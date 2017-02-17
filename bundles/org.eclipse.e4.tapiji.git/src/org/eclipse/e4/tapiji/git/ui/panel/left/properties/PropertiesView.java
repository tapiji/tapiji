package org.eclipse.e4.tapiji.git.ui.panel.left.properties;


import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.eclipse.e4.tapiji.git.model.exception.GitServiceException;
import org.eclipse.e4.tapiji.git.ui.panel.left.file.FileView;
import org.eclipse.e4.tapiji.git.ui.panel.left.stash.StashView;
import org.eclipse.e4.tapiji.git.ui.panel.left.tag.TagView;
import org.eclipse.e4.tapiji.resource.ITapijiResourceProvider;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;


public class PropertiesView implements PropertiesContract.View {

    @Inject
    ITapijiResourceProvider resourceProvider;

    @Inject
    UISynchronize sync;

    @Inject
    EMenuService menuService;

    @Inject
    ESelectionService selectionService;

    @Inject
    PropertiesPresenter presenter;

    private Composite parent;

    @Inject
    FileView filesView;

    @Inject
    StashView stashView;

    @Inject
    TagView tagView;

    @PostConstruct
    public void createPartControl(final Composite parent) {
        this.parent = parent;
        presenter.setView(this);

        ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL);
        scrolledComposite.setAlwaysShowScrollBars(true);

        Composite compositeMain = new Composite(scrolledComposite, SWT.NONE);
        compositeMain.setLayout(new GridLayout(1, false));

        filesView.createPartControl(compositeMain, scrolledComposite);
        stashView.createPartControl(compositeMain, scrolledComposite);
        tagView.createPartControl(compositeMain, scrolledComposite);

        scrolledComposite.setContent(compositeMain);
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setMinSize(compositeMain.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }

    @Override
    public void showError(GitServiceException exception) {
        sync.asyncExec(() -> {
            MessageDialog.openError(parent.getShell(), "Error: ", exception.getMessage());
        });
    }
}
