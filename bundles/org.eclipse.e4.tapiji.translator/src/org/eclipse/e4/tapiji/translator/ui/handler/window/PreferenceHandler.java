package org.eclipse.e4.tapiji.translator.ui.handler.window;


import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.tapiji.translator.ui.preferences.PreferenceRegistry;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.widgets.Shell;


public class PreferenceHandler {

    @Execute
    public void switchPerspective(Shell shell, PreferenceRegistry registry) {
        final PreferenceDialog dlg = new PreferenceDialog(shell, registry.getPreferenceManager());
        dlg.create();
        dlg.open();
    }
}
