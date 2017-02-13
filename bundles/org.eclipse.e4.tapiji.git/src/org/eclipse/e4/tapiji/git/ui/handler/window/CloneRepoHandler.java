package org.eclipse.e4.tapiji.git.ui.handler.window;


import javax.inject.Named;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.tapiji.git.ui.dialog.CloneRepositoryDialog;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.widgets.Shell;


public class CloneRepoHandler {

    @Execute
    public void exec(final IEclipseContext context, @Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {
        CloneRepositoryDialog.show(context, shell);
    }

}
