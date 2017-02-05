package org.eclipse.e4.tapiji.git.ui.handler.window;


import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.tapiji.git.ui.dialog.CloneRepositoryDialog;
import org.eclipse.swt.widgets.Shell;


public class CloneRepoHandler {

    @Execute
    public void exec(final IEclipseContext context, Shell shell) {
        CloneRepositoryDialog.show(context, shell);
    }
}
