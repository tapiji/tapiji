package org.eclipse.e4.tapiji.glossary.ui.window;


import javax.inject.Named;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.tapiji.glossary.ui.dialog.UpdateHistoryDialog;
import org.eclipse.swt.widgets.Shell;


public class UpdateHistory {    
   
    @Execute
    public void execute(final IEclipseContext context, Shell shell,@Named("changelog_file") String changeLogFile ) {
    	System.out.println("Updte log");
        UpdateHistoryDialog.show(context, shell, changeLogFile);
    }
}
