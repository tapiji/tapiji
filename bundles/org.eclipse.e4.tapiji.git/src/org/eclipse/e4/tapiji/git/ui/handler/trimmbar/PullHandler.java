package org.eclipse.e4.tapiji.git.ui.handler.trimmbar;


import javax.inject.Inject;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.model.GitServiceResult;
import org.eclipse.e4.tapiji.git.model.IGitServiceCallback;
import org.eclipse.e4.tapiji.git.model.exception.GitServiceException;
import org.eclipse.e4.tapiji.git.ui.preference.Preferences;
import org.eclipse.e4.tapiji.logger.Log;
import org.eclipse.e4.tapiji.mylyn.core.api.IMylynService;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.swt.widgets.Shell;


public class PullHandler {

    private static final String TAG = PullHandler.class.getSimpleName();

    @Inject
    UISynchronize sync;

    @Execute
    public void execute(final Shell shell, final IGitService service, final IMylynService mylyn) {
        Log.d(TAG, "PullHandler execute()");
        service.pullFastForward(new IGitServiceCallback<Void>() {

            @Override
            public void onSuccess(GitServiceResult<Void> response) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onError(GitServiceException exception) {
                Log.d(TAG, "PullHandler execute()" + exception);
                if (exception.getCause() instanceof WrongRepositoryStateException) {
                    Log.d(TAG, "PullHandler execute()" + (exception.getCause()));
                    if (exception.getCause().getMessage().contains("MERGING")) {
                        service.mergeStatus(new IGitServiceCallback<Void>() {

                            @Override
                            public void onSuccess(GitServiceResult<Void> response) {
                                // TODO Auto-generated method stub

                            }

                            @Override
                            public void onError(GitServiceException exception) {
                                // TODO Auto-generated method stub

                            }
                        });
                    }
                }
            }
        });
    }

    @CanExecute
    public boolean canExecute(Preferences prefs) {
        return prefs.getSelectedRepository() != null;
    }
}
