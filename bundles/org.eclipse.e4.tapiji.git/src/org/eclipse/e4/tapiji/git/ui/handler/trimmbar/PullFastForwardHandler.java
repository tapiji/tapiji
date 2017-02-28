package org.eclipse.e4.tapiji.git.ui.handler.trimmbar;


import javax.inject.Inject;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.ui.preferences.Preferences;
import org.eclipse.e4.tapiji.logger.Log;
import org.eclipse.e4.tapiji.mylyn.core.api.IMylynService;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.swt.widgets.Shell;


public class PullFastForwardHandler {

    private static final String TAG = PullFastForwardHandler.class.getSimpleName();

    @Inject
    UISynchronize sync;

    @Execute
    public void execute(final Shell shell, final IGitService service, final IMylynService mylyn) {
        Log.d(TAG, "PullFastForwardHandler execute()");
        service.pullWithMerge();
    }

    @CanExecute
    public boolean canExecute(Preferences prefs) {
        return prefs.getSelectedRepository() != null;
    }
}
