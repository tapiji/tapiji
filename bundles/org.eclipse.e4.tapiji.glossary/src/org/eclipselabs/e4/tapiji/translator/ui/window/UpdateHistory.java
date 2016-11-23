package org.eclipselabs.e4.tapiji.translator.ui.window;


import javax.inject.Named;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;
import org.eclipselabs.e4.tapiji.translator.ui.dialog.UpdateHistoryDialog;


public class UpdateHistory {    
   
    @Execute
    public void execute(final IEclipseContext context, Shell shell,@Named("changelog_file") String changeLogFile ) {       
        UpdateHistoryDialog.show(context, shell, changeLogFile);
    }
}
