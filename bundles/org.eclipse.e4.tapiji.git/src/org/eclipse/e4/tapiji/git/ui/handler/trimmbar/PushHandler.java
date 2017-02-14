package org.eclipse.e4.tapiji.git.ui.handler.trimmbar;


import java.util.List;
import javax.inject.Inject;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.model.GitServiceResult;
import org.eclipse.e4.tapiji.git.model.IGitServiceCallback;
import org.eclipse.e4.tapiji.git.model.exception.GitServiceException;
import org.eclipse.e4.tapiji.git.model.push.GitPushMessage;
import org.eclipse.e4.tapiji.git.ui.dialog.LoginDialog;
import org.eclipse.e4.tapiji.logger.Log;
import org.eclipse.e4.tapiji.mylyn.core.api.IMylynService;
import org.eclipse.e4.tapiji.mylyn.model.Notification;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.swt.widgets.Shell;


public class PushHandler {

    private static final String TAG = PushHandler.class.getSimpleName();

    @Inject
    UISynchronize sync;

    @Execute
    public void exec(final IEclipseContext context, final Shell shell, final IGitService service, final IMylynService mylyn) {
        service.pushChanges(new IGitServiceCallback<Void>() {

            @Override
            public void onSuccess(GitServiceResult<Void> response) {
                List<GitPushMessage> messages = response.getMessages();
                for (GitPushMessage message : messages) {
                    Log.d(TAG, message.toString());
                    switch (message.getRemoteStatus()) {
                        case UP_TO_DATE:
                            sync.asyncExec(() -> mylyn.sendNotification(new Notification("Up-To-Date", "Nothing to push.")));
                            break;
                        case OK:
                            sync.asyncExec(() -> mylyn.sendNotification(new Notification("Pushed Successfully", message.getLocalName() + "to origin")));
                            break;
                        default:
                            break;
                    }
                }
            }

            @Override
            public void onError(GitServiceException exception) {
                Log.d(TAG, "onError(" + exception.toString() + ")");
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
