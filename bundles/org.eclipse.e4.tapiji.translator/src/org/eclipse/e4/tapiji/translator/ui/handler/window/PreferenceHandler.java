package org.eclipse.e4.tapiji.translator.ui.handler.window;


import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.widgets.Shell;
import at.nucle.e4.plugin.preferences.core.api.EPreferenceService;


public class PreferenceHandler {

    @Execute
    public void switchPerspective(Shell shell, EPreferenceService service) {
        final PreferenceDialog dlg = new PreferenceDialog(shell, service.getPreferenceManager());
        dlg.create();
        dlg.open();
    }
}
