package org.eclipselabs.e4.tapiji.translator.handlers;


import javax.inject.Named;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;


public class ExitHandler {

  private static final String TAG = ExitHandler.class.getSimpleName();

  @Execute
  public void execute(final IWorkbench workbench, final EPartService partService,
          @Named(IServiceConstants.ACTIVE_SHELL) final Shell shell) {
    System.out.println("Execute: " + TAG);

    if (!partService.getDirtyParts().isEmpty()) {
      final boolean confirm = MessageDialog.openConfirm(shell, "Unsaved", "Unsaved data, do you want to save?");
      if (confirm) {
        partService.saveAll(false);
        workbench.close();
        return;
      }
    }

    final boolean result = MessageDialog.openConfirm(shell, "Confirmation", "Do you want to exit?");
    if (result) {
      workbench.close();
    }
  }
}
