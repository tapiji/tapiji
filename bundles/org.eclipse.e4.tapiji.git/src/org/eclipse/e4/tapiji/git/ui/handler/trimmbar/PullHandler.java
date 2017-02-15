package org.eclipse.e4.tapiji.git.ui.handler.trimmbar;


import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.tapiji.git.ui.preferences.Preferences;


public class PullHandler {

    @Execute
    public void execute() {

    }

    @CanExecute
    public boolean canExecute(Preferences prefs) {
        return !prefs.getRepositories().isEmpty();
    }
}
