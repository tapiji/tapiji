package org.eclipse.e4.tapiji.git.ui.commit;


import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;


public class CommitView implements CommitContract.View {

    @Inject
    CommitPresenter presenter;

    @PostConstruct
    public void createPartControl(final Composite parent) {
        parent.setLayout(new GridLayout(1, false));
        presenter.setView(this);

        final Composite parentComp = new Composite(parent, SWT.BORDER);
        parentComp.setLayout(new GridLayout(2, false));
        parentComp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
    }
}
