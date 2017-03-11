package org.eclipse.e4.tapiji.translator.ui.handler.window;


import javax.inject.Named;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.tapiji.translator.ui.dialog.ChangelogDialog;
import org.eclipse.swt.widgets.Shell;


public class ChangeLogHandler {

    @Execute
    public void execute(final IEclipseContext context, Shell shell, @Named("org.eclipse.e4.tapiji.translator.commandparameter.changelog") String changeLogFile) {
        ChangelogDialog.show(context, shell, changeLogFile);
    }
}
