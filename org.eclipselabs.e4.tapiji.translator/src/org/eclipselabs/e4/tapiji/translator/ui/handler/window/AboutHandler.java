package org.eclipselabs.e4.tapiji.translator.ui.handler.window;


import javax.inject.Named;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipselabs.e4.tapiji.translator.ui.dialog.AboutDialog;


public class AboutHandler {

    private static final String TAG = AboutHandler.class.getSimpleName();

    @Execute
    public void execute(final @Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {
        System.out.println("Execute: " + TAG);
        AboutDialog.create(shell).open();
    }
}
