package org.eclipse.e4.tapiji.git.ui.handler.trimmbar;


import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.tapiji.git.ui.constant.UIEventConstants;
import org.eclipse.e4.tapiji.git.ui.preference.Preferences;


public class ShowLogsHandler {

    @Execute
    public void exec(IEventBroker eventBroker) {
        eventBroker.send(UIEventConstants.SWITCH_CONTENT_VIEW, null);
    }

    @CanExecute
    public boolean canExecute(Preferences prefs) {
        return prefs.getSelectedRepository() != null;
    }
}
