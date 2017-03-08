package org.eclipse.e4.tapiji.git.ui.handler.trimmbar;


import javax.inject.Inject;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.model.GitServiceResult;
import org.eclipse.e4.tapiji.git.model.IGitServiceCallback;
import org.eclipse.e4.tapiji.git.model.exception.GitServiceException;
import org.eclipse.e4.tapiji.git.ui.preferences.Preferences;
import org.eclipse.e4.tapiji.mylyn.core.api.IMylynService;
import org.eclipse.e4.tapiji.mylyn.model.Notification;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;


public class FetchAllHandler {

    private static final String TAG = FetchAllHandler.class.getSimpleName();

    @Inject
    UISynchronize sync;

    @Execute
    public void execute(final Shell shell, final IGitService service, final IMylynService mylyn) {
        service.fetchAll(new IGitServiceCallback<String>() {

            @Override
            public void onSuccess(GitServiceResult<String> response) {
                if (response.getResult() == null) {
                    sync.asyncExec(() -> mylyn.sendNotification(new Notification("Fetch data successfully", "")));
                } else {
                    sync.asyncExec(() -> mylyn.sendNotification(new Notification("Fetch result", response.getResult())));
                }
            }

            @Override
            public void onError(GitServiceException exception) {
                sync.asyncExec(() -> MessageDialog.openError(shell, "Error: ", exception.getMessage()));
            }
        });
    }

    @CanExecute
    public boolean canExecute(Preferences prefs) {
        return prefs.getSelectedRepository() != null;
    }
}