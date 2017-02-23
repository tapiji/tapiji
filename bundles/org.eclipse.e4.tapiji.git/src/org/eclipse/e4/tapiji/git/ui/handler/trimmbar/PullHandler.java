package org.eclipse.e4.tapiji.git.ui.handler.trimmbar;


import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.ui.preferences.Preferences;
import org.eclipse.e4.tapiji.logger.Log;
import org.eclipse.e4.tapiji.mylyn.core.api.IMylynService;


public class PullHandler {

    private static final String TAG = PullHandler.class.getSimpleName();

    @Execute
    public void execute(final IGitService service, final IMylynService mylyn) {
        Log.d(TAG, "execute()");

    }

    @CanExecute
    public boolean canExecute(Preferences prefs) {
        return prefs.getSelectedRepository() != null;
    }
}
