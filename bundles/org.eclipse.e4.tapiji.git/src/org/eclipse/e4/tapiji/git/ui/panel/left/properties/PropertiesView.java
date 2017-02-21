package org.eclipse.e4.tapiji.git.ui.panel.left.properties;


import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.tapiji.git.model.exception.GitServiceException;
import org.eclipse.e4.tapiji.git.ui.constants.UIEventConstants;
import org.eclipse.e4.tapiji.git.ui.panel.left.file.FileView;
import org.eclipse.e4.tapiji.git.ui.panel.left.stash.StashView;
import org.eclipse.e4.tapiji.git.ui.panel.left.tag.TagView;
import org.eclipse.e4.tapiji.resource.ITapijiResourceProvider;
import org.eclipse.e4.ui.di.UIEventTopic;
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

    private Composite compositeMain;

    private ScrolledComposite scrollView;

    @PostConstruct
    public void createPartControl(final Composite parent) {
        this.parent = parent;
        presenter.setView(this);

        scrollView = new ScrolledComposite(parent, SWT.V_SCROLL);
        scrollView.setAlwaysShowScrollBars(true);
        scrollView.setExpandHorizontal(true);
        scrollView.setExpandVertical(true);

        compositeMain = new Composite(scrollView, SWT.NONE);
        compositeMain.setLayout(new GridLayout(1, false));

        filesView.createPartControl(compositeMain, scrollView);
        stashView.createPartControl(compositeMain, scrollView);
        tagView.createPartControl(compositeMain, scrollView);

        scrollView.setContent(compositeMain);
        scrollView.setMinSize(compositeMain.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        scrollView.setVisible(false);
    }

    @Inject
    @Optional
    public void closeHandler(@UIEventTopic(UIEventConstants.TOPIC_RELOAD_VIEW) String payload) {
        filesView.getPresenter().loadFiles();
        stashView.getPresenter().loadStashes();
        tagView.getPresenter().loadTags();
        scrollView.setMinSize(compositeMain.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        scrollView.setVisible(true);
    }

    @Override
    public void showError(GitServiceException exception) {
        sync.asyncExec(() -> {
            MessageDialog.openError(parent.getShell(), "Error: ", exception.getMessage());
        });
    }
}
