package org.eclipse.e4.tapiji.git.ui.handler.window;


import javax.inject.Inject;
import javax.inject.Named;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.ui.preference.Preferences;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.widgets.Shell;


public class ProfileHandler {

    @Inject
    IGitService service;

    @Inject
    Preferences prefs;

    @Inject
    UISynchronize sync;

    @Execute
    public void exec(final IEclipseContext context, @Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {

    }
}
