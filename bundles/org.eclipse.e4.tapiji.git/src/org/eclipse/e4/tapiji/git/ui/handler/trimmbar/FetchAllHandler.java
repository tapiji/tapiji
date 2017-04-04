package org.eclipse.e4.tapiji.git.ui.handler.trimmbar;


import javax.inject.Inject;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.model.GitResponse;
import org.eclipse.e4.tapiji.git.model.IGitServiceCallback;
import org.eclipse.e4.tapiji.git.model.exception.GitException;
import org.eclipse.e4.tapiji.git.ui.preference.Preferences;
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
        service.fetch(new IGitServiceCallback<String>() {

            @Override
            public void onSuccess(GitResponse<String> response) {
                if (response.body() == null) {
                    sync.asyncExec(() -> mylyn.sendNotification(new Notification("Fetch data successfully", "")));
                } else {
                    sync.asyncExec(() -> mylyn.sendNotification(new Notification("Fetch result", response.body())));
                }
            }

            @Override
            public void onError(GitException exception) {
                sync.asyncExec(() -> MessageDialog.openError(shell, "Error: ", exception.getMessage()));
            }
        });
    }

    @CanExecute
    public boolean canExecute(Preferences prefs) {
        return prefs.getSelectedRepository() != null;
    }
}
