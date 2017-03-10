package org.eclipse.e4.tapiji.git.ui.handler.trimmbar;


import javax.inject.Inject;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.ui.preference.Preferences;
import org.eclipse.e4.tapiji.logger.Log;
import org.eclipse.e4.tapiji.mylyn.core.api.IMylynService;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.swt.widgets.Shell;


public class PullRebaseHandler {

    private static final String TAG = PullRebaseHandler.class.getSimpleName();

    @Inject
    UISynchronize sync;

    @Execute
    public void execute(final Shell shell, final IGitService service, final IMylynService mylyn) {
        Log.d(TAG, "execute()");
        service.pullWithRebase();

    }

    @CanExecute
    public boolean canExecute(Preferences prefs) {
        return prefs.getSelectedRepository() != null;
    }
}
