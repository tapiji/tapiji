package org.eclipse.e4.tapiji.git.ui.handler.trimmbar;


import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.tapiji.git.ui.preferences.Preferences;
import org.eclipse.e4.tapiji.logger.Log;


public class PullFastForwardHandler {

    private static final String TAG = PullFastForwardHandler.class.getSimpleName();

    @Execute
    public void execute() {
        Log.d(TAG, "execute()");
    }

    @CanExecute
    public boolean canExecute(Preferences prefs) {
        return prefs.getSelectedRepository() != null;
    }
}
