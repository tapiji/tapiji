package org.eclipselabs.e4.tapiji.translator.handlers;


import javax.inject.Named;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;


public class ExitHandler {

  private static final String TAG = ExitHandler.class.getSimpleName();

  @Execute
  public void execute(IWorkbench workbench, IEclipseContext context, @Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {
    System.out.println("Execute: " + TAG);
    if (MessageDialog.openConfirm(shell, "Confirmation", "Do you want to exit?")) {
      workbench.close();
    }
  }

}
