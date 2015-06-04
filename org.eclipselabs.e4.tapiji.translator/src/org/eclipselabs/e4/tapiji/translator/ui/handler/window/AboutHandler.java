package org.eclipselabs.e4.tapiji.translator.ui.handler.window;


import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipselabs.e4.tapiji.resource.ITapijiResourceProvider;
import org.eclipselabs.e4.tapiji.translator.ui.dialog.AboutDialog;


public class AboutHandler {

    private static final String TAG = AboutHandler.class.getSimpleName();

    @Execute
    public void execute(final IEclipseContext context, ITapijiResourceProvider provider) {
        new AboutDialog(new Shell(SWT.SHELL_TRIM), provider).open();
    }
}
