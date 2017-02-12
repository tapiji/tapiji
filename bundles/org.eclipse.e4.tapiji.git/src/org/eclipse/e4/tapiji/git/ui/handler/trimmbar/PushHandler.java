package org.eclipse.e4.tapiji.git.ui.handler.trimmbar;


import javax.inject.Inject;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.model.GitServiceException;
import org.eclipse.e4.tapiji.git.model.GitServiceResult;
import org.eclipse.e4.tapiji.git.model.IGitServiceCallback;
import org.eclipse.e4.tapiji.git.ui.dialog.LoginDialog;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.swt.widgets.Shell;


public class PushHandler {

    @Inject
    UISynchronize sync;

    @Execute
    public void exec(final IEclipseContext context, Shell shell, final IGitService service) {
        service.pushChanges(new IGitServiceCallback<Void>() {

            @Override
            public void onSuccess(GitServiceResult<Void> response) {
                // TODO mylyn notifications
            }

            @Override
            public void onError(GitServiceException exception) {
                if (exception.getCause() instanceof TransportException) {
                    // TODO check real authentication
                    sync.asyncExec(() -> LoginDialog.show(context, shell, this));
                } else {
                    sync.asyncExec(() -> MessageDialog.openError(shell, "Error: ", exception.getMessage()));
                }
            }
        });
    }
}
