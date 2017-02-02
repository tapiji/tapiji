package org.eclipse.e4.tapiji.git.ui.filediff;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class FileDiffView implements FileDiffContract.View {


	@Inject
	FileDiffPresenter presenter;

    @PostConstruct
    public void createPartControl(final Composite parent) {
        parent.setLayout(new GridLayout(1, false));
        presenter.setView(this);

        final Composite parentComp = new Composite(parent, SWT.BORDER);
        parentComp.setLayout(new GridLayout(2, false));
        parentComp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
    }
}
